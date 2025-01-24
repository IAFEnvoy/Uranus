package com.iafenvoy.uranus.mixin;

import com.iafenvoy.uranus.event.EntityEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    public void onEntityJoin(Entity entity, CallbackInfo ci) {
        if (EntityEvents.ON_JOIN_WORLD.invoker().onJoinWorld(entity, (World) (Object) this))
            ci.cancel();
    }
}

