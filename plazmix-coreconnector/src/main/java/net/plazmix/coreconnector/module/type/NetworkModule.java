package net.plazmix.coreconnector.module.type;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.NonNull;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.module.BaseModuleManager;
import net.plazmix.coreconnector.module.BaseServerModule;
import net.plazmix.coreconnector.module.type.economy.EconomyModule;
import net.plazmix.coreconnector.module.type.friend.FriendsModule;
import net.plazmix.coreconnector.module.type.group.Group;
import net.plazmix.coreconnector.module.type.group.GroupModule;
import net.plazmix.coreconnector.module.type.party.PartyModule;
import net.plazmix.coreconnector.module.type.skin.SkinsModule;
import net.plazmix.coreconnector.protocol.server.SPlayerCommandPacket;
import net.plazmix.coreconnector.protocol.server.SPlayerServerRedirectPacket;
import net.plazmix.coreconnector.utility.server.ServerMode;
import net.plazmix.coreconnector.utility.server.ServerSubMode;
import net.plazmix.coreconnector.utility.server.ServerSubModeType;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Пусть это будет типа референс-класса,
 * через которые можно получить данные любого модуля
 * и не только))
 */
@Getter
public final class NetworkModule extends BaseServerModule {

    private static final BaseModuleManager MANAGER = BaseModuleManager.INSTANCE;

    public static NetworkModule getInstance() {
        return BaseModuleManager.INSTANCE.find(NetworkModule.class);
    }

    public NetworkModule() {
        super("TynixCloud");
    }

// ================================================================================================================== //

    public GroupModule getGroupModule() {
        return MANAGER.find(GroupModule.class);
    }

    public EconomyModule getEconomyModule() {
        return MANAGER.find(EconomyModule.class);
    }

    public PartyModule getPartyModule() {
        return MANAGER.find(PartyModule.class);
    }

    public FriendsModule getFriendsModule() {
        return MANAGER.find(FriendsModule.class);
    }

    public SkinsModule getSkinsModule() {
        return MANAGER.find(SkinsModule.class);
    }

// ================================================================================================================== //

    private final Map<Integer, String> playerNameByIdsMap = new HashMap<>();
    private final Map<String, Integer> playerIdByNamesMap = new HashMap<>();

    private final TIntObjectMap<Group> playerGroupMap = new TIntObjectHashMap<>();

    private final Map<String, Integer> serversOnlineMap = new ConcurrentHashMap<>();

// ================================================================================================================== //

    /**
     * Получить ник игрока по его номеру
     *
     * @param playerId - номер игрока
     */
    public String getPlayerName(int playerId) {
        if (playerId <= 0) {
            return null;
        }

        String playerName = playerNameByIdsMap.get(playerId);

        if (playerName == null) {
            playerName = CoreConnector.getInstance().getMysqlConnection().executeQuery(false, "SELECT * FROM `PlayerIdentifier` WHERE `Id`=?",
                    resultSet -> resultSet.next() ? resultSet.getString("Name") : null, playerId);

            if (playerName != null) {

                playerIdByNamesMap.put(playerName.toLowerCase(), playerId);
                playerNameByIdsMap.put(playerId, playerName);
            }
        }

        return playerName;
    }

    /**
     * Получить номер игрока по его нику
     *
     * @param playerName - ник игрока
     */
    public int getPlayerId(String playerName) {
        if (playerName == null)
            return -1;

        int playerId = playerIdByNamesMap.getOrDefault(playerName.toLowerCase(), -1);

        if (playerId < 0) {
            playerId = CoreConnector.getInstance().getMysqlConnection().executeQuery(false, "SELECT * FROM `PlayerIdentifier` WHERE `Name`=?",
                    resultSet -> {

                        if (!resultSet.next()) {
                            CoreConnector.getInstance().getMysqlConnection().execute(false, "INSERT INTO `PlayerIdentifier` (`Name`) VALUES (?)", playerName);

                            return getPlayerId(playerName);
                        }

                        return resultSet.getInt("Id");
                    }, playerName.toLowerCase());

            if (playerId > 0) {

                playerIdByNamesMap.put(playerName.toLowerCase(), playerId);
                playerNameByIdsMap.put(playerId, playerName);
            }
        }

        return playerId;
    }

// ================================================================================================================== //

    /**
     * Выполнить команду на коре от имени игрока
     *
     * @param playerName - ник игрока
     * @param command    - команда на коре
     */
    public void executeCommand(@NonNull String playerName, @NonNull String command) {
        sendPacket(new SPlayerCommandPacket(playerName, command));
    }

// ================================================================================================================== //

    /**
     * Переместить игрока на конкректно
     * указанный сервер
     *
     * @param playerName - ник игрока
     * @param serverName - имя сервера
     */
    public void redirect(@NonNull String playerName, @NonNull String serverName) {
        sendPacket(new SPlayerServerRedirectPacket(playerName, serverName, true));
    }

