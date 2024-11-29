package com.iafenvoy.uranus.mixin;

import com.iafenvoy.uranus.client.tick.ClientTickRateTracker;
import com.iafenvoy.uranus.event.EntityEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World {
    protected ClientWorldMixin(MutableWorldProperties writableLevelData, RegistryKey<World> levelResourceKey, DynamicRegistryManager registryAccess, RegistryEntry<DimensionType> dimensionTypeHolder, Supplier<Profiler> filler, boolean b1, boolean b2, long seed, int i) {
        super(writableLevelData, levelResourceKey, registryAccess, dimensionTypeHolder, filler, b1, b2, seed, i);
    }

    @ModifyConstant(method = "tickTime", constant = @Constant(longValue = 1L), expect = 2)
    private long clientSetDayTime(long timeIn) {
        return ClientTickRateTracker.getForClient(MinecraftClient.getInstance()).getDayTimeIncrement(timeIn);
    }

    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    public void onEntityJoin(int i, Entity entity, CallbackInfo ci) {
        if (EntityEvents.ON_JOIN_WORLD.invoker().onJoinWorld(entity, (World) (Object) this))
            ci.cancel();
    }
}

