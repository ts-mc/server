package com.theysix.tsxmas;

import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.*;

public final class Rewards {
    private final TSXMASPlugin plugin;
    private final Store db;
    public Rewards(TSXMASPlugin plugin,Store db) { this.plugin=plugin;this.db=db; }
    public void enqueue(String uuid,String source,String reward) throws Exception {
        plugin.settings().requireReward(reward);
        int i=0;
        for(var map:plugin.settings().files.get("rewards").getMapList("rewards."+reward)) {
            var yaml=new YamlConfiguration();
            for(var entry:map.entrySet()) yaml.set(String.valueOf(entry.getKey()),entry.getValue());
            db.enqueue(uuid,source,i++,yaml.saveToString());
        }
    }
    public void gift(String uuid,String source,ItemStack item) throws Exception {
        var y=new YamlConfiguration();y.set("type","ITEM");y.set("serialized",Base64.getEncoder().encodeToString(item.serializeAsBytes()));
        db.enqueue(uuid,source,0,y.saveToString());
    }
    public ItemStack item(YamlConfiguration action) throws Exception {
        if(action.contains("serialized")) return ItemStack.deserializeBytes(Base64.getDecoder().decode(action.getString("serialized")));
        ItemStack item=action.getString("type","").equals("HEADDB")?plugin.integrations().head(action.getString("id")):new ItemStack(Objects.requireNonNull(Material.matchMaterial(action.getString("material","COOKIE"))));
        int amount=action.getInt("amount",1);
        if(amount<1 || amount>item.getMaxStackSize()) throw new IllegalArgumentException("Item amount exceeds one stack");
        item.setAmount(amount);
        var meta=item.getItemMeta();
        if(action.contains("name")) meta.displayName(Component.text(action.getString("name"),NamedTextColor.AQUA));
        if(action.contains("lore")) meta.lore(action.getStringList("lore").stream().map(Component::text).toList());
        if(action.contains("custom-model-data")) meta.setCustomModelData(action.getInt("custom-model-data"));
        item.setItemMeta(meta);
        var ench=action.getConfigurationSection("enchantments");
        if(ench!=null) for(String name:ench.getKeys(false)) {
            var e=Registry.ENCHANTMENT.get(NamespacedKey.fromString(name));
            if(e==null) throw new IllegalArgumentException("Unknown enchantment: "+name);
            item.addUnsafeEnchantment(e,ench.getInt(name));
        }
        return item;
    }
    static boolean fits(ItemStack[] slots,ItemStack item) {
        int[] room=new int[slots.length];
        for(int i=0;i<slots.length;i++){ItemStack s=slots[i];room[i]=s==null || s.getType().isAir()?item.getMaxStackSize():(s.isSimilar(item)?Math.max(0,s.getMaxStackSize()-s.getAmount()):0);}
        return InventoryCapacity.fits(item.getAmount(),room);
    }
    public void deliver(Player player) throws Exception {
        String uuid=player.getUniqueId().toString();
        for(var row:db.pending(uuid)) {
            if(!player.isOnline()) return;
            long id=Long.parseLong(row.get("id"));
            var y=new YamlConfiguration();y.loadFromString(row.get("payload"));
            String type=y.getString("type");
            ItemStack stack=null;
            try {
                if(type.equals("ITEM") || type.equals("HEADDB")) {
                    stack=item(y);
                    if(!fits(player.getInventory().getStorageContents(),stack)) continue;
                }
                if(type.equals("MONEY")) plugin.integrations().economy();
                String required=switch(type) { case "LUCKPERMS"->"LuckPerms";case "ULTRACOSMETICS"->"UltraCosmetics";case "DISCORD"->"DiscordSRV";default->""; };
                if(!required.isEmpty() && !plugin.integrations().has(required)) continue;
            } catch(Exception e) { continue; }
            if(type.equals("TOKENS")) {
                db.tx(()->{
                    if(db.exec("UPDATE outbox SET status='DONE' WHERE id=? AND status='PENDING'",id)==1) db.tokens(uuid,y.getLong("amount"));
                    return null;
                });continue;
            }
            if(db.exec("UPDATE outbox SET status='IN_FLIGHT' WHERE id=? AND status='PENDING'",id)!=1) continue;
            try {
                switch(type) {
                    case "ITEM","HEADDB" -> {
                        if(!player.getInventory().addItem(stack).isEmpty()) throw new IllegalStateException("Inventory changed during delivery");
                        player.saveData();
                    }
                    case "XP" -> { player.giveExp(y.getInt("amount"));player.saveData(); }
                    case "MONEY" -> plugin.integrations().money(player,y.getDouble("amount"),true);
                    case "CONSOLE","ULTRACOSMETICS" -> {
                        if(!Bukkit.dispatchCommand(Bukkit.getConsoleSender(),expand(y.getString("command"),player))) throw new IllegalStateException("Reward command failed");
                    }
                    case "PLAYER" -> { if(!player.performCommand(expand(y.getString("command"),player))) throw new IllegalStateException("Player command failed"); }
                    case "LUCKPERMS" -> {
                        String permission=y.getString("permission","");String duration=y.getString("duration","");
                        if(!permission.matches("[a-zA-Z0-9_.-]+") || !duration.matches("[0-9]+[smhdw]")) throw new IllegalArgumentException("Invalid temporary permission");
                        if(!Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"lp user "+uuid+" permission settemp "+permission+" true "+duration+" accumulate")) throw new IllegalStateException("LuckPerms command failed");
                    }
                    case "SOUND" -> player.playSound(player.getLocation(),y.getString("sound","block.note_block.chime"),1,1);
                    case "PARTICLE" -> player.spawnParticle(Particle.valueOf(y.getString("particle","SNOWFLAKE")),player.getLocation().add(0,1,0),20,.5,.5,.5,0);
                    case "DISCORD" -> plugin.integrations().discord(expand(y.getString("message","Christmas reward claimed!"),player));
                    default -> throw new IllegalArgumentException("Unknown reward action");
                }
                db.exec("UPDATE outbox SET status='DONE' WHERE id=?",id);
            } catch(Exception e) {
                db.exec("UPDATE outbox SET status='HELD' WHERE id=?",id);
                db.audit("system","HELD reward "+id+" type="+type);
                plugin.getLogger().warning("Reward "+id+" held for staff reconciliation ("+e.getClass().getSimpleName()+"). No automatic replay.");
            }
        }
    }
    private String expand(String value,Player p) {
        if(value==null || value.contains("\n") || value.startsWith("/")) throw new IllegalArgumentException("Invalid reward command/message");
        return value.replace("{uuid}",p.getUniqueId().toString()).replace("{player}",p.getName());
    }
}
