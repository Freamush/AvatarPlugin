package me.fream.Skills;

import me.fream.Main;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalTime;
import java.util.*;

import static me.fream.Skills.Earth.EarthThrowingStone.isNotReady;
import static me.fream.Skills.Earth.EarthTotem.circles;

public class GlobalConditions {
    public static Map<UUID, LocalTime> cooldown = new HashMap<>();
    public static Map<UUID, BukkitTask> progressnar = new HashMap<>();

    public static void setTotemCooldown(Player p, int seconds, double tick, Location location){
        startProgress(p, tick, location);
        UUID uuidPlayer = p.getUniqueId();
        cooldown.put(uuidPlayer, LocalTime.now().plusSeconds(seconds));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2*25, 2));
    }
    public static void setCooldown(Player p, int seconds, double tick){
        UUID uuidPlayer = p.getUniqueId();
        cooldown.put(uuidPlayer, LocalTime.now().plusSeconds(seconds));
        startProgress(p, tick);

    }






    public static void startProgress(Player player, double tick, Location location) {
        int bars = 10;
        int ticks = 20;
        long time = (long) tick *ticks/bars;
        BukkitTask bukkitTask = new BukkitRunnable() {


            String bar = ChatColor.GRAY+"[Применяется...] ";
            int counter = 0;

            @Override
            public void run() {
                // Добавляем символы к строке в actionbar
                bar += ChatColor.GREEN+ "█";
                player.sendActionBar(bar);
                // После 10 символов останавливаем
                if (++counter >= 10) {
                    // Останавливаем задачу после добавления 10 символов
                    cancel();
                    player.sendActionBar(ChatColor.GREEN+"Готов!");
                    progressnar.remove(player.getUniqueId());
                    createParticleCircle(location, player);/////////////////
                }
            }

        }.runTaskTimer(Main.getInstance(), 0L, time);
        progressnar.put(player.getUniqueId(), bukkitTask);
    }


    public static void startProgress(Player player, double tick) {
        int bars = 10;
        int ticks = 20;
        long time = (long) tick *ticks/bars;
        BukkitTask bukkitTask = new BukkitRunnable() {


            String bar = ChatColor.GRAY+"[Применяется...] ";
            int counter = 0;

            @Override
            public void run() {
                // Добавляем символы к строке в actionbar
                bar += ChatColor.GREEN+ "█";
                player.sendActionBar(bar);
                // После 10 символов останавливаем
                if (++counter >= 10) {
                    // Останавливаем задачу после добавления 10 символов
                    cancel();
                    player.sendActionBar(ChatColor.GREEN+"Готов!");
                    progressnar.remove(player.getUniqueId());
                    isNotReady.remove(player.getUniqueId());
                }
            }

        }.runTaskTimer(Main.getInstance(), 0L, time);
        progressnar.put(player.getUniqueId(), bukkitTask);
    }



    public static void createParticleCircle(Location center, Player player) {
        int iterations = 100; // Количество частиц в круге
        double radius = 7.0; // Радиус круга
        int duration = 5; // Общее количество итераций
        int delay = 2 * 20; // Задержка между итерациями в тиках (2 секунды * 20 тиков/секунду)
      BukkitTask task =  new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count < duration) {
                    // Создаем круг частиц
                    for (int i = 0; i < iterations; i++) {
                        double angle = 2 * Math.PI * i / iterations;
                        double x = center.getX() + radius * Math.cos(angle);
                        double z = center.getZ() + radius * Math.sin(angle);
                        double y = center.getY(); // Вы можете настроить высоту частиц
                        Location particleLoc = new Location(center.getWorld(), x, y, z);
                      particleLoc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 1, new Particle.DustOptions(Color.fromARGB(150,164, 145, 117), 5));
                    }
                    count++;
                } else {
                    // Остановка задачи после 5 итераций
                    circles.remove(player.getUniqueId());
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, delay);
        circles.put(player.getUniqueId(), task);

    }
}
