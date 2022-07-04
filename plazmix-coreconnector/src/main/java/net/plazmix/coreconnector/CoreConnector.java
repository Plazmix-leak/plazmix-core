package net.plazmix.coreconnector;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.plazmix.core.connection.protocol.server.SAchievementRegisterPacket;
import net.plazmix.core.protocol.handler.BossHandler;
import net.plazmix.core.protocol.ChannelWrapper;
import net.plazmix.core.protocol.Packet;
import net.plazmix.core.protocol.Protocol;
import net.plazmix.core.protocol.pipeline.PipelineUtil;
import net.plazmix.core.protocol.handshake.Handshake;
import net.plazmix.coreconnector.core.CoreManager;
import net.plazmix.coreconnector.module.BaseModuleManager;
import net.plazmix.coreconnector.module.BaseServerModule;
import net.plazmix.coreconnector.module.type.NetworkModule;
import net.plazmix.coreconnector.module.type.coloredprefix.ColoredPrefixModule;
import net.plazmix.coreconnector.module.type.economy.EconomyModule;
import net.plazmix.coreconnector.module.type.friend.FriendsModule;
import net.plazmix.coreconnector.module.type.group.GroupModule;
import net.plazmix.coreconnector.module.type.party.PartyModule;
import net.plazmix.coreconnector.module.type.rewards.RewardsModule;
import net.plazmix.coreconnector.module.type.skin.SkinsModule;
import net.plazmix.coreconnector.mysql.MysqlConnection;
import net.plazmix.coreconnector.mysql.MysqlDatabaseConnection;
import net.plazmix.coreconnector.protocol.client.*;
import net.plazmix.coreconnector.protocol.server.*;
import net.plazmix.coreconnector.utility.CoreReconnector;
import net.plazmix.coreconnector.utility.server.ServerMode;
import net.plazmix.coreconnector.utility.server.ServerSubMode;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
public final class CoreConnector {

    @Setter private ChannelWrapper channelWrapper;
    @Setter private boolean connected;


// ============================================================================ //

    private boolean bungee;

    private String serverName;
    private String serverHost;
    private String serverMotd;

    private int serverPort;
    private int versionId;

    @Setter
    private int globalOnline;

    @Deprecated
    private final Map<String, Integer> serversOnlineMap = new ConcurrentHashMap<>();

// ============================================================================ //

    private final CoreManager coreManager = new CoreManager();

    private final MysqlDatabaseConnection mysqlConnection
            = MysqlConnection.createMysqlConnection("135.181.39.144", "plazma", "QcKtPb6zrhxgScaThzw7Ckafd2rqJ8DDUUeXZccPRAmesdxsBRPsP4qeYNz8q8xHV6P7QL2ydWfpZ8MSPy2t5", 3306)
            .createDatabaseScheme("game_new", true);

// ================================================================================================================== //

    @Getter
    private static final CoreConnector instance = new CoreConnector();

    public static NetworkModule getNetworkInstance() {
        return NetworkModule.getInstance();
    }

// ================================================================================================================== //


    public void createConnection(boolean bungee, @NonNull String serverName, @NonNull String serverMotd, @NonNull String serverHost, int serverPort, int versionId) {

        this.bungee = bungee;
        this.serverName = serverName;
        this.serverMotd = serverMotd;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.versionId = versionId;

        // Create connection to Core.
        registerPackets();
        tryConnectionToCore();

        // Register server modules.
        registerModules();
    }

    public void tryConnectionToCore() {
        if (channelWrapper != null) {

            channelWrapper.getChannel().closeFuture();
            channelWrapper.close(null);
        }

        InetSocketAddress address = new InetSocketAddress("135.181.39.144", 5505);

        ChannelFutureListener listener = future -> {
            setConnected(future.isSuccess());

            if (future.isSuccess()) {
                CoreReconnector.disableReconnect();

                future.channel().writeAndFlush(new Handshake(serverName, serverMotd, new InetSocketAddress(serverHost, serverPort), versionId, bungee));
            }

            else {
                CoreReconnector.enableReconnect();
            }
        };

        new Bootstrap()
                .handler(new ChannelInitializer<Channel>() {

                    @Override
                    protected void initChannel(Channel ch) {
                        PipelineUtil.initPipeline(ch);

                        ch.pipeline().get(BossHandler.class).getHandler().addHandler(new CoreHandshake());
                    }
                })

                .channel(PipelineUtil.getClientChannel())
                .group(PipelineUtil.getEventLoopGroup(2))

                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)

