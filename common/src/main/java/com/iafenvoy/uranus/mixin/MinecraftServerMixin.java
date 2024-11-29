package com.iafenvoy.uranus.mixin;

import com.iafenvoy.uranus.ServerHelper;
import com.iafenvoy.uranus.server.world.ModifiableTickRateServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements ModifiableTickRateServer {
    @Unique
    private long modifiedMsPerTick = -1;
    @Unique
    private long masterMs;

    @Unique
    private void masterTick() {
        this.masterMs += 50L;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onServerCreated(CallbackInfo ci) {
        ServerHelper.server = (MinecraftServer) (Object) this;
    }

    @ModifyConstant(method = "runServer()V", constant = @Constant(longValue = 50L), expect = 4)
    private long serverMsPerTick(long value) {
        return this.modifiedMsPerTick == -1 ? value : this.modifiedMsPerTick;
    }

    @Override
    public void setGlobalTickLengthMs(long msPerTick) {
        this.modifiedMsPerTick = msPerTick;
    }

    @Override
    public long getMasterMs() {
        return this.masterMs;
    }
}
