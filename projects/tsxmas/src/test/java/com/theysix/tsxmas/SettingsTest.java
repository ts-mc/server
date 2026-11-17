package com.theysix.tsxmas;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class SettingsTest {
    @TempDir Path dir;
    @BeforeEach void resources() throws Exception {
        for(String n:List.of("config","database","events","messages","rewards","locations"))try(var in=getClass().getResourceAsStream("/"+n+".yml")){Files.copy(in,dir.resolve(n+".yml"));}
    }
    private void replace(String file,String from,String to) throws Exception {Path p=dir.resolve(file+".yml");Files.writeString(p,Files.readString(p).replace(from,to));}
    @Test void oversizedMailboxRadiusIsRejected() throws Exception {replace("events","marker-horizontal-radius: 1.5","marker-horizontal-radius: 30");assertThrows(IllegalArgumentException.class,()->new Settings(dir));}
    @Test void missingHologramNpcIsRejected() throws Exception {replace("locations","npc: santa","npc: unknown");assertThrows(IllegalArgumentException.class,()->new Settings(dir));}
    @Test void excessiveHologramHeightIsRejected() throws Exception {replace("locations","height: 2.7","height: 100");assertThrows(IllegalArgumentException.class,()->new Settings(dir));}
    @Test void defaultsLoadDisabledAndPreserveTimezone() throws Exception {Settings s=new Settings(dir);assertFalse(s.config().getBoolean("enabled"));assertEquals("Europe/Bucharest",s.zone.getId());}
    @Test void invalidTimezoneFailsCandidateReload() throws Exception {replace("config","Europe/Bucharest","Not/AZone");assertThrows(Exception.class,()->new Settings(dir));}
    @Test void productionWorldNamesAreRefused() throws Exception {replace("locations","world: tsxmas_christmas_2026","world: world_lobby");assertThrows(IllegalArgumentException.class,()->new Settings(dir));}
    @Test void resourcePackRequiresHashAndHttps() throws Exception {replace("config","enabled: false","enabled: true");replace("config","848a7a8e8d2ef149bec10b336b77185cb15936a2","invalid");assertThrows(IllegalArgumentException.class,()->new Settings(dir));}
    @Test void invalidRewardReferenceFails() throws Exception {replace("config","'1': daily","'1': unknown");assertThrows(IllegalArgumentException.class,()->new Settings(dir));}
    @Test void traversalDatabasePathIsRefused() throws Exception {replace("database","tsxmas.db","../outside.db");assertThrows(IllegalArgumentException.class,()->new Settings(dir));}
    @Test void commonVenueDoesNotRequireUnconfiguredOptionalAreas() throws Exception {
        Settings s=new Settings(dir);assertEquals(Set.of("hub"),s.requiredPoints(null));
        assertFalse(s.locations().getBoolean("points.build.configured"));
        assertFalse(s.locations().getBoolean("points.grinch.configured"));
    }
    @Test void activitiesOnlyRequireTheirOwnConfiguredLocations() throws Exception {
        Settings s=new Settings(dir);
        assertEquals(Set.of("hub","forest-present","candy-cane","snowman"),s.requiredPoints("hunt"));
        assertEquals(Set.of("hub","workshop","house-one","house-two","house-three"),s.requiredPoints("delivery"));
        assertEquals(Set.of("hub","market"),s.requiredPoints("market"));
        assertEquals(Set.of("hub"),s.requiredPoints("calendar"));
        assertEquals(Set.of("hub","build"),s.requiredPoints("build"));
        assertEquals(Set.of("hub","grinch"),s.requiredPoints("grinch"));
    }
    @Test void customObjectiveAndGrinchLocationsRemainValidated() throws Exception {
        replace("events","location: forest-present","location: hidden-bell");replace("events","location: grinch","location: forest-finale");
        Settings s=new Settings(dir);assertTrue(s.requiredPoints("hunt").contains("hidden-bell"));assertTrue(s.requiredPoints("grinch").contains("forest-finale"));
        assertThrows(IllegalArgumentException.class,()->s.requiredPoints("typo"));
    }
    @Test void leaderboardLimitsFailBeforeReplacingSettings() throws Exception {
        replace("config","limit: 10","limit: 99");assertThrows(IllegalArgumentException.class,()->new Settings(dir));
    }
    @Test void leaderboardHologramsAreOptionalAndTheirEventNamesAreChecked() throws Exception {
        Settings s=new Settings(dir);assertEquals(Set.of("hub"),s.requiredPoints(null));
        replace("locations","leaderboard: hunt","leaderboard: unknown");assertThrows(IllegalArgumentException.class,()->new Settings(dir));
    }
}
