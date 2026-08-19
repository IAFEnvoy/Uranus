package com.iafenvoy.uranus.client.model;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.world.entity.Entity;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface ITabulaModelAnimator<T extends Entity> {
    void setRotationAngles(TabulaModel<T> model, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale);
}