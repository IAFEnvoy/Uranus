package com.iafenvoy.uranus.mixin;

import com.iafenvoy.uranus.event.LivingEntityEvents;
import com.iafenvoy.uranus.object.item.ISwingable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    protected LivingEntityMixin(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract ItemStack getStackInHand(Hand hand);

    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;Z)V", at = @At("HEAD"), cancellable = true)
    private void onSwingHand(Hand hand, boolean fromServerPlayer, CallbackInfo ci) {
        ItemStack stack = this.getStackInHand(hand);
        Item item = stack.getItem();
        if (item instanceof ISwingable iSwingable)
            if (iSwingable.onEntitySwing(stack, this))
                ci.cancel();
    }

    @Inject(method = "handleFallDamage", at = @At("HEAD"))
    public void onEntityFall(float fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntityEvents.FALL.invoker().onFall((LivingEntity) (Object) this, fallDistance, multiplier, source);
    }
}
