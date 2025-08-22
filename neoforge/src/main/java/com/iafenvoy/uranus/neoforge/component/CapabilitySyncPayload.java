package com.iafenvoy.uranus.neoforge.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

@Deprecated(forRemoval = true)
public record CapabilitySyncPayload(Identifier id, int entityId, NbtCompound compound) implements CustomPayload {
    public static final Id<CapabilitySyncPayload> ID = new Id<>(CapabilitySyncHelper.CAPABILITY_SYNC);
    public static final PacketCodec<ByteBuf, CapabilitySyncPayload> CODEC = PacketCodecs.codec(RecordCodecBuilder.create(i -> i.group(
            Identifier.CODEC.fieldOf("id").forGetter(CapabilitySyncPayload::id),
            Codec.INT.fieldOf("entityId").forGetter(CapabilitySyncPayload::entityId),
            NbtCompound.CODEC.fieldOf("compound").forGetter(CapabilitySyncPayload::compound)
    ).apply(i, CapabilitySyncPayload::new)));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
