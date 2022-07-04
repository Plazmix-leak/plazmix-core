package net.plazmix.core.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import lombok.Setter;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;
import net.plazmix.core.protocol.Protocol;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    @Setter
    private Protocol protocol = Protocol.HANDSHAKE;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            int id = BufferedQuery.readVarInt(in);

            if (id < 0) {
                throw new IllegalStateException(String.format("Bad packet id %s", id));
            }

            Packet<?> packet = protocol.TO_CLIENT.getPacket(id);

            if (packet == null) {
                throw new DecoderException(String.format("Unable to decode, packet id %s not found packet", id));
            }

            try {
                packet.readPacket(in);
            } catch (Exception e) {
                throw new DecoderException(String.format("Unable to decode packet %s", packet), e);
            }

            out.add(packet);

        } finally {
            in.skipBytes(in.readableBytes());
        }
    }

}
