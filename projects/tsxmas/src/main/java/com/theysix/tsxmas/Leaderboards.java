package com.theysix.tsxmas;

import java.util.*;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

public final class Leaderboards {
    public record Entry(String uuid,long score,int rank) {}
    public record Board(String event,List<Entry> entries,boolean hidden) {}
    private record Cached(String context,int limit,long expires,Board board) {}
    private final Store db;
    private final IntSupplier limit;
    private final LongSupplier lifetime;
    private final Map<String,Cached> cache=new HashMap<>();
    public Leaderboards(Store db,IntSupplier limit,LongSupplier lifetime) {this.db=db;this.limit=limit;this.lifetime=lifetime;}
    public synchronized void clear() {cache.clear();}
    public synchronized Board board(String event) throws Exception {
        if(!Events.NAMES.contains(event))throw new IllegalArgumentException("Unknown event: "+event);
        int size=Math.max(1,Math.min(28,limit.getAsInt()));
        String round=List.of("build","raffle").contains(event)?db.get("round","server",event,"1"):"";
        String phase=List.of("secret","raffle").contains(event)?db.get("phase","server",event,"REGISTRATION"):"";
        if(event.equals("secret")&&!phase.equals("REVEALED"))return new Board(event,List.of(),true);
        if(event.equals("raffle")&&phase.equals("CANCELLED"))return new Board(event,List.of(),false);
        String context=round+":"+phase;long now=System.nanoTime();Cached old=cache.get(event);
        if(old!=null&&old.context.equals(context)&&old.limit==size&&old.expires>now)return old.board;
        List<Map<String,String>> rows;
        if(event.equals("build")) {
            String scope="build:"+round;
            rows=db.query("SELECT a.owner,COUNT(v.owner) AS score FROM state a LEFT JOIN state v ON v.season=a.season AND v.scope=? AND v.key='target' AND v.value=a.owner WHERE a.season=? AND a.scope=? AND a.key='approved' AND a.value='true' GROUP BY a.owner ORDER BY score DESC,a.owner ASC LIMIT ?",scope+":votes",db.season,scope,size);
        } else if(event.equals("raffle")) {
            rows=db.query("SELECT owner,CAST(value AS INTEGER) AS score FROM state WHERE season=? AND scope=? AND key='tickets' AND CAST(value AS INTEGER)>0 ORDER BY score DESC,owner ASC LIMIT ?",db.season,"raffle:"+round,size);
        } else if(event.equals("market")) {
            rows=db.query("SELECT owner,COUNT(*) AS score FROM state WHERE season=? AND scope='orders' AND value LIKE 'PAID:%' GROUP BY owner ORDER BY score DESC,owner ASC LIMIT ?",db.season,size);
        } else {
            String scope=switch(event){case "secret"->"secret-gift";case "grinch"->"completion";default->event;};
            String pattern=switch(event){case "calendar"->"day-%";case "secret"->"sent";case "grinch"->"grinch";default->"%";};
            rows=db.query("SELECT owner,COUNT(*) AS score FROM state WHERE season=? AND scope=? AND key LIKE ? GROUP BY owner ORDER BY score DESC,owner ASC LIMIT ?",db.season,scope,pattern,size);
        }
        List<Entry> entries=new ArrayList<>();long previous=Long.MIN_VALUE;int rank=0;
        for(int i=0;i<rows.size();i++) {
            var row=rows.get(i);long score=Long.parseLong(row.get("score"));
            if(score!=previous)rank=i+1;
            entries.add(new Entry(row.get("owner"),score,rank));previous=score;
        }
        Board board=new Board(event,List.copyOf(entries),false);
        cache.put(event,new Cached(context,size,now+Math.max(0,lifetime.getAsLong())*1_000_000L,board));return board;
    }
}
