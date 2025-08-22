package com.iafenvoy.uranus.neoforge.component;

import net.minecraft.nbt.NbtCompound;

@Deprecated(forRemoval = true)
public interface IAttachment {
    NbtCompound serializeNBT();

    void deserializeNBT(NbtCompound nbt);
}
