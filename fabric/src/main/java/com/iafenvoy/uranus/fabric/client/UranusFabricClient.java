package com.iafenvoy.uranus.fabric.client;

import com.iafenvoy.uranus.client.UranusClient;
import net.fabricmc.api.ClientModInitializer;

public final class UranusFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        UranusClient.init();
        UranusClient.process();
    }
}
