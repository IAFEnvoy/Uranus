package com.iafenvoy.uranus.object;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.enchantment.Enchantment;

public class RegistryHelper {
    public static <T> T get(RegistryAccess manager, ResourceKey<Registry<T>> registry, ResourceKey<T> key) {
        return manager.lookupOrThrow(registry).get(key).map(Holder.Reference::value).orElseThrow();
    }

    public static <T> Holder<T> entry(RegistryAccess manager, ResourceKey<Registry<T>> registry, T obj) {
        return manager.lookupOrThrow(registry).wrapAsHolder(obj);
    }

    public static <T> Holder<T> getEntry(RegistryAccess manager, ResourceKey<Registry<T>> registry, ResourceKey<T> key) {
        return entry(manager, registry, get(manager, registry, key));
    }

    public static Holder<Enchantment> getEnchantment(RegistryAccess manager, ResourceKey<Enchantment> key) {
        return getEntry(manager, Registries.ENCHANTMENT, key);
    }

    public static Holder<DamageType> getDamageSource(RegistryAccess manager, ResourceKey<DamageType> key) {
        return getEntry(manager, Registries.DAMAGE_TYPE, key);
    }
}