                .remoteAddress(address)
                .connect()
                .addListener(listener);
    }

    public void sendPacket(@NonNull Packet<?> packet) {
        channelWrapper.write(packet);
    }

    private void registerPackets() {

        // Handshake
        Protocol.HANDSHAKE.registerAll(0x00, Handshake.class, Handshake::new);

        // Play
        Protocol.PLAY.TO_SERVER.registerPacket(0x00, SPlayerConnectPacket.class, SPlayerConnectPacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x01, SInventoryClosePacket.class, SInventoryClosePacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x02, SInventoryInteractPacket.class, SInventoryInteractPacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x03, SPlayerLoginPacket.class, SPlayerLoginPacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x04, SPlayerChatPacket.class, SPlayerChatPacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x05, SPlayerCommandPacket.class, SPlayerCommandPacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x06, SPlayerLeavePacket.class, SPlayerLeavePacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x07, SServerMotdPacket.class, SServerMotdPacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x08, SPlayerLevelUpdatePacket.class, SPlayerLevelUpdatePacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x09, SPlayerEconomyUpdatePacket.class, SPlayerEconomyUpdatePacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x11, SPlayerServerRedirectPacket.class, SPlayerServerRedirectPacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x12, SPlayerStatisticPacket.class, SPlayerStatisticPacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x13, SAchievementRegisterPacket.class, SAchievementRegisterPacket::new);

        Protocol.PLAY.TO_CLIENT.registerPacket(0x00, CBukkitCommandsPacket.class, CBukkitCommandsPacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x01, CInventoryClosePacket.class, CInventoryClosePacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x02, CBungeeServerCreatePacket.class, CBungeeServerCreatePacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x03, CInventoryOpenPacket.class, CInventoryOpenPacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x04, CPlayerChatPacket.class, CPlayerChatPacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x05, CPlayerCommandPacket.class, CPlayerCommandPacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x06, CPlayerKickPacket.class, CPlayerKickPacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x07, CPlayerMutePacket.class, CPlayerMutePacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x08, CPlayerLevelUpdatePacket.class, CPlayerLevelUpdatePacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x09, CPlayerEconomyUpdatePacket.class, CPlayerEconomyUpdatePacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x11, CPlayerServerRedirectPacket.class, CPlayerServerRedirectPacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x12, CPlayerGroupUpdatePacket.class, CPlayerGroupUpdatePacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x13, CRestartServerPacket.class, CRestartServerPacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x14, CInventoryClearPacket.class, CInventoryClearPacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x15, CGlobalOnlinePacket.class, CGlobalOnlinePacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x16, CPlayerLocaleUpdatePacket.class, CPlayerLocaleUpdatePacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x17, CPlayerTagPrefixUpdatePacket.class, CPlayerTagPrefixUpdatePacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x18, CPlayerTagSuffixUpdatePacket.class, CPlayerTagSuffixUpdatePacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x19, CPlayerUnmutePacket.class, CPlayerUnmutePacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x20, CLanguagesReloadPacket.class, CLanguagesReloadPacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x21, CInventorySetItemPacket.class, CInventorySetItemPacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x22, CPlayerAuthCompletePacket.class, CPlayerAuthCompletePacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x23, CModuleDataUpdatePacket.class, CModuleDataUpdatePacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x24, CPlayerSoundPacket.class, CPlayerSoundPacket::new);
    }

    private void registerModules() {
        if (!bungee) {
            BaseModuleManager.INSTANCE.registerModule(new ColoredPrefixModule());
            BaseModuleManager.INSTANCE.registerModule(new RewardsModule());
        }

        BaseModuleManager.INSTANCE.registerModule(new EconomyModule());
        BaseModuleManager.INSTANCE.registerModule(new FriendsModule());
        BaseModuleManager.INSTANCE.registerModule(new GroupModule());
        BaseModuleManager.INSTANCE.registerModule(new PartyModule());
        BaseModuleManager.INSTANCE.registerModule(new SkinsModule());

        BaseModuleManager.INSTANCE.registerModule(new NetworkModule());
    }

    public int getOnline(String... serverNames) {
        int onlineCount = 0;

        for (String serverName : serverNames) {
            if (isServerConnected(serverName)) {

                String correctName = serversOnlineMap.keySet().stream().map(String::toLowerCase)
                        .filter(s -> s.equalsIgnoreCase(serverName))
                        .findFirst()
                        .orElse(null);

                if (correctName != null) {
                    onlineCount += serversOnlineMap.get(correctName);
                }
            }
        }

        return onlineCount;
    }

    public <T extends BaseServerModule> T findModule(@NonNull Class<T> moduleType) {
        return BaseModuleManager.INSTANCE.find(moduleType);
    }

    public BaseServerModule findModule(@NonNull String module) {
        return BaseModuleManager.INSTANCE.find(module);
    }

    @Deprecated
    public int getOnlineByPrefix(@NonNull String serverPrefix) {
        return serversOnlineMap.keySet().stream().filter(s -> s.toLowerCase(Locale.ROOT).startsWith(serverPrefix.toLowerCase()))
                .mapToInt(serversOnlineMap::get).sum();
    }

    @Deprecated
    public int getOnlineByPrefixes(String... serverPrefixes) {
        int onlineCount = 0;

        for (String serverPrefix : serverPrefixes) {
            if (getConnectedServersCount(serverPrefix) <= 0) {
                continue;
            }

            onlineCount += getOnlineByPrefix(serverPrefix);
        }

        return onlineCount;
    }

    @Deprecated
    public int getConnectedServersCount(@NonNull String serverPrefix) {
        return getConnectedServers(serverPrefix).size();
    }

    @Deprecated
    public Collection<String> getConnectedServers(@NonNull String serverPrefix) {
        return serversOnlineMap.keySet().stream()
                .filter(s -> s.toLowerCase().startsWith(serverPrefix.toLowerCase()) && isServerConnected(s))
                .collect(Collectors.toSet());
    }

    @Deprecated
    public String getBestServer(boolean checkOnline, @NonNull ServerSubMode serverSubMode) {
        Collection<String> connectedServers = getConnectedServers(serverSubMode.getSubPrefix());

        if (connectedServers.isEmpty()) {
            return null;
        }

        if (checkOnline) {
            return connectedServers.stream().max(Comparator.comparing(this::getOnline))
                    .orElse(null);

        } else {

            return connectedServers.stream().findFirst().orElse(null);
        }
    }

    @Deprecated
    public String getBestServer(@NonNull ServerSubMode serverSubMode) {
        return getBestServer(true, serverSubMode);
    }

    @Deprecated
    public boolean isServerConnected(@NonNull String serverName) {
        return serversOnlineMap.keySet().stream().map(String::toLowerCase).collect(Collectors.toList()).contains(serverName.toLowerCase());
    }

    @Deprecated
    public int getGlobalOnline() {
        return ServerMode.BUNGEE.getOnline();
    }

    public void setMotd(@NonNull String motd) {
        sendPacket(new SServerMotdPacket(this.serverMotd = motd));
    }

}
