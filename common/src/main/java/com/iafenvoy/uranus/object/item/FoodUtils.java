package com.iafenvoy.uranus.object.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import org.jetbrains.annotations.NotNull;

public class FoodUtils {
    public static int getFoodPoints(Entity entity) {
        if (entity instanceof PassiveEntity) return Math.round(entity.getWidth() * entity.getHeight() * 10);
        if (entity instanceof PlayerEntity) return 15;
        return 0;
    }

    public static int getFoodPoints(@NotNull ItemStack stack, boolean meatOnly, boolean includeFish) {
        FoodComponent food = stack.get(DataComponentTypes.FOOD);
        if (stack != ItemStack.EMPTY && stack.getItem() != null && food != null)
            if (!meatOnly || stack.isIn(ItemTags.MEAT) || includeFish && stack.isIn(ItemTags.FISHES))
                return food.nutrition() * 10;
        return 0;
    }
}
