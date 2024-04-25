package me.fream.Skills.Earth;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.fream.Main;
import me.fream.Skills.GlobalConditions;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.fream.Skills.GlobalConditions.cooldown;

public class EarthTotem implements Listener {
    public static Map<UUID, ActiveMob> totemCaster = new HashMap<>();
    public static Map<UUID, BukkitTask> circles = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.COAL)) {
            if (!cooldown.containsKey(event.getPlayer().getUniqueId()) || cooldown.get(event.getPlayer().getUniqueId()).isBefore(LocalTime.now())) {
                if (event.getAction().isRightClick() || event.getAction() == Action.RIGHT_CLICK_AIR) {
                    if (event.getClickedBlock() == null || !event.getClickedBlock().getType().isInteractable()) {
                        if (event.getClickedBlock().isSolid()){
                            spawnTotem(event.getPlayer());
                        }
                    }

                }
            }
        }
    }

    @EventHandler
    public void playerDamage(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof Player){
            Player p = (Player) event.getEntity();
            if (totemCaster.containsKey(p.getUniqueId())){
                Location location = totemCaster.get(p.getUniqueId()).getEntity().getBukkitEntity().getLocation();
                double distance = p.getLocation().distance(location);
                if (distance<7){
                    event.setCancelled(true);
                }

            }
        }
    }

    public void spawnTotem(Player p) {
        try{
            Location eyeloc = p.getTargetBlockExact(5).getLocation().add(0, 1,0);
            if (totemCaster.containsKey(p.getUniqueId())) {
                totemCaster.get(p.getUniqueId()).remove();
                totemCaster.remove(p.getUniqueId());
                circles.get(p.getUniqueId()).cancel();
            }
            ActiveMob activeMob = MythicBukkit.inst().getMobManager().spawnMob("Earthtotem", eyeloc);
            totemCaster.put(p.getUniqueId(), activeMob);
            GlobalConditions.setTotemCooldown(p, 3, 3, eyeloc);


            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
                if (activeMob.getUniqueId() == totemCaster.get(p.getUniqueId()).getUniqueId()){
                    Entity entity = totemCaster.get(p.getUniqueId()).getEntity().getBukkitEntity();
                    LivingEntity entity1 = (LivingEntity) entity;
                    entity1.damage(entity1.getMaxHealth());
                    totemCaster.remove(p.getUniqueId());
                }
            }, 11*20); //
        } catch (Exception e){
            p.sendMessage(ChatColor.RED + "Слишком далеко! Не удалось поставить тотем");
        }
    }
    // Метод для отмены задачи удаления частиц

}

