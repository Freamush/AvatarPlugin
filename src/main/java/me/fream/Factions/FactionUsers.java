package me.fream.Factions;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FactionUsers implements Listener{
    public static Map<UUID, Faction> playerFactionData = new HashMap<>();

    public Faction getPlayerFaction(Player player){
        UUID uuid = player.getUniqueId();
        return playerFactionData.get(uuid);
    }

}
