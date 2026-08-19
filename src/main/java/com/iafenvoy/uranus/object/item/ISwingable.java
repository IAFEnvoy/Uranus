package com.iafenvoy.uranus.object.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ISwingable {
    boolean onEntitySwing(ItemStack itemtack, Entity entity);

    boolean onSwingHand(ItemStack itemtack, Level world, double x, double y, double z);
}
