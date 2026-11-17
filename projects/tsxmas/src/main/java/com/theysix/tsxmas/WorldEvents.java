package com.theysix.tsxmas;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;

public final class WorldEvents implements Listener {
    private final TSXMASPlugin p;
    public WorldEvents(TSXMASPlugin p) {this.p=p;}
    private boolean venue(Location l) {return p.settings().config().getBoolean("enabled") && l.getWorld().getName().equals(p.settings().locations().getString("world"));}
    private boolean mayBuild(Player player,Location l) {
        if(player.hasPermission("tsxmas.admin") && p.settings().config().getBoolean("protection.admin-build-bypass"))return true;
        try {return p.events().canBuild(player,l);}catch(Exception e){return false;}
    }
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void place(BlockPlaceEvent e) {if(venue(e.getBlock().getLocation())&&!mayBuild(e.getPlayer(),e.getBlock().getLocation())) e.setCancelled(true);}
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void multi(BlockMultiPlaceEvent e) {if(venue(e.getBlock().getLocation()))for(var b:e.getReplacedBlockStates())if(!mayBuild(e.getPlayer(),b.getLocation()))e.setCancelled(true);}
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void breakBlock(BlockBreakEvent e) {if(venue(e.getBlock().getLocation())&&!mayBuild(e.getPlayer(),e.getBlock().getLocation()))e.setCancelled(true);}
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void bucket(PlayerBucketEmptyEvent e) {if(venue(e.getBlock().getLocation()))e.setCancelled(true);}
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void bucketFill(PlayerBucketFillEvent e) {if(venue(e.getBlock().getLocation()))e.setCancelled(true);}
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void entity(EntityChangeBlockEvent e) {if(venue(e.getBlock().getLocation()))e.setCancelled(true);}
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void entityExplosion(EntityExplodeEvent e) {e.blockList().removeIf(b->venue(b.getLocation()));}
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void blockExplosion(BlockExplodeEvent e) {e.blockList().removeIf(b->venue(b.getLocation()));}
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void piston(BlockPistonExtendEvent e) {if(venue(e.getBlock().getLocation()))e.setCancelled(true);}
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void retract(BlockPistonRetractEvent e) {if(venue(e.getBlock().getLocation()))e.setCancelled(true);}
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void flow(BlockFromToEvent e) {if(venue(e.getBlock().getLocation())||venue(e.getToBlock().getLocation()))e.setCancelled(true);}
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void burn(BlockBurnEvent e) {if(venue(e.getBlock().getLocation()))e.setCancelled(true);}
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void ignite(BlockIgniteEvent e) {if(venue(e.getBlock().getLocation()))e.setCancelled(true);}
    @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)
    public void interact(PlayerInteractEvent e) {
        if(e.getHand()!=EquipmentSlot.HAND || e.getAction()!=Action.RIGHT_CLICK_BLOCK || e.getClickedBlock()==null || !venue(e.getPlayer().getLocation()) || !e.getPlayer().hasPermission("tsxmas.player"))return;
        handleMarker(e.getPlayer(),e.getClickedBlock().getLocation());
    }
    @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)
    public void entityInteract(PlayerInteractEntityEvent e) {
        if(e.getHand()==EquipmentSlot.HAND && venue(e.getPlayer().getLocation()) && e.getPlayer().hasPermission("tsxmas.player"))handleMarker(e.getPlayer(),e.getRightClicked().getLocation());
    }
    private void handleMarker(Player player,Location click) {
        for(String stop:p.settings().events().getStringList("delivery.stops"))if(matches(click,stop,true)) {
            try {if(p.events().awaitingDelivery(player,stop)){p.tryPlayer(player,()->p.events().deliver(player,stop));return;}}
            catch(Exception e){p.reply(player,"Unable to read your route. Please contact staff.");return;}
        }
        for(String obj:p.settings().events().getConfigurationSection("hunt.objectives").getKeys(false)) {
            String point=p.settings().events().getString("hunt.objectives."+obj+".location");
            if(matches(click,point,false)){p.tryPlayer(player,()->p.events().discover(player,obj));return;}
        }
    }
    private boolean matches(Location clicked,String name,boolean mailbox) {
        try {Location target=p.settings().point(name);
            return target.getWorld().equals(clicked.getWorld()) && MarkerMatch.matches(clicked.getX(),clicked.getY(),clicked.getZ(),target.getX(),target.getY(),target.getZ(),mailbox?p.settings().events().getDouble("delivery.marker-horizontal-radius",1.5):0,mailbox?p.settings().events().getDouble("delivery.marker-vertical-radius",2.5):0);
        }catch(Exception e){return false;}
    }
    @EventHandler public void join(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLater(p,()->p.tryPlayer(e.getPlayer(),()->{
            p.store().put("players",e.getPlayer().getUniqueId().toString(),"name",e.getPlayer().getName());
            p.rewards().deliver(e.getPlayer());
            if(p.settings().config().getString("resource-pack.mode").equals("SEASONAL"))p.sendPack(e.getPlayer());
        }),40L);
    }
    @EventHandler public void quit(PlayerQuitEvent e){p.presentation().stopMusic(e.getPlayer().getUniqueId());}
    @EventHandler public void worldChange(PlayerChangedWorldEvent e){p.presentation().stopMusic(e.getPlayer().getUniqueId());}
}
