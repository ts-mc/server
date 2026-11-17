package com.theysix.tsxmas;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public final class Expansion extends PlaceholderExpansion {
    private final TSXMASPlugin p;
    public Expansion(TSXMASPlugin p){this.p=p;}
    @Override public String getIdentifier(){return "tsxmas";}
    @Override public String getAuthor(){return "TheySix";}
    @Override public String getVersion(){return p.getPluginMeta().getVersion();}
    @Override public boolean persist(){return true;}
    @Override public String onRequest(OfflinePlayer player,String parameter) {
        try {
            if(parameter.equals("season"))return p.settings().season;
            if(parameter.startsWith("state_"))return p.events().state(parameter.substring(6));
            if(parameter.startsWith("top_")) {
                String[] parts=parameter.split("_");
                if(parts.length!=4||!Events.NAMES.contains(parts[1])||!parts[2].matches("[1-9][0-9]?")||!java.util.List.of("name","score","rank").contains(parts[3]))return null;
                int position=Integer.parseInt(parts[2]);var board=p.leaderboards().board(parts[1]);
                if(position>28)return null;
                if(board.hidden()||position>board.entries().size())return parts[3].equals("score")?"0":p.text("leaderboard-empty-entry");
                var entry=board.entries().get(position-1);
                return switch(parts[3]) {case "name"->p.playerName(entry.uuid());case "score"->String.valueOf(entry.score());case "rank"->String.valueOf(entry.rank());default->null;};
            }
            if(player==null)return "0";
            return switch(parameter) {
                case "tokens"->String.valueOf(p.store().tokens(player.getUniqueId().toString()));
                case "hunt_found"->String.valueOf(p.events().progress("hunt",player.getUniqueId().toString()));
                case "delivery_done"->String.valueOf(p.events().progress("delivery",player.getUniqueId().toString()));
                case "calendar_claims"->String.valueOf(p.events().progress("calendar",player.getUniqueId().toString()));
                default->null;
            };
        }catch(Exception e){return "unavailable";}
    }
}
