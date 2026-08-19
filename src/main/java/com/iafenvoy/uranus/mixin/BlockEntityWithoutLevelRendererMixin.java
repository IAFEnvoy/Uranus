package com.iafenvoy.uranus.mixin;

import com.iafenvoy.uranus.client.render.DynamicItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(BlockEntityWithoutLevelRenderer.class)
public abstract class BlockEntityWithoutLevelRendererMixin {
    @Inject(method = "renderByItem", at = @At("HEAD"), cancellable = true)
    private void onRender(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, CallbackInfo info) {
        DynamicItemRenderer renderer = DynamicItemRenderer.RENDERERS.get(stack.getItem());
        if (renderer != null) {
            renderer.render(stack, mode, matrices, vertexConsumers, light, overlay);
            info.cancel();
        }
    }
}
