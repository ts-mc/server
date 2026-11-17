package com.theysix.tsxmas;

final class InventoryCapacity {
    private InventoryCapacity(){}
    static boolean fits(int amount,int[] available){
        if(amount<=0)throw new IllegalArgumentException("Invalid item amount");
        long room=0;for(int n:available){room+=Math.max(0,n);if(room>=amount)return true;}return false;
    }
}
