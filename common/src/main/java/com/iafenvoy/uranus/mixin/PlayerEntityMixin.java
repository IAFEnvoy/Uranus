package com.iafenvoy.uranus.mixin;

import com.iafenvoy.uranus.event.LivingEntityEvents;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @ModifyExpressionValue(method = "applyDamage", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"))
    private float livingDamageEvent(float value, DamageSource pDamageSource) {
        return LivingEntityEvents.DAMAGE.invoker().onLivingDamage((LivingEntity) (Object) this, pDamageSource, value);
    }
}
