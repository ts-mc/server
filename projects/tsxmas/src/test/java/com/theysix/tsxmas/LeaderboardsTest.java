package com.theysix.tsxmas;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class LeaderboardsTest {
    @TempDir Path dir;
    private static final String A="00000000-0000-0000-0000-000000000001",B="00000000-0000-0000-0000-000000000002",C="00000000-0000-0000-0000-000000000003";
    private Store db;private Leaderboards boards;
    @BeforeEach void setup() throws Exception {db=new Store(dir.resolve("event.db"),"christmas-2026");boards=new Leaderboards(db,()->10,()->0);}
    @AfterEach void close() throws Exception {db.close();}
    @Test void calendarAndHuntCountClaimsInsteadOfOtherStateKeys() throws Exception {
        db.once("calendar",A,"day-1");db.once("calendar",A,"day-2");db.once("calendar",B,"day-1");db.put("calendar",B,"unrelated-setting","x");
        assertEquals(2,boards.board("calendar").entries().getFirst().score());
        assertEquals(A,boards.board("calendar").entries().getFirst().uuid());
        db.once("hunt",A,"present");db.once("hunt",A,"candy");db.once("hunt",B,"present");
        assertEquals(2,boards.board("hunt").entries().getFirst().score());
        assertFalse(db.once("hunt",A,"present"));assertEquals(2,boards.board("hunt").entries().getFirst().score());
    }
    @Test void deliveryIgnoresSessionAndCompletionMetadata() throws Exception {
        db.once("delivery",A,"house-one");db.once("delivery",A,"house-two");db.put("delivery-session",A,"started",1234);db.once("completion",A,"delivery");
        assertEquals(2,boards.board("delivery").entries().getFirst().score());
    }
    @Test void onlyApprovedCurrentBuildEntriesAndTheirVotesArePublic() throws Exception {
        db.put("build:1",A,"approved",true);db.put("build:1",B,"approved",true);db.put("build:1",C,"approved",false);
        db.put("build:1:votes",B,"target",A);db.put("build:1:votes",C,"target",A);db.put("build:1:votes",A,"target",C);
        var first=boards.board("build");assertEquals(2,first.entries().size());assertEquals(A,first.entries().getFirst().uuid());assertEquals(2,first.entries().getFirst().score());assertEquals(0,first.entries().get(1).score());
        db.put("round","server","build",2);assertTrue(boards.board("build").entries().isEmpty());
    }
    @Test void secretSantaNeverPublishesAssignmentsOrGiftParticipantsBeforeReveal() throws Exception {
        db.put("secret-match",A,"recipient",C);db.once("secret-gift",A,"sent");db.put("phase","server","secret","MATCHED");
        assertTrue(boards.board("secret").hidden());assertTrue(boards.board("secret").entries().isEmpty());
        db.put("phase","server","secret","REVEALED");var revealed=boards.board("secret");assertFalse(revealed.hidden());assertEquals(List.of(new Leaderboards.Entry(A,1,1)),revealed.entries());
        assertTrue(revealed.entries().stream().noneMatch(e->e.uuid().equals(C)));
    }
    @Test void privacyIsCheckedBeforeReturningCachedResults() throws Exception {
        var cached=new Leaderboards(db,()->10,()->60_000);db.once("secret-gift",A,"sent");db.put("phase","server","secret","REVEALED");assertEquals(1,cached.board("secret").entries().size());
        db.put("phase","server","secret","REGISTRATION");assertTrue(cached.board("secret").hidden());assertTrue(cached.board("secret").entries().isEmpty());
    }
    @Test void raffleRanksCurrentTicketsAndHidesCancelledEntries() throws Exception {
        db.put("raffle:1",A,"tickets",3);db.put("raffle:1",A,"paid",15);db.put("raffle:1",B,"tickets",7);db.put("raffle:1:refund",A,"refunded",true);
        assertEquals(B,boards.board("raffle").entries().getFirst().uuid());assertEquals(7,boards.board("raffle").entries().getFirst().score());
        db.put("phase","server","raffle","CANCELLED");assertTrue(boards.board("raffle").entries().isEmpty());
        db.put("round","server","raffle",2);db.put("phase","server","raffle","REGISTRATION");assertTrue(boards.board("raffle").entries().isEmpty());
    }
    @Test void marketCountsPaidOrdersWithoutHeldReservations() throws Exception {
        db.put("orders",A,"one","PAID:cookies:10");db.put("orders",A,"two","PAID:keepsake:25");db.put("orders",B,"three","HELD:cookies:10");db.put("market-count",B,"cookies",20);
        assertEquals(List.of(new Leaderboards.Entry(A,2,1)),boards.board("market").entries());
    }
    @Test void grinchListsFinaleCompletionsWithSharedRanks() throws Exception {
        db.once("completion",A,"grinch");db.once("completion",B,"grinch");db.once("completion",C,"hunt");
        assertEquals(List.of(new Leaderboards.Entry(A,1,1),new Leaderboards.Entry(B,1,1)),boards.board("grinch").entries());
    }
    @Test void queriesAreSeasonIsolatedReadOnlyAndBounded() throws Exception {
        db.once("hunt",A,"present");db.enqueue(A,"reward",0,"item");db.tokens(A,9);db.exec("INSERT INTO state VALUES (?,?,?,?,?)","christmas-2025","hunt",B,"present","1");
        var limited=new Leaderboards(db,()->1,()->0);assertEquals(1,limited.board("hunt").entries().size());assertEquals(A,limited.board("hunt").entries().getFirst().uuid());
        assertEquals(9,db.tokens(A));assertEquals(1,db.pending(A).size());assertEquals("1",db.get("hunt",A,"present",""));
        assertThrows(IllegalArgumentException.class,()->boards.board("hunt' OR 1=1 --"));
    }
    @Test void manualCacheInvalidationShowsCommittedProgress() throws Exception {
        var cached=new Leaderboards(db,()->10,()->60_000);assertTrue(cached.board("hunt").entries().isEmpty());db.once("hunt",A,"present");
        assertTrue(cached.board("hunt").entries().isEmpty());cached.clear();assertEquals(1,cached.board("hunt").entries().size());
    }
}
