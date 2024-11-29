package com.iafenvoy.uranus.mixin;

import com.iafenvoy.uranus.event.LivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @ModifyVariable(method = "applyDamage", at = @At(value = "LOAD", ordinal = 5), index = 2, argsOnly = true)
    private float livingDamageEvent(float value, DamageSource pDamageSource) {
        return LivingEntityEvents.DAMAGE.invoker().onLivingDamage((LivingEntity) (Object) this, pDamageSource, value);
    }
}
