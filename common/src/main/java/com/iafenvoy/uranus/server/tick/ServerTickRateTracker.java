package com.iafenvoy.uranus.server.tick;

import com.iafenvoy.uranus.ServerHelper;
import com.iafenvoy.uranus.StaticVariables;
import com.iafenvoy.uranus.network.PacketBufferUtils;
import com.iafenvoy.uranus.server.tick.modifier.TickRateModifier;
import com.iafenvoy.uranus.server.world.UranusServerData;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerTickRateTracker extends TickRateTracker {
    public static final Logger LOGGER = LogManager.getLogger("uranus-server-tick");

    public final MinecraftServer server;

    public ServerTickRateTracker(MinecraftServer server) {
        this.server = server;
    }

    public ServerTickRateTracker(MinecraftServer server, NbtCompound tag) {
        this(server);
        this.fromTag(tag);
    }

    public static ServerTickRateTracker getForServer(MinecraftServer server) {
        return UranusServerData.get(server).getOrCreateTickRateTracker();
    }

    public static void modifyTickRate(World level, TickRateModifier modifier) {
        if (level instanceof ServerWorld serverLevel)
            getForServer(serverLevel.getServer()).addTickRateModifier(modifier);
    }

    public void addTickRateModifier(TickRateModifier modifier) {
        this.tickRateModifierList.add(modifier);
        this.sync();
    }

    @Override
    public void tickEntityAtCustomRate(Entity entity) {
        if (!entity.getWorld().isClient && entity.getWorld() instanceof ServerWorld serverWorld)
            serverWorld.tickEntity(entity);
    }

    @Override
    protected void sync() {
        ServerHelper.sendToAll(StaticVariables.SYNC_CLIENT_TICK, PacketBufferUtils.create().writeNbt(this.toTag()));
    }
}
