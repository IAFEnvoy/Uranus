package com.iafenvoy.uranus.client;

import com.iafenvoy.uranus.StaticVariables;
import com.iafenvoy.uranus.Uranus;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import com.iafenvoy.uranus.client.model.util.TabulaModelHandlerHelper;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class UranusClient {
    public static void init() {
        ReloadListenerRegistry.register(ResourceType.CLIENT_RESOURCES, new TabulaModelHandlerHelper(), new Identifier(Uranus.MOD_ID, "tabula"));
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
