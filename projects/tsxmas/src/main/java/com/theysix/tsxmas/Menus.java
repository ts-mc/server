package com.theysix.tsxmas;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.*;

public final class Menus implements Listener {
    private final TSXMASPlugin p;
    public Menus(TSXMASPlugin p) {this.p=p;}
    private static final class View implements InventoryHolder {
        Inventory inventory;
        final Map<Integer,String> actions=new HashMap<>();
        public Inventory getInventory() {return inventory;}
    }
    private View view(String title,int size) {
        View v=new View();v.inventory=Bukkit.createInventory(v,size,Component.text(title,NamedTextColor.DARK_AQUA));return v;
    }
    private void button(View v,int slot,Material material,String title,List<String> lore,String action,int amount) {
        ItemStack item=new ItemStack(material,Math.min(64,Math.max(1,amount)));var meta=item.getItemMeta();
        meta.displayName(Component.text(title,NamedTextColor.AQUA));meta.lore(lore.stream().map(s->Component.text(s,NamedTextColor.GRAY)).toList());item.setItemMeta(meta);
        v.inventory.setItem(slot,item);if(action!=null)v.actions.put(slot,action);
    }
    public void main(Player player) {
        View v=view(p.text("menu-title"),27);
        String[] names={"calendar","hunt","delivery","build","secret","raffle","market"};
        Material[] icons={Material.CHEST,Material.SWEET_BERRIES,Material.MINECART,Material.SNOW_BLOCK,Material.WRITABLE_BOOK,Material.PAPER,Material.EMERALD};
        for(int i=0;i<names.length;i++)button(v,10+i,icons[i],p.text("menu-"+names[i]),List.of(p.text("menu-open")),names[i],1);
        button(v,22,Material.BOOK,p.text("menu-leaderboards"),List.of(p.text("menu-open")),"top",1);
        button(v,24,Material.CARVED_PUMPKIN,p.text("menu-grinch"),List.of(p.text("grinch-info")),"grinch",1);
        player.openInventory(v.inventory);
    }
    public void activity(Player player,String event) throws Exception {activity(player,event,1);}
    public void activity(Player player,String event,int page) throws Exception {
        View v=view(p.text("menu-"+event),54);
        button(v,4,Material.CLOCK,p.events().state(event),List.of(p.text("activity-state-info")),null,1);
        if(event.equals("delivery"))button(v,2,Material.CHEST,p.text("delivery-start-button"),List.of(p.text("delivery-start-hint")),"delivery start",1);
        List<String> ids=event.equals("delivery")?p.settings().events().getStringList("delivery.stops"):new ArrayList<>(p.settings().events().getConfigurationSection("hunt.objectives").getKeys(false));
        int pages=Math.max(1,(ids.size()+27)/28);
        if(page<1 || page>pages)throw new IllegalArgumentException("Choose a page between 1 and "+pages);
        int offset=(page-1)*28;
        for(int index=offset;index<Math.min(ids.size(),offset+28);index++) {
            String id=ids.get(index);int position=index-offset;int slot=10+(position/7)*9+position%7;
            String point=event.equals("delivery")?id:p.settings().events().getString("hunt.objectives."+id+".location");
            var loc=p.settings().point(point);
            boolean done=!p.store().get(event,player.getUniqueId().toString(),id,"").isEmpty();
            button(v,slot,done?Material.LIME_DYE:Material.CHEST,id,List.of(done?p.text("activity-complete"):p.text("activity-visit"),event.equals("delivery")?"X: "+loc.getBlockX()+" Y: "+loc.getBlockY()+" Z: "+loc.getBlockZ():p.text("hunt-search-hint")),done?null:event+(event.equals("delivery")?" deliver ":" find ")+id,1);
        }
        if(page>1)button(v,45,Material.ARROW,p.text("activity-previous"),List.of(),event+" menu "+(page-1),1);
        if(page<pages)button(v,53,Material.ARROW,p.text("activity-next"),List.of(),event+" menu "+(page+1),1);
        button(v,49,Material.ARROW,p.text("leaderboard-back"),List.of(),"menu",1);player.openInventory(v.inventory);
    }
    public void leaderboardSelect(Player player) {
        View v=view(p.text("leaderboards-title"),27);int i=0;
        for(String event:Events.NAMES) {
            int slot=i<7?10+i:22;i++;
            button(v,slot,Material.BOOK,p.leaderboardTitle(event),List.of(p.leaderboardMetric(event)),"top "+event,1);
        }
        button(v,26,Material.ARROW,p.text("leaderboard-back"),List.of(),"menu",1);player.openInventory(v.inventory);
    }
    public void leaderboard(Player player,String event) throws Exception {
        var board=p.leaderboards().board(event);View v=view(p.text("leaderboard-heading").replace("{event}",p.leaderboardTitle(event)),54);
        if(board.hidden()||board.entries().isEmpty())button(v,22,Material.PAPER,p.text(board.hidden()?"leaderboard-private":"leaderboard-empty"),List.of(p.leaderboardMetric(event)),null,1);
        else for(int i=0;i<board.entries().size();i++) {
            var entry=board.entries().get(i);int slot=10+(i/7)*9+i%7;
            Material icon=entry.rank()==1?Material.GOLD_INGOT:entry.rank()==2?Material.IRON_INGOT:entry.rank()==3?Material.COPPER_INGOT:Material.PAPER;
            String title=p.text("leaderboard-entry-title").replace("{rank}",String.valueOf(entry.rank())).replace("{player}",p.playerName(entry.uuid()));
            button(v,slot,icon,title,List.of(p.text("leaderboard-score").replace("{score}",String.valueOf(entry.score())),p.leaderboardMetric(event)),null,1);
        }
        button(v,49,Material.ARROW,p.text("leaderboard-back"),List.of(p.text("leaderboard-refresh").replace("{seconds}",String.valueOf(p.settings().config().getInt("leaderboards.refresh-seconds",10)))),"top",1);player.openInventory(v.inventory);
    }
    public void calendar(Player player,boolean preview) throws Exception {
        if(preview && !player.hasPermission("tsxmas.staff")) throw new IllegalArgumentException(p.text("no-permission"));
        View v=view(p.text("calendar-title")+(preview?" (Preview)":""),45);
        for(int day=1;day<=25;day++) {
            var state=p.events().calendar(player,day);
            Material icon=switch(state) {case CLAIMED->Material.LIME_STAINED_GLASS_PANE;case AVAILABLE->Material.CHEST;case MISSED->Material.GRAY_STAINED_GLASS_PANE;default->Material.RED_STAINED_GLASS_PANE;};
            button(v,day+8,icon,"December "+day,List.of(p.text("calendar-"+state.name().toLowerCase(Locale.ROOT)),"Reward: "+p.settings().config().getString("calendar.days."+day)),preview?null:"calendar claim "+day,day);
        }
        player.openInventory(v.inventory);
    }
    public void market(Player player) throws Exception {
        View v=view(p.text("market-title"),54);int slot=0;
        var items=p.settings().events().getConfigurationSection("market.items");
        for(String id:items.getKeys(false)) {
            if(slot>=54) break;
            String path="market.items."+id;
            button(v,slot++,Material.matchMaterial(p.settings().events().getString(path+".icon","CHEST")),p.settings().events().getString(path+".name",id),List.of("Price: "+p.settings().events().getLong(path+".price")+" "+p.settings().events().getString(path+".currency"),"Stock: "+p.events().stock(id)),"market buy "+id,1);
        }player.openInventory(v.inventory);
    }
    @EventHandler(priority=EventPriority.HIGHEST)
    public void click(InventoryClickEvent event) {
        if(!(event.getView().getTopInventory().getHolder() instanceof View v))return;
        event.setCancelled(true);
        if(!(event.getWhoClicked() instanceof Player player) || event.getRawSlot()<0 || event.getRawSlot()>=v.inventory.getSize())return;
        String action=v.actions.get(event.getRawSlot());
        if(action!=null) Bukkit.getScheduler().runTask(p,()->{player.closeInventory();p.commands().execute(player,false,action.split(" "));});
    }
    @EventHandler public void drag(InventoryDragEvent event) {if(event.getView().getTopInventory().getHolder() instanceof View)event.setCancelled(true);}
}
