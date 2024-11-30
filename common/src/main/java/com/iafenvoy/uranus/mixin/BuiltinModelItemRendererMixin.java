package com.iafenvoy.uranus.mixin;

import com.iafenvoy.uranus.client.render.DynamicItemRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BuiltinModelItemRenderer.class)
public abstract class BuiltinModelItemRendererMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void fabric_onRender(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo info) {
        DynamicItemRenderer renderer = DynamicItemRenderer.RENDERERS.get(stack.getItem());
        if (renderer != null) {
            renderer.render(stack, mode, matrices, vertexConsumers, light, overlay);
            info.cancel();
        }
    }
}
