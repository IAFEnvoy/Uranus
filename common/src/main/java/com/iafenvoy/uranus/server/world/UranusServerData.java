package com.iafenvoy.uranus.server.world;

import com.iafenvoy.uranus.server.tick.ServerTickRateTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class UranusServerData extends PersistentState {
    private static final Map<MinecraftServer, UranusServerData> dataMap = new HashMap<>();

    private static final String IDENTIFIER = "uranus_world_data";

    private final MinecraftServer server;

    private ServerTickRateTracker tickRateTracker = null;

    public UranusServerData(MinecraftServer server) {
        super();
        this.server = server;
    }


    public static UranusServerData get(MinecraftServer server) {
        UranusServerData fromMap = dataMap.get(server);
        if (fromMap == null) {
            PersistentStateManager storage = server.getWorld(World.OVERWORLD).getPersistentStateManager();
            UranusServerData data = storage.getOrCreate((tag) -> load(server, tag), () -> new UranusServerData(server), IDENTIFIER);
            if (data != null)
                data.markDirty();
            dataMap.put(server, data);
            return data;
        }
        return fromMap;
    }

    public static UranusServerData load(MinecraftServer server, NbtCompound tag) {
        UranusServerData data = new UranusServerData(server);
        data.tickRateTracker = tag.contains("TickRateTracker") ? new ServerTickRateTracker(server, tag.getCompound("TickRateTracker")) : new ServerTickRateTracker(server);
        return data;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        if (this.tickRateTracker != null)
            tag.put("TickRateTracker", this.tickRateTracker.toTag());
        return tag;
    }

    public ServerTickRateTracker getOrCreateTickRateTracker() {
        if (this.tickRateTracker == null)
            this.tickRateTracker = new ServerTickRateTracker(this.server);
        return this.tickRateTracker;
    }
}
