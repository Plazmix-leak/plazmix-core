package net.plazmix.core.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Setter;
import net.plazmix.core.protocol.*;

public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {

    @Setter
    private Protocol protocol = Protocol.HANDSHAKE;

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet<?> packet, ByteBuf out) throws Exception {
        int id = protocol.TO_SERVER.getPacketId(packet);

        if (id < 0) {
            throw new EncoderException(String.format("Unable to encode bad packet id %s", id));
        }

        BufferedQuery.writeVarInt(id, out);
        packet.writePacket(out);
    }
}