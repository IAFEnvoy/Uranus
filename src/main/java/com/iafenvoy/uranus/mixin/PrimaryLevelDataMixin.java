package com.iafenvoy.uranus.mixin;

import com.iafenvoy.uranus.Uranus;
import com.mojang.serialization.Lifecycle;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PrimaryLevelData.class)
public class PrimaryLevelDataMixin {
    @Shadow
    @Final
    private Lifecycle worldGenSettingsLifecycle;

    @Inject(method = "worldGenSettingsLifecycle()Lcom/mojang/serialization/Lifecycle;", at = @At("HEAD"), cancellable = true)
    private void getLifecycle(CallbackInfoReturnable<Lifecycle> cir) {
        if (this.worldGenSettingsLifecycle == Lifecycle.experimental()) {
            Uranus.LOGGER.warn("Suppressing EXPERIMENTAL level lifecycle");
            cir.setReturnValue(Lifecycle.stable());
            cir.cancel();
        }
    }
}
