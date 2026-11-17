package com.theysix.tsxmas;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class InventoryCapacityTest {
    @Test void fullStorageDefersItems(){assertFalse(InventoryCapacity.fits(1,new int[36]));}
    @Test void compatiblePartialStacksCanCombine(){assertTrue(InventoryCapacity.fits(8,new int[]{3,0,5}));assertFalse(InventoryCapacity.fits(9,new int[]{3,0,5}));}
    @Test void unstackableNeedsEmptySlot(){assertFalse(InventoryCapacity.fits(1,new int[]{0,0}));assertTrue(InventoryCapacity.fits(1,new int[]{0,1}));}
    @Test void invalidAmountIsRejected(){assertThrows(IllegalArgumentException.class,()->InventoryCapacity.fits(0,new int[]{64}));}
}
