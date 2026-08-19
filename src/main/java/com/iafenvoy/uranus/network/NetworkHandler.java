package com.iafenvoy.uranus.network;

import com.iafenvoy.uranus.network.payload.AnimationPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;

@EventBusSubscriber
public final class NetworkHandler {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToClient(AnimationPayload.ID, AnimationPayload.CODEC, new MainThreadPayloadHandler<>(ClientNetworkHandlers::onAnimation));
    }
}
