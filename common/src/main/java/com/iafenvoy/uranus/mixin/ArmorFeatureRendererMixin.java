package com.iafenvoy.uranus.mixin;

import com.iafenvoy.uranus.client.render.armor.IArmorTextureProvider;
import com.iafenvoy.uranus.client.render.armor.IArmorRendererBase;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    public ArmorFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(method = "getArmorTexture", at = @At("TAIL"), cancellable = true)
    private void pushTexture(ArmorItem item, boolean secondLayer, String overlay, CallbackInfoReturnable<Identifier> cir) {
        if (item instanceof IArmorTextureProvider provider)
            cir.setReturnValue(provider.getArmorTexture(item.getDefaultStack(), null, item.getSlotType(), overlay));
    }

    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private void onRenderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        ItemStack stack = entity.getEquippedStack(armorSlot);
        @SuppressWarnings("unchecked")
        IArmorRendererBase<T> renderer = (IArmorRendererBase<T>) IArmorRendererBase.RENDERERS.get(stack.getItem());
        if (renderer != null) {
            renderer.render(matrices, vertexConsumers, entity, armorSlot, light, stack, this.getContextModel());
            ci.cancel();
        }
    }
}
