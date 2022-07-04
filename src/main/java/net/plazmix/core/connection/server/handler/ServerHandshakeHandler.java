package net.plazmix.core.connection.server.handler;

import lombok.extern.log4j.Log4j2;
import net.plazmix.core.api.MinecraftVersion;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.connection.server.AbstractServer;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.connection.server.impl.BungeeServer;
import net.plazmix.core.protocol.ChannelWrapper;
import net.plazmix.core.protocol.handshake.HandshakeHandler;
import net.plazmix.core.protocol.handshake.Handshake;

@Log4j2
public class ServerHandshakeHandler implements HandshakeHandler {

    private ChannelWrapper wrapper;

    @Override
    public void channelActive(ChannelWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public void handle(Handshake handshakePacket) {
        String name = handshakePacket.getName();

        AbstractServer server = handshakePacket.isBungee()
                ? new BungeeServer(name, handshakePacket.getInetSocketAddress(), wrapper)
                : new BukkitServer(name, handshakePacket.getMotd(), handshakePacket.getInetSocketAddress(), MinecraftVersion.getByVersionId(handshakePacket.getVersionId()), wrapper);

        log.info(ChatColor.YELLOW + "[Handshake] Connected -> {}, {}", server.getName(), handshakePacket.getInetSocketAddress().toString());

        wrapper.write(new Handshake(handshakePacket.getName(), handshakePacket.getMotd(), handshakePacket.getInetSocketAddress(), handshakePacket.getVersionId(), handshakePacket.isBungee()));
        wrapper.handleConnect(server, this);
    }

}
