package net.plazmix.core.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;
import lombok.NonNull;
import net.plazmix.core.protocol.BufferedQuery;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

// TODO Optimize
public class PacketCompressor extends ByteToMessageCodec<ByteBuf> {

    private static final int MAX_SIZE = 2097152;
    public static final int DEFAULT_THRESHOLD = 256;

    private final int threshold;

    private final Inflater inflater;
    private final Deflater deflater;

    public PacketCompressor() {
        this(DEFAULT_THRESHOLD);
    }

    public PacketCompressor(int threshold) {
        this.inflater = new Inflater();
        this.deflater = new Deflater();

        this.threshold = threshold;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf from, ByteBuf to) {
        compressBuffer(deflater, from, to);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        if (buf.readableBytes() != 0) {
            final int i = BufferedQuery.readVarInt(buf);

            if (i == 0) {
                out.add(buf.readRetainedSlice(buf.readableBytes()));
            } else {
                if (i < this.threshold) {
                    throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + this.threshold);
                }

                if (i > MAX_SIZE) {
                    throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of " + MAX_SIZE);
                }

                // TODO optimize to do not initialize arrays each time

                byte[] input = new byte[buf.readableBytes()];
                buf.readBytes(input);

                inflater.setInput(input);
                byte[] output = new byte[i];
                inflater.inflate(output);
                inflater.reset();

                out.add(Unpooled.wrappedBuffer(output));
            }
        }
    }

    public void compressBuffer(@NonNull Deflater deflater, @NonNull ByteBuf packetBuffer, @NonNull ByteBuf compressionTarget) {
        final int packetLength = packetBuffer.readableBytes();
        final boolean compression = packetLength > threshold;

        BufferedQuery.writeVarInt(compression ? packetLength : 0, compressionTarget);

        if (compression) {
            compress(deflater, packetBuffer, compressionTarget);
        } else {
            compressionTarget.writeBytes(packetBuffer);
        }
    }

    private void compress(@NonNull Deflater deflater, @NonNull ByteBuf uncompressed, @NonNull ByteBuf compressed) {
        deflater.setInput(uncompressed.nioBuffer().array());
        deflater.finish();

        while (!deflater.finished()) {
            ByteBuffer nioBuffer = compressed.nioBuffer(compressed.writerIndex(), compressed.writableBytes());
            compressed.writerIndex(deflater.deflate(nioBuffer.array()) + compressed.writerIndex());

            if (compressed.writableBytes() == 0) {
                compressed.ensureWritable(8192);
            }
        }

        deflater.reset();
    }

}