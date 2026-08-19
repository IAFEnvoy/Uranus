package com.iafenvoy.uranus.network;

import com.iafenvoy.uranus.animation.IAnimatedEntity;
import com.iafenvoy.uranus.network.payload.AnimationPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientNetworkHandlers {
    public static void onAnimation(AnimationPayload payload, IPayloadContext context) {
        Level world = context.player().level();
        if (world.getEntity(payload.entityID()) instanceof IAnimatedEntity entity) {
            if (payload.index() == -1) entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
            else entity.setAnimation(entity.getAnimations()[payload.index()]);
            entity.setAnimationTick(0);
        }
    }
}
