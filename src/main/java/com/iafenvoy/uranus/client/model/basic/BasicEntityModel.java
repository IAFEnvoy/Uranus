package com.iafenvoy.uranus.client.model.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.function.Function;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public abstract class BasicEntityModel<T extends Entity> extends EntityModel<T> {
    public final int textureWidth = 64;
    public final int textureHeight = 32;

    protected BasicEntityModel() {
        this(RenderType::entityCutoutNoCull);
    }

    protected BasicEntityModel(Function<ResourceLocation, RenderType> layerFunction) {
        super(layerFunction);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack matrices, @NotNull VertexConsumer vertices, int light, int overlay, int color) {
        this.parts().forEach(part -> part.render(matrices, vertices, light, overlay, color));
    }

    public abstract Iterable<BasicModelPart> parts();

    @Override
    public abstract void setupAnim(@NotNull T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch);

    @Override
    public void prepareMobModel(@NotNull T entity, float limbAngle, float limbDistance, float tickDelta) {
    }
}