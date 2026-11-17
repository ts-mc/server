package com.theysix.tsxmas;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;

public final class Commands implements CommandExecutor,TabCompleter {
    private final TSXMASPlugin p;
    private record Confirmation(boolean admin,String[] args,String nonce,long expires) {}
    private final Map<String,Confirmation> confirmations=new HashMap<>();
    public Commands(TSXMASPlugin p){this.p=p;}
    private void permission(CommandSender sender,String node) {if(!sender.hasPermission(node))throw new IllegalArgumentException(p.text("no-permission"));}
    private Player player(CommandSender sender) {if(!(sender instanceof Player pl))throw new IllegalArgumentException(p.text("player-only"));return pl;}
    private String key(CommandSender sender) {return sender instanceof Player pl?pl.getUniqueId().toString():"console";}
    private String arg(String[] args,int i,String usage) {if(args.length<=i)throw new IllegalArgumentException("Usage: "+usage);return args[i];}
    private int positive(String input) {int n=Integer.parseInt(input);if(n<1 || n>1_000_000)throw new IllegalArgumentException("Amount must be 1–1000000");return n;}
    private String target(String[] args,int i,String usage) throws Exception {return p.resolvePlayer(arg(args,i,usage));}
    private void staff(CommandSender sender){permission(sender,"tsxmas.staff");}
    private boolean confirm(CommandSender sender,boolean admin,String[] args,boolean confirmed) {
        if(confirmed)return true;
        String nonce=UUID.randomUUID().toString().substring(0,8);
        confirmations.put(key(sender),new Confirmation(admin,args.clone(),nonce,System.currentTimeMillis()+30000));
        p.reply(sender,p.text("confirm")+" /"+(admin?"tsxmas":"xmas")+" confirm "+nonce+" (30s)");return false;
    }
    @Override public boolean onCommand(CommandSender sender,Command command,String label,String[] args) {execute(sender,command.getName().equals("tsxmas"),args);return true;}
    public void execute(CommandSender sender,boolean admin,String[] args) {
        try {
            if(args.length>0 && args[0].equalsIgnoreCase("confirm")) {
                Confirmation c=confirmations.remove(key(sender));
                if(c==null || c.admin()!=admin || c.expires()<System.currentTimeMillis() || !c.nonce().equals(arg(args,1,"confirm <code>")))throw new IllegalArgumentException("Confirmation expired or invalid");
                run(sender,c.admin(),c.args(),true);
            }else run(sender,admin,args,false);
        }catch(NumberFormatException e){p.reply(sender,p.text("error")+"Enter a valid number");}
        catch(IllegalArgumentException e){p.reply(sender,p.text("error")+e.getMessage());}
        catch(Exception e){p.reply(sender,p.text("internal-error"));p.getLogger().warning("Command failed ("+e.getClass().getSimpleName()+"); inspect reward ledger before repeating economic actions");}
    }
    private void run(CommandSender sender,boolean admin,String[] args,boolean confirmed) throws Exception {
        permission(sender,admin?"tsxmas.admin":"tsxmas.player");
        String cmd=args.length==0?(admin?"help":"menu"):args[0].toLowerCase(Locale.ROOT);
        if(admin){admin(sender,cmd,args,confirmed);return;}
        Store db=p.store(); Events events=p.events();
        if(Events.NAMES.contains(cmd)&&args.length>1&&args[1].equalsIgnoreCase("top")) {showLeaderboard(sender,cmd);return;}
        switch(cmd) {
            case "top","leaderboard","leaderboards" -> {
                if(args.length>1)showLeaderboard(sender,args[1].toLowerCase(Locale.ROOT));
                else if(sender instanceof Player pl)p.menus().leaderboardSelect(pl);
                else p.reply(sender,"Use /xmas top <event>: "+String.join(", ",Events.NAMES));
            }
            case "music" -> p.presentation().music(player(sender),!arg(args,1,"/xmas music <on|off>").equals("off"));
            case "grinch" -> {events.require("grinch");Player pl=player(sender);if(!events.near(pl,p.settings().events().getString("grinch.location")))throw new IllegalArgumentException("Visit the Grinch challenge area");if(db.get("completion",key(sender),"hunt","").isEmpty()||db.get("completion",key(sender),"delivery","").isEmpty())throw new IllegalArgumentException("Complete the hunt and Santa's route first");db.tx(()->{if(!db.once("completion",key(sender),"grinch"))throw new IllegalArgumentException("Challenge already completed");p.rewards().enqueue(key(sender),"grinch:complete",p.settings().events().getString("grinch.reward"));return null;});p.rewards().deliver(pl);p.msg(sender,"claimed");}
            case "help" -> p.settings().files.get("messages").getStringList(sender.hasPermission("tsxmas.staff")?"staff-help":"player-help").forEach(l->p.reply(sender,l));
            case "menu","events" -> p.menus().main(player(sender));
            case "rewards" -> {Player pl=player(sender);p.rewards().deliver(pl);p.reply(sender,p.text("rewards-pending")+db.query("SELECT status,COUNT(*) AS count FROM outbox WHERE season=? AND uuid=? AND status!='DONE' GROUP BY status",db.season,key(sender)));}
            case "stats" -> p.reply(sender,"Tokens: "+db.tokens(key(sender))+" | Hunt: "+events.progress("hunt",key(sender))+" | Deliveries: "+events.progress("delivery",key(sender))+" | Calendar: "+events.progress("calendar",key(sender)));
            case "pack" -> {Player pl=player(sender);if(args.length>1&&args[1].equals("off"))pl.removeResourcePack(UUID.fromString(p.settings().config().getString("resource-pack.id")));else p.sendPack(pl);}
            case "calendar" -> {
                if(args.length<2)p.menus().calendar(player(sender),false);
                else switch(args[1]) {
                    case "preview" -> p.menus().calendar(player(sender),true);
                    case "claim" -> events.claim(player(sender),positive(arg(args,2,"/xmas calendar claim <day>")));
                    case "unlock" -> {staff(sender);String uuid=target(args,2,"/xmas calendar unlock <player> <day>");int day=positive(arg(args,3,"/xmas calendar unlock <player> <day>"));if(day>25)throw new IllegalArgumentException("Day must be 1–25");db.tx(()->{db.put("unlock",uuid,"day-"+day,"1");db.audit(key(sender),"calendar unlock "+uuid+" "+day);return null;});p.msg(sender,"saved");}
                    default -> throw new IllegalArgumentException("Use calendar [preview|claim <day>|unlock <player> <day>]");
                }
            }
            case "hunt" -> {
                String sub=args.length<2?"menu":args[1];
                switch(sub) {
                    case "menu" -> p.menus().activity(player(sender),"hunt",args.length>2?Integer.parseInt(args[2]):1);
                    case "progress" -> p.reply(sender,"Hunt: "+events.progress("hunt",key(sender))+" / "+p.settings().events().getConfigurationSection("hunt.objectives").getKeys(false).size());
                    case "top" -> events.top("hunt").forEach(l->p.reply(sender,l));
                    case "find" -> events.discover(player(sender),arg(args,2,"/xmas hunt find <objective>"));
                    case "reset" -> {staff(sender);String uuid=target(args,2,"/xmas hunt reset <player>");if(confirm(sender,false,args,confirmed))events.reset(uuid,"hunt",key(sender));}
                    case "reload" -> {staff(sender);p.reloadSettings();p.msg(sender,"reloaded");}
                    default -> throw new IllegalArgumentException("Use hunt [progress|top|find <id>|reset <player>|reload]");
                }
            }
            case "delivery" -> {
                String sub=args.length<2?"menu":args[1];
                switch(sub) {
                    case "menu" -> p.menus().activity(player(sender),"delivery",args.length>2?Integer.parseInt(args[2]):1);
                    case "start" -> events.deliveryStart(player(sender));
                    case "progress" -> {
                        p.reply(sender,"Deliveries: "+events.progress("delivery",key(sender))+" / "+p.settings().events().getStringList("delivery.stops").size());
                        for(String stop:p.settings().events().getStringList("delivery.stops"))if(db.get("delivery",key(sender),stop,"").isEmpty())p.reply(sender,"Remaining: "+stop);
                    }
                    case "deliver" -> events.deliver(player(sender),arg(args,2,"/xmas delivery deliver <stop>"));
                    case "reset" -> {staff(sender);String uuid=target(args,2,"/xmas delivery reset <player>");if(confirm(sender,false,args,confirmed))events.reset(uuid,"delivery",key(sender));}
                    default -> throw new IllegalArgumentException("Use delivery [start|progress|deliver <stop>|reset <player>]");
                }
            }
            case "build" -> {
                String sub=args.length<2?"status":args[1];
                switch(sub) {
                    case "join" -> events.buildJoin(player(sender),args.length>2?args[2]:p.settings().events().getStringList("build.categories").getFirst());
                    case "submit" -> events.buildSubmit(player(sender));
                    case "vote" -> {if(args.length<3)p.reply(sender,"Use /xmas build vote <player>; approved entries: "+db.list("build:"+events.round("build")).stream().filter(r->r.get("key").equals("approved")&&r.get("value").equals("true")).map(r->p.playerName(r.get("owner"))).toList());else events.buildVote(player(sender),target(args,2,"/xmas build vote <player>"));}
                    case "winners" -> events.buildWinners().forEach(r->p.reply(sender,r.get("key")+": "+p.playerName(r.get("owner"))+" ("+r.get("value")+" votes)"));
                    case "approve","reject" -> {staff(sender);events.buildModerate(target(args,2,"/xmas build "+sub+" <player>"),sub.equals("approve"),key(sender));p.msg(sender,"saved");}
                    case "start" -> {staff(sender);events.transition("build","start",key(sender));p.msg(sender,"saved");}
                    case "end" -> {staff(sender);if(confirm(sender,false,args,confirmed)){events.buildEnd(key(sender));p.reply(sender,"Build phase: "+events.phase("build"));}}
                    case "reset" -> {permission(sender,"tsxmas.admin");if(confirm(sender,false,args,confirmed)){events.buildReset(key(sender));p.msg(sender,"saved");}}
                    case "status" -> p.reply(sender,"Build phase: "+events.phase("build")+" | Plot: "+db.get("build:"+events.round("build"),key(sender),"plot","none"));
                    default -> throw new IllegalArgumentException("Use build [join|submit|vote|winners|approve|reject|start|end|reset]");
                }
            }
            case "secret" -> {
                String sub=args.length<2?"status":args[1];
                switch(sub) {
                    case "join" -> events.secretJoin(player(sender));
                    case "gift" -> {if(confirm(sender,false,args,confirmed))events.secretGift(player(sender));}
                    case "status" -> p.reply(sender,events.secretStatus(key(sender)));
                    case "match" -> {permission(sender,"tsxmas.admin");if(confirm(sender,false,args,confirmed))events.secretMatch(key(sender));}
                    case "reveal" -> {permission(sender,"tsxmas.admin");if(confirm(sender,false,args,confirmed))events.secretReveal();}
                    case "inspect" -> {permission(sender,"tsxmas.admin");p.reply(sender,events.secretStatus(target(args,2,"/xmas secret inspect <player>")));}
                    case "reset" -> {permission(sender,"tsxmas.admin");String uuid=target(args,2,"/xmas secret reset <player>");if(confirm(sender,false,args,confirmed))events.reset(uuid,"secret",key(sender));}
                    default -> throw new IllegalArgumentException("Use secret [join|gift|status|match|reveal|inspect|reset]");
                }
            }
            case "raffle" -> {
                String sub=args.length<2?"status":args[1];
                switch(sub) {
                    case "buy" -> events.raffleBuy(player(sender),positive(arg(args,2,"/xmas raffle buy <amount>")));
                    case "status" -> events.raffleStatus(key(sender)).forEach(l->p.reply(sender,l));
                    case "draw","cancel","refund" -> {permission(sender,"tsxmas.admin");if(confirm(sender,false,args,confirmed)){switch(sub){case "draw"->events.raffleDraw(key(sender));case "cancel"->events.raffleCancel(key(sender));default->events.raffleRefund(key(sender));}p.msg(sender,"saved");}}
                    default -> throw new IllegalArgumentException("Use raffle [buy <amount>|status|draw|cancel|refund]");
                }
            }
            case "market" -> {
                String sub=args.length<2?"menu":args[1];
                switch(sub) {
                    case "menu" -> p.menus().market(player(sender));
                    case "buy" -> p.events().marketBuy(player(sender),arg(args,2,"/xmas market buy <item>"));
                    case "stock" -> {for(String item:p.settings().events().getConfigurationSection("market.items").getKeys(false))p.reply(sender,item+": "+events.stock(item));}
                    case "restock" -> {staff(sender);String item=arg(args,2,"/xmas market restock <item>");if(confirm(sender,false,args,confirmed))events.restock(item,key(sender));}
                    case "reload" -> {staff(sender);p.reloadSettings();p.msg(sender,"reloaded");}
                    default -> throw new IllegalArgumentException("Use market [stock|buy <item>|restock <item>|reload]");
                }
            }
            case "staff" -> {staff(sender);for(String name:Events.NAMES)p.reply(sender,name+": "+events.state(name));}
            case "start","stop","pause","resume" -> {staff(sender);if(confirm(sender,false,args,confirmed)){
                String event=arg(args,1,"/xmas "+cmd+" <event>");
                if(cmd.equals("start") && event.equals("ready")) {
                    for(String candidate:Events.NAMES) {
                        if(!events.state(candidate).equals("STOPPED"))continue;
                        var errors=p.settings().validateWorld(candidate);
                        if(!errors.isEmpty()){p.reply(sender,"Skipped "+candidate+": "+String.join("; ",errors));continue;}
                        events.transition(candidate,"start",key(sender));p.reply(sender,"Started "+candidate);
                    }
                }else events.transition(event,cmd,key(sender));p.msg(sender,"saved");
            }}
            case "setspawn","setlocation","setnpc","sethologram" -> {
                staff(sender);Player pl=player(sender);if(!pl.getWorld().getName().equals(p.settings().locations().getString("world")))throw new IllegalArgumentException("Stand in the configured Christmas world");
                String name=cmd.equals("setspawn")?"hub":arg(args,1,"/xmas "+cmd+" <name>");
                if(!name.matches("[a-z0-9_-]{1,48}"))throw new IllegalArgumentException("Use a simple lowercase location name");
                String root=switch(cmd){case "setnpc"->"npcs.";case "sethologram"->"holograms.";default->"points.";};
                var y=p.settings().locations();String path=root+name+".";var l=pl.getLocation();
                y.set(path+"configured",true);y.set(path+"world",l.getWorld().getName());y.set(path+"x",l.getX());y.set(path+"y",l.getY());y.set(path+"z",l.getZ());y.set(path+"yaw",l.getYaw());y.set(path+"pitch",l.getPitch());
                y.save(p.getDataFolder().toPath().resolve("locations.yml").toFile());db.audit(key(sender),cmd+" "+name);p.presentation().refresh();p.msg(sender,"location-saved");
            }
            case "reload" -> {staff(sender);p.reloadSettings();p.msg(sender,"reloaded");}
            case "inspect" -> {staff(sender);String uuid=target(args,1,"/xmas inspect <player>");p.reply(sender,"UUID: "+uuid+" | Tokens: "+db.tokens(uuid)+" | Pending: "+db.query("SELECT id,source,status FROM outbox WHERE season=? AND uuid=? AND status!='DONE'",db.season,uuid));}
            case "give" -> {permission(sender,"tsxmas.admin");String uuid=target(args,1,"/xmas give <player> <reward>"),reward=arg(args,2,"/xmas give <player> <reward>");p.settings().requireReward(reward);if(confirm(sender,false,args,confirmed)){db.tx(()->{p.rewards().enqueue(uuid,"staff:"+UUID.randomUUID(),reward);db.audit(key(sender),"give "+uuid+" "+reward);return null;});p.msg(sender,"saved");}}
            case "tokens" -> {permission(sender,"tsxmas.admin");String sub=arg(args,1,"/xmas tokens <add|remove> <player> <amount>"),uuid=target(args,2,"/xmas tokens <add|remove> <player> <amount>");long amount=positive(arg(args,3,"/xmas tokens <add|remove> <player> <amount>"));if(!List.of("add","remove").contains(sub))throw new IllegalArgumentException("Use add or remove");if(confirm(sender,false,args,confirmed)){db.tx(()->{db.tokens(uuid,sub.equals("add")?amount:-amount);db.audit(key(sender),"tokens "+sub+" "+uuid+" "+amount);return null;});p.msg(sender,"saved");}}
            case "reset" -> {permission(sender,"tsxmas.admin");String uuid=target(args,1,"/xmas reset <player> <feature>"),feature=arg(args,2,"/xmas reset <player> <feature>");if(confirm(sender,false,args,confirmed)){events.reset(uuid,feature,key(sender));p.msg(sender,"saved");}}
            case "broadcast" -> {staff(sender);String message=String.join(" ",Arrays.copyOfRange(args,1,args.length));if(message.isBlank())throw new IllegalArgumentException("Provide a message");p.announceText(message);}
            default -> throw new IllegalArgumentException("Unknown command. Use /xmas help");
        }
    }
    private void showLeaderboard(CommandSender sender,String event) throws Exception {
        var board=p.leaderboards().board(event);
        if(sender instanceof Player pl) {p.menus().leaderboard(pl,event);return;}
        p.reply(sender,p.text("leaderboard-heading").replace("{event}",p.leaderboardTitle(event))+" | "+p.leaderboardMetric(event));
        if(board.hidden()||board.entries().isEmpty())p.reply(sender,p.text(board.hidden()?"leaderboard-private":"leaderboard-empty"));
        else for(var entry:board.entries())p.reply(sender,p.text("leaderboard-row").replace("{rank}",String.valueOf(entry.rank())).replace("{player}",p.playerName(entry.uuid())).replace("{score}",String.valueOf(entry.score())));
    }
    private void admin(CommandSender sender,String cmd,String[] args,boolean confirmed) throws Exception {
        switch(cmd) {
            case "help" -> p.reply(sender,"/tsxmas setup|validate|version|reload|backup|restore|migrate|debug|resolve|shutdown");
            case "setup","validate" -> {var errors=p.settings().validateWorld(args.length>1?args[1]:null);if(errors.isEmpty())p.msg(sender,"validation-ok");else errors.forEach(l->p.reply(sender,l));}
            case "version" -> p.reply(sender,"TSXMAS "+p.getPluginMeta().getVersion()+" | Paper "+Bukkit.getMinecraftVersion()+" | Java "+Runtime.version().feature());
            case "reload" -> {p.reloadSettings();p.msg(sender,"reloaded");}
            case "backup" -> {var path=p.backup();p.reply(sender,"TSXMAS data/config snapshot: "+path.getFileName()+". This is not a full server backup.");}
            case "restore" -> p.msg(sender,"offline-restore");
            case "migrate" -> {p.reply(sender,"Database schema 1; migrations run transactionally on startup. Never import unreviewed test data. See README.");}
            case "debug" -> {permission(sender,"tsxmas.debug");p.reply(sender,"Rewards: "+p.store().query("SELECT status,COUNT(*) AS count FROM outbox GROUP BY status"));for(String name:List.of("Vault","WorldGuard","FastAsyncWorldEdit","LuckPerms","FancyNpcs","FancyHolograms","DiscordSRV","HeadDB","HeadDatabase","NoteBlockAPI","floodgate"))p.reply(sender,name+": "+p.integrations().has(name));}
            case "resolve" -> {
                long id=positive(arg(args,1,"/tsxmas resolve <id> <done|retry>"));String decision=arg(args,2,"/tsxmas resolve <id> <done|retry>");
                if(!List.of("done","retry").contains(decision))throw new IllegalArgumentException("Use done or retry after verifying whether the effect happened");
                if(confirm(sender,true,args,confirmed)) {p.store().tx(()->{if(p.store().exec("UPDATE outbox SET status=? WHERE id=? AND status='HELD'",decision.equals("done")?"DONE":"PENDING",id)!=1)throw new IllegalArgumentException("No held reward with that ID");p.store().audit(key(sender),"resolve reward "+id+" "+decision);return null;});p.msg(sender,"saved");}
            }
            case "shutdown" -> {if(confirm(sender,true,args,confirmed)){p.store().audit(key(sender),"shutdown TSXMAS");Bukkit.getPluginManager().disablePlugin(p);p.msg(sender,"shutdown");}}
            default -> throw new IllegalArgumentException("Unknown admin command. Use /tsxmas help");
        }
    }
    @Override public List<String> onTabComplete(CommandSender sender,Command command,String alias,String[] args) {
        boolean admin=command.getName().equals("tsxmas");if(!sender.hasPermission(admin?"tsxmas.admin":"tsxmas.player"))return List.of();
        List<String> result=new ArrayList<>();
        if(args.length==1) {
            result.addAll(admin?List.of("setup","validate","version","reload","backup","restore","migrate","shutdown"):List.of("help","menu","calendar","events","rewards","stats","hunt","delivery","build","secret","raffle","market","pack","music","grinch","top","leaderboards"));
            if(admin&&sender.hasPermission("tsxmas.debug"))result.add("debug");
            if(admin)result.add("resolve");
            if(!admin&&sender.hasPermission("tsxmas.staff"))result.addAll(List.of("staff","start","stop","pause","resume","setspawn","setlocation","setnpc","sethologram","reload","inspect","broadcast"));
            if(!admin&&sender.hasPermission("tsxmas.admin"))result.addAll(List.of("give","tokens","reset"));
        } else if(args.length==2) {
            if(!admin&&Events.NAMES.contains(args[0]))result.add("top");
            switch(args[0]) {
                case "top","leaderboard","leaderboards" -> {if(!admin)result.addAll(Events.NAMES);}
                case "sethologram","setnpc","setlocation" -> {
                    if(!admin&&sender.hasPermission("tsxmas.staff")) {
                        String root=args[0].equals("sethologram")?"holograms":args[0].equals("setnpc")?"npcs":"points";
                        var section=p.settings().locations().getConfigurationSection(root);if(section!=null)result.addAll(section.getKeys(false));
                    }
                }
                case "setup","validate" -> {if(admin)result.addAll(Events.NAMES);}
                case "calendar" -> {result.add("claim");if(sender.hasPermission("tsxmas.staff"))result.addAll(List.of("preview","unlock"));}
                case "hunt" -> {result.addAll(List.of("progress","top","find"));if(sender.hasPermission("tsxmas.staff"))result.addAll(List.of("reset","reload"));}
                case "delivery" -> {result.addAll(List.of("start","progress","deliver"));if(sender.hasPermission("tsxmas.staff"))result.add("reset");}
                case "build" -> {result.addAll(List.of("join","submit","vote","winners","status"));if(sender.hasPermission("tsxmas.staff"))result.addAll(List.of("approve","reject","start","end"));if(sender.hasPermission("tsxmas.admin"))result.add("reset");}
                case "secret" -> {result.addAll(List.of("join","gift","status"));if(sender.hasPermission("tsxmas.admin"))result.addAll(List.of("match","reveal","inspect","reset"));}
                case "raffle" -> {result.addAll(List.of("buy","status"));if(sender.hasPermission("tsxmas.admin"))result.addAll(List.of("draw","cancel","refund"));}
                case "market" -> {result.addAll(List.of("stock","buy"));if(sender.hasPermission("tsxmas.staff"))result.addAll(List.of("restock","reload"));}
                case "start","stop","pause","resume" -> {if(sender.hasPermission("tsxmas.staff")){result.addAll(Events.NAMES);if(args[0].equals("start"))result.add("ready");}}
                case "pack","music" -> result.addAll(List.of("on","off"));
                case "tokens" -> {if(sender.hasPermission("tsxmas.admin"))result.addAll(List.of("add","remove"));}
                default -> Bukkit.getOnlinePlayers().forEach(pl->result.add(pl.getName()));
            }
        } else if(args.length==3) {
            if(args[0].equals("calendar")&&args[1].equals("claim"))for(int i=1;i<=25;i++)result.add(String.valueOf(i));
            else if(args[0].equals("hunt")&&args[1].equals("find"))result.addAll(p.settings().events().getConfigurationSection("hunt.objectives").getKeys(false));
            else if(args[0].equals("delivery")&&args[1].equals("deliver"))result.addAll(p.settings().events().getStringList("delivery.stops"));
            else if(args[0].equals("build")&&args[1].equals("join"))result.addAll(p.settings().events().getStringList("build.categories"));
            else if(args[0].equals("market"))result.addAll(p.settings().events().getConfigurationSection("market.items").getKeys(false));
            else if(args[0].equals("give")&&sender.hasPermission("tsxmas.admin"))result.addAll(p.settings().files.get("rewards").getConfigurationSection("rewards").getKeys(false));
            else Bukkit.getOnlinePlayers().forEach(pl->result.add(pl.getName()));
        }
        String prefix=args.length==0?"":args[args.length-1].toLowerCase(Locale.ROOT);return result.stream().filter(x->x.toLowerCase(Locale.ROOT).startsWith(prefix)).distinct().sorted().toList();
    }
}
