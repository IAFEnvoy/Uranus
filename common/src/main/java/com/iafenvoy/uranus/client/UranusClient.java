package com.iafenvoy.uranus.client;

import com.iafenvoy.uranus.animation.IAnimatedEntity;
import com.iafenvoy.uranus.network.AnimationPayload;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;

public class UranusClient {
    public static void process() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, AnimationPayload.ID, AnimationPayload.CODEC, (payload, ctx) -> {
            World world = MinecraftClient.getInstance().world;
            if (world != null && world.getEntityById(payload.entityID()) instanceof IAnimatedEntity entity) {
                if (payload.index() == -1) entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
                else entity.setAnimation(entity.getAnimations()[payload.index()]);
                entity.setAnimationTick(0);
            }
        });
    }
}
