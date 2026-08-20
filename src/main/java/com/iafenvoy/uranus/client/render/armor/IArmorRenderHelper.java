package com.iafenvoy.uranus.client.render.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

/** Utilities for submitting armor geometry through the 26.1 render collector. */
public interface IArmorRenderHelper {
    static <S> void submitPart(PoseStack poseStack, SubmitNodeCollector collector, int light, Model<S> model, S state, Identifier texture, int color) {
        collector.submitModel(model, state, poseStack, RenderTypes.armorCutoutNoCull(texture), light, OverlayTexture.NO_OVERLAY, color, (ModelFeatureRenderer.CrumblingOverlay) null);
    }

    // From trinkets, updated to consume the extracted player render state.
    static void translateToChest(PoseStack poseStack, PlayerModel model, AvatarRenderState state) {
        if (state.isCrouching && !state.isVisuallySwimming) {
            poseStack.translate(0.0F, 0.2F, 0.0F);
            poseStack.mulPose(Axis.XP.rotation(model.body.xRot));
        }
        poseStack.mulPose(Axis.YP.rotation(model.body.yRot));
        poseStack.translate(0.0F, 0.4F, -0.16F);
    }
}
