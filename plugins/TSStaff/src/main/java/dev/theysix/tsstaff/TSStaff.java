package dev.theysix.tsstaff;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class TSStaff
extends JavaPlugin
implements Listener,
CommandExecutor,
TabCompleter {
    private boolean chatMuted = false;
    private final Set<UUID> frozen = new HashSet<UUID>();
    private final Map<UUID, Deque<Long>> chatTimes = new ConcurrentHashMap<UUID, Deque<Long>>();
    private final Map<UUID, String> lastMsg = new ConcurrentHashMap<UUID, String>();
    private final Map<UUID, Long> lastMsgAt = new ConcurrentHashMap<UUID, Long>();
    private final Map<UUID, Long> spamMutedUntil = new ConcurrentHashMap<UUID, Long>();
    private TSStaffUpdates updates;

    public void onEnable() {
        this.saveDefaultConfig();
        this.chatMuted = this.getConfig().getBoolean("state.chat-muted", false);
        for (String string : this.getConfig().getStringList("state.frozen")) {
            try {
                this.frozen.add(UUID.fromString(string));
            }
            catch (IllegalArgumentException illegalArgumentException) {}
        }
        for (String string : new String[]{"clearchat", "mutechat", "unmutechat", "freeze", "unfreeze", "announce", "tsstaff"}) {
            PluginCommand pluginCommand = this.getCommand(string);
            if (pluginCommand == null) continue;
            pluginCommand.setExecutor((CommandExecutor)this);
            pluginCommand.setTabCompleter((TabCompleter)this);
        }
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this);
        this.updates = new TSStaffUpdates(this);
        PluginCommand updateCommand = this.getCommand("update");
        if (updateCommand != null) updateCommand.setExecutor(this.updates);
        Bukkit.getPluginManager().registerEvents(this.updates, this);
        long l = (long)Math.max(1, this.getConfig().getInt("freeze.reminder-seconds", 5)) * 20L;
        Bukkit.getScheduler().runTaskTimer((Plugin)this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!this.frozen.contains(player.getUniqueId())) continue;
                this.remindFrozen(player);
            }
        }, l, l);
    }

    public void onDisable() {
        this.saveState();
        Bukkit.getScheduler().cancelTasks(this);
        if (this.updates != null) this.updates.shutdown();
    }

    private void saveState() {
        this.getConfig().set("state.chat-muted", (Object)this.chatMuted);
        ArrayList<String> arrayList = new ArrayList<String>();
        for (UUID uUID : this.frozen) {
            arrayList.add(uUID.toString());
        }
        this.getConfig().set("state.frozen", arrayList);
        this.saveConfig();
    }

    private String c(String string) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)(string == null ? "" : string));
    }

    private String msg(String string, String string2) {
        return this.c(this.getConfig().getString("prefix", "&8[&dTheySix&8] ") + this.getConfig().getString("messages." + string, string2));
    }

    private void broadcast(String string) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(string);
        }
        Bukkit.getConsoleSender().sendMessage(string);
    }

    private boolean perm(CommandSender commandSender, String string) {
        if (commandSender.hasPermission(string)) {
            return true;
        }
        commandSender.sendMessage(this.msg("no-permission", "&cYou don't have permission."));
        return false;
    }

    private void remindFrozen(Player player) {
        player.sendTitle(this.c(this.getConfig().getString("freeze.title", "&b&lFROZEN")), this.c(this.getConfig().getString("freeze.subtitle", "&fDon't log out. Staff will speak to you.")), 0, 50, 10);
    }

    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] stringArray) {
        String string2 = commandSender.getName();
        switch (command.getName().toLowerCase(Locale.ROOT)) {
            case "clearchat": {
                if (!this.perm(commandSender, "tsstaff.clearchat")) {
                    return true;
                }
                int n = this.getConfig().getInt("clearchat.lines", 150);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("tsstaff.clearchat.bypass")) continue;
                    for (int i = 0; i < n; ++i) {
                        player.sendMessage(" ");
                    }
                }
                this.broadcast(this.msg("chat-cleared", "&fChat was cleared by &d%staff%&f.").replace("%staff%", string2));
                return true;
            }
            case "mutechat": 
            case "unmutechat": {
                if (!this.perm(commandSender, "tsstaff.mutechat")) {
                    return true;
                }
                boolean bl = command.getName().equalsIgnoreCase("mutechat");
                if (bl && stringArray.length > 0 && stringArray[0].equalsIgnoreCase("toggle")) {
                    boolean bl2 = bl = !this.chatMuted;
                }
                if (this.chatMuted == bl) {
                    commandSender.sendMessage(this.msg(bl ? "chat-already-muted" : "chat-not-muted", bl ? "&cChat is already frozen. Use /unmutechat." : "&cChat isn't frozen."));
                    return true;
                }
                this.chatMuted = bl;
                this.saveState();
                this.broadcast(this.msg(bl ? "chat-muted" : "chat-unmuted", bl ? "&cChat has been frozen by &d%staff%&c." : "&aChat has been unfrozen by &d%staff%&a.").replace("%staff%", string2));
                return true;
            }
            case "freeze": 
            case "unfreeze": {
                boolean bl;
                if (!this.perm(commandSender, "tsstaff.freeze")) {
                    return true;
                }
                if (stringArray.length < 1) {
                    commandSender.sendMessage(this.msg("usage-" + command.getName(), "&cUsage: /" + command.getName() + " <player>"));
                    return true;
                }
                Player player = Bukkit.getPlayerExact((String)stringArray[0]);
                if (player == null) {
                    commandSender.sendMessage(this.msg("player-not-found", "&cPlayer not online: &f%player%").replace("%player%", stringArray[0]));
                    return true;
                }
                boolean bl3 = command.getName().equalsIgnoreCase("freeze");
                if (bl3 && player.hasPermission("tsstaff.freeze.exempt") && !commandSender.hasPermission("tsstaff.freeze.override")) {
                    commandSender.sendMessage(this.msg("freeze-exempt", "&cYou can't freeze that player."));
                    return true;
                }
                boolean bl4 = bl = bl3 ? this.frozen.add(player.getUniqueId()) : this.frozen.remove(player.getUniqueId());
                if (!bl) {
                    commandSender.sendMessage(this.msg(bl3 ? "already-frozen" : "not-frozen", bl3 ? "&c%player% is already frozen." : "&c%player% isn't frozen.").replace("%player%", player.getName()));
                    return true;
                }
                this.saveState();
                if (bl3) {
                    player.sendMessage(this.msg("you-frozen", "&bYou have been frozen by staff. Don't log out!"));
                    this.remindFrozen(player);
                    player.playSound(player.getLocation(), this.getConfig().getString("freeze.sound", "minecraft:block.glass.break"), 1.0f, 0.6f);
                } else {
                    player.sendMessage(this.msg("you-unfrozen", "&aYou have been unfrozen. You can move again."));
                    player.sendTitle(" ", " ", 0, 1, 0);
                }
                this.notifyStaff(this.msg(bl3 ? "staff-froze" : "staff-unfroze", bl3 ? "&d%staff% &ffroze &b%player%&f." : "&d%staff% &funfroze &b%player%&f.").replace("%staff%", string2).replace("%player%", player.getName()));
                if (!commandSender.hasPermission("tsstaff.freeze.notify")) {
                    commandSender.sendMessage(this.msg("done", "&aDone."));
                }
                return true;
            }
            case "announce": {
                String string32;
                if (!this.perm(commandSender, "tsstaff.announce")) {
                    return true;
                }
                if (stringArray.length == 0) {
                    commandSender.sendMessage(this.msg("usage-announce", "&cUsage: /announce <message>"));
                    return true;
                }
                String string4 = this.c(String.join((CharSequence)" ", stringArray));
                for (String lineFormat : this.getConfig().getStringList("announce.format")) {
                    this.broadcast(this.c(lineFormat.replace("%staff%", string2)).replace("%message%", string4));
                }
                String string5 = this.getConfig().getString("announce.title", "");
                string32 = this.getConfig().getString("announce.sound", "");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (string5 != null && !string5.isEmpty()) {
                        player.sendTitle(this.c(string5), this.getConfig().getBoolean("announce.message-as-subtitle", true) ? string4 : "", 10, 60, 15);
                    }
                    if (string32 == null || string32.isEmpty()) continue;
                    player.playSound(player.getLocation(), string32, 1.0f, 1.0f);
                }
                return true;
            }
            case "tsstaff": {
                if (!this.perm(commandSender, "tsstaff.reload")) {
                    return true;
                }
                this.reloadConfig();
                commandSender.sendMessage(this.msg("reloaded", "&aTSStaff config reloaded."));
                return true;
            }
        }
        return false;
    }

    private void notifyStaff(String string) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("tsstaff.freeze.notify")) continue;
            player.sendMessage(string);
        }
        Bukkit.getConsoleSender().sendMessage(string);
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String string, String[] stringArray) {
        ArrayList<String> arrayList = new ArrayList<String>();
        String string2 = command.getName().toLowerCase(Locale.ROOT);
        if (stringArray.length == 1 && (string2.equals("freeze") || string2.equals("unfreeze"))) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                boolean bl = this.frozen.contains(player.getUniqueId());
                if (string2.equals("freeze") == bl || !player.getName().toLowerCase(Locale.ROOT).startsWith(stringArray[0].toLowerCase(Locale.ROOT))) continue;
                arrayList.add(player.getName());
            }
        }
        return arrayList;
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        if (!this.chatMuted || asyncPlayerChatEvent.getPlayer().hasPermission("tsstaff.mutechat.bypass")) {
            return;
        }
        asyncPlayerChatEvent.setCancelled(true);
        asyncPlayerChatEvent.getPlayer().sendMessage(this.msg("chat-is-muted", "&cChat is frozen right now."));
    }

    private static String norm(String string) {
        return string.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onSpam(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        Deque deque;
        if (asyncPlayerChatEvent.isCancelled() || !this.getConfig().getBoolean("antispam.enabled", true)) {
            return;
        }
        Player player = asyncPlayerChatEvent.getPlayer();
        if (player.hasPermission("tsstaff.antispam.bypass")) {
            return;
        }
        UUID uUID2 = player.getUniqueId();
        long l = System.currentTimeMillis();
        Long l2 = this.spamMutedUntil.get(uUID2);
        if (l2 != null && l2 > l) {
            asyncPlayerChatEvent.setCancelled(true);
            player.sendMessage(this.msg("spam-muted", "&cYou're muted for spamming. Wait &f%seconds%s&c.").replace("%seconds%", String.valueOf((l2 - l + 999L) / 1000L)));
            return;
        }
        long l3 = (long)(this.getConfig().getDouble("antispam.cooldown-seconds", 1.5) * 1000.0);
        Long l4 = this.lastMsgAt.get(uUID2);
        if (l4 != null && l - l4 < l3) {
            asyncPlayerChatEvent.setCancelled(true);
            player.sendMessage(this.msg("spam-slow", "&cSlow down! Wait a moment before chatting again."));
            return;
        }
        String string = TSStaff.norm(asyncPlayerChatEvent.getMessage());
        long l5 = (long)this.getConfig().getInt("antispam.duplicate-seconds", 30) * 1000L;
        String string2 = this.lastMsg.get(uUID2);
        if (this.getConfig().getBoolean("antispam.block-duplicates", true) && string2 != null && !string.isEmpty() && string2.equals(string) && l4 != null && l - l4 < l5) {
            asyncPlayerChatEvent.setCancelled(true);
            player.sendMessage(this.msg("spam-duplicate", "&cDon't repeat the same message."));
            return;
        }
        int n = this.getConfig().getInt("antispam.burst.max-messages", 4);
        long l6 = (long)this.getConfig().getInt("antispam.burst.seconds", 6) * 1000L;
        Deque deque2 = deque = this.chatTimes.computeIfAbsent(uUID2, uUID -> new ArrayDeque());
        synchronized (deque2) {
            while (!deque.isEmpty() && l - (Long)deque.peekFirst() > l6) {
                deque.pollFirst();
            }
            deque.addLast(l);
            if (deque.size() > n) {
                deque.clear();
                int n2 = this.getConfig().getInt("antispam.burst.mute-seconds", 30);
                this.spamMutedUntil.put(uUID2, l + (long)n2 * 1000L);
                asyncPlayerChatEvent.setCancelled(true);
                player.sendMessage(this.msg("spam-muted", "&cYou're muted for spamming. Wait &f%seconds%s&c.").replace("%seconds%", String.valueOf(n2)));
                String string3 = this.msg("spam-staff", "&e%player% &7was auto-muted for %seconds%s (spam).").replace("%player%", player.getName()).replace("%seconds%", String.valueOf(n2));
                for (Player player2 : Bukkit.getOnlinePlayers()) {
                    if (!player2.hasPermission("tsstaff.freeze.notify")) continue;
                    player2.sendMessage(string3);
                }
                return;
            }
        }
        int n3 = this.getConfig().getInt("antispam.caps.min-length", 8);
        int n4 = this.getConfig().getInt("antispam.caps.max-percent", 70);
        String string4 = asyncPlayerChatEvent.getMessage();
        if (this.getConfig().getBoolean("antispam.caps.enabled", true) && string4.length() >= n3) {
            int n5 = 0;
            int n6 = 0;
            for (char c : string4.toCharArray()) {
                if (!Character.isLetter(c)) continue;
                ++n5;
                if (!Character.isUpperCase(c)) continue;
                ++n6;
            }
            if (n5 >= n3 && n6 * 100 / n5 > n4) {
                asyncPlayerChatEvent.setMessage(string4.toLowerCase(Locale.ROOT));
            }
        }
        this.lastMsgAt.put(uUID2, l);
        this.lastMsg.put(uUID2, string);
    }

    private boolean isFrozen(Entity entity) {
        return entity instanceof Player && this.frozen.contains(entity.getUniqueId());
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onMove(PlayerMoveEvent playerMoveEvent) {
        if (!this.frozen.contains(playerMoveEvent.getPlayer().getUniqueId())) {
            return;
        }
        Location location = playerMoveEvent.getFrom();
        Location location2 = playerMoveEvent.getTo();
        if (location2 == null) {
            return;
        }
        if (location.getX() != location2.getX() || location.getY() != location2.getY() || location.getZ() != location2.getZ()) {
            Location location3 = location.clone();
            location3.setYaw(location2.getYaw());
            location3.setPitch(location2.getPitch());
            playerMoveEvent.setTo(location3);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onCmd(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
        if (!this.frozen.contains(playerCommandPreprocessEvent.getPlayer().getUniqueId())) {
            return;
        }
        String string = playerCommandPreprocessEvent.getMessage().substring(1).split(" ")[0].toLowerCase(Locale.ROOT);
        if (string.contains(":")) {
            string = string.substring(string.indexOf(58) + 1);
        }
        if (this.getConfig().getStringList("freeze.allowed-commands").contains(string)) {
            return;
        }
        playerCommandPreprocessEvent.setCancelled(true);
        playerCommandPreprocessEvent.getPlayer().sendMessage(this.msg("frozen-no-command", "&cYou can't use commands while frozen."));
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent blockBreakEvent) {
        if (this.frozen.contains(blockBreakEvent.getPlayer().getUniqueId())) {
            blockBreakEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent blockPlaceEvent) {
        if (this.frozen.contains(blockPlaceEvent.getPlayer().getUniqueId())) {
            blockPlaceEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent playerInteractEvent) {
        if (this.frozen.contains(playerInteractEvent.getPlayer().getUniqueId())) {
            playerInteractEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onDrop(PlayerDropItemEvent playerDropItemEvent) {
        if (this.frozen.contains(playerDropItemEvent.getPlayer().getUniqueId())) {
            playerDropItemEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent entityDamageEvent) {
        if (this.getConfig().getBoolean("freeze.invulnerable", true) && this.isFrozen(entityDamageEvent.getEntity())) {
            entityDamageEvent.setCancelled(true);
        }
        if (entityDamageEvent instanceof EntityDamageByEntityEvent && this.isFrozen(((EntityDamageByEntityEvent)entityDamageEvent).getDamager())) {
            entityDamageEvent.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        if (!this.frozen.contains(playerJoinEvent.getPlayer().getUniqueId())) {
            return;
        }
        playerJoinEvent.getPlayer().sendMessage(this.msg("you-frozen", "&bYou have been frozen by staff. Don't log out!"));
        this.remindFrozen(playerJoinEvent.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent playerQuitEvent) {
        this.chatTimes.remove(playerQuitEvent.getPlayer().getUniqueId());
        if (!this.frozen.contains(playerQuitEvent.getPlayer().getUniqueId())) {
            return;
        }
        this.notifyStaff(this.msg("frozen-logout", "&c&l! &b%player% &clogged out while frozen.").replace("%player%", playerQuitEvent.getPlayer().getName()));
        for (String string : this.getConfig().getStringList("freeze.on-logout-commands")) {
            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)string.replace("%player%", playerQuitEvent.getPlayer().getName()));
        }
    }
}

