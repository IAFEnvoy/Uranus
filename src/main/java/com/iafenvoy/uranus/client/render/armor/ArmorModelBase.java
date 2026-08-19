package com.iafenvoy.uranus.client.render.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ArmorModelBase extends HumanoidModel<LivingEntity> {
    protected static final float INNER_MODEL_OFFSET = 0.38F;
    protected static final float OUTER_MODEL_OFFSET = 0.45F;

    public ArmorModelBase(ModelPart p_170677_) {
        super(p_170677_);
    }

    @Override
    public void setupAnim(@NotNull LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entityIn instanceof ArmorStand armorStand) {
            this.head.xRot = ((float) Math.PI / 180F) * armorStand.getHeadPose().getX();
            this.head.yRot = ((float) Math.PI / 180F) * armorStand.getHeadPose().getY();
            this.head.zRot = ((float) Math.PI / 180F) * armorStand.getHeadPose().getZ();
            this.body.xRot = ((float) Math.PI / 180F) * armorStand.getBodyPose().getX();
            this.body.yRot = ((float) Math.PI / 180F) * armorStand.getBodyPose().getY();
            this.body.zRot = ((float) Math.PI / 180F) * armorStand.getBodyPose().getZ();
            this.leftArm.xRot = ((float) Math.PI / 180F) * armorStand.getLeftArmPose().getX();
            this.leftArm.yRot = ((float) Math.PI / 180F) * armorStand.getLeftArmPose().getY();
            this.leftArm.zRot = ((float) Math.PI / 180F) * armorStand.getLeftArmPose().getZ();
            this.rightArm.xRot = ((float) Math.PI / 180F) * armorStand.getRightArmPose().getX();
            this.rightArm.yRot = ((float) Math.PI / 180F) * armorStand.getRightArmPose().getY();
            this.rightArm.zRot = ((float) Math.PI / 180F) * armorStand.getRightArmPose().getZ();
            this.leftLeg.xRot = ((float) Math.PI / 180F) * armorStand.getLeftLegPose().getX();
            this.leftLeg.yRot = ((float) Math.PI / 180F) * armorStand.getLeftLegPose().getY();
            this.leftLeg.zRot = ((float) Math.PI / 180F) * armorStand.getLeftLegPose().getZ();
            this.rightLeg.xRot = ((float) Math.PI / 180F) * armorStand.getRightLegPose().getX();
            this.rightLeg.yRot = ((float) Math.PI / 180F) * armorStand.getRightLegPose().getY();
            this.rightLeg.zRot = ((float) Math.PI / 180F) * armorStand.getRightLegPose().getZ();
            this.hat.copyFrom(this.head);
        } else
            super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public void render(EquipmentSlot slot, PoseStack matrices, MultiBufferSource vertexConsumers, int light, ItemStack stack, ResourceLocation texture) {
        switch (slot) {
            case HEAD -> this.renderHelmet(matrices, vertexConsumers, light, stack, texture);
            case CHEST -> this.renderChestplate(matrices, vertexConsumers, light, stack, texture);
            case LEGS -> this.renderLeggings(matrices, vertexConsumers, light, stack, texture);
            case FEET -> this.renderBoots(matrices, vertexConsumers, light, stack, texture);
        }
    }

    public void renderHelmet(PoseStack matrices, MultiBufferSource vertexConsumers, int light, ItemStack stack, ResourceLocation texture) {
        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(vertexConsumers, RenderType.armorCutoutNoCull(texture), stack.hasFoil());
        this.head.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, -1);
    }

    public void renderChestplate(PoseStack matrices, MultiBufferSource vertexConsumers, int light, ItemStack stack, ResourceLocation texture) {
        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(vertexConsumers, RenderType.armorCutoutNoCull(texture), stack.hasFoil());
        this.body.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, -1);
        this.leftArm.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, -1);
        this.rightArm.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, -1);
    }

    public void renderLeggings(PoseStack matrices, MultiBufferSource vertexConsumers, int light, ItemStack stack, ResourceLocation texture) {
        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(vertexConsumers, RenderType.armorCutoutNoCull(texture), stack.hasFoil());
        this.leftLeg.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, -1);
        this.rightLeg.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, -1);
    }

    public void renderBoots(PoseStack matrices, MultiBufferSource vertexConsumers, int light, ItemStack stack, ResourceLocation texture) {
        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(vertexConsumers, RenderType.armorCutoutNoCull(texture), stack.hasFoil());
        this.leftLeg.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, -1);
        this.rightLeg.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, -1);
    }
}
