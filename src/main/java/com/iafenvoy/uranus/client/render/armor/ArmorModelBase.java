package com.iafenvoy.uranus.client.render.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;

/** Base humanoid armor model for the 26.1 render-state pipeline. */
public class ArmorModelBase extends HumanoidModel<HumanoidRenderState> {
    protected static final float INNER_MODEL_OFFSET = 0.38F;
    protected static final float OUTER_MODEL_OFFSET = 0.45F;

    public ArmorModelBase(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(HumanoidRenderState state) {
        super.setupAnim(state);
        if (state instanceof ArmorStandRenderState armorStand) {
            applyRotation(head, armorStand.headPose.x(), armorStand.headPose.y(), armorStand.headPose.z());
            applyRotation(body, armorStand.bodyPose.x(), armorStand.bodyPose.y(), armorStand.bodyPose.z());
            applyRotation(leftArm, armorStand.leftArmPose.x(), armorStand.leftArmPose.y(), armorStand.leftArmPose.z());
            applyRotation(rightArm, armorStand.rightArmPose.x(), armorStand.rightArmPose.y(), armorStand.rightArmPose.z());
            applyRotation(leftLeg, armorStand.leftLegPose.x(), armorStand.leftLegPose.y(), armorStand.leftLegPose.z());
            applyRotation(rightLeg, armorStand.rightLegPose.x(), armorStand.rightLegPose.y(), armorStand.rightLegPose.z());
            applyRotation(hat, head.xRot * 180.0F / (float) Math.PI, head.yRot * 180.0F / (float) Math.PI, head.zRot * 180.0F / (float) Math.PI);
        }
    }

    public void submit(EquipmentSlot slot, PoseStack poseStack, SubmitNodeCollector collector, int light, HumanoidRenderState state, Identifier texture, int color) {
        setPartVisibility(slot);
        collector.submitModel(this, state, poseStack, RenderTypes.armorCutoutNoCull(texture), light, OverlayTexture.NO_OVERLAY, color, (ModelFeatureRenderer.CrumblingOverlay) null);
    }

    private void setPartVisibility(EquipmentSlot slot) {
        head.visible = false;
        hat.visible = false;
        body.visible = false;
        leftArm.visible = false;
        rightArm.visible = false;
        leftLeg.visible = false;
        rightLeg.visible = false;
        switch (slot) {
            case HEAD -> {
                head.visible = true;
                hat.visible = true;
            }
            case CHEST -> {
                body.visible = true;
                leftArm.visible = true;
                rightArm.visible = true;
            }
            case LEGS, FEET -> {
                leftLeg.visible = true;
                rightLeg.visible = true;
            }
        }
    }

    private static void applyRotation(ModelPart part, float x, float y, float z) {
        part.xRot = x * (float) Math.PI / 180.0F;
        part.yRot = y * (float) Math.PI / 180.0F;
        part.zRot = z * (float) Math.PI / 180.0F;
    }
}
