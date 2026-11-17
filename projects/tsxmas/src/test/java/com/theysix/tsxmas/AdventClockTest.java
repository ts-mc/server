package com.theysix.tsxmas;

import org.junit.jupiter.api.Test;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;

class AdventClockTest {
    private final ZoneId zone=ZoneId.of("Europe/Bucharest");
    private AdventClock.Day state(String instant,int day,String policy,boolean unlocked){return AdventClock.state(Instant.parse(instant),zone,2026,day,false,policy,unlocked);}
    @Test void bucharestMidnightOpensDecemberFirst(){
        assertEquals(AdventClock.Day.CLOSED,state("2026-11-30T21:59:59Z",1,"LOCKED",false));
        assertEquals(AdventClock.Day.AVAILABLE,state("2026-11-30T22:00:00Z",1,"LOCKED",false));
    }
    @Test void futureDaysCannotBeUnlocked(){assertEquals(AdventClock.Day.FUTURE,state("2026-12-01T12:00:00Z",25,"STAFF",true));}
    @Test void missedDayPolicies(){
        assertEquals(AdventClock.Day.MISSED,state("2026-12-05T12:00:00Z",1,"LOCKED",false));
        assertEquals(AdventClock.Day.AVAILABLE,state("2026-12-05T12:00:00Z",1,"LATE",false));
        assertEquals(AdventClock.Day.MISSED,state("2026-12-05T12:00:00Z",1,"STAFF",false));
        assertEquals(AdventClock.Day.AVAILABLE,state("2026-12-05T12:00:00Z",1,"STAFF",true));
    }
    @Test void yearBoundaryAndInvalidDays(){
        assertEquals(AdventClock.Day.CLOSED,state("2027-01-01T00:00:00Z",25,"LATE",true));
        assertThrows(IllegalArgumentException.class,()->state("2026-12-01T00:00:00Z",26,"LOCKED",false));
        assertEquals(AdventClock.Day.CLAIMED,AdventClock.state(Instant.parse("2026-12-01T00:00:00Z"),zone,2026,1,true,"LATE",false));
    }
}
