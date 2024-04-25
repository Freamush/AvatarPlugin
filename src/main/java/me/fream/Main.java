package me.fream;

import me.fream.Factions.Faction;
import me.fream.Factions.FactionUsers;
import me.fream.Skills.Earth.EarthShake;
import me.fream.Skills.Earth.EarthThrowingStone;
import me.fream.Skills.Earth.EarthTotem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public final class Main extends JavaPlugin {

    private static Main instance;
    File playerDataFile;
    FileConfiguration playerDataConfig;
    public static Main getInstance() {
        return instance;
    }
    @Override
    public void onEnable() {
        instance = this;
        playerDataFile = new File(getDataFolder(), "playerdata.yml");
        playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
        // Plugin startup logic
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[AppaCraft] Аппа был хорошо покормлен сеном и соизволил поработать....");
        getServer().getPluginManager().registerEvents(new FactionUsers(), this);
        getServer().getPluginManager().registerEvents(new EarthThrowingStone(), this);
        getServer().getPluginManager().registerEvents(new EarthTotem(), this);
        //getCommand("factioncommand").setExecutor(new FactionCommand());
        getServer().getPluginManager().registerEvents(new EarthShake(), this);
        loadConfig();
        //todo сделать авто сейвер конфигу




        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (EarthTotem.totemCaster.containsKey(player.getUniqueId())) {
                        if (EarthTotem.totemCaster.get(player.getUniqueId()).isDead()) {
                            EarthTotem.totemCaster.remove(player.getUniqueId());
                            EarthTotem.circles.get(player.getUniqueId()).cancel();
                            EarthTotem.circles.remove(player.getUniqueId());
                        }
                    }

                        if(EarthThrowingStone.isNotReady.contains(player.getUniqueId())){
                            BukkitTask task2 = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.getLocation().getWorld().spawnParticle(Particle.REDSTONE, player.getLocation(), 30, 0.5, 1, 0.5, 0.1,
                                            new Particle.DustOptions(Color.fromARGB(50, 164, 145, 117), 4));
                                }
                            }.runTaskLater(Main.getInstance(), 14); // 30 тиков = 1.5 секунды
                        }
                }

            }
        }.runTaskTimer(this, 0L, 20L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        configsave();
    }

    public void configsave(){
        for (Map.Entry<UUID, Faction> entry : FactionUsers.playerFactionData.entrySet()) {
            playerDataConfig.set("playerFactions." + entry.getKey(), entry.getValue().getName());
        }
        try {
            playerDataConfig.save(playerDataFile);
        } catch (IOException e) {
            getLogger().warning("Не удалось сохранить данные в playerdata.yml: " + e.getMessage());
        }
    }
    public void loadConfig(){
        // Используем playerDataConfig для загрузки данных из файла playerdata.yml
        ConfigurationSection factionSection = playerDataConfig.getConfigurationSection("playerFactions");
        if (factionSection != null) {
            for (String playerId : factionSection.getKeys(false)) {
                UUID uuid = UUID.fromString(playerId);
                String factionName = factionSection.getString(playerId);
                Faction faction = null;
                for (Faction f : Faction.values()) {
                    if (f.getName().equals(factionName)) {
                        faction = f;
                        break;
                    }
                }
                if (faction != null) {
                    FactionUsers.playerFactionData.put(uuid, faction);
                }
            }
        }
    }
}



