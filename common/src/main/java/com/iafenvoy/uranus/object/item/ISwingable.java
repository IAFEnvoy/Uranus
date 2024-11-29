package com.iafenvoy.uranus.object.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ISwingable {
    boolean onEntitySwing(ItemStack itemtack, Entity entity) ;

    boolean onSwingHand(ItemStack itemtack, World world, double x, double y, double z);
}
