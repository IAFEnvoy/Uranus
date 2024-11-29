package com.iafenvoy.uranus.client;

import com.iafenvoy.uranus.StaticVariables;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import com.iafenvoy.uranus.client.tick.ClientTickRateTracker;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;

public class UranusClient {
    public static void process() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, StaticVariables.ANIMATION, (buf, ctx) -> {
            int entityID = buf.readInt();
            int index = buf.readInt();
            World world = MinecraftClient.getInstance().world;
            if (world != null) {
                IAnimatedEntity entity = (IAnimatedEntity) world.getEntityById(entityID);
                if (entity != null) {
                    if (index == -1) entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
                    else entity.setAnimation(entity.getAnimations()[index]);
                    entity.setAnimationTick(0);
                }
            }
        });
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, StaticVariables.SYNC_CLIENT_TICK, (buf, ctx) -> ClientTickRateTracker.getForClient(MinecraftClient.getInstance()).syncFromServer(buf.readNbt()));
    }
}
