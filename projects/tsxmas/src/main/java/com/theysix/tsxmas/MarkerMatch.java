package com.theysix.tsxmas;
final class MarkerMatch {
    static boolean matches(double x,double y,double z,double tx,double ty,double tz,double horizontal,double vertical) {
        if(horizontal==0 && vertical==0)return Math.floor(x)==Math.floor(tx)&&Math.floor(y)==Math.floor(ty)&&Math.floor(z)==Math.floor(tz);
        if(horizontal<0 || horizontal>3 || vertical<0 || vertical>4)return false;
        double dx=Math.floor(x)-Math.floor(tx),dz=Math.floor(z)-Math.floor(tz);
        return dx*dx+dz*dz<=horizontal*horizontal && Math.abs(Math.floor(y)-Math.floor(ty))<=vertical;
    }
}
