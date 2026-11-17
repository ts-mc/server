package com.theysix.tsxmas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class StoreTest {
    @TempDir Path dir;
    @Test void duplicateClaimsAndActionsAreIdempotent() throws Exception {
        try(Store db=new Store(dir.resolve("data.db"),"christmas")) {
            String uuid=UUID.randomUUID().toString();
            db.tx(()->{assertTrue(db.once("calendar",uuid,"1"));db.enqueue(uuid,"calendar:1",0,"original");return null;});
            assertFalse(db.once("calendar",uuid,"1"));
            db.enqueue(uuid,"calendar:1",0,"changed-after-reload");
            assertEquals(1,db.pending(uuid).size());assertEquals("original",db.pending(uuid).getFirst().get("payload"));
        }
    }
    @Test void failedPurchaseRollsBackBalanceStockAndQueue() throws Exception {
        try(Store db=new Store(dir.resolve("data.db"),"christmas")) {
            db.tokens("player",10);
            assertThrows(IllegalArgumentException.class,()->db.tx(()->{db.put("stock","server","cookie",0);db.enqueue("player","purchase",0,"item");db.tokens("player",-11);return null;}));
            assertEquals(10,db.tokens("player"));assertEquals("5",db.get("stock","server","cookie","5"));assertTrue(db.pending("player").isEmpty());
            assertThrows(ArithmeticException.class,()->db.tokens("player",Long.MAX_VALUE));
        }
    }
    @Test void disconnectAndCrashRecoveryNeverReplaysUncertainEffects() throws Exception {
        Path file=dir.resolve("data.db");
        try(Store db=new Store(file,"christmas")) {
            db.enqueue("offline","source",0,"item");db.enqueue("offline","source",1,"command");
            db.exec("UPDATE outbox SET status='IN_FLIGHT' WHERE step=1");
        }
        try(Store db=new Store(file,"christmas")) {
            assertEquals(1,db.pending("offline").size());
            assertEquals("HELD",db.query("SELECT status FROM outbox WHERE step=1").getFirst().get("status"));
        }
    }
    @Test void snapshotIncludesWalAndSeasonIsolation() throws Exception {
        Path file=dir.resolve("data.db"),backup=dir.resolve("backup.db");
        try(Store db=new Store(file,"2026")){db.tokens("player",23);db.backup(backup);assertThrows(IllegalArgumentException.class,()->db.backup(backup));}
        try(Store db=new Store(backup,"2026")){assertEquals(23,db.tokens("player"));}
        try(Store db=new Store(file,"2027")){assertEquals(0,db.tokens("player"));}
    }
    @Test void futureSchemaIsRejectedWithoutDestruction() throws Exception {
        Path file=dir.resolve("data.db");
        try(Store db=new Store(file,"2026")){db.exec("PRAGMA user_version=99");}
        assertThrows(IllegalStateException.class,()->new Store(file,"2026"));
    }
}
