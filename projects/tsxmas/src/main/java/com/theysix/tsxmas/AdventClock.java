package com.theysix.tsxmas;

import java.time.*;

public final class AdventClock {
    public enum Day { FUTURE, AVAILABLE, CLAIMED, MISSED, CLOSED }
    private AdventClock() {}
    public static Day state(Instant now, ZoneId zone, int year, int day, boolean claimed, String policy, boolean unlocked) {
        if (day < 1 || day > 25) throw new IllegalArgumentException("Calendar day must be 1–25");
        if (claimed) return Day.CLAIMED;
        LocalDate today = now.atZone(zone).toLocalDate();
        LocalDate date = LocalDate.of(year, 12, day);
        if (today.getYear() != year || today.getMonthValue() != 12) return Day.CLOSED;
        if (today.isBefore(date)) return Day.FUTURE;
        if (today.equals(date) || unlocked || policy.equals("LATE")) return Day.AVAILABLE;
        return Day.MISSED;
    }
}
