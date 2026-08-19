package com.iafenvoy.uranus.mixin;

import com.iafenvoy.uranus.object.item.ISwingable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    protected LivingEntityMixin(EntityType<? extends Entity> entityType, Level world) {
        super(entityType, world);
    }

    @Shadow
    public abstract ItemStack getItemInHand(InteractionHand hand);

    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"), cancellable = true)
    private void onSwingHand(InteractionHand hand, boolean fromServerPlayer, CallbackInfo ci) {
        ItemStack stack = this.getItemInHand(hand);
        Item item = stack.getItem();
        if (item instanceof ISwingable iSwingable)
            if (iSwingable.onEntitySwing(stack, this))
                ci.cancel();
    }
}
