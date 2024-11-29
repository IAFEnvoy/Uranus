package com.iafenvoy.uranus.object.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.function.Function;

public class ItemBase extends Item implements ISwingable {
    public ItemBase(Function<Settings, Settings> properties) {
        super(properties.apply(new Settings().rarity(Rarity.COMMON).maxCount(64)));
    }

    public boolean onDroppedByPlayer(ItemStack itemstack, PlayerEntity entity) {
        return false;
    }

    public boolean onEntitySwing(ItemStack itemtack, Entity entity) {
        return onSwingHand(itemtack, entity.getWorld(), entity.getX(), entity.getY(), entity.getZ());
    }

    public boolean onSwingHand(ItemStack itemtack, World world, double x, double y, double z) {
        return false;
    }
}
