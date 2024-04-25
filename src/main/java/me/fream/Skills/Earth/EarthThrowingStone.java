package me.fream.Skills.Earth;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicDamageEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.fream.Main;
import me.fream.Skills.GlobalConditions;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.time.LocalTime;
import java.util.*;

import static me.fream.Skills.GlobalConditions.*;

public class EarthThrowingStone implements Listener {
    public static Map<UUID, ActiveMob> caster = new HashMap<>();
  //  protected static Map<UUID, BukkitTask> timeToDestroy = new HashMap<>();
      public static Set<UUID> isNotReady = new HashSet<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.BOW)) {
            if (!cooldown.containsKey(event.getPlayer().getUniqueId()) || cooldown.get(event.getPlayer().getUniqueId()).isBefore(LocalTime.now())) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                    event.getPlayer().getInventory().setItem(40, new ItemStack(Material.ARROW));
                    spawnBall(event.getPlayer());
                }
            } else {
                event.setCancelled(true);
                //todo фикс райт клика чтобы не спавнило валун
        }
            }
            }



    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Fireball && MythicBukkit.inst().getMobManager().isMythicMob(event.getEntity())) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onProjectileLaunch(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
                if (isNotReady.contains(player.getUniqueId())) {
                    ActiveMob activeMob = caster.get(player.getUniqueId());
                    if (activeMob != null) {
                        activeMob.remove();
                        caster.remove(player.getUniqueId());
                    }
                    caster.remove(player.getUniqueId());
                    progressnar.get(player.getUniqueId()).cancel();
                    player.sendActionBar(ChatColor.RED + "Отменено");
                    isNotReady.remove(player.getUniqueId());
                } else {
                    ActiveMob activeMob = caster.get(player.getUniqueId());
                    if (activeMob != null) {
                        Vector direction = player.getLocation().getDirection().normalize();
                        Entity fireball = caster.get(player.getUniqueId()).getEntity().getBukkitEntity();
                        fireball.setVelocity(direction.multiply(0.5));

                        //timeToDestroy.get(player.getUniqueId()).cancel();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!fireball.isDead()) {
                                    Location fireballLocation = fireball.getLocation();
                                    for (Entity entity : fireball.getNearbyEntities(2, 2, 2)) {
                                        if (entity instanceof LivingEntity && !isCaster(entity)) {

                                            ((LivingEntity) entity).damage(5);
                                            breakBlocksAround(fireball, fireballLocation);
                                            playEffectofExplosion(fireballLocation);
                                            fireball.remove();
                                            this.cancel(); // Stop monitoring the Fireball
                                            caster.remove(player.getUniqueId());
                                            return;

                                        }
                                    }
                                }
                            }
                        }.runTaskTimer(Main.getInstance(), 0, 1);
                    }
                }
                event.setCancelled(true);
        }
    }
        private static void breakBlocksAround(Entity entity, Location location) {
            int radius = 2;
            int height = 5;
            for (int x = -radius; x <= radius; x++) {
                for (int y = -height; y <= height; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        Location currentLocation = location.clone().add(x, y, z);
                        if (currentLocation.distanceSquared(location) <= radius * radius) {
                            Block block = currentLocation.getBlock();
                            // Проверяем, не является ли текущий блок падающим блоком
                            if (!isFallingBlock(block)) {
                                sendFallingBlock(entity, block);
                            }
                        }
                    }
                }
            }
        }

        // Метод для проверки, является ли блок падающим блоком
        private static boolean isFallingBlock(Block block) {
            return block.getType() == Material.SAND || block.getType() == Material.GRAVEL || block.getType() == Material.ANVIL;
        }

        private static void sendFallingBlock(Entity entity, Block block) {
            if (block.getType() != Material.AIR) {
                FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());
                Vector direction = entity.getLocation().toVector().subtract(block.getLocation().toVector()).normalize();
                fallingBlock.setVelocity(direction);
                fallingBlock.setDropItem(false);
            }
        }
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Fireball && MythicBukkit.inst().getMobManager().isMythicMob(event.getEntity())) {
            event.setCancelled(true);
            Location location = event.getLocation();
            breakBlocksAround(event.getEntity(), location);
            playEffectofExplosion(location);
        }
    }

        @EventHandler
        public void onBlockFall(EntityChangeBlockEvent event) {
            if (event.getEntity() instanceof FallingBlock) {
                playEffectofExplosion(event.getEntity().getLocation());
                event.setCancelled(true);
            }
        }

    public void spawnBall(Player p) {
        UUID uuidPlayer = p.getUniqueId();
        Location eyeloc = p.getLocation().add(0, 1.5, 0);
        Vector vector = p.getLocation().getDirection().multiply(2);
        Location frontspawn = eyeloc.add(vector);
        ActiveMob activeMob = MythicBukkit.inst().getMobManager().spawnMob("Earthbig", frontspawn);
        caster.put(uuidPlayer, activeMob);
        isNotReady.add(uuidPlayer);
       setCooldown(p,3,2.7);
        // BukkitTask scheduler =  Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> caster.get(p.getUniqueId()).remove(), 7*20); //
       // timeToDestroy.put(p.getUniqueId(), scheduler);
    }
    
    public void playEffectofExplosion(Location location){
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 1.0f);
        location.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, location, 1);
        location.getWorld().spawnParticle(Particle.SMOKE_LARGE, location, 5);

    }


    public boolean isCaster(Entity entity){
        if (entity instanceof Player){
            Player player = (Player) entity;
            if (caster.containsKey(player.getUniqueId())){
                return true;
                //todo чтобы проверял хозяйна камня кто кинул
            }else {

            }
        }return false;
    }
}








