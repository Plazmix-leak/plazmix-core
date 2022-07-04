package net.plazmix.core.protocol;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.plazmix.core.protocol.exception.OverflowPacketException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

@UtilityClass
public class BufferedQuery {

    public void writeString(String s, ByteBuf buf) {
        byte[] b = s.getBytes(StandardCharsets.UTF_8);

        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

    public String readString(ByteBuf buf, ToIntFunction<ByteBuf> sizeSupplier) {
        int len = sizeSupplier.applyAsInt(buf);

        byte[] b = new byte[len];
        buf.readBytes(b);

        return new String(b, Charsets.UTF_8);
    }

    public String readString(ByteBuf buf) {
        return readString(buf, BufferedQuery::readVarInt);
    }

    public <T> void writeArray(@NonNull T[] array,
                               @NonNull ByteBuf buf,
                               @NonNull BiConsumer<T, ByteBuf> dataWriter) {

        writeVarInt(array.length, buf);

        for (T value : array) {
            dataWriter.accept(value, buf);
        }
    }

    public <T> T[] readArray(
            @NonNull ByteBuf buf,
            @NonNull Function<Integer, T[]> arrayCreator,
            @NonNull Function<ByteBuf, T> dataReader) {

        int length = readVarInt(buf);
        T[] array = arrayCreator.apply(length);

        for (int i = 0; i < length; i++) {
            array[i] = dataReader.apply(buf);
        }

        return array;
    }

    public void writeArray(byte[] b, ByteBuf buf) {
        if (b.length > Short.MAX_VALUE) {
            throw new OverflowPacketException(String.format("Cannot send byte array longer than Short.MAX_VALUE (got %s bytes)", b.length));
        }

        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

    public byte[] toArray(ByteBuf buf) {
        byte[] ret = new byte[buf.readableBytes()];
        buf.readBytes(ret);

        return ret;
    }

    public byte[] readArray(ByteBuf buf) {
        return readArray(buf, buf.readableBytes());
    }

    public byte[] readArray(ByteBuf buf, int limit) {
        int len = readVarInt(buf);

        if (len > limit) {
            throw new OverflowPacketException(String.format("Cannot receive byte array longer than %s (got %s bytes)", limit, len));
        }

        byte[] ret = new byte[len];
        buf.readBytes(ret);

        return ret;
    }

    public void writeStringArray(List<String> s, ByteBuf buf) {
        writeVarInt(s.size(), buf);
        for (String str : s) {
            writeString(str, buf);
        }
    }

    public List<String> readStringArray(ByteBuf buf) {
        int len = readVarInt(buf);

        List<String> ret = new ArrayList<>(len);

        for (int i = 0; i < len; i++) {
            ret.add(readString(buf));
        }

        return ret;
    }

    public void writeBoolean(boolean flag, ByteBuf buf) {
        buf.writeByte(flag ? 1 : 0);
    }

    public boolean readBoolean(ByteBuf buf) {
        return buf.readByte() == 1;
    }

    public long readVarLong(ByteBuf buf) {
        int numRead = 0;
        long result = 0;

        byte read;

        do {
            read = buf.readByte();

            int value = (read & 0b01111111);
            result |= ((long) value << (7 * numRead));

            numRead++;

            if (numRead > 10) {
                throw new RuntimeException("VarLong is too big");
            }

        } while ((read & 0b10000000) != 0);

        return result;
    }

    public void writeVarLong(long value, ByteBuf buf) {
        do {
            byte temp = (byte) (value & 0b01111111);

            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;

            if (value != 0) {
                temp |= 0b10000000;
            }

            buf.writeByte(temp);
        } while (value != 0);
    }

    public int readVarInt(ByteBuf input) {
        return readVarInt(input, 5);
    }

    public int readVarInt(ByteBuf input, int maxBytes) {
        int result = 0;
        int numRead = 0;

        byte read;

        do {
            read = input.readByte();
            result |= (read & 127) << numRead++ * 7;

            if (numRead > maxBytes) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 128) == 128);

        return result;
    }

    public void writeVarInt(int value, ByteBuf output) {
        while ((value & -128) != 0) {
            output.writeByte(value & 127 | 128);
            value >>>= 7;
        }

        output.writeByte(value);
    }

    public int getVarIntSize(int input) {
        return (input & 0xFFFFFF80) == 0
                ? 1 : (input & 0xFFFFC000) == 0
                ? 2 : (input & 0xFFE00000) == 0
                ? 3 : (input & 0xF0000000) == 0
                ? 4 : 5;
    }

    public void writeEnum(Enum en, ByteBuf buf) {
        writeVarInt(en.ordinal(), buf);
    }

    public <T> T readEnum(Class<T> clazz, ByteBuf buf) {
        return clazz.getEnumConstants()[readVarInt(buf)];
    }

    public int readVarShort(ByteBuf buf) {
        int low = buf.readUnsignedShort();
        int high = 0;

        if ((low & 0x8000) != 0) {
            low = low & 0x7FFF;
            high = buf.readUnsignedByte();
        }

        return ((high & 0xFF) << 15) | low;
    }

    public void writeVarShort(ByteBuf buf, int toWrite) {
        int low = toWrite & 0x7FFF;
        int high = (toWrite & 0x7F8000) >> 15;

        if (high != 0) {
            low = low | 0x8000;
        }

        buf.writeShort(low);

        if (high != 0) {
            buf.writeByte(high);
        }
    }

    public void writeUUID(UUID value, ByteBuf output) {
        output.writeLong(value.getMostSignificantBits());
        output.writeLong(value.getLeastSignificantBits());
    }

    public UUID readUUID(ByteBuf input) {
        return new UUID(input.readLong(), input.readLong());
    }

}
