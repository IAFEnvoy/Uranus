package com.iafenvoy.uranus.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.apache.commons.lang3.Validate;

import java.nio.charset.StandardCharsets;

/**
 * Utilities for interacting with PacketBuffer.
 *
 * @author cpw
 * @since 1.0.0
 */
public class PacketBufferUtils {
    public static PacketByteBuf create() {
        return new PacketByteBuf(Unpooled.buffer());
    }

    /**
     * The number of bytes to write the supplied int using the 7 bit varint encoding.
     *
     * @param toCount The number to analyse
     * @return The number of bytes it will take to write it (maximum of 5)
     */
    public static int varIntByteCount(int toCount) {
        return (toCount & 0xFFFFFF80) == 0 ? 1 : ((toCount & 0xFFFFC000) == 0 ? 2 : ((toCount & 0xFFE00000) == 0 ? 3 : ((toCount & 0xF0000000) == 0 ? 4 : 5)));
    }

    /**
     * Read a varint from the supplied buffer.
     *
     * @param buf     The buffer to read from
     * @param maxSize The maximum length of bytes to read
     * @return The integer
     */
    public static int readVarInt(ByteBuf buf, int maxSize) {
        Validate.isTrue(maxSize < 6 && maxSize > 0, "Varint length is between 1 and 5, not %d", maxSize);
        int i = 0;
        int j = 0;
        byte b0;

        do {
            b0 = buf.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > maxSize) throw new RuntimeException("VarInt too big");
        } while ((b0 & 128) == 128);
        return i;
    }

    /**
     * An extended length short. Used by custom payload packets to extend size.
     */
    public static int readVarShort(ByteBuf buf) {
        int low = buf.readUnsignedShort();
        int high = 0;
        if ((low & 0x8000) != 0) {
            low = low & 0x7FFF;
            high = buf.readUnsignedByte();
        }
        return ((high & 0xFF) << 15) | low;
    }

    public static void writeVarShort(ByteBuf buf, int toWrite) {
        int low = toWrite & 0x7FFF;
        int high = (toWrite & 0x7F8000) >> 15;
        if (high != 0) low = low | 0x8000;
        buf.writeShort(low);
        if (high != 0) buf.writeByte(high);
    }

    /**
     * Write an integer to the buffer using variable length encoding. The maxSize constrains
     * how many bytes (and therefore the maximum number) that will be written.
     *
     * @param to      The buffer to write to
     * @param toWrite The integer to write
     * @param maxSize The maximum number of bytes to use
     */
    public static void writeVarInt(ByteBuf to, int toWrite, int maxSize) {
        Validate.isTrue(varIntByteCount(toWrite) <= maxSize, "Integer is too big for %d bytes", maxSize);
        while ((toWrite & -128) != 0) {
            to.writeByte(toWrite & 127 | 128);
            toWrite >>>= 7;
        }
        to.writeByte(toWrite);
    }

    /**
     * Read a UTF8 string from the byte buffer.
     * It is encoded as <varint length>[<UTF8 char bytes>]
     *
     * @param from The buffer to read from
     * @return The string
     */
    public static String readUTF8String(ByteBuf from) {
        int len = readVarInt(from, 2);
        String str = from.toString(from.readerIndex(), len, StandardCharsets.UTF_8);
        from.readerIndex(from.readerIndex() + len);
        return str;
    }

    /**
     * Write a String with UTF8 byte encoding to the buffer.
     * It is encoded as <varint length>[<UTF8 char bytes>]
     *
     * @param to     the buffer to write to
     * @param string The string to write
     */
    public static void writeUTF8String(ByteBuf to, String string) {
        byte[] utf8Bytes = string.getBytes(StandardCharsets.UTF_8);
        Validate.isTrue(varIntByteCount(utf8Bytes.length) < 3, "The string is too long for this encoding.");
        writeVarInt(to, utf8Bytes.length, 2);
        to.writeBytes(utf8Bytes);
    }

    /**
     * Write an {@link NbtCompound} to the byte buffer. It uses the minecraft encoding.
     *
     * @param to  The buffer to write to
     * @param tag The tag to write
     */
    public static void writeTag(ByteBuf to, NbtCompound tag) {
        PacketByteBuf pb = new PacketByteBuf(to);
        pb.writeNbt(tag);
    }

    /**
     * Read an {@link NbtCompound} from the byte buffer. It uses the minecraft encoding.
     *
     * @param from The buffer to read from
     * @return The read tag
     */
    public static NbtCompound readTag(ByteBuf from) {
        PacketByteBuf pb = new PacketByteBuf(from);
        try {
            return pb.readNbt();
        } catch (Exception e) {
            // Unpossible?
            throw new RuntimeException(e);
        }
    }
}
