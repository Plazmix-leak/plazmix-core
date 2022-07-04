package net.plazmix.core.protocol.pipeline;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.experimental.UtilityClass;
import net.plazmix.core.protocol.codec.PacketCompressor;
import net.plazmix.core.protocol.codec.PacketFramer;
import net.plazmix.core.protocol.handler.AbstractPacketHandler;
import net.plazmix.core.protocol.handler.BossHandler;
import net.plazmix.core.protocol.codec.PacketDecoder;
import net.plazmix.core.protocol.codec.PacketEncoder;
import net.plazmix.core.protocol.metrics.PerformanceMetrics;
import net.plazmix.core.protocol.metrics.TrafficCounter;

@UtilityClass
public class PipelineUtil {

    public final String TRAFFIC_COUNTER     = "traffic-counter";

    public final String PACKET_FRAMER       = "packet-framer";
    public final String PACKET_COMPRESSOR   = "packet-compress";

    public final String PACKET_DECODER      = "packet-decoder";
    public final String PACKET_ENCODER      = "packet-encoder";

    public final String PACKET_HANDLER      = "boss-handler";


    public void initPipeline(Channel ch) {
        PerformanceMetrics.startMetrics();

        // Init channel config options.
        ch.config().setOption(ChannelOption.IP_TOS, 0x18);
        ch.config().setOption(ChannelOption.SO_SNDBUF, 262_144);
        ch.config().setOption(ChannelOption.TCP_NODELAY, true);
        ch.config().setOption(ChannelOption.SO_KEEPALIVE, true);
        ch.config().setOption(ChannelOption.TCP_NODELAY, true);

        ch.config().setAllocator(ByteBufAllocator.DEFAULT);

        // Init channel handler.
        BossHandler bossHandler = new BossHandler();
        bossHandler.setHandler(new AbstractPacketHandler());

        // Init bytes handlers.
        ch.pipeline().addLast(PACKET_FRAMER, new PacketFramer());
        // ch.pipeline().addLast(PACKET_COMPRESSOR, new PacketCompressor());

        ch.pipeline().addLast(PACKET_DECODER, new PacketDecoder());
        ch.pipeline().addLast(PACKET_ENCODER, new PacketEncoder());

        ch.pipeline().addLast(TRAFFIC_COUNTER, new TrafficCounter());

        ch.pipeline().addLast(PACKET_HANDLER, bossHandler);
    }

    public EventLoopGroup getEventLoopGroup(int threads) {
        return Epoll.isAvailable() ? new EpollEventLoopGroup(threads) : new NioEventLoopGroup(threads);
    }

    public ChannelFactory<ServerSocketChannel> getChannelFactory() {
        return Epoll.isAvailable() ? EpollServerSocketChannel::new : NioServerSocketChannel::new;
    }

    public Class<? extends Channel> getClientChannel() {
        return Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class;
    }
}
