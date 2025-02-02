package com.iafenvoy.uranus.object;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

public class DamageUtil {
    public static DamageSource build(World world, DamageSource origin, RegistryKey<DamageType> newType) {
        return new DamageSource(RegistryHelper.getDamageSource(world.getRegistryManager(), newType), origin.getSource(), origin.getAttacker());
    }

    public static DamageSource build(Entity entity, RegistryKey<DamageType> newType) {
        return new DamageSource(RegistryHelper.getDamageSource(entity.getRegistryManager(), newType), entity, entity);
    }
}
