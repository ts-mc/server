package com.theysix.tsxmas;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class MarkerMatchTest {
 @Test void mailboxAboveFenceAndAdjacentSavedFeetMatch(){assertTrue(MarkerMatch.matches(-131,70,488,-130.4,68,488.5,1.5,2.5));}
 @Test void anotherHouseDoesNotMatch(){assertFalse(MarkerMatch.matches(-135,70,488,-130.4,68,488.5,1.5,2.5));}
 @Test void huntStillRequiresExactBlock(){assertFalse(MarkerMatch.matches(1,2,3,1,1,3,0,0));}
 @Test void oversizedRadiusFailsClosed(){assertFalse(MarkerMatch.matches(1,1,1,1,1,1,100,100));}
}
