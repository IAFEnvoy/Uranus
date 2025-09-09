package com.iafenvoy.uranus.object.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.world.World;

@Deprecated(forRemoval = true)
public class SwordItemBase extends SwordItem implements ISwingable {
    public SwordItemBase(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public boolean onEntitySwing(ItemStack itemtack, Entity entity) {
        return onSwingHand(itemtack, entity.getWorld(), entity.getX(), entity.getY(), entity.getZ());
    }

    @Override
    public boolean onSwingHand(ItemStack itemtack, World world, double x, double y, double z) {
        return false;
    }
}
