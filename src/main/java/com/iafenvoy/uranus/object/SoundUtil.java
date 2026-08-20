package com.iafenvoy.uranus.object;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public class SoundUtil {
    public static void playSound(Level world, double x, double y, double z, Identifier soundId, float volume, float pitch) {
        SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(soundId).map(holder -> holder.value()).orElse(null);
        if (soundEvent == null) return;
        if (world.isClientSide())
            world.playLocalSound(x, y, z, soundEvent, SoundSource.NEUTRAL, volume, pitch, false);
        else
            world.playSound(null, VecUtil.createBlockPos(x, y, z), soundEvent, SoundSource.NEUTRAL, volume, pitch);
    }

    public static void playPlayerSound(Level world, double x, double y, double z, Identifier soundId, float volume, float pitch) {
        SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(soundId).map(holder -> holder.value()).orElse(null);
        if (soundEvent == null) return;
        if (world.isClientSide())
            world.playLocalSound(x, y, z, soundEvent, SoundSource.PLAYERS, volume, pitch, false);
        else
            world.playSound(null, VecUtil.createBlockPos(x, y, z), soundEvent, SoundSource.PLAYERS, volume, pitch);
    }

    public static void stopSound(Level world, Identifier soundId) {
        if (world instanceof ServerLevel serverLevel) {
            ClientboundStopSoundPacket stopSoundPacket = new ClientboundStopSoundPacket(soundId, SoundSource.NEUTRAL);
            for (ServerPlayer serverPlayer : serverLevel.players())
                serverPlayer.connection.send(stopSoundPacket);
        }
    }
}
