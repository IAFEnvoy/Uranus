package com.iafenvoy.uranus.client;

import com.iafenvoy.uranus.StaticVariables;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;

public class UranusClient {
    public static void init() {
    }

    public static void process() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, StaticVariables.ANIMATION, (buf, ctx) -> {
            int entityID = buf.readInt();
            int index = buf.readInt();
            World world = MinecraftClient.getInstance().world;
            if (world != null && world.getEntityById(entityID) instanceof IAnimatedEntity entity) {
                if (index == -1) entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
                else entity.setAnimation(entity.getAnimations()[index]);
                entity.setAnimationTick(0);
            }
        });
    }
}
