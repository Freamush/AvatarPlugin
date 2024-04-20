package me.fream.Skills.Earth;

import io.lumine.mythic.core.mobs.ActiveMob;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlobalConditions {
    public static Map<UUID, LocalTime> cooldown = new HashMap<>();
    public static Map<UUID, ActiveMob> caster = new HashMap<>();
}
