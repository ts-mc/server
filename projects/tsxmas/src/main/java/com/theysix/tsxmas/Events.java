package com.theysix.tsxmas;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.time.*;
import java.util.*;
import java.security.SecureRandom;

public final class Events {
    public static final List<String> NAMES=List.of("calendar","hunt","delivery","build","secret","raffle","market","grinch");
    private final TSXMASPlugin p;
    private final Store db;
    private final Rewards rewards;
    private final Random random=new SecureRandom();
    public Events(TSXMASPlugin p,Store db,Rewards rewards) { this.p=p;this.db=db;this.rewards=rewards; }
    private Settings s() { return p.settings(); }
    private String id(Player p) { return p.getUniqueId().toString(); }
    public String state(String name) throws Exception {
        if(!NAMES.contains(name)) throw new IllegalArgumentException("Unknown event: "+name);
        return db.get("event","server",name,"STOPPED");
    }
    public void require(String name) throws Exception {
        if(!s().config().getBoolean("enabled")) throw new IllegalArgumentException("Christmas events are disabled until setup is complete");
        if(!state(name).equals("RUNNING")) throw new IllegalArgumentException(name+" is "+state(name).toLowerCase(Locale.ROOT));
    }
    public void transition(String name,String action,String actor) throws Exception {
        String old=state(name);
        String next=switch(action) {case "start","resume"->"RUNNING";case "stop"->"STOPPED";case "pause"->"PAUSED";default->throw new IllegalArgumentException("Unknown transition");};
        if(action.equals("resume") && !old.equals("PAUSED") || action.equals("pause") && !old.equals("RUNNING") || action.equals("start") && !old.equals("STOPPED")) throw new IllegalArgumentException("Invalid event transition from "+old);
        if(next.equals("RUNNING")) {
            if(!s().config().getBoolean("enabled")) throw new IllegalArgumentException("Enable config after setup validation");
            var errors=s().validateWorld(name);
            if(!errors.isEmpty()) throw new IllegalArgumentException(String.join("; ",errors));
            if(s().config().getBoolean("protection.require-worldguard") && !p.integrations().has("WorldGuard")) throw new IllegalArgumentException("WorldGuard is required by the protection policy");
        }
        db.tx(()->{db.put("event","server",name,next);db.audit(actor,action+" "+name);return null;});
    }
    public AdventClock.Day calendar(Player player,int day) throws Exception {
        return AdventClock.state(Instant.now(),s().zone,s().year,day,!db.get("calendar",id(player),"day-"+day,"").isEmpty(),s().config().getString("calendar.missed-days"),!db.get("unlock",id(player),"day-"+day,"").isEmpty());
    }
    public void claim(Player player,int day) throws Exception {
        require("calendar");
        db.tx(()->{
            if(calendar(player,day)!=AdventClock.Day.AVAILABLE) throw new IllegalArgumentException("This calendar day is not available");
            if(!db.once("calendar",id(player),"day-"+day)) throw new IllegalArgumentException("Already claimed");
            rewards.enqueue(id(player),"calendar:"+day,s().config().getString("calendar.days."+day));
            db.audit(id(player),"calendar day "+day);return null;
        });
        rewards.deliver(player);p.msg(player,"claimed");
    }
    public long progress(String feature,String uuid) throws Exception {
        return db.list(feature).stream().filter(r->r.get("owner").equals(uuid)).count();
    }
    public List<String> top(String feature) throws Exception {
        return db.query("SELECT owner,COUNT(*) AS total FROM state WHERE season=? AND scope=? GROUP BY owner ORDER BY total DESC,owner LIMIT 10",db.season,feature).stream().map(r->p.playerName(r.get("owner"))+": "+r.get("total")).toList();
    }
    public void discover(Player player,String objective) throws Exception {
        require("hunt");
        String path="hunt.objectives."+objective;
        if(!s().events().isConfigurationSection(path)) throw new IllegalArgumentException("Unknown objective");
        if(!near(player,s().events().getString(path+".location"))) throw new IllegalArgumentException("Find this objective in the Christmas world");
        db.tx(()->{
            if(!db.once("hunt",id(player),objective)) throw new IllegalArgumentException("You already found this present");
            rewards.enqueue(id(player),"hunt:"+objective,s().events().getString(path+".reward"));
            int total=s().events().getConfigurationSection("hunt.objectives").getKeys(false).size();
            if(progress("hunt",id(player))==total && db.once("completion",id(player),"hunt")) rewards.enqueue(id(player),"hunt:complete",s().events().getString("hunt.completion-reward"));
            return null;
        });
        rewards.deliver(player);p.msg(player,"discovery");
    }
    public boolean near(Player player,String point) {
        try {
            var target=s().point(point);
            return target.getWorld().equals(player.getWorld()) && target.distanceSquared(player.getLocation())<=s().config().getDouble("interaction-radius",4)*s().config().getDouble("interaction-radius",4) && s().inside(target,"boundary") && p.integrations().canUse(player,target);
        } catch(Exception e) {return false;}
    }
    private boolean nearSanta(Player player) {
        var y=s().locations();String base="npcs.santa.";
        if(!y.getBoolean(base+"configured"))return false;
        var world=Bukkit.getWorld(y.getString(base+"world",y.getString("world")));
        if(world==null || !world.equals(player.getWorld()))return false;
        var location=new org.bukkit.Location(world,y.getDouble(base+"x"),y.getDouble(base+"y"),y.getDouble(base+"z"));
        return s().inside(location,"boundary") && location.distanceSquared(player.getLocation())<=16 && p.integrations().canUse(player,location);
    }
    public boolean awaitingDelivery(Player player,String stop) throws Exception {
        return state("delivery").equals("RUNNING") && !db.get("delivery-session",id(player),"started","").isEmpty() && db.get("delivery",id(player),stop,"").isEmpty();
    }
    public void deliveryStart(Player player) throws Exception {
        require("delivery");
        if(!near(player,"workshop") && !nearSanta(player)) throw new IllegalArgumentException("Visit Santa at the workshop to start");
        if(!db.get("completion",id(player),"delivery","").isEmpty()) throw new IllegalArgumentException("You completed this season's route");
        if(!db.get("delivery-session",id(player),"started","").isEmpty()) throw new IllegalArgumentException("You already have a delivery route");
        db.put("delivery-session",id(player),"started",Instant.now().getEpochSecond());
        p.msg(player,"delivery-started");
    }
    public void deliver(Player player,String stop) throws Exception {
        require("delivery");
        String started=db.get("delivery-session",id(player),"started","");
        if(started.isEmpty()) throw new IllegalArgumentException("Start your route at Santa's workshop");
        long limit=s().events().getLong("delivery.time-limit-seconds");
        if(limit>0 && Instant.now().getEpochSecond()-Long.parseLong(started)>limit) throw new IllegalArgumentException("Delivery time expired; ask staff to reset your route");
        if(!s().events().getStringList("delivery.stops").contains(stop) || !near(player,stop)) throw new IllegalArgumentException("Visit a configured delivery stop");
        db.tx(()->{
            if(!db.once("delivery",id(player),stop)) throw new IllegalArgumentException("This house already received its present");
            rewards.enqueue(id(player),"delivery:"+stop,s().events().getString("delivery.stop-reward"));
            if(progress("delivery",id(player))==s().events().getStringList("delivery.stops").size() && db.once("completion",id(player),"delivery")) rewards.enqueue(id(player),"delivery:complete",s().events().getString("delivery.completion-reward"));
            return null;
        });rewards.deliver(player);p.msg(player,"delivered");
    }
    public String round(String feature) throws Exception {return db.get("round","server",feature,"1");}
    public String phase(String feature) throws Exception {return db.get("phase","server",feature,"REGISTRATION");}
    private String scope(String feature) throws Exception {return feature+":"+round(feature);}
    public void buildJoin(Player player,String category) throws Exception {
        require("build");
        if(!phase("build").equals("REGISTRATION")) throw new IllegalArgumentException("Building is closed");
        if(!s().events().getStringList("build.categories").contains(category)) throw new IllegalArgumentException("Choose category: "+s().events().getStringList("build.categories"));
        String scope=scope("build");
        db.tx(()->{
            if(!db.get(scope,id(player),"plot","").isEmpty()) throw new IllegalArgumentException("You already have a plot");
            var plots=s().locations().getConfigurationSection("plots");
            if(plots==null) throw new IllegalArgumentException("No build plots configured");
            Set<String> used=new HashSet<>();for(var r:db.list(scope)) if(r.get("key").equals("plot")) used.add(r.get("value"));
            for(String plot:plots.getKeys(false)) if(!used.contains(plot) && s().locations().getBoolean("plots."+plot+".configured")) {
                db.put(scope,id(player),"plot",plot);db.put(scope,id(player),"category",category);return null;
            }
            throw new IllegalArgumentException("No free plots available");
        });p.msg(player,"build-joined");
    }
    public boolean canBuild(Player player,Location location) throws Exception {
        if(!s().config().getBoolean("enabled") || !state("build").equals("RUNNING") || !phase("build").equals("REGISTRATION")) return false;
        String plot=db.get(scope("build"),id(player),"plot","");
        return !plot.isEmpty() && s().inside(location,"boundary") && s().inside(location,"plots."+plot) && db.get(scope("build"),id(player),"submitted","").isEmpty();
    }
    public void buildSubmit(Player player) throws Exception {
        require("build");String scope=scope("build");
        if(!phase("build").equals("REGISTRATION") || db.get(scope,id(player),"plot","").isEmpty()) throw new IllegalArgumentException("Join the open contest first");
        if(!db.once(scope,id(player),"submitted")) throw new IllegalArgumentException("Already submitted");p.msg(player,"build-submitted");
    }
    public void buildModerate(String uuid,boolean approve,String actor) throws Exception {
        String scope=scope("build");
        if(db.get(scope,uuid,"submitted","").isEmpty()) throw new IllegalArgumentException("Player has not submitted");
        db.tx(()->{db.put(scope,uuid,"approved",approve);db.audit(actor,"build moderation "+uuid+" "+approve);return null;});
    }
    public void buildVote(Player player,String target) throws Exception {
        require("build");String scope=scope("build");
        if(!phase("build").equals("VOTING") || !db.get(scope,target,"approved","false").equals("true")) throw new IllegalArgumentException("Voting is closed or submission is not approved");
        if(target.equals(id(player))) throw new IllegalArgumentException("You cannot vote for yourself");
        db.tx(()->{
            if(!db.once(scope+":voted",id(player),"vote")) throw new IllegalArgumentException("You already voted this round");
            db.put(scope+":votes",id(player),"target",target);return null;
        });p.msg(player,"voted");
    }
    public void buildEnd(String actor) throws Exception {
        require("build");String scope=scope("build");
        db.tx(()->{
            if(phase("build").equals("REGISTRATION")) {db.put("phase","server","build","VOTING");return null;}
            if(!phase("build").equals("VOTING")) throw new IllegalArgumentException("Contest already ended");
            for(String category:s().events().getStringList("build.categories")) {
                List<String> entries=new ArrayList<>();
                for(var r:db.list(scope)) if(r.get("key").equals("approved") && r.get("value").equals("true") && db.get(scope,r.get("owner"),"category","").equals(category)) entries.add(r.get("owner"));
                Map<String,Long> votes=new HashMap<>();for(var r:db.list(scope+":votes")) votes.merge(r.get("value"),1L,Long::sum);
                entries.sort(Comparator.<String>comparingLong(u->votes.getOrDefault(u,0L)).reversed().thenComparing(u->u));
                if(!entries.isEmpty()) {
                    String winner=entries.getFirst();db.put(scope+":winners",winner,category,votes.getOrDefault(winner,0L));
                    rewards.enqueue(winner,scope+":winner",s().events().getString("build.winner-reward"));
                }
                for(String uuid:entries) rewards.enqueue(uuid,scope+":participation",s().events().getString("build.participant-reward"));
            }
            db.put("phase","server","build","ENDED");db.audit(actor,"build ended "+round("build"));return null;
        });
    }
    public List<Map<String,String>> buildWinners() throws Exception {return db.list(scope("build")+":winners");}
    public void buildReset(String actor) throws Exception {
        db.tx(()->{db.put("round","server","build",Long.parseLong(round("build"))+1);db.put("phase","server","build","REGISTRATION");db.audit(actor,"new build round (old data preserved)");return null;});
    }
    public void secretJoin(Player player) throws Exception {
        require("secret");
        if(!phase("secret").equals("REGISTRATION") || Instant.now().isAfter(ZonedDateTime.parse(s().events().getString("secret.deadline")).toInstant())) throw new IllegalArgumentException("Secret Santa registration is closed");
        if(!db.once("secret-joined",id(player),"joined")) throw new IllegalArgumentException("Already registered");p.msg(player,"secret-joined");
    }
    public void secretMatch(String actor) throws Exception {
        require("secret");
        if(Instant.now().isBefore(ZonedDateTime.parse(s().events().getString("secret.deadline")).toInstant())) throw new IllegalArgumentException("Wait for the registration deadline");
        db.tx(()->{
            if(!phase("secret").equals("REGISTRATION")) throw new IllegalArgumentException("Matches already generated");
            List<String> people=new ArrayList<>(db.list("secret-joined").stream().map(r->r.get("owner")).toList());
            if(people.size()<2) throw new IllegalArgumentException("At least two players must register");
            Collections.shuffle(people,random);
            for(int i=0;i<people.size();i++) db.put("secret-match",people.get(i),"recipient",people.get((i+1)%people.size()));
            db.put("phase","server","secret","MATCHED");db.audit(actor,"Secret Santa matched (recipients omitted)");return null;
        });p.announce("secret-matched");
    }
    public String secretStatus(String uuid) throws Exception {
        String target=db.get("secret-match",uuid,"recipient","");
        return target.isEmpty()?"Registration: "+!db.get("secret-joined",uuid,"joined","").isEmpty():"Your recipient: "+p.playerName(target)+". Gift submitted: "+!db.get("secret-gift",uuid,"sent","").isEmpty();
    }
    public void secretGift(Player player) throws Exception {
        require("secret");String uuid=id(player);
        if(!phase("secret").equals("MATCHED")) throw new IllegalArgumentException("Gift exchange is not open");
        String target=db.get("secret-match",uuid,"recipient","");
        if(target.isEmpty()) throw new IllegalArgumentException("No recipient assigned");
        ItemStack gift=player.getInventory().getItemInMainHand().clone();
        if(!s().events().getStringList("secret.allowed-materials").contains(gift.getType().name()) || gift.getAmount()<s().events().getInt("secret.min-amount",1) || gift.getAmount()>s().events().getInt("secret.max-amount",64) || gift.hasItemMeta()) throw new IllegalArgumentException("Hold an allowed, unmodified gift stack; containers and custom NBT gifts are not accepted");
        String source="secret:"+uuid;
        db.tx(()->{
            if(!db.once("secret-gift",uuid,"sent")) throw new IllegalArgumentException("Gift already sent");
            long budget=s().events().getLong("secret.token-budget",0);if(budget>0) db.tokens(uuid,-budget);
            rewards.gift(target,source,gift);
            db.exec("UPDATE outbox SET status='HELD' WHERE season=? AND uuid=? AND source=?",db.season,target,source);
            db.audit(uuid,"Secret gift escrow created; no recipient in audit");return null;
        });
        player.getInventory().setItemInMainHand(null);player.saveData();
        db.exec("UPDATE outbox SET status='PENDING' WHERE season=? AND uuid=? AND source=? AND status='HELD'",db.season,target,source);
        Player recipient=Bukkit.getPlayer(UUID.fromString(target));if(recipient!=null) rewards.deliver(recipient);
        p.msg(player,"secret-sent");
    }
    public void secretReveal() throws Exception {
        require("secret");
        if(!phase("secret").equals("MATCHED")) throw new IllegalArgumentException("No matches to reveal");
        long sent=db.list("secret-gift").size(),total=db.list("secret-match").size();
        if(sent!=total) throw new IllegalArgumentException("All gifts must be submitted before reveal");
        db.put("phase","server","secret","REVEALED");
        for(var r:db.list("secret-match")) Bukkit.broadcast(net.kyori.adventure.text.Component.text(p.playerName(r.get("owner"))+" was Secret Santa for "+p.playerName(r.get("value"))));
    }
    public void raffleBuy(Player player,int amount) throws Exception {
        require("raffle");String scope=scope("raffle");
        if(Instant.now().isAfter(ZonedDateTime.parse(s().events().getString("raffle.draw-at")).toInstant()) || !phase("raffle").equals("REGISTRATION")) throw new IllegalArgumentException("Raffle sales are closed");
        if(amount<1 || amount>s().events().getInt("raffle.max-tickets")) throw new IllegalArgumentException("Invalid ticket amount");
        long cost=Math.multiplyExact(s().events().getLong("raffle.ticket-cost"),amount);
        db.tx(()->{
            long current=Long.parseLong(db.get(scope,id(player),"tickets","0"));
            if(current+amount>s().events().getInt("raffle.max-tickets")) throw new IllegalArgumentException("Ticket limit reached");
            db.tokens(id(player),-cost);db.put(scope,id(player),"tickets",current+amount);
            db.put(scope,id(player),"paid",Math.addExact(Long.parseLong(db.get(scope,id(player),"paid","0")),cost));return null;
        });p.msg(player,"purchased");
    }
    public List<String> raffleStatus(String uuid) throws Exception {
        String scope=scope("raffle");
        return List.of("Round "+round("raffle")+": "+phase("raffle"),"Your tickets: "+db.get(scope,uuid,"tickets","0"),"Draw: "+s().events().getString("raffle.draw-at"));
    }
    public void raffleDraw(String actor) throws Exception {
        require("raffle");String scope=scope("raffle");List<String> winners=new ArrayList<>();
        db.tx(()->{
            if(!phase("raffle").equals("REGISTRATION")) throw new IllegalArgumentException("Raffle already closed");
            Map<String,Long> entries=new TreeMap<>();for(var r:db.list(scope)) if(r.get("key").equals("tickets")) entries.put(r.get("owner"),Long.parseLong(r.get("value")));
            List<String> prizes=s().events().getStringList("raffle.prizes");
            for(int i=0;i<s().events().getInt("raffle.winners") && !entries.isEmpty();i++) {
                long total=entries.values().stream().mapToLong(Long::longValue).sum();if(total<=0) break;
                long pick=random.nextLong(total);String selected=null;
                for(var e:entries.entrySet()) {pick-=e.getValue();if(pick<0) {selected=e.getKey();break;}}
                entries.remove(selected);winners.add(selected);
                rewards.enqueue(selected,scope+":winner",prizes.get(i%prizes.size()));
                db.put(scope+":winners",selected,"prize",prizes.get(i%prizes.size()));
            }
            db.put("phase","server","raffle","DRAWN");db.audit(actor,"raffle drawn round "+round("raffle"));return null;
        });
        for(String winner:winners) p.announceText("Christmas raffle winner: "+p.playerName(winner));
    }
    public void raffleCancel(String actor) throws Exception {
        if(!phase("raffle").equals("REGISTRATION")) throw new IllegalArgumentException("Only an undrawn raffle can be cancelled");
        db.tx(()->{db.put("phase","server","raffle","CANCELLED");db.audit(actor,"raffle cancelled");return null;});
    }
    public void raffleRefund(String actor) throws Exception {
        String scope=scope("raffle");
        db.tx(()->{
            if(!phase("raffle").equals("CANCELLED")) throw new IllegalArgumentException("Cancel raffle before refunding");
            for(var r:db.list(scope)) if(r.get("key").equals("paid") && db.once(scope+":refund",r.get("owner"),"refunded")) db.tokens(r.get("owner"),Long.parseLong(r.get("value")));
            db.audit(actor,"raffle refunds completed");return null;
        });
    }
    public long stock(String item) throws Exception {
        String path="market.items."+item;
        if(!s().events().isConfigurationSection(path)) throw new IllegalArgumentException("Unknown market item");
        return Long.parseLong(db.get("market-stock","server",item,String.valueOf(s().events().getLong(path+".stock"))));
    }
    public void marketBuy(Player player,String item) throws Exception {
        require("market");String path="market.items."+item,uuid=id(player);
        long stock=stock(item),price=s().events().getLong(path+".price");
        long purchased=Long.parseLong(db.get("market-count",uuid,item,"0"));
        if(stock<=0 || purchased>=s().events().getLong(path+".limit")) throw new IllegalArgumentException("Sold out or purchase limit reached");
        String source="market:"+UUID.randomUUID(),reward=s().events().getString(path+".reward");
        if(s().events().getString(path+".currency").equals("MONEY")) {
            p.integrations().economy();
            db.tx(()->{db.put("market-stock","server",item,stock-1);db.put("market-count",uuid,item,purchased+1);db.put("orders",uuid,source,"HELD:"+item+":"+price);return null;});
            try { p.integrations().money(player,price,false); }
            catch(Exception e) { throw new IllegalArgumentException("Order held for staff reconciliation: "+source); }
            db.tx(()->{rewards.enqueue(uuid,source,reward);db.put("orders",uuid,source,"PAID:"+item+":"+price);return null;});
        } else db.tx(()->{
            db.tokens(uuid,-price);db.put("market-stock","server",item,stock-1);db.put("market-count",uuid,item,purchased+1);
            rewards.enqueue(uuid,source,reward);db.put("orders",uuid,source,"PAID:"+item+":"+price);return null;
        });rewards.deliver(player);p.msg(player,"purchased");
    }
    public void restock(String item,String actor) throws Exception {
        stock(item);db.tx(()->{db.put("market-stock","server",item,s().events().getLong("market.items."+item+".stock"));db.audit(actor,"restock "+item);return null;});
    }
    public void reset(String uuid,String feature,String actor) throws Exception {
        if(!List.of("hunt","delivery","calendar","secret").contains(feature)) throw new IllegalArgumentException("Supported resets: hunt, delivery, calendar, secret");
        if(feature.equals("secret") && !phase("secret").equals("REGISTRATION")) throw new IllegalArgumentException("Matched Secret Santa records cannot be removed; repair via a reviewed offline database migration");
        db.tx(()->{
            db.exec("DELETE FROM state WHERE season=? AND scope=? AND owner=?",db.season,feature.equals("secret")?"secret-joined":feature,uuid);
            if(feature.equals("delivery")) db.exec("DELETE FROM state WHERE season=? AND scope='delivery-session' AND owner=?",db.season,uuid);
            db.audit(actor,"reset "+feature+" "+uuid+" (reward ledger preserved)");return null;
        });
    }
    public void tick() throws Exception {
        if(!s().config().getBoolean("enabled")) return;
        if(state("raffle").equals("RUNNING") && phase("raffle").equals("REGISTRATION") && !Instant.now().isBefore(ZonedDateTime.parse(s().events().getString("raffle.draw-at")).toInstant())) raffleDraw("scheduler");
        var today=LocalDate.now(s().zone);
        if(state("calendar").equals("RUNNING") && today.getYear()==s().year && today.getMonthValue()==12 && today.getDayOfMonth()<=25 && db.once("announcement","server",today.toString())) p.announce("daily-calendar");
    }
}
