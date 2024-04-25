package me.fream.Skills.Earth;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.fream.Main;
import me.fream.Skills.GlobalConditions;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.fream.Skills.GlobalConditions.cooldown;

public class EarthShake implements Listener {
    public static Map<UUID, ActiveMob> shakeCaster = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.LAPIS_LAZULI)) {
            if (!cooldown.containsKey(event.getPlayer().getUniqueId()) || cooldown.get(event.getPlayer().getUniqueId()).isBefore(LocalTime.now())) {
                if (event.getAction().isRightClick() || event.getAction() == Action.RIGHT_CLICK_AIR) {
                    if (event.getClickedBlock() == null || !event.getClickedBlock().getType().isInteractable()) {
                        //todo spawn
                        spawnShake(event.getPlayer());
                    }

                }
            }
        }
    }

    public void spawnShake(Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,  (2*20), 2));
        GlobalConditions.setCooldown(player, 3, 2);
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
            Location location = player.getLocation();
            ActiveMob activeMob = MythicBukkit.inst().getMobManager().spawnMob("Earthshake", location);
            shakeCaster.put(player.getUniqueId(), activeMob);
            Collection<Entity> nearbyEntities = player.getNearbyEntities(5, 5, 5);
            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2 * 20, 10));
                    livingEntity.damage(6);
                } else if (entity instanceof Player) {
                    Player victim = (Player) entity;
                    if (!shakeCaster.containsKey(victim.getUniqueId())){
                        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2 * 20, 10));
                        victim.damage(16);
                    }
                }
            }
        }, 2*20);
        createDust(player.getLocation());
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
            shakeCaster.get(player.getUniqueId()).remove();
            shakeCaster.remove(player.getUniqueId());
        }, 3*23);
    }


    public static void createDust(Location center) {
        int iterations = 100; // Количество частиц в круге
        double radius = 5.0; // Радиус круга
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
            for (int i = 0; i < iterations; i++) {
                double angle = 2 * Math.PI * i / iterations;
                double x = center.getX() + radius * Math.cos(angle);
                double z = center.getZ() + radius * Math.sin(angle);
                double y = center.getY(); // Вы можете настроить высоту частиц
                Location particleLoc = new Location(center.getWorld(), x, y, z);
                particleLoc.getWorld().spawnParticle(Particle.REDSTONE, center, 20, 4, 0.5, 4, 0.1, new Particle.DustOptions(Color.fromARGB(50, 164, 145, 117), 4));
            }
        }, 3*20);
    }


}
