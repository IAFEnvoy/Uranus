package com.iafenvoy.uranus.object.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class ArmorWithTickItem extends ArmorItem {
    private static final List<ArmorWithTickItem> NEED_TO_TICK = new ArrayList<>();

    public ArmorWithTickItem(RegistryEntry<ArmorMaterial> material, Type slot, Settings settings) {
        super(material, slot, settings);
        NEED_TO_TICK.add(this);
    }

    public static void doTick(World world, PlayerEntity entity) {
        synchronized (NEED_TO_TICK) {
            for (ArmorWithTickItem item : NEED_TO_TICK)
                item.onArmorTick(world, entity);
        }
    }

    public abstract void onArmorTick(World world, PlayerEntity entity);
}
