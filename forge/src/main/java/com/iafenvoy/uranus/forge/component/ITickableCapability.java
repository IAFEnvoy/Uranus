package com.iafenvoy.uranus.forge.component;

import net.minecraft.nbt.NbtCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface ITickableCapability extends INBTSerializable<NbtCompound> {
    void tick();

    boolean isDirty();
}
