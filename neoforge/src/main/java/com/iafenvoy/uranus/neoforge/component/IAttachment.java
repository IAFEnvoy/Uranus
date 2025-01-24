package com.iafenvoy.uranus.neoforge.component;

import net.minecraft.nbt.NbtCompound;

public interface IAttachment {
    NbtCompound serializeNBT();

    void deserializeNBT(NbtCompound nbt);
}