    /**
     * Переместить игрока на первый
     * активный сервер
     *
     * @param playerName   - ник игрока
     * @param serverPrefix - префикс серверов
     */
    public void redirectToFirst(@NonNull String playerName, @NonNull String serverPrefix) {
        String firstActiveServer = getConnectedServers(serverPrefix)
                .stream()
                .findFirst()
                .orElse(null);

        if (firstActiveServer == null) {
            return;
        }

        redirect(playerName, firstActiveServer);
    }

    /**
     * Переместить игрока на первый
     * активный сервер определенный типом
     *
     * @param playerName - ник игрока
     * @param serverMode - тип серверов
     */
    public void redirectToFirst(@NonNull String playerName, @NonNull ServerMode serverMode) {
        String firstActiveServer = serverMode.getFirstActiveServer();

        if (firstActiveServer == null) {
            return;
        }

        redirect(playerName, firstActiveServer);
    }

    /**
     * Переместить игрока на лучший сервер
     * по онлайну с определенного типа серверов.
     *
     * @param playerName    - ник игрока
     * @param serverSubMode - подтип сервера
     */
    public void redirectToBest(@NonNull String playerName, @NonNull ServerSubMode serverSubMode) {
        String bestServer = getBestServer(serverSubMode);

        if (bestServer == null) {
            return;
        }

        redirect(playerName, bestServer);
    }

    /**
     * Переместить игрока на лучший сервер
     * по онлайну с определенного типа серверов.
     *
     * @param playerName  - ник игрока
     * @param serverMode  - режим серверов
     * @param subModeType - тип подрежима сервера
     */
    public void redirectToBest(@NonNull String playerName, @NonNull ServerMode serverMode, @NonNull ServerSubModeType subModeType) {
        serverMode.getSubModes(subModeType)
                .stream()
                .findFirst()
                .ifPresent(firstSubMode -> NetworkModule.getInstance().redirectToBest(playerName, firstSubMode));
    }

// ================================================================================================================== //

    /**
     * Получить общее количество онлайна
     * с определенных серверов.
     *
     * @param serverNames - имена серверов, онлайн
     *                    который нужно подсчитывать.
     */
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

    /**
     * Получить общий онлайн всех серверов
     * по указанному префиксу
     *
     * @param serverPrefix - префикс серверов
     */
    public int getOnlineByPrefix(@NonNull String serverPrefix) {
        return serversOnlineMap.keySet().stream().filter(s -> s.toLowerCase().startsWith(serverPrefix.toLowerCase()))
                .mapToInt(serversOnlineMap::get).sum();
    }

    /**
     * Получить общий онлайн по нескольким префиксам
     * подключенных серверов
     *
     * @param serverPrefixes - массив префиксов серверовы
     */
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

    /**
     * Получить количество подключенных серверов
     * по указанному префиксу.
     *
     * @param serverPrefix - префикс серверов.
     */
    public int getConnectedServersCount(@NonNull String serverPrefix) {
        return getConnectedServers(serverPrefix).size();
    }

    /**
     * Получить список названий подключенных серверов
     * по префиксу.
     *
     * @param serverPrefix - префикс серверов.
     */
    public Collection<String> getConnectedServers(@NonNull String serverPrefix) {
        return serversOnlineMap.keySet().stream()
                .filter(s -> s.toLowerCase().startsWith(serverPrefix.toLowerCase()) && isServerConnected(s))
                .collect(Collectors.toSet());
    }

    /**
     * Получить лучший сервер, отсортированный по
     * количеству онлайна
     *
     * @param checkOnline - разрешение на сортировку по онлайну
     * @param serverMode  - режим серверов.
     */
    public String getBestServer(boolean checkOnline, @NonNull ServerMode serverMode) {
        Collection<String> connectedServers = getConnectedServers(serverMode.getServersPrefix());

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

    public String getBestServer(@NonNull ServerMode serverMode) {
        return getBestServer(true, serverMode);
    }

    /**
     * Получить лучший сервер, отсортированный по
     * количеству онлайна
     *
     * @param checkOnline   - разрешение на сортировку по онлайну
     * @param serverSubMode - подрежим серверов
     */
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

    public String getBestServer(@NonNull ServerSubMode serverSubMode) {
        return getBestServer(true, serverSubMode);
    }

    /**
     * Проверить сервер на наличие подключения
     * к кору.
     *
     * @param serverName - имя проверяемого сервера
     */
    public boolean isServerConnected(@NonNull String serverName) {
        return serversOnlineMap.keySet().stream().map(String::toLowerCase).collect(Collectors.toList()).contains(serverName.toLowerCase());
    }

    /**
     * Получить общий онлайн со всех серверов.
     */
    public int getGlobalOnline() {
        return ServerMode.BUNGEE.getOnline();
    }

// ================================================================================================================== //

    public void setMotd(@NonNull String motd) {
        CoreConnector.getInstance().setMotd(motd);
    }

    public void sendPacket(@NonNull Packet<?> packet) {
        CoreConnector.getInstance().sendPacket(packet);
    }

// ================================================================================================================== //
}
