package me.fream.Skills.Earth;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.fream.Skills.Earth.GlobalConditions.cooldown;

public class EarthTotem implements Listener {
    public static Map<UUID, ActiveMob> totemCaster = new HashMap<>();
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.COAL)) {
            if (!cooldown.containsKey(event.getPlayer().getUniqueId()) || cooldown.get(event.getPlayer().getUniqueId()).isBefore(LocalTime.now())) {
                if (event.getAction().isRightClick() || event.getAction() == Action.RIGHT_CLICK_AIR) {
                    if (event.getClickedBlock() == null || !event.getClickedBlock().getType().isInteractable()) {
                        if (totemCaster.containsKey(event.getPlayer().getUniqueId())) {
                            totemCaster.get(event.getPlayer().getUniqueId()).despawn();
                            spawnTotem(event.getPlayer());
                        } else {
                            spawnTotem(event.getPlayer());

                        }
                    }

                }
            }
        }
    }

    public void spawnTotem(Player p) {
        UUID uuidPlayer = p.getUniqueId();
        try{
            Location eyeloc = p.getTargetBlockExact(5).getLocation().add(0, 1,0);

            ActiveMob activeMob = MythicBukkit.inst().getMobManager().spawnMob("Earthtotem", eyeloc);
            cooldown.put(uuidPlayer, LocalTime.now().plusSeconds(5));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2*25, 2));
        } catch (Exception e){
            p.sendMessage("Не удалось поставить Тотем. Слишком далеко");
        }


    }



}
