package net.plazmix.core;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.util.internal.SocketUtils;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import net.plazmix.core.api.command.impl.ShutdownCommand;
import org.apache.logging.log4j.Logger;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.command.CommandManager;
import net.plazmix.core.api.event.EventManager;
import net.plazmix.core.api.inventory.BaseInventoryListener;
import net.plazmix.core.api.inventory.BaseInventoryManager;
import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.ModuleManager;
import net.plazmix.core.api.module.command.ModuleCommand;
import net.plazmix.core.api.module.execute.ModuleExecuteListener;
import net.plazmix.core.api.module.execute.ModuleExecuteQuery;
import net.plazmix.core.api.module.execute.ModuleExecuteType;
import net.plazmix.core.api.mysql.MysqlConnection;
import net.plazmix.core.api.mysql.MysqlDatabaseConnection;
import net.plazmix.core.api.scheduler.SchedulerManager;
import net.plazmix.core.common.coloredprefix.ColoredPrefixListener;
import net.plazmix.core.common.coloredprefix.PrefixCommand;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.group.GroupManager;
import net.plazmix.core.common.group.listener.PlayerGroupListener;
import net.plazmix.core.common.language.LanguageType;
import net.plazmix.core.common.pass.SpacePassSqlHandler;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.player.PlayerManager;
import net.plazmix.core.connection.player.offline.OfflineMessageListener;
import net.plazmix.core.connection.protocol.client.*;
import net.plazmix.core.connection.protocol.server.*;
import net.plazmix.core.connection.server.AbstractServer;
import net.plazmix.core.connection.server.handler.ServerHandshakeHandler;
import net.plazmix.core.connection.server.ServerManager;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.connection.server.impl.BungeeServer;
import net.plazmix.core.connection.server.mode.ServerSubMode;
import net.plazmix.core.protocol.handler.BossHandler;
import net.plazmix.core.protocol.ChannelWrapper;
import net.plazmix.core.protocol.Packet;
import net.plazmix.core.protocol.Protocol;
import net.plazmix.core.protocol.pipeline.PipelineUtil;
import net.plazmix.core.protocol.handshake.Handshake;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/*  Leaked by https://t.me/leak_mine
    - Все слитые материалы вы используете на свой страх и риск.

    - Мы настоятельно рекомендуем проверять код плагинов на хаки!
    - Список софта для декопиляции плагинов:
    1. Luyten (последнюю версию можно скачать можно тут https://github.com/deathmarine/Luyten/releases);
    2. Bytecode-Viewer (последнюю версию можно скачать можно тут https://github.com/Konloch/bytecode-viewer/releases);
    3. Онлайн декомпиляторы https://jdec.app или http://www.javadecompilers.com/

    - Предложить свой слив вы можете по ссылке @leakmine_send_bot или https://t.me/leakmine_send_bot
*/

