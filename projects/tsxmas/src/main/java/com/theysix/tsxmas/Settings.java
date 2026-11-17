package com.theysix.tsxmas;

import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.function.Function;

public final class Settings {
    public final Map<String,YamlConfiguration> files = new HashMap<>();
    public final ZoneId zone;
    public final int year;
    public final String season;
    public Settings(Path dir) throws Exception {
        for (String name: List.of("config","messages","rewards","events","locations","database")) {
            var yaml=new YamlConfiguration();
            yaml.load(dir.resolve(name+".yml").toFile());
            if (yaml.getInt("schema-version")!=1) throw new IllegalArgumentException(name+": unsupported schema-version");
            files.put(name,yaml);
        }
        zone=ZoneId.of(config().getString("timezone","Europe/Bucharest"));
        year=config().getInt("year");
        LocalDate.of(year,12,1);
        season=config().getString("season",String.valueOf(year));
        if (!season.matches("[a-zA-Z0-9_-]{1,48}")) throw new IllegalArgumentException("Invalid season");
        if (!List.of("LOCKED","LATE","STAFF").contains(config().getString("calendar.missed-days"))) throw new IllegalArgumentException("calendar.missed-days must be LOCKED, LATE or STAFF");
        for (int i=1;i<=25;i++) requireReward(config().getString("calendar.days."+i));
        String world=locations().getString("world","");
        if (!world.matches("[a-zA-Z0-9_-]+") || List.of("world","world_lobby","world_nether","world_the_end","event_lobby","event_arena").contains(world) || world.startsWith("PG-")) throw new IllegalArgumentException("Use a separate Christmas world name");
        if (!files.get("database").getString("file","").matches("[a-zA-Z0-9_-]+\\.db")) throw new IllegalArgumentException("Database file must be a local .db filename");
        if (config().getBoolean("resource-pack.enabled")) {
            if (!config().getString("resource-pack.url","").startsWith("https://") || !config().getString("resource-pack.sha1","").matches("[a-fA-F0-9]{40}")) throw new IllegalArgumentException("Pack needs HTTPS URL and 40-digit SHA-1");
            if (!List.of("OPT_IN","SEASONAL").contains(config().getString("resource-pack.mode"))) throw new IllegalArgumentException("Invalid resource-pack mode");
        }
        var rewards=files.get("rewards").getConfigurationSection("rewards");
        if(rewards==null) throw new IllegalArgumentException("No rewards configured");
        for(String id:rewards.getKeys(false)) {
            var actions=rewards.getMapList(id);
            if(actions.isEmpty()) throw new IllegalArgumentException("Empty reward: "+id);
            for(var a:actions) {
                String type=String.valueOf(a.get("type"));
                if(!List.of("ITEM","TOKENS","XP","MONEY","CONSOLE","PLAYER","SOUND","PARTICLE","LUCKPERMS","HEADDB","ULTRACOSMETICS","DISCORD").contains(type)) throw new IllegalArgumentException("Unknown reward type in "+id);
                Object amount=a.get("amount");
                if(amount!=null && (!(amount instanceof Number n) || !Double.isFinite(n.doubleValue()) || n.doubleValue()<=0 || n.doubleValue()>1_000_000)) throw new IllegalArgumentException("Invalid amount in "+id);
                if(type.equals("ITEM") && (Material.matchMaterial(String.valueOf(a.get("material")))==null || List.of(Material.AIR,Material.CAVE_AIR,Material.VOID_AIR).contains(Material.matchMaterial(String.valueOf(a.get("material")))))) throw new IllegalArgumentException("Invalid item in "+id);
            }
        }
        var market=events().getConfigurationSection("market.items");
        if(market!=null) for(String id:market.getKeys(false)) {
            String p="market.items."+id;
            requireReward(events().getString(p+".reward"));
            if(events().getLong(p+".price")<=0 || events().getInt(p+".stock")<0 || events().getInt(p+".limit")<=0) throw new IllegalArgumentException("Invalid market price, stock or limit: "+id);
            if(!List.of("TOKENS","MONEY").contains(events().getString(p+".currency"))) throw new IllegalArgumentException("Invalid currency: "+id);
        }
        if(events().getLong("raffle.ticket-cost")<=0 || events().getInt("raffle.max-tickets")<=0 || events().getInt("raffle.winners")<=0) throw new IllegalArgumentException("Invalid raffle configuration");
        ZonedDateTime.parse(events().getString("raffle.draw-at"));
        ZonedDateTime.parse(events().getString("secret.deadline"));
        for(String path:List.of("hunt.completion-reward","delivery.stop-reward","delivery.completion-reward","build.winner-reward","build.participant-reward","grinch.reward"))requireReward(events().getString(path));
        var objectives=events().getConfigurationSection("hunt.objectives");
        if(objectives==null || objectives.getKeys(false).isEmpty())throw new IllegalArgumentException("Hunt needs objectives");
        for(String id:objectives.getKeys(false))requireReward(events().getString("hunt.objectives."+id+".reward"));
        if(events().getStringList("delivery.stops").isEmpty() || events().getStringList("build.categories").isEmpty() || events().getStringList("raffle.prizes").isEmpty())throw new IllegalArgumentException("Missing delivery stops, build categories or raffle prizes");
        for(String prize:events().getStringList("raffle.prizes"))requireReward(prize);
        UUID.fromString(config().getString("resource-pack.id"));
        if(config().getInt("leaderboards.limit",10)<1||config().getInt("leaderboards.limit",10)>28)throw new IllegalArgumentException("leaderboards.limit must be 1–28");
        if(config().getLong("leaderboards.refresh-seconds",10)<1||config().getLong("leaderboards.refresh-seconds",10)>300)throw new IllegalArgumentException("leaderboards.refresh-seconds must be 1–300");
        double mh=events().getDouble("delivery.marker-horizontal-radius",1.5),mv=events().getDouble("delivery.marker-vertical-radius",2.5);
        if(!Double.isFinite(mh)||!Double.isFinite(mv)||mh<0||mh>3||mv<0||mv>4)throw new IllegalArgumentException("Delivery marker radii must be horizontal 0..3 and vertical 0..4");
        var holograms=locations().getConfigurationSection("holograms");
        if(holograms!=null)for(String name:holograms.getKeys(false)) {
            String anchor=locations().getString("holograms."+name+".npc");
            if(anchor!=null && !locations().isConfigurationSection("npcs."+anchor))throw new IllegalArgumentException("Unknown hologram NPC: "+name);
            double height=locations().getDouble("holograms."+name+".height",2.6);
            if(!Double.isFinite(height)||height<0||height>10)throw new IllegalArgumentException("Hologram height must be 0..10: "+name);
            String path="holograms."+name+".leaderboard";
            if(locations().contains(path)&&!Events.NAMES.contains(locations().getString(path)))throw new IllegalArgumentException("Invalid leaderboard hologram: "+name);
        }
    }
    public YamlConfiguration config() { return files.get("config"); }
    public YamlConfiguration events() { return files.get("events"); }
    public YamlConfiguration locations() { return files.get("locations"); }
    public void requireReward(String id) { if(id==null || !files.get("rewards").isList("rewards."+id)) throw new IllegalArgumentException("Unknown reward: "+id); }
    public Location point(String name) {return point(name,Bukkit::getWorld);}
    private Location point(String name,Function<String,World> findWorld) {
        String p="points."+name;
        if (!locations().getBoolean(p+".configured")) throw new IllegalArgumentException("Staff must configure location: "+name);
        World w=findWorld.apply(locations().getString(p+".world",locations().getString("world")));
        if(w==null) throw new IllegalArgumentException("Location world is not loaded: "+name);
        return new Location(w,locations().getDouble(p+".x"),locations().getDouble(p+".y"),locations().getDouble(p+".z"),(float)locations().getDouble(p+".yaw"),(float)locations().getDouble(p+".pitch"));
    }
    public Set<String> requiredPoints(String event) {
        Set<String> points=new LinkedHashSet<>();points.add("hub");
        if(event==null)return points;
        switch(event) {
            case "calendar","raffle" -> {}
            case "hunt" -> {
                for(String id:events().getConfigurationSection("hunt.objectives").getKeys(false))points.add(events().getString("hunt.objectives."+id+".location"));
            }
            case "delivery" -> {points.add("workshop");points.addAll(events().getStringList("delivery.stops"));}
            case "market" -> points.add("market");
            case "secret" -> points.add("secret");
            case "build" -> points.add("build");
            case "grinch" -> points.add(events().getString("grinch.location","grinch"));
            default -> throw new IllegalArgumentException("Unknown event: "+event);
        }
        return points;
    }
    public List<String> validateWorld() {return validateWorld(null);}
    public List<String> validateWorld(String event) {return validateWorld(event,Bukkit::getWorld);}
    List<String> validateWorld(String event,Function<String,World> findWorld) {
        List<String> errors=new ArrayList<>();
        if(findWorld.apply(locations().getString("world"))==null) errors.add("Christmas world is not loaded; TSXMAS never creates or imports worlds automatically.");
        for(String n:requiredPoints(event)) try { var l=point(n,findWorld);if(!inside(l,"boundary"))errors.add("Point outside event boundary: "+n); } catch(IllegalArgumentException e) { errors.add(e.getMessage()); }
        if(!locations().getBoolean("boundary.configured")) errors.add("Event boundary is unconfigured.");
        List<String> boxes=new ArrayList<>();boxes.add("boundary");
        var plots=locations().getConfigurationSection("plots");
        if("build".equals(event)) {
            if(plots!=null)for(String name:plots.getKeys(false))if(locations().getBoolean("plots."+name+".configured"))boxes.add("plots."+name);
            if(boxes.size()==1)errors.add("Build contest needs at least one configured plot; other activities do not require plots.");
        }
        for(String box:boxes)for(String axis:List.of("x","y","z")) {
            double min=locations().getDouble(box+".min-"+axis),max=locations().getDouble(box+".max-"+axis);
            if(!Double.isFinite(min)||!Double.isFinite(max)||min>max)errors.add("Invalid bounds: "+box+" "+axis);
            if(!box.equals("boundary")&&(min<locations().getDouble("boundary.min-"+axis)||max>locations().getDouble("boundary.max-"+axis)))errors.add("Plot outside boundary: "+box);
        }
        for(int i=1;i<boxes.size();i++)for(int j=i+1;j<boxes.size();j++) {
            boolean overlap=true;
            for(String axis:List.of("x","y","z"))overlap &= locations().getDouble(boxes.get(i)+".min-"+axis)<=locations().getDouble(boxes.get(j)+".max-"+axis)&&locations().getDouble(boxes.get(j)+".min-"+axis)<=locations().getDouble(boxes.get(i)+".max-"+axis);
            if(overlap)errors.add("Overlapping plots: "+boxes.get(i)+" / "+boxes.get(j));
        }
        return errors;
    }
    public boolean inside(Location l,String box) {
        var y=locations();String p=box+".";
        return y.getBoolean(p+"configured") && l.getWorld().getName().equals(y.getString("world")) &&
            l.getX()>=y.getDouble(p+"min-x") && l.getX()<=y.getDouble(p+"max-x") &&
            l.getY()>=y.getDouble(p+"min-y") && l.getY()<=y.getDouble(p+"max-y") &&
            l.getZ()>=y.getDouble(p+"min-z") && l.getZ()<=y.getDouble(p+"max-z");
    }
}
