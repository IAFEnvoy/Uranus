package com.iafenvoy.uranus.network.payload;

import com.iafenvoy.uranus.Uranus;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record AnimationPayload(int entityID, int index) implements CustomPacketPayload {
    public static final Type<AnimationPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Uranus.MOD_ID, "animation"));
    public static final StreamCodec<ByteBuf, AnimationPayload> CODEC = ByteBufCodecs.fromCodec(RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("entityID").forGetter(AnimationPayload::entityID),
            Codec.INT.fieldOf("index").forGetter(AnimationPayload::index)
    ).apply(i, AnimationPayload::new)));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
