package com.iafenvoy.uranus.neoforge;

import com.iafenvoy.uranus.client.UranusClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class UranusNeoForgeClient {
    @SubscribeEvent
    public static void onInit(FMLClientSetupEvent event) {
        event.enqueueWork(UranusClient::process);
    }
}
