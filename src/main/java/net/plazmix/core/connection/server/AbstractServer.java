package net.plazmix.core.connection.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.MinecraftVersion;
import net.plazmix.core.api.event.impl.PlayerServerRedirectEvent;
import net.plazmix.core.api.event.impl.ProtocolPacketHandleEvent;
import net.plazmix.core.api.event.impl.ServerConnectedEvent;
import net.plazmix.core.api.event.impl.ServerDisconnectedEvent;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.protocol.client.CPlayerServerRedirectPacket;
import net.plazmix.core.connection.protocol.client.CServerMotdPacket;
import net.plazmix.core.connection.protocol.server.*;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.connection.server.mode.ServerMode;
import net.plazmix.core.protocol.ChannelWrapper;
import net.plazmix.core.protocol.Packet;
import net.plazmix.core.protocol.handler.BothHandler;

import java.net.InetSocketAddress;
import java.util.Collection;

@Getter
@Setter
@Log4j2
@AllArgsConstructor
public abstract class AbstractServer implements BothHandler {

    protected final String name;
    protected String motd;

    protected final InetSocketAddress inetSocketAddress;

    protected final MinecraftVersion minecraftVersion;
    protected final ChannelWrapper channelWrapper;


    @Override
    public void channelActive(ChannelWrapper wrapper) {
        PlazmixCore.getInstance().getEventManager().callEvent(new ServerConnectedEvent(this));
    }

    @Override
    public void channelInactive() {
        disconnect();

        log.info("[Server] {} ({}) was disconnected", name, inetSocketAddress.toString());
        sendOnlineUpdatePacket();

        PlazmixCore.getInstance().getEventManager().callEvent(new ServerDisconnectedEvent(this));
    }

    /**
     * Получить название версии ядра сервера.
     */
    public String getMinecraftVersionName() {
        String versionName = getMinecraftVersion().name();
        return versionName.substring(2).replace("_", ".");
    }


    /**
     * Получить список игроков, которые сейчас
     * в сети на данном сервере.
     */
    public abstract Collection<CorePlayer> getOnlinePlayers();


    /**
     * Отправить пакет на сервер.
     *
     * @param packet - пакет
     */
    public void sendPacket(Packet<?> packet) {
        channelWrapper.write(packet);
    }

    public int getOnlineCount() {
        return getOnlinePlayers().size();
    }

    /**
     * Выполнить команду от имени игрока
     * на его сервере
     *
     * @param playerName - ник игрока
     * @param command - команда
     */
    public void dispatchCommand(@NonNull String playerName,
                                @NonNull String command) {

        sendPacket( new SPlayerCommandPacket(playerName, command) );
    }

    /**
     * Выполнить команду от имени консоли сервера
     *
     * @param command - команда
     */
    public void dispatchCommand(@NonNull String command) {
        dispatchCommand("%console%", command);
    }

    /**
     * Удаленно перезагрузить сервер со
     * стандартной причиной
     */
    public void restart() {
        restart("§cСервер перезагружается!");
    }

    /**
     * Удаленно перезагрузить сервер с
     * указанной причиной
     *
     * @param reasonMessage - причина перезагрузки
     */
    public void restart(@NonNull String reasonMessage) {
        sendPacket( new SRestartServerPacket(reasonMessage) );
    }


    /**
     * Получить хост сервера.
     */
    public String getServerHost() {
        return getInetSocketAddress().getHostName();
    }

    /**
     * Получить порт сервера.
     */
    public int getServerPort() {
        return getInetSocketAddress().getPort();
    }

    /**
     * Отсоединить сервер от кора.
     */
    public void disconnect() {
        if (isBungee()) {
            PlazmixCore.getInstance().getServerManager().removeBungee(name);

        } else {

            PlazmixCore.getInstance().getServerManager().removeBukkit(name);
        }
    }

    public boolean isBungee() {
        return ServerMode.isTyped(this, ServerMode.BUNGEE);
    }

    @Override
    public void handle(Packet msg) throws Exception {
        msg.handle(this);

        ProtocolPacketHandleEvent packetHandleEvent = new ProtocolPacketHandleEvent(this, msg);
        PlazmixCore.getInstance().getEventManager().callEvent(packetHandleEvent);
    }

    public void handle(@NonNull CPlayerServerRedirectPacket packet) {
        CorePlayer corePlayer = PlazmixCore.getInstance().getPlayer(packet.getPlayerName());
        BukkitServer bukkitServer = PlazmixCore.getInstance().getBukkitServer(packet.getServerName());

        if (corePlayer == null || bukkitServer == null) {
            return;
        }

        if (corePlayer.getBukkitServer() != null && corePlayer.getBukkitServer().getName().equalsIgnoreCase(packet.getServerName())) {
            return;
        }

        PlayerServerRedirectEvent redirectEvent = new PlayerServerRedirectEvent(corePlayer, bukkitServer, corePlayer.getBukkitServer());
        PlazmixCore.getInstance().getEventManager().callEvent(redirectEvent);

        if (redirectEvent.isCancelled()) {
            return;
        }

        if (packet.isRedirect()) {

            // TODO - 30.10.2021 - Просто попробуем так...
            corePlayer.connectToServer(bukkitServer);
        }

        corePlayer.setBukkitServer(bukkitServer);
        corePlayer.getPlayerOfflineData().setLastServerName(bukkitServer.getName());

        log.info("[Player] " + corePlayer.getName() + " redirected to the server " + bukkitServer.getName());

        // Get player data response
        sendPlayerRequestPackets(corePlayer, bukkitServer);
        sendOnlineUpdatePacket();
    }

    public void handle(@NonNull CServerMotdPacket packet) {
        setMotd(packet.getMotd());
    }


    protected void sendPlayerRequestPackets(@NonNull CorePlayer corePlayer, @NonNull BukkitServer bukkitServer) {
        // bukkitServer.sendPacket(new SPlayerLevelUpdatePacket(corePlayer.getName(), LevelingSqlHandler));

        bukkitServer.sendPacket(new SPlayerLocaleUpdatePacket(corePlayer.getName(), corePlayer.getLanguageType().ordinal()));
        bukkitServer.sendPacket(new SPlayerEconomyUpdatePacket(corePlayer.getName(), corePlayer.getCoins(), corePlayer.getPlazma()));

        bukkitServer.sendPacket(new SPlayerGroupUpdatePacket(corePlayer.getName(), corePlayer.getGroup()));
    }

    protected void sendOnlineUpdatePacket() {
        PlazmixCore.getInstance().broadcastPacket(new SGlobalOnlinePacket());
    }

}
