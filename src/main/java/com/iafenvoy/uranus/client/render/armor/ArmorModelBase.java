package com.iafenvoy.uranus.client.render.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
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
            applyRotation(this.head, armorStand.headPose.x(), armorStand.headPose.y(), armorStand.headPose.z());
            applyRotation(this.body, armorStand.bodyPose.x(), armorStand.bodyPose.y(), armorStand.bodyPose.z());
            applyRotation(this.leftArm, armorStand.leftArmPose.x(), armorStand.leftArmPose.y(), armorStand.leftArmPose.z());
            applyRotation(this.rightArm, armorStand.rightArmPose.x(), armorStand.rightArmPose.y(), armorStand.rightArmPose.z());
            applyRotation(this.leftLeg, armorStand.leftLegPose.x(), armorStand.leftLegPose.y(), armorStand.leftLegPose.z());
            applyRotation(this.rightLeg, armorStand.rightLegPose.x(), armorStand.rightLegPose.y(), armorStand.rightLegPose.z());
            applyRotation(this.hat, this.head.xRot * 180.0F / (float) Math.PI, this.head.yRot * 180.0F / (float) Math.PI, this.head.zRot * 180.0F / (float) Math.PI);
        }
    }

    public void submit(EquipmentSlot slot, PoseStack poseStack, SubmitNodeCollector collector, int light, HumanoidRenderState state, Identifier texture, int color) {
        this.setPartVisibility(slot);
        collector.submitModel(this, state, poseStack, RenderTypes.armorCutoutNoCull(texture), light, OverlayTexture.NO_OVERLAY, color, null);
    }

    private void setPartVisibility(EquipmentSlot slot) {
        this.head.visible = false;
        this.hat.visible = false;
        this.body.visible = false;
        this.leftArm.visible = false;
        this.rightArm.visible = false;
        this.leftLeg.visible = false;
        this.rightLeg.visible = false;
        switch (slot) {
            case HEAD -> {
                this.head.visible = true;
                this.hat.visible = true;
            }
            case CHEST -> {
                this.body.visible = true;
                this.leftArm.visible = true;
                this.rightArm.visible = true;
            }
            case LEGS, FEET -> {
                this.leftLeg.visible = true;
                this.rightLeg.visible = true;
            }
        }
    }

    private static void applyRotation(ModelPart part, float x, float y, float z) {
        part.xRot = x * (float) Math.PI / 180.0F;
        part.yRot = y * (float) Math.PI / 180.0F;
        part.zRot = z * (float) Math.PI / 180.0F;
    }
}
