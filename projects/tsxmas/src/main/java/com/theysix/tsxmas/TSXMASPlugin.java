package com.theysix.tsxmas;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.nio.file.*;
import java.time.*;
import java.util.*;

public final class TSXMASPlugin extends JavaPlugin {
    private volatile Settings settings;
    private Store store;
    private Integrations integrations;
    private Rewards rewards;
    private Events events;
    private Menus menus;
    private Commands commands;
    private Expansion expansion;
    private Presentation presentation;
    private Leaderboards leaderboards;
    private long lastFailure;
    @Override public void onEnable() {
        try {
            for(String name:List.of("config","messages","rewards","events","locations","database")) if(!Files.exists(getDataFolder().toPath().resolve(name+".yml")))saveResource(name+".yml",false);
            settings=new Settings(getDataFolder().toPath());
            store=new Store(getDataFolder().toPath().resolve(settings.files.get("database").getString("file")),settings.season);
            integrations=new Integrations(this);rewards=new Rewards(this,store);events=new Events(this,store,rewards);menus=new Menus(this);commands=new Commands(this);
            leaderboards=new Leaderboards(store,()->settings.config().getInt("leaderboards.limit",10),()->settings.config().getLong("leaderboards.refresh-seconds",10)*1000L);
            for(String name:List.of("xmas","tsxmas")) {Objects.requireNonNull(getCommand(name)).setExecutor(commands);getCommand(name).setTabCompleter(commands);}
            getServer().getPluginManager().registerEvents(menus,this);
            getServer().getPluginManager().registerEvents(new WorldEvents(this),this);
            presentation=new Presentation(this);
            getServer().getScheduler().runTaskLater(this,presentation::refresh,40L);
            if(integrations.has("PlaceholderAPI")) {expansion=new Expansion(this);expansion.register();}
            getServer().getScheduler().runTaskTimer(this,()->{
                try {events.tick();for(Player player:Bukkit.getOnlinePlayers()) rewards.deliver(player);}
                catch(Exception e) {if(System.currentTimeMillis()-lastFailure>60000){getLogger().severe("Event scheduler failed: "+e.getClass().getSimpleName()+"; inspect configuration and database");lastFailure=System.currentTimeMillis();}}
            },200L,200L);
            getServer().getScheduler().runTaskTimer(this,presentation::updateLeaderboards,20L,20L);
            getLogger().info("TSXMAS "+getPluginMeta().getVersion()+" ready; season="+settings.season+", enabled="+settings.config().getBoolean("enabled")+". Worlds are never imported automatically.");
        } catch(Exception e) {getLogger().severe("Startup refused: "+e.getClass().getSimpleName()+": "+e.getMessage());getServer().getPluginManager().disablePlugin(this);}
    }
    @Override public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        if(expansion!=null)expansion.unregister();
        if(presentation!=null)presentation.close();
        if(store!=null)try{store.close();}catch(Exception e){getLogger().severe("Database close failed");}
    }
    public void reloadSettings() throws Exception {
        Settings candidate=new Settings(getDataFolder().toPath());
        if(!candidate.season.equals(settings.season) || !candidate.files.get("database").getString("file").equals(settings.files.get("database").getString("file")) || candidate.year!=settings.year)throw new IllegalArgumentException("Season, year and database changes require a restart and backup");
        if(candidate.config().getBoolean("enabled")) {
            var errors=candidate.validateWorld();
            for(String event:Events.NAMES)if(!events.state(event).equals("STOPPED"))errors.addAll(candidate.validateWorld(event));
            if(!errors.isEmpty())throw new IllegalArgumentException(String.join("; ",errors));
        }
        for(Player player:Bukkit.getOnlinePlayers())player.closeInventory();
        settings=candidate;
        if(leaderboards!=null)leaderboards.clear();
        if(presentation!=null)presentation.refresh();
    }
    public String text(String key) {return settings.files.get("messages").getString(key,key);}
    public Leaderboards leaderboards() {return leaderboards;}
    public String leaderboardTitle(String event) {return settings.files.get("messages").getString("leaderboard-titles."+event,event);}
    public String leaderboardMetric(String event) {return settings.files.get("messages").getString("leaderboard-metrics."+event,event);}
    public List<String> leaderboardLines(String event) throws Exception {
        var board=leaderboards.board(event);List<String> lines=new ArrayList<>();
        lines.add(text("leaderboard-hologram-heading").replace("{event}",leaderboardTitle(event)));
        lines.add(text("leaderboard-metric").replace("{metric}",leaderboardMetric(event)));
        if(board.hidden())lines.add(text("leaderboard-private"));
        else if(board.entries().isEmpty())lines.add(text("leaderboard-empty"));
        else for(var entry:board.entries())lines.add(text("leaderboard-row").replace("{rank}",String.valueOf(entry.rank())).replace("{player}",net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().escapeTags(playerName(entry.uuid()))).replace("{score}",String.valueOf(entry.score())));
        return lines;
    }
    public void msg(CommandSender target,String key) {target.sendMessage(MessageFormat.format(text("prefix"),text(key)));}
    public void reply(CommandSender target,String value) {target.sendMessage(MessageFormat.format(text("prefix"),value));}
    public void announce(String key) {announceText(text(key));}
    public void announceText(String message) {
        Bukkit.broadcast(MessageFormat.format(text("prefix"),message));
        if(settings.config().getBoolean("integrations.discord-announcements") && integrations.has("DiscordSRV"))try{integrations.discord(message);}catch(Exception e){getLogger().warning("Discord announcement unavailable; no private match data is sent");}
        for(Player player:Bukkit.getOnlinePlayers())player.playSound(player.getLocation(),settings.config().getString("announcement-sound","block.note_block.chime"),1,1);
    }
    public void sendPack(Player player) throws Exception {
        var config=settings.config();
        if(!config.getBoolean("resource-pack.enabled"))return;
        if(integrations.bedrock(player.getUniqueId())) {msg(player,"pack-bedrock");return;}
        UUID pack=UUID.fromString(config.getString("resource-pack.id"));
        if(!config.getString("resource-pack.minecraft-version").equals(Bukkit.getMinecraftVersion()))throw new IllegalArgumentException("Resource pack targets a different Minecraft version");
        player.addResourcePack(pack,config.getString("resource-pack.url"),HexFormat.of().parseHex(config.getString("resource-pack.sha1")),config.getString("resource-pack.prompt"),config.getBoolean("resource-pack.required"));
    }
    public String playerName(String uuid) {
        try{return store.get("players",uuid,"name",uuid);}catch(Exception e){return uuid;}
    }
    public String resolvePlayer(String name) throws Exception {
        try{return UUID.fromString(name).toString();}catch(IllegalArgumentException ignored){}
        Player online=Bukkit.getPlayerExact(name);if(online!=null)return online.getUniqueId().toString();
        for(var r:store.list("players"))if(r.get("value").equalsIgnoreCase(name))return r.get("owner");
        throw new IllegalArgumentException("Unknown player; use an online player, known name, or UUID");
    }
    public Path backup() throws Exception {
        Path dir=getDataFolder().toPath().resolve("backups").resolve(Instant.now().toString().replace(':','-')+"-"+UUID.randomUUID().toString().substring(0,8));
        Files.createDirectories(dir);store.backup(dir.resolve("tsxmas.db"));
        for(String name:settings.files.keySet())Files.copy(getDataFolder().toPath().resolve(name+".yml"),dir.resolve(name+".yml"));
        return dir;
    }
    @FunctionalInterface public interface Action {void run() throws Exception;}
    public void tryPlayer(Player player,Action work) {try{work.run();}catch(Exception e){reply(player,text("error")+e.getMessage());}}
    public Settings settings(){return settings;} public Store store(){return store;} public Rewards rewards(){return rewards;} public Events events(){return events;}
    public Integrations integrations(){return integrations;} public Menus menus(){return menus;} public Commands commands(){return commands;}
    public Presentation presentation(){return presentation;}
}
