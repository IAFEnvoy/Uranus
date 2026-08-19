package com.iafenvoy.uranus.object.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ToolMaterialUtil {
    public static Tier of(int uses, float speed, float attackDamageBonus, int enchantmentLevel, ItemLike... repairIngredients) {
        return of(TagKey.create(Registries.BLOCK, ResourceLocation.parse("")), uses, speed, attackDamageBonus, enchantmentLevel, repairIngredients);
    }

    public static Tier of(TagKey<Block> inverseTag, int uses, float speed, float attackDamageBonus, int enchantmentLevel, ItemLike... repairIngredients) {
        return new Tier() {
            @Override
            public int getUses() {
                return uses;
            }

            @Override
            public float getSpeed() {
                return speed;
            }

            @Override
            public float getAttackDamageBonus() {
                return attackDamageBonus;
            }

            @Override
            public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
                return inverseTag;
            }

            @Override
            public int getEnchantmentValue() {
                return enchantmentLevel;
            }

            @Override
            public @NotNull Ingredient getRepairIngredient() {
                return Ingredient.of(repairIngredients);
            }
        };
    }
}
