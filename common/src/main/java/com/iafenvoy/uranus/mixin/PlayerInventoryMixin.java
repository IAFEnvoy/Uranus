package com.iafenvoy.uranus.mixin;

import com.iafenvoy.uranus.object.item.ArmorWithTickItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Final
    @Shadow
    public PlayerEntity player;

    @Deprecated(forRemoval = true)
    @Inject(method = "updateItems", at = @At("RETURN"))
    private void onInventoryTick(CallbackInfo ci) {
        ArmorWithTickItem.doTick(player.getWorld(), player);
    }
}
