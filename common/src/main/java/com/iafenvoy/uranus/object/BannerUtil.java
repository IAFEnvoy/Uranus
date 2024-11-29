package com.iafenvoy.uranus.object;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

public class BannerUtil {
    @SafeVarargs
    public static ItemStack create(String translateId, Item baseItem, Pair<RegistryKey<BannerPattern>, DyeColor>... patterns) {
        NbtCompound nbtCompound = new NbtCompound();
        BannerPattern.Patterns p = new BannerPattern.Patterns();
        for (Pair<RegistryKey<BannerPattern>, DyeColor> pattern : patterns)
            p.add(pattern.getLeft(), pattern.getRight());
        nbtCompound.put("Patterns", p.toNbt());
        ItemStack itemStack = new ItemStack(baseItem);
        BlockItem.setBlockEntityNbt(itemStack, BlockEntityType.BANNER, nbtCompound);
        itemStack.setCustomName(Text.translatable(translateId).setStyle(Style.EMPTY.withColor(Formatting.AQUA).withItalic(false)));
        itemStack.addHideFlag(ItemStack.TooltipSection.ADDITIONAL);
        return itemStack;
    }
}
