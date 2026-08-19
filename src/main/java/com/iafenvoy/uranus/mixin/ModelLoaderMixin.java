package com.iafenvoy.uranus.mixin;

import com.iafenvoy.uranus.client.model.util.TabulaModelHandlerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBakery.class)
public class ModelLoaderMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void loadTabulaModel(CallbackInfo ci) {
        TabulaModelHandlerHelper.reloadModel(Minecraft.getInstance().getResourceManager());
    }
}