@Getter
@Setter
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlazmixCore {

    @Getter
    private static final PlazmixCore instance                         = new PlazmixCore();

    private boolean running;


    // Ебать я гений просто, какой я ахуенный
    private final Logger logger                                     = log;

    private final EventManager eventManager                         = new EventManager();
    private final SchedulerManager schedulerManager                 = new SchedulerManager();

    private final PlayerManager playerManager                       = new PlayerManager();
    private final ServerManager serverManager                       = new ServerManager();
    private final CommandManager commandManager                     = new CommandManager();
    private final BaseInventoryManager inventoryManager             = new BaseInventoryManager();

    private final ModuleManager moduleManager                       = new ModuleManager();


    private final File modulesFolder                                = new File("modules");
    private final File coreFolder                                   = new File("");


    private final MysqlDatabaseConnection mysqlConnection
            = MysqlConnection.createMysqlConnection("135.181.39.144", "plazma", "QcKtPb6zrhxgScaThzw7Ckafd2rqJ8DDUUeXZccPRAmesdxsBRPsP4qeYNz8q8xHV6P7QL2ydWfpZ8MSPy2t5", 3306)
            .createDatabaseScheme("game_new", true);

    private ChannelWrapper channelWrapper;
    private long startSessionMillis;


    /**
     * Запустить сервер кора через Netty Bootstrap, зарегистрировать
     * необходимые для его работы команды и пакеты по указанному
     * адресу
     */
    public void launch() {
        setRunning(true);
        bind();
    }

    public void shutdown() {
        setRunning(false);

        moduleManager.handleAllModules(CoreModule::disableModule);

        if (channelWrapper != null) {
            channelWrapper.close(null);
        }

        System.exit(0);
    }

    @SneakyThrows
    private void bind() {
        createMysqlTables();
        log.info(ChatColor.YELLOW + "[MySQL] was successfully connected on: " + mysqlConnection.getConnection().getMetaData().getURL());

        for (LanguageType languageType : LanguageType.VALUES) {
            log.info(ChatColor.YELLOW + "[LanguageManager] Resources for " + languageType + " lang has been initialized!");

            languageType.getResource().initResources();
        }

        InetSocketAddress address = SocketUtils.socketAddress("135.181.39.144", 5505);
        ChannelFutureListener listener = future -> {

            if (!future.isSuccess()) {
                log.warn(ChatColor.RED + "[Channel] Could not bind to host " + address.toString());

            } else {

                setChannelWrapper(new ChannelWrapper(future.channel()));
                SpacePassSqlHandler.INSTANCE.cleanDatabase();

                log.info(ChatColor.GREEN + "[Channel] Listening on " + address.toString());

                // Register system management.
                registerCommands();
                registerListeners();

                activateModules();

                registerPackets();

                inventoryManager.startInventoryUpdateTask();
            }

            this.startSessionMillis = System.currentTimeMillis();
        };

        new ServerBootstrap()
                .childHandler(new ChannelInitializer<Channel>() {

                    @Override
                    protected void initChannel(Channel ch) {
                        PipelineUtil.initPipeline(ch);

                        ch.pipeline().get(BossHandler.class).getHandler().addHandler(new ServerHandshakeHandler());
                    }
                })

                .channelFactory(PipelineUtil.getChannelFactory())
                .group(PipelineUtil.getEventLoopGroup(1), PipelineUtil.getEventLoopGroup(4))

                .bind(address)
                .addListener(listener);
    }

    private void registerPackets() {

        // Handshake
        Protocol.HANDSHAKE.registerAll(0x00, Handshake.class, Handshake::new);

        // Play
        Protocol.PLAY.TO_CLIENT.registerPacket(0x00, CPlayerConnectPacket.class, CPlayerConnectPacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x01, CInventoryClosePacket.class, CInventoryClosePacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x02, CInventoryInteractPacket.class, CInventoryInteractPacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x03, CPlayerLoginPacket.class, CPlayerLoginPacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x04, CPlayerChatPacket.class, CPlayerChatPacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x05, CPlayerCommandPacket.class, CPlayerCommandPacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x06, CPlayerLeavePacket.class, CPlayerLeavePacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x07, CServerMotdPacket.class, CServerMotdPacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x08, CPlayerLevelUpdatePacket.class, CPlayerLevelUpdatePacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x09, CPlayerEconomyUpdatePacket.class, CPlayerEconomyUpdatePacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x11, CPlayerServerRedirectPacket.class, CPlayerServerRedirectPacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x12, CPlayerStatisticPacket.class, CPlayerStatisticPacket::new);
        Protocol.PLAY.TO_CLIENT.registerPacket(0x13, CAchievementRegisterPacket.class, CAchievementRegisterPacket::new);

        Protocol.PLAY.TO_SERVER.registerPacket(0x00, SBukkitCommandsPacket.class, SBukkitCommandsPacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x01, SInventoryClosePacket.class, SInventoryClosePacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x02, SBungeeServerCreatePacket.class, SBungeeServerCreatePacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x03, SInventoryOpenPacket.class, SInventoryOpenPacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x04, SPlayerChatPacket.class, SPlayerChatPacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x05, SPlayerCommandPacket.class, SPlayerCommandPacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x06, SPlayerKickPacket.class, SPlayerKickPacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x07, SPlayerMutePacket.class, SPlayerMutePacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x08, SPlayerLevelUpdatePacket.class, SPlayerLevelUpdatePacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x09, SPlayerEconomyUpdatePacket.class, SPlayerEconomyUpdatePacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x11, SPlayerServerRedirectPacket.class, SPlayerServerRedirectPacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x12, SPlayerGroupUpdatePacket.class, SPlayerGroupUpdatePacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x13, SRestartServerPacket.class, SRestartServerPacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x14, SInventoryClearPacket.class, SInventoryClearPacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x15, SGlobalOnlinePacket.class, SGlobalOnlinePacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x16, SPlayerLocaleUpdatePacket.class, SPlayerLocaleUpdatePacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x17, SPlayerTagPrefixUpdatePacket.class, SPlayerTagPrefixUpdatePacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x18, SPlayerTagSuffixUpdatePacket.class, SPlayerTagSuffixUpdatePacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x19, SPlayerUnmutePacket.class, SPlayerUnmutePacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x20, SLanguagesReloadPacket.class, SLanguagesReloadPacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x21, SInventorySetItemPacket.class, SInventorySetItemPacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x22, SPlayerAuthCompletePacket.class, SPlayerAuthCompletePacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x23, SModuleDataUpdatePacket.class, SModuleDataUpdatePacket::new);
        Protocol.PLAY.TO_SERVER.registerPacket(0x24, SPlayerSoundPacket.class, SPlayerSoundPacket::new);
    }

    protected void registerCommands() {
        commandManager.registerCommand(new ShutdownCommand());
        commandManager.registerCommand(new ModuleCommand());
        commandManager.registerCommand(new PrefixCommand());

        log.info(ChatColor.WHITE + "[CommandManager] {} commands has been registered", new HashSet<>(commandManager.getCommandMap().values()).size());
    }

    protected void registerListeners() {
        eventManager.registerListener(new BaseInventoryListener());
        eventManager.registerListener(new OfflineMessageListener());
        eventManager.registerListener(new PlayerGroupListener());
        eventManager.registerListener(new ModuleExecuteListener());

        eventManager.registerListener(new ColoredPrefixListener());

        log.info(ChatColor.WHITE + "[EventManager] {} listeners has been registered", eventManager.getListenerHandlers().size());
    }

    @SneakyThrows
    protected void activateModules() {
        if (!modulesFolder.exists()) {
            Files.createDirectory(modulesFolder.toPath());
        }

        moduleManager.loadModules(modulesFolder);

        log.info(ChatColor.WHITE + "[ModuleManager] {} modules has been activated", moduleManager.getModuleMap().size());
    }

    /**
     * Инициализация подключения к базе данных Mysql
     */
    private void createMysqlTables() {
        mysqlConnection.createTable(true, "PlayerIdentifier", "`Id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, `Name` VARCHAR(16) NOT NULL");
        mysqlConnection.createTable(true, "PlayerGroup", "`Id` INT NOT NULL PRIMARY KEY, `Group` INT NOT NULL");
        mysqlConnection.createTable(true, "PlayerAuth", "`Id` INT NOT NULL PRIMARY KEY, `Password` TEXT, `VkId` INT, `Mail` VARCHAR(255), `RegisterDate` TIMESTAMP, `ExpireSessionTime` TIMESTAMP, `RegisterAddress` VARCHAR(50), `LastAddress` VARCHAR(50), `License` BOOLEAN");
        mysqlConnection.createTable(true, "PlayerQuests", "`Id` INT NOT NULL PRIMARY KEY, `QuestId` INT NOT NULL, `TaskId` INT NOT NULL, `TaskJson` LONGTEXT NOT NULL");
        mysqlConnection.createTable(true, "PlayerAchievements", "`Id` INT NOT NULL PRIMARY KEY, `AchievementId` INT NOT NULL, `TaskId` INT NOT NULL, `TaskJson` LONGTEXT NOT NULL");
        mysqlConnection.createTable(true, "PlayerPass", "`Id` INT NOT NULL PRIMARY KEY, `Date` TIMESTAMP, `Experience` INT NOT NULL, `Activation` BOOLEAN NOT NULL");

        mysqlConnection.createTable(true, "PlayerColoredPrefix", "`Id` INT NOT NULL PRIMARY KEY, `Code` VARCHAR(1) NOT NULL");
        mysqlConnection.createTable(true, "PlayerEconomy", "`Id` INT NOT NULL PRIMARY KEY, `Coins` INT NOT NULL, `Golds` INT NOT NULL");
        mysqlConnection.createTable(true, "PlayerEconomyOutlay", "`Id` INT NOT NULL, `EconomyType` TEXT NOT NULL, `Value` INT NOT NULL, `Server` LONGTEXT, `Ip` TEXT, `Datetime` TIMESTAMP NOT NULL");
        mysqlConnection.createTable(true, "PlayerLastOnline", "`Id` INT NOT NULL PRIMARY KEY, `LastServer` VARCHAR(255) NOT NULL, `LastOnline` TIMESTAMP");
        mysqlConnection.createTable(true, "PlayerLanguage", "`Id` INT NOT NULL PRIMARY KEY, `Lang` INT NOT NULL");

        mysqlConnection.createTable(true, "CoreGuilds", "`Id` INT NOT NULL PRIMARY KEY, `Json` LONGTEXT NOT NULL");

        mysqlConnection.createTable(true, "PunishmentData", "`Intruder` INT NOT NULL, `Owner` INT NOT NULL, `Type` INT NOT NULL, `Reason` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL, `Time` TIMESTAMP");
        mysqlConnection.createTable(true, "PunishmentHistory", "`Intruder` INT NOT NULL, `Owner` INT NOT NULL, `Type` INT NOT NULL, `Reason` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL, `Time` TIMESTAMP");
    }

    public long getSessionMillis() {
        return System.currentTimeMillis() - startSessionMillis;
    }

    /**
     * Получить игрока по его номеру
     *
     * @param playerId - номер игрока
     */
    public CorePlayer getPlayer(int playerId) {
        return playerManager.getPlayer(playerId);
    }

    /**
     * Получить игрока по его нику
     *
     * @param playerName - ник игрока
     */
    public CorePlayer getPlayer(@NonNull String playerName) {
        return playerManager.getPlayer(playerName);
    }

    /**
     * Получить offline данные игрока по его нику
     *
     * @param playerName - ник игрока
     */
    public CorePlayer getOfflinePlayer(@NonNull String playerName) {
        return playerManager.getOfflinePlayer(playerName);
    }

    /**
     * Получить список оффлайн игроков по группе
     *
     * @param group - группа
     */
    public Collection<CorePlayer> getOfflinePlayersByGroup(@NonNull Group group) {
        return GroupManager.INSTANCE.getOfflinePlayersByGroup(group);
    }

    public BukkitServer getBukkitServer(@NonNull String serverName) {
        return serverManager.getBukkit(serverName);
    }

    public BungeeServer getBungeeServer(@NonNull String serverName) {
        return serverManager.getBungee(serverName);
    }

    public BungeeServer getBestBungee() {
        return getBungeeServers()
                .stream()

                .min(Comparator.comparingInt(BungeeServer::getOnlineCount))
                .orElse(null);
    }


    public Collection<BukkitServer> getBukkitServers() {
        return serverManager.getBukkitServers().values();
    }

    public Collection<BungeeServer> getBungeeServers() {
        return serverManager.getBungeeServers().values();
    }

    /**
     * Получить подключенный сервер к кору
     * по его названию и типу
     *
     * @param serverPrefix - префикс сервера
     */
    public BukkitServer getRandomServerByPrefix(@NonNull String serverPrefix) {
        Collection<BukkitServer> serversByPrefix = getServersByPrefix(serverPrefix);

        return serversByPrefix.stream()
                .skip((long)(Math.random() * serversByPrefix.size()))
                .findFirst()
                .orElse(null);
    }

    public Collection<CorePlayer> getOnlinePlayers() {
        return playerManager.getOnlinePlayers(player -> true);
    }

    /**
     * Получить отфлитрованный список онлайн игроков
     * по какому-то условию
     *
     * @param playerResponseHandler - условие фильтрования онлайн игроков
     */
    public Collection<CorePlayer> getOnlinePlayers(@NonNull PlayerManager.PlayerResponseHandler playerResponseHandler) {
        return playerManager.getOnlinePlayers(playerResponseHandler);
    }

    public int getGlobalOnline() {
        return getOnlinePlayers().size();
    }

    /**
     * Получить сумму онлайна нескольких серверов
     * по указанному префиксу
     *
     * @param serverPrefix - префикс серверов
     */
    public int getOnlineByServerPrefix(@NonNull String serverPrefix) {
        return serverManager.getOnlineByServerPrefix(serverPrefix);
    }

    public int getConnectedServersCount(@NonNull String serverPrefix) {
        return (int) (getBukkitServers().stream()
                .filter(s -> s.getName().toLowerCase(Locale.ROOT).startsWith(serverPrefix.toLowerCase()))
                .count());
    }

    public Collection<BukkitServer> getConnectedServers(@NonNull String serverPrefix) {
        return getBukkitServers().stream()
                .filter(s -> s.getName().toLowerCase(Locale.ROOT).startsWith(serverPrefix.toLowerCase()))
                .collect(Collectors.toSet());
    }

    public BukkitServer getBestServer(boolean checkOnline, @NonNull ServerSubMode serverSubMode) {
        Collection<BukkitServer> connectedServers = getConnectedServers(serverSubMode.getSubPrefix());

        if (connectedServers.isEmpty()) {
            return null;
        }

        if (checkOnline) {
            return connectedServers.stream().max(Comparator.comparing(BukkitServer::getOnlineCount))
                    .orElse(null);

        } else {

            return connectedServers.stream().findFirst().orElse(null);
        }
    }

    public BukkitServer getBestServer(@NonNull ServerSubMode serverSubMode) {
        return getBestServer(true, serverSubMode);
    }

    /**
     * Получить список нескольких серверов
     * по указанному префиксу
     *
     * @param serverPrefix - префикс серверов
     */
    public Collection<BukkitServer> getServersByPrefix(@NonNull String serverPrefix) {
        return serverManager.getServersByPrefix(serverPrefix);
    }

    /**
     * Отправить offline сообщения
     *
     * @param playerName      - оффлайн игрок
     * @param messageSupplier - обработчик сообщения
     */
    public void sendOfflineMessage(@NonNull String playerName, Supplier<String> messageSupplier) {
        playerManager.sendOfflineMessage(playerName, messageSupplier);
    }

    public void broadcastBukkitPacket(@NonNull Packet<?> packet) {
        for (BukkitServer bukkitServer : getBukkitServers()) {
            bukkitServer.sendPacket(packet);
        }
    }

    public void broadcastBungeePacket(@NonNull Packet<?> packet) {
        for (BungeeServer bungeeServer : getBungeeServers()) {
            bungeeServer.sendPacket(packet);
        }
    }

    public void broadcastPacket(@NonNull Packet<?> packet) {
        broadcastBungeePacket(packet);
        broadcastBukkitPacket(packet);
    }


    @Getter
    private final Table<String, String, ModuleExecuteQuery> moduleExecuteQueries = HashBasedTable.create();

    public void execute(@NonNull AbstractServer server, @NonNull ModuleExecuteType executeType, @NonNull String moduleName, @NonNull String key, Object value) {
        ModuleExecuteQuery query = new ModuleExecuteQuery(moduleName, key, value, executeType);
        moduleExecuteQueries.put(moduleName, key, query);

        query.execute(server);
    }

    public void executeBroadcast(@NonNull ModuleExecuteType executeType, @NonNull String moduleName, @NonNull String key, Object value) {
        ModuleExecuteQuery query = new ModuleExecuteQuery(moduleName, key, value, executeType);
        moduleExecuteQueries.put(moduleName, key, query);

        query.executeBroadcast();
    }

}
