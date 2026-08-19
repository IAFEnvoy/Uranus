package com.iafenvoy.uranus.util;

import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.concurrent.CopyOnWriteArrayList;

@EventBusSubscriber
public final class Timeout {
    private static final CopyOnWriteArrayList<Timeout> TIMEOUTS = new CopyOnWriteArrayList<>();
    private final int waitTicks;
    private final int maxTimes;
    private final Runnable callback;
    public boolean shouldRemove = false;
    private int ticks = 0;
    private int currentTimes = 0;

    private Timeout(int waitTicks, int maxTimes, Runnable callback) {
        this.waitTicks = waitTicks;
        this.maxTimes = maxTimes;
        this.callback = callback;
    }

    public static void create(int waitTicks, Runnable callback) {
        create(waitTicks, 1, callback);
    }

    public static void create(int waitTicks, int maxTimes, Runnable callback) {
        if (maxTimes <= 0) return;
        TIMEOUTS.add(new Timeout(waitTicks, maxTimes, callback));
    }

    public void tick(MinecraftServer server) {
        this.ticks++;
        if (this.ticks >= this.waitTicks) {
            server.execute(this.callback);
            this.currentTimes++;
            if (this.currentTimes >= this.maxTimes)
                this.shouldRemove = true;
        }
    }

    public boolean shouldRemove() {
        return this.shouldRemove;
    }

    @SubscribeEvent
    public static void onTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        TIMEOUTS.forEach(x -> x.tick(server));
        TIMEOUTS.removeAll(TIMEOUTS.stream().filter(Timeout::shouldRemove).toList());
    }
}
