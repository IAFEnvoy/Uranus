package com.iafenvoy.uranus.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ITabulaModelAnimator<T extends Entity> {
    void setRotationAngles(TabulaModel<T> model, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale);
}