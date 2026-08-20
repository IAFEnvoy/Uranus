package com.iafenvoy.uranus.client.model.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.world.entity.Entity;

public abstract class BasicEntityModel<T extends Entity> {
    public final int textureWidth = 64;
    public final int textureHeight = 32;

    protected BasicEntityModel() {
    }

    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.parts().forEach(part -> part.render(matrices, vertices, light, overlay, color));
    }

    public abstract Iterable<BasicModelPart> parts();

    public abstract void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch);

    public void prepareMobModel(T entity, float limbAngle, float limbDistance, float tickDelta) {
    }
}
