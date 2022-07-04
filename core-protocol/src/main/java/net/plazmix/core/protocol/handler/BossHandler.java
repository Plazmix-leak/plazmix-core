package net.plazmix.core.protocol.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.plazmix.core.protocol.ChannelWrapper;
import net.plazmix.core.protocol.Packet;

@ChannelHandler.Sharable
public class BossHandler extends SimpleChannelInboundHandler<Packet<?>> {

    @Getter
    @Setter
    private PacketHandler handler;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet<?> msg) throws Exception {
        handler.handle(msg);
    }

    @Override
    public void channelActive(@NonNull ChannelHandlerContext ctx) {
        handler.channelActive(new ChannelWrapper(ctx.channel()));
    }

    @Override
    public void channelInactive(@NonNull ChannelHandlerContext ctx) {
        handler.channelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        handler.handle(cause);
    }
}
