package com.iafenvoy.uranus;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class ServerHelper {
    @Nullable
    public static MinecraftServer server = null;

    public static void sendToAll(CustomPayload payload) {
        if (server != null)
            NetworkManager.sendToPlayers(server.getPlayerManager().getPlayerList(), payload);
    }
}
