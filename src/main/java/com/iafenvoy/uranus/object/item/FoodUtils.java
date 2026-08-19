package com.iafenvoy.uranus.object.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FoodUtils {
    public static int getFoodPoints(Entity entity) {
        if (entity instanceof AgeableMob) return Math.round(entity.getBbWidth() * entity.getBbHeight() * 10);
        if (entity instanceof Player) return 15;
        return 0;
    }

    public static int getFoodPoints(@NotNull ItemStack stack, boolean meatOnly, boolean includeFish) {
        FoodProperties food = stack.get(DataComponents.FOOD);
        if (stack != ItemStack.EMPTY && stack.getItem() != null && food != null)
            if (!meatOnly || stack.is(ItemTags.MEAT) || includeFish && stack.is(ItemTags.FISHES))
                return food.nutrition() * 10;
        return 0;
    }
}
