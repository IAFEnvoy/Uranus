package com.iafenvoy.uranus.network;

import com.iafenvoy.uranus.Uranus;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record AnimationPayload(int entityID, int index) implements CustomPayload {
    private static final Identifier ANIMATION = Identifier.of(Uranus.MOD_ID, "animation");
    public static final Id<AnimationPayload> ID = new Id<>(ANIMATION);
    public static final PacketCodec<ByteBuf, AnimationPayload> CODEC = PacketCodecs.codec(RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("entityID").forGetter(AnimationPayload::entityID),
            Codec.INT.fieldOf("index").forGetter(AnimationPayload::index)
    ).apply(i, AnimationPayload::new)));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
