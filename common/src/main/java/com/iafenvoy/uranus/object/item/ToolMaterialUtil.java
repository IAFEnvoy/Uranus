package com.iafenvoy.uranus.object.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ToolMaterialUtil {
    public static ToolMaterial of(int uses, float speed, float attackDamageBonus, int enchantmentLevel, ItemConvertible... repairIngredients) {
        return of(TagKey.of(RegistryKeys.BLOCK, Identifier.of("")), uses, speed, attackDamageBonus, enchantmentLevel, repairIngredients);
    }

    public static ToolMaterial of(TagKey<Block> inverseTag, int uses, float speed, float attackDamageBonus, int enchantmentLevel, ItemConvertible... repairIngredients) {
        return new ToolMaterial() {
            @Override
            public int getDurability() {
                return uses;
            }

            @Override
            public float getMiningSpeedMultiplier() {
                return speed;
            }

            @Override
            public float getAttackDamage() {
                return attackDamageBonus;
            }

            @Override
            public TagKey<Block> getInverseTag() {
                return inverseTag;
            }

            @Override
            public int getEnchantability() {
                return enchantmentLevel;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return Ingredient.ofItems(repairIngredients);
            }
        };
    }
}
