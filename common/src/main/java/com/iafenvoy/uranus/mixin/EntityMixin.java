package com.iafenvoy.uranus.mixin;

import com.iafenvoy.uranus.object.item.ItemBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract World getWorld();

    @Inject(method = "dropStack(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;", at = @At("HEAD"))
    private void onDropItem(ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir) {
        if (stack.getItem() instanceof ItemBase itemBase && (Entity) (Object) this instanceof PlayerEntity player)
            itemBase.onDroppedByPlayer(stack, player);
    }
}
