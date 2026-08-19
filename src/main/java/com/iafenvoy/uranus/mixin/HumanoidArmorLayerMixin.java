package com.iafenvoy.uranus.mixin;

import com.iafenvoy.uranus.client.render.armor.IArmorRendererBase;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public HumanoidArmorLayerMixin(RenderLayerParent<T, M> context) {
        super(context);
    }

    @Inject(method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void onRenderArmor(PoseStack matrices, MultiBufferSource vertexConsumers, LivingEntity entity, EquipmentSlot armorSlot, int light, A arg5, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        ItemStack stack = entity.getItemBySlot(armorSlot);
        @SuppressWarnings("unchecked")
        IArmorRendererBase<T> renderer = (IArmorRendererBase<T>) IArmorRendererBase.RENDERERS.get(stack.getItem());
        if (renderer != null) {
            renderer.render(matrices, vertexConsumers, entity, armorSlot, light, stack, this.getParentModel());
            ci.cancel();
        }
    }
}
