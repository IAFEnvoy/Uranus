package com.iafenvoy.uranus.client.render.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@OnlyIn(Dist.CLIENT)
public interface IArmorRenderHelper {
    /**
     * Helper method for rendering a specific armor model, comes after setting visibility.
     *
     * <p>This primarily handles applying glint and the correct {@link RenderType}
     *
     * @param matrices        the matrix stack
     * @param vertexConsumers the vertex consumer provider
     * @param light           packed lightmap coordinates
     * @param stack           the item stack of the armor item
     * @param model           the model to be rendered
     * @param texture         the texture to be applied
     */
    static void renderPart(PoseStack matrices, MultiBufferSource vertexConsumers, int light, ItemStack stack, Model model, ResourceLocation texture) {
        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(vertexConsumers, RenderType.armorCutoutNoCull(texture), stack.hasFoil());
        model.renderToBuffer(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, -1);
    }

    //From trinkets
    static void translateToChest(PoseStack matrices, PlayerModel<AbstractClientPlayer> model, AbstractClientPlayer player) {
        if (player.isCrouching() && !model.riding && !player.isSwimming()) {
            matrices.translate(0.0F, 0.2F, 0.0F);
            matrices.mulPose(Axis.XP.rotation(model.body.xRot));
        }
        matrices.mulPose(Axis.YP.rotation(model.body.yRot));
        matrices.translate(0.0F, 0.4F, -0.16F);
    }
}
