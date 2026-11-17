package com.theysix.tsxmas;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.lang.reflect.*;
import java.util.*;

public final class Integrations {
    private final TSXMASPlugin plugin;
    public Integrations(TSXMASPlugin plugin) { this.plugin=plugin; }
    public boolean has(String name) { return Bukkit.getPluginManager().isPluginEnabled(name); }
    public boolean bedrock(UUID id) {
        if(!has("floodgate")) return false;
        try {
            Class<?> api=Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            return (boolean)api.getMethod("isFloodgatePlayer",UUID.class).invoke(api.getMethod("getInstance").invoke(null),id);
        } catch(ReflectiveOperationException e) { return true; }
    }
    @SuppressWarnings({"unchecked","rawtypes"})
    public Object economy() throws Exception {
        if(!has("Vault")) throw new IllegalArgumentException("Vault is unavailable; reward retained");
        Class type=Class.forName("net.milkbowl.vault.economy.Economy");
        var registration=Bukkit.getServicesManager().getRegistration(type);
        if(registration==null) throw new IllegalArgumentException("No Vault economy provider; reward retained");
        return registration.getProvider();
    }
    public void money(Player player,double amount,boolean deposit) throws Exception {
        Object economy=economy();
        Class<?> api=Class.forName("net.milkbowl.vault.economy.Economy");
        Object response=api.getMethod(deposit?"depositPlayer":"withdrawPlayer",OfflinePlayer.class,String.class,double.class).invoke(economy,player,player.getWorld().getName(),amount);
        if(!(boolean)response.getClass().getMethod("transactionSuccess").invoke(response)) throw new IllegalStateException("Economy rejected transaction");
    }
    public ItemStack head(String id) throws Exception {
        if(has("HeadDB")) {
            var future=heads.computeIfAbsent(id,key->{
                try {return (java.util.concurrent.CompletableFuture<?>)Class.forName("tsp.headdb.api.HeadAPI").getMethod("findHeadById",int.class).invoke(null,Integer.parseInt(key));}
                catch(Exception e){return java.util.concurrent.CompletableFuture.failedFuture(e);}
            });
            if(!future.isDone() || future.isCompletedExceptionally()) throw new IllegalArgumentException("HeadDB lookup pending or unavailable");
            Optional<?> result=(Optional<?>)future.getNow(null);
            if(result.isPresent())return ((ItemStack)result.get().getClass().getMethod("getItem").invoke(result.get())).clone();
        }
        if(has("HeadDatabase")) {
            Object api=Class.forName("me.arcaniax.hdb.api.HeadDatabaseAPI").getConstructor().newInstance();
            ItemStack item=(ItemStack)api.getClass().getMethod("getItemHead",String.class).invoke(api,id);
            if(item!=null) return item;
        }
        throw new IllegalArgumentException("HeadDatabase API not available; configure an ITEM or reviewed HeadDB command reward instead");
    }
    private final Map<String,java.util.concurrent.CompletableFuture<?>> heads=new HashMap<>();
    public boolean canUse(Player player,Location location) {
        if(!has("WorldGuard")) return !plugin.settings().config().getBoolean("protection.require-worldguard");
        try {
            Class<?> wg=Class.forName("com.sk89q.worldguard.WorldGuard");
            Object instance=wg.getMethod("getInstance").invoke(null);
            Object platform=instance.getClass().getMethod("getPlatform").invoke(instance);
            Object container=platform.getClass().getMethod("getRegionContainer").invoke(platform);
            Object query=container.getClass().getMethod("createQuery").invoke(container);
            Class<?> adapter=Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
            Object point=adapter.getMethod("adapt",Location.class).invoke(null,location);
            Object wgPlugin=Bukkit.getPluginManager().getPlugin("WorldGuard");
            Object local=wgPlugin.getClass().getMethod("wrapPlayer",Player.class).invoke(wgPlugin,player);
            Class<?> flag=Class.forName("com.sk89q.worldguard.protection.flags.StateFlag");
            Class<?> flags=Class.forName("com.sk89q.worldguard.protection.flags.Flags");
            Object array=Array.newInstance(flag,2);
            Array.set(array,0,flags.getField("USE").get(null));
            Array.set(array,1,flags.getField("INTERACT").get(null));
            for(Method m:query.getClass().getMethods()) if(m.getName().equals("testState") && m.getParameterCount()==3 && m.getParameterTypes()[1].isInstance(local)) return (boolean)m.invoke(query,point,local,array);
            return false;
        } catch(Exception e) { return false; }
    }
    public void discord(String message) throws Exception {
        if(!has("DiscordSRV")) throw new IllegalArgumentException("DiscordSRV unavailable");
        String channel=plugin.settings().config().getString("integrations.discord-channel","global");
        Class<?> api=Class.forName("github.scarsz.discordsrv.DiscordSRV");
        Object instance=api.getMethod("getPlugin").invoke(null);
        Object text=api.getMethod("getDestinationTextChannelForGameChannelName",String.class).invoke(instance,channel);
        if(text==null) throw new IllegalArgumentException("Discord channel mapping missing");
        Class<?> util=Class.forName("github.scarsz.discordsrv.util.DiscordUtil");
        for(Method m:util.getMethods()) if(m.getName().equals("sendMessage") && m.getParameterCount()==2 && m.getParameterTypes()[0].isInstance(text) && m.getParameterTypes()[1]==String.class) { m.invoke(null,text,message);return; }
        throw new IllegalStateException("DiscordSRV adapter requires review for installed version");
    }
}
