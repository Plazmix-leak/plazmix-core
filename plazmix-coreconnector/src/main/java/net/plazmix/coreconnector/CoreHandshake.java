package net.plazmix.coreconnector;

import net.plazmix.core.protocol.ChannelWrapper;
import net.plazmix.core.protocol.handshake.HandshakeHandler;
import net.plazmix.core.protocol.handshake.Handshake;
import net.plazmix.coreconnector.protocol.BukkitHandler;
import net.plazmix.coreconnector.protocol.BungeeHandler;

public class CoreHandshake implements HandshakeHandler {

    private ChannelWrapper channelWrapper;

    @Override
    public void channelActive(ChannelWrapper channelWrapper) {
        this.channelWrapper = channelWrapper;
        CoreConnector.getInstance().setChannelWrapper(channelWrapper);
    }

    @Override
    public void handle(Handshake handshakePacket) {
        // channelWrapper.setCompression(256);
        channelWrapper.handleConnect(handshakePacket.isBungee() ? new BungeeHandler() : new BukkitHandler(), this);
    }

}
