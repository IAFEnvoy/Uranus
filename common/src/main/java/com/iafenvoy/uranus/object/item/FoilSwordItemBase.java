package com.iafenvoy.uranus.object.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;

public class FoilSwordItemBase extends SwordItemBase {
    public FoilSwordItemBase(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean hasGlint(ItemStack itemtack) {
        return true;
    }
}
