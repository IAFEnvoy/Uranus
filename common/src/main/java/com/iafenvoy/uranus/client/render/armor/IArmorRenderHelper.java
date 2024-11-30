package com.iafenvoy.uranus.client.render.armor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public interface IArmorRenderHelper {
    // From net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer

    /**
     * Helper method for rendering a specific armor model, comes after setting visibility.
     *
     * <p>This primarily handles applying glint and the correct {@link RenderLayer}
     *
     * @param matrices        the matrix stack
     * @param vertexConsumers the vertex consumer provider
     * @param light           packed lightmap coordinates
     * @param stack           the item stack of the armor item
     * @param model           the model to be rendered
     * @param texture         the texture to be applied
     */
    static void renderPart(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack, Model model, Identifier texture) {
        VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(texture), false, stack.hasGlint());
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
    }

    //From trinkets
    static void translateToChest(MatrixStack matrices, PlayerEntityModel<AbstractClientPlayerEntity> model, AbstractClientPlayerEntity player) {
        if (player.isInSneakingPose() && !model.riding && !player.isSwimming()) {
            matrices.translate(0.0F, 0.2F, 0.0F);
            matrices.multiply(RotationAxis.POSITIVE_X.rotation(model.body.pitch));
        }
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(model.body.yaw));
        matrices.translate(0.0F, 0.4F, -0.16F);
    }
}
