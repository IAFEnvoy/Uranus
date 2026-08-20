package com.iafenvoy.uranus.object.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ToolMaterialUtil {
    public static ToolMaterial of(int uses, float speed, float attackDamageBonus, int enchantmentLevel, ItemLike... repairIngredients) {
        return of(TagKey.create(Registries.BLOCK, Identifier.parse("minecraft:incorrect_for_uranus_tools")), uses, speed, attackDamageBonus, enchantmentLevel, repairIngredients);
    }

    public static ToolMaterial of(TagKey<Block> inverseTag, int uses, float speed, float attackDamageBonus, int enchantmentLevel, ItemLike... repairIngredients) {
        TagKey<net.minecraft.world.item.Item> repairTag = TagKey.create(Registries.ITEM, Identifier.parse("minecraft:repairable/uranus_tools"));
        return new ToolMaterial(inverseTag, uses, speed, attackDamageBonus, enchantmentLevel, repairTag);
    }
}
