package com.iafenvoy.uranus.object.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;

import java.util.function.Function;

@Deprecated(forRemoval = true)
public class FoilItemBase extends ItemBase {
    public FoilItemBase(Function<Settings, Settings> properties) {
        super(properties);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean hasGlint(ItemStack itemtack) {
        return true;
    }
}
