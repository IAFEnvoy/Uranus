package com.iafenvoy.uranus.object.entity;

import net.minecraft.nbt.NbtCompound;

/**
 * @author Alexthe666
 * @since 1.7.0
 */
public interface IUranusDataEntity {
    NbtCompound getUranusEntityData();

    void setUranusEntityData(NbtCompound nbt);
}
