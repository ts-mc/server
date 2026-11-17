package com.theysix.tsxmas;

import org.bukkit.*;
import org.bukkit.entity.Player;
import java.util.*;
import java.util.function.*;

public final class Presentation implements AutoCloseable {
    private final TSXMASPlugin p;
    private final List<Runnable> cleanup=new ArrayList<>();
    private final Map<UUID,Object> songs=new HashMap<>();
    private final List<LiveBoard> boards=new ArrayList<>();
    private long nextBoardUpdate;
    private static final class LiveBoard {
        final String event;final Object data,hologram;List<String> lines;
        LiveBoard(String event,Object data,Object hologram,List<String> lines){this.event=event;this.data=data;this.hologram=hologram;this.lines=lines;}
    }
    public Presentation(TSXMASPlugin p){this.p=p;}
    private Object call(Object target,String method,Class<?>[] types,Object...args) throws Exception {
        return target.getClass().getMethod(method,types).invoke(target,args);
    }
    private Location point(String root,String name) {
        var y=p.settings().locations();String path=root+"."+name+".";
        if(!y.getBoolean(path+"configured"))throw new IllegalArgumentException("Unconfigured marker");
        World w=Bukkit.getWorld(y.getString(path+"world",y.getString("world")));
        if(w==null || !w.getName().equals(y.getString("world")))throw new IllegalArgumentException("Unloaded marker world");
        var l=new Location(w,y.getDouble(path+"x"),y.getDouble(path+"y"),y.getDouble(path+"z"),(float)y.getDouble(path+"yaw"),(float)y.getDouble(path+"pitch"));
        if(!p.settings().inside(l,"boundary"))throw new IllegalArgumentException("Marker outside boundary");return l;
    }
    @SuppressWarnings("unchecked")
    public void refresh() {
        close();if(!p.settings().config().getBoolean("enabled"))return;
        var y=p.settings().locations();
        if(p.integrations().has("FancyNpcs"))for(String name:y.getConfigurationSection("npcs").getKeys(false)) {
            if(!y.getBoolean("npcs."+name+".configured"))continue;
            try {
                Location location=point("npcs",name);String base="npcs."+name+".";
                Class<?> api=Class.forName("de.oliver.fancynpcs.api.FancyNpcsPlugin"),dataType=Class.forName("de.oliver.fancynpcs.api.NpcData"),npcType=Class.forName("de.oliver.fancynpcs.api.Npc");
                Object apiInstance=api.getMethod("get").invoke(null);
                Object manager=api.getMethod("getNpcManager").invoke(apiInstance);
                Object data=dataType.getConstructor(String.class,UUID.class,Location.class).newInstance("tsxmas_"+name,UUID.nameUUIDFromBytes("TSXMAS".getBytes(java.nio.charset.StandardCharsets.UTF_8)),location);
                dataType.getMethod("setDisplayName",String.class).invoke(data,y.getBoolean(base+"hide-name")?"":y.getString(base+"name",name));
                dataType.getMethod("setTurnToPlayer",boolean.class).invoke(data,true);
                dataType.getMethod("setCollidable",boolean.class).invoke(data,false);
                String skin=y.getString(base+"skin","");
                if(!skin.isBlank())try {dataType.getMethod("setSkin",String.class).invoke(data,skin);}
                catch(Exception skinFailure){p.getLogger().warning("NPC skin unavailable: "+name+". Spawning with default skin.");}
                Consumer<Player> click=player->Bukkit.getScheduler().runTask(p,()->{
                    if(!player.isOnline() || !player.hasPermission("tsxmas.player") || !player.getWorld().equals(location.getWorld()) || player.getLocation().distanceSquared(location)>16 || !p.integrations().canUse(player,location))return;
                    p.reply(player,y.getString(base+"dialogue","Happy Christmas!"));
                    p.commands().execute(player,false,y.getString(base+"command","xmas menu").replaceFirst("^xmas ","").split(" "));
                });
                dataType.getMethod("setOnClick",Consumer.class).invoke(data,click);
                Function<Object,Object> adapter=(Function<Object,Object>)api.getMethod("getNpcAdapter").invoke(apiInstance);
                Object npc=adapter.apply(data);
                npcType.getMethod("setSaveToFile",boolean.class).invoke(npc,false);
                Class<?> managerType=Class.forName("de.oliver.fancynpcs.api.NpcManager");
                managerType.getMethod("registerNpc",npcType).invoke(manager,npc);
                cleanup.add(()->{try{npcType.getMethod("removeForAll").invoke(npc);managerType.getMethod("removeNpc",npcType).invoke(manager,npc);}catch(Exception ignored){}});
                npcType.getMethod("create").invoke(npc);npcType.getMethod("spawnForAll").invoke(npc);
            }catch(Exception|LinkageError e){p.getLogger().warning("Optional NPC unavailable: "+name+". Commands remain available.");}
        }
        if(p.integrations().has("FancyHolograms"))for(String name:y.getConfigurationSection("holograms").getKeys(false)) {
            String anchor=y.getString("holograms."+name+".npc");
            if(anchor==null?!y.getBoolean("holograms."+name+".configured"):!y.getBoolean("npcs."+anchor+".configured"))continue;
            try {
                Class<?> api=Class.forName("de.oliver.fancyholograms.api.FancyHologramsPlugin"),dataType=Class.forName("de.oliver.fancyholograms.api.data.TextHologramData"),base=Class.forName("de.oliver.fancyholograms.api.data.HologramData"),holoType=Class.forName("de.oliver.fancyholograms.api.hologram.Hologram"),managerType=Class.forName("de.oliver.fancyholograms.api.HologramManager");
                Object instance=api.getMethod("get").invoke(null),manager=api.getMethod("getHologramManager").invoke(instance);
                Object data=dataType.getConstructor(String.class,Location.class).newInstance("tsxmas_"+name,anchor==null?point("holograms",name):point("npcs",anchor).add(0,y.getDouble("holograms."+name+".height",2.6),0));
                String event=y.getString("holograms."+name+".leaderboard");
                List<String> lines=event==null?y.getStringList("holograms."+name+".lines"):p.leaderboardLines(event);
                dataType.getMethod("setText",List.class).invoke(data,lines);
                base.getMethod("setPersistent",boolean.class).invoke(data,false);
                Object holo=managerType.getMethod("create",base).invoke(manager,data);
                managerType.getMethod("addHologram",holoType).invoke(manager,holo);
                cleanup.add(()->{try{managerType.getMethod("removeHologram",holoType).invoke(manager,holo);}catch(Exception ignored){}});
                if(event!=null)boards.add(new LiveBoard(event,data,holo,lines));
            }catch(Exception|LinkageError e){p.getLogger().warning("Optional hologram unavailable: "+name+". Commands remain available.");}
        }
    }
    public void updateLeaderboards() {
        if(boards.isEmpty()||!p.settings().config().getBoolean("enabled")||(nextBoardUpdate!=0&&System.nanoTime()<nextBoardUpdate))return;
        nextBoardUpdate=System.nanoTime()+p.settings().config().getLong("leaderboards.refresh-seconds",10)*1_000_000_000L;
        p.leaderboards().clear();
        for(var board:boards)try {
            var lines=p.leaderboardLines(board.event);if(lines.equals(board.lines))continue;
            Class<?> dataType=Class.forName("de.oliver.fancyholograms.api.data.TextHologramData"),holoType=Class.forName("de.oliver.fancyholograms.api.hologram.Hologram");
            dataType.getMethod("setText",List.class).invoke(board.data,lines);
            holoType.getMethod("clearTextCache").invoke(board.hologram);
            holoType.getMethod("queueUpdate").invoke(board.hologram);
            holoType.getMethod("refreshForViewers").invoke(board.hologram);board.lines=lines;
        }catch(Exception|LinkageError e){p.getLogger().warning("Leaderboard display update unavailable: "+board.event+". GUI/commands remain available.");}
    }
    public void music(Player player,boolean play) throws Exception {
        stopMusic(player.getUniqueId());if(!play)return;
        if(!p.settings().config().getBoolean("music.enabled") || !p.integrations().has("NoteBlockAPI"))throw new IllegalArgumentException("Optional Christmas music is unavailable");
        if(!player.getWorld().getName().equals(p.settings().locations().getString("world")))throw new IllegalArgumentException("Visit the Christmas world first");
        String name=p.settings().config().getString("music.file");if(!name.matches("[a-zA-Z0-9_-]+\\.nbs"))throw new IllegalArgumentException("Invalid music filename");
        var file=p.getDataFolder().toPath().resolve("music").resolve(name).toFile();if(!file.isFile())throw new IllegalArgumentException("Music file is not installed");
        Class<?> song=Class.forName("com.xxmicloxx.NoteBlockAPI.model.Song");
        Object decoded=Class.forName("com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder").getMethod("parse",java.io.File.class).invoke(null,file);
        if(decoded==null)throw new IllegalArgumentException("Invalid NBS file");
        Object radio=Class.forName("com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer").getConstructor(song).newInstance(decoded);
        songs.put(player.getUniqueId(),radio);call(radio,"addPlayer",new Class<?>[]{Player.class},player);call(radio,"setPlaying",new Class<?>[]{boolean.class},true);
    }
    public void stopMusic(UUID uuid){Object song=songs.remove(uuid);if(song!=null)try{call(song,"destroy",new Class<?>[]{});}catch(Exception ignored){}}
    @Override public void close(){boards.clear();nextBoardUpdate=0;for(UUID uuid:new ArrayList<>(songs.keySet()))stopMusic(uuid);for(Runnable r:cleanup)r.run();cleanup.clear();}
}
