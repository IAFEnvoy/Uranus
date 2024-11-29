package com.iafenvoy.uranus.object;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

public class DamageUtil {
    public static DamageSource build(World world, DamageSource origin, RegistryKey<DamageType> newType) {
        Registry<DamageType> registry = world.getDamageSources().registry;
        DamageType type = registry.get(newType);
        return new DamageSource(registry.getEntry(type), origin.getSource(), origin.getAttacker());
    }

    public static DamageSource build(Entity entity, RegistryKey<DamageType> newType) {
        Registry<DamageType> registry = entity.getDamageSources().registry;
        DamageType type = registry.get(newType);
        return new DamageSource(registry.getEntry(type), entity, entity);
    }
}
