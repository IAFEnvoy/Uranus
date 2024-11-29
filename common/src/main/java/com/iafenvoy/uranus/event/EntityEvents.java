package com.iafenvoy.uranus.event;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class EntityEvents {
    public static final Event<JoinWorld> ON_JOIN_WORLD = new Event<>(callbacks -> (entity, world) -> {
        for (JoinWorld callback : callbacks)
            if (!callback.onJoinWorld(entity, world))
                return true;
        return false;
    });

    public static final Event<Tracking> START_TRACKING_TAIL = new Event<>(callbacks -> (entity, player) -> {
        for (Tracking callback : callbacks)
            callback.onTrackingStart(entity, player);
    });

    @FunctionalInterface
    public interface JoinWorld {
        boolean onJoinWorld(Entity entity, World world);
    }

    @FunctionalInterface
    public interface Tracking {
        void onTrackingStart(Entity tracking, ServerPlayerEntity player);
    }
}
