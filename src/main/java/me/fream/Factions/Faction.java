package me.fream.Factions;

import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public enum Faction {
    FACTION_FIRE (ChatColor.GOLD + " \uD83D\uDD25 ", 1, "fire"),
    FACTION_AQUA (ChatColor.BLUE +" \uD83C\uDF0A ", 2, "aqua"),
    FACTION_EARTH(ChatColor.GRAY + "" + ChatColor.BOLD + " \uD83E\uDEA8 ", 3, "earth"),
    FACTION_AIR(ChatColor.WHITE +" ≈ ", 4, "air");


    private String symbol;
    private int id;
    private String name;
    Faction(String symbol, int id, String name){
        this.symbol= symbol;
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }
}



