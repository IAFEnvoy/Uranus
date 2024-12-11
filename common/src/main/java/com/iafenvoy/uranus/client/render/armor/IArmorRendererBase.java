package com.iafenvoy.uranus.client.render.armor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.HashMap;

@Environment(EnvType.CLIENT)
public interface IArmorRendererBase<T extends LivingEntity> {
    HashMap<ItemConvertible, IArmorRendererBase<? extends LivingEntity>> RENDERERS = new HashMap<>();

    BipedEntityModel<T> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, BipedEntityModel<T> defaultModel);

    Identifier getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot);

    default void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity entity, EquipmentSlot slot, int light, ItemStack stack, BipedEntityModel<T> defaultModel) {
        BipedEntityModel<T> armorModel = this.getHumanoidArmorModel(entity, stack, slot, defaultModel);
        defaultModel.copyBipedStateTo(armorModel);
        armorModel.head.visible = slot == EquipmentSlot.HEAD;
        armorModel.body.visible = slot == EquipmentSlot.CHEST;
        armorModel.leftArm.visible = slot == EquipmentSlot.CHEST;
        armorModel.rightArm.visible = slot == EquipmentSlot.CHEST;
        armorModel.leftLeg.visible = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
        armorModel.rightLeg.visible = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(this.getArmorTexture(stack, entity, slot)));
        armorModel.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
    }

    static <T extends LivingEntity> void register(IArmorRendererBase<T> renderer, ItemConvertible... items) {
        Arrays.stream(items).forEach(x -> RENDERERS.put(x, renderer));
    }
}
