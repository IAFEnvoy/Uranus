package com.iafenvoy.uranus.object;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class DamageUtil {
    public static DamageSource build(Level world, DamageSource origin, ResourceKey<DamageType> newType) {
        return new DamageSource(RegistryHelper.getDamageSource(world.registryAccess(), newType), origin.getDirectEntity(), origin.getEntity());
    }

    public static DamageSource build(Entity entity, ResourceKey<DamageType> newType) {
        return new DamageSource(RegistryHelper.getDamageSource(entity.registryAccess(), newType), entity, entity);
    }
}
