package net.plazmix.coreconnector.utility.server;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.module.type.NetworkModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
public enum ServerMode {
    UNKNOWN("N/A", "unknown"),

    /**
     * Список основный игровых серверов,
     * благодаря которым хотя-бы можно заходить на сервер
     */
    BUNGEE("Bungee", "bungee") {{
        addSubMode("Bungee", "bungee", ServerSubModeType.MAIN);
    }},

    AUTH("auth", "auth") {{
        addSubMode("auth", "auth", ServerSubModeType.MAIN);
    }},

    LIMBO("Limbo", "limbo") {{
        addSubMode("Limbo", "limbo", ServerSubModeType.MAIN);
    }},

    HUB("hub", "hub") {{
        addSubMode("hub", "hub", ServerSubModeType.MAIN);
    }},

    BUILD("Build", "build") {{
        addSubMode("Build", "build", ServerSubModeType.MAIN);
    }},


    /**
     * Список серверов для выживания
     */
    MMORPG("MMORPG", "mmorpg") {{
        addSubMode("MMORPG", "mmorpg", ServerSubModeType.SURVIVAL);
    }},

    ONEBLOCK("OneBlock", "oneblock") {{
        addSubMode("OneBlock", "oneblock", ServerSubModeType.SURVIVAL);
    }},

    PRISON("Prison", "prison") {{

        addSubMode("Prison", "prison", ServerSubModeType.SURVIVAL);
        // ...
    }},

    /**
     * Список игровых серверов
     */
    SKYWARS("SkyWars", "sw") {{
        addSubMode("SkyWars Lobby", "swlobby", ServerSubModeType.GAME_LOBBY);

        addSubMode("SkyWars Solo", "sws", ServerSubModeType.GAME_ARENA);
        addSubMode("SkyWars Doubles", "swd", ServerSubModeType.GAME_ARENA);
        addSubMode("SkyWars Team", "swt", ServerSubModeType.GAME_ARENA);

        addSubMode("SkyWars Ranked", "swr", ServerSubModeType.GAME_ARENA);
        addSubMode("SkyWars Crazy", "swc", ServerSubModeType.GAME_ARENA);
    }},

    BEDWARS("BedWars", "bw") {{
        addSubMode("BedWars Lobby", "bwlobby", ServerSubModeType.GAME_LOBBY);

        addSubMode("BedWars Solo", "bws", ServerSubModeType.GAME_ARENA);
        addSubMode("BedWars Doubles", "bwd", ServerSubModeType.GAME_ARENA);
        addSubMode("BedWars Team", "bwt", ServerSubModeType.GAME_ARENA);
    }},

    ARCADE("ArcadeGames", "arcade") {{
        addSubMode("Arcade Lobby", "arcadelobby", ServerSubModeType.GAME_LOBBY);

        addSubMode("Squid", "squid", ServerSubModeType.GAME_ARENA);
        addSubMode("BuildBattle", "buildbattle", ServerSubModeType.GAME_ARENA);
        addSubMode("SpeedBuilders", "speedbuilders", ServerSubModeType.GAME_ARENA);
        addSubMode("PartyGames", "partygames", ServerSubModeType.GAME_ARENA);
        addSubMode("Parkour", "parkour", ServerSubModeType.GAME_ARENA);
        addSubMode("TntTag", "tnttag", ServerSubModeType.GAME_ARENA);
    }},

    UHC("UHC", "uhc") {{
        addSubMode("UHC Lobby", "uhclobby", ServerSubModeType.GAME_LOBBY);

        addSubMode("UHC Speed", "uhcspeed", ServerSubModeType.GAME_ARENA);
        addSubMode("UHC Champions", "uhcchamp", ServerSubModeType.GAME_ARENA);
        addSubMode("UHC Effects", "uhceff", ServerSubModeType.GAME_ARENA);
    }},

    DUELS("Duels", "duels") {{
        addSubMode("Duels Lobby", "duelslobby", ServerSubModeType.GAME_LOBBY);

        addSubMode("MlgRush 1vs1", "duelsmlgrush", ServerSubModeType.GAME_ARENA);
        addSubMode("Gapple 1vs1", "duelsgapple", ServerSubModeType.GAME_ARENA);
        addSubMode("Nodebuff 1vs1", "duelsnodebuff", ServerSubModeType.GAME_ARENA);
        addSubMode("Sumo 1vs1", "duelssumo", ServerSubModeType.GAME_ARENA);
    }},

    GUNGAME("GunGame", "gungame") {{
        addSubMode("GunGame", "gungame", ServerSubModeType.GAME_ARENA);
    }},

    LUCKYWARS("LuckyWars", "lw") {{
        addSubMode("LuckyWars Lobby", "lwl", ServerSubModeType.GAME_LOBBY);

        addSubMode("LuckyWars 1x8", "lw-1x8", ServerSubModeType.GAME_ARENA);
        addSubMode("LuckyWars 2x8", "lw-2x8", ServerSubModeType.GAME_ARENA);
        addSubMode("LuckyWars 4x8", "lw-4x8", ServerSubModeType.GAME_ARENA);
    }},

    SAVECHRISTMAS("SaveChristmas", "savechristmas") {{
        addSubMode("SaveChristmas", "savechristmas", ServerSubModeType.GAME_ARENA);
    }},

    ;

    private final String name;
    private final String serversPrefix;

    private final Collection<ServerSubMode> subModes = new LinkedList<>();


// ============================================= // MODE FACTORY // ============================================= //

    /**
     * Получить подтипизацию сервера
     *
     * @param server - префикс или имя сервера.
     */
    public static ServerSubMode getSubMode(@NonNull String server) {
        for (ServerMode serverMode : ServerMode.values()) {
            for (ServerSubMode serverSubMode : serverMode.subModes) {

                if (serverSubMode.getSubPrefix().equalsIgnoreCase(server.substring(0, server.lastIndexOf("-")))) {
                    return serverSubMode;
                }
            }
        }

        return null;
    }

    /**
     * Получить типизацию сервера
     *
     * @param server - префикс или имя сервера.
     */
    public static ServerMode getMode(@NonNull String server) {
        for (ServerMode serverMode : ServerMode.values()) {

            if (server.toLowerCase().startsWith(serverMode.serversPrefix.toLowerCase())) {
                return serverMode;
            }
        }

        return UNKNOWN;
    }

    public static boolean isCurrentTyped(@NonNull ServerMode mode) {
        return isTyped(CoreConnector.getInstance().getServerName(), mode);
    }

    public static boolean isCurrentTyped(@NonNull ServerSubModeType type) {
        return isTyped(CoreConnector.getInstance().getServerName(), type);
    }


    /**
     * Проверить типизацию подсерверов.
     *
     * @param server - префикс или имя сервера.
     * @param type   - типизация, на равность которой проверяем.
     */
    public static boolean isTyped(@NonNull String server, @NonNull ServerSubModeType type) {
        return getMode(server).getSubModes(type)
                .stream().anyMatch(serverSubMode -> server.toLowerCase().startsWith(serverSubMode.getSubPrefix().toLowerCase()));
    }

    /**
     * Проверить типизацию сервера.
     *
     * @param server - префикс или имя сервера.
     * @param mode   - типизация, на равность которой проверяем.
     */
    public static boolean isTyped(@NonNull String server, @NonNull ServerMode mode) {
        return getMode(server).equals(mode);
    }


    /**
     * Проверить типизацию сервера: Является ли сервер основным.
     *
     * @param server - префикс или имя сервера.
     */
    public static boolean isMain(@NonNull String server) {
        return isTyped(server, ServerSubModeType.MAIN);
    }

    /**
     * Проверить типизацию сервера: Является ли сервер выживанием.
     *
     * @param server - префикс или имя сервера.
     */
    public static boolean isSurvival(@NonNull String server) {
        return isTyped(server, ServerSubModeType.SURVIVAL);
    }

    /**
     * Проверить типизацию сервера: Является ли сервер игровым лобби.
     *
     * @param server - префикс или имя сервера.
     */
    public static boolean isGameLobby(@NonNull String server) {
        return isTyped(server, ServerSubModeType.GAME_LOBBY);
    }

    /**
     * Проверить типизацию сервера: Является ли сервер игровой ареной.
     *
     * @param server - префикс или имя сервера.
     */
    public static boolean isGameArena(@NonNull String server) {
        return isTyped(server, ServerSubModeType.GAME_ARENA);
    }

    /**
     * Получить лобби-сервер, если указанный упадет.
     *
     * @param server - префикс или имя сервера.
     */
    public static String getFallbackLobby(@NonNull String server) {
        ServerMode serverMode = getMode(server);
        ServerMode fallbackMode = ServerMode.LIMBO;

        if (isTyped(server, ServerSubModeType.GAME_ARENA)) {

            if (!serverMode.getSubModes(ServerSubModeType.GAME_LOBBY).isEmpty()) {
                return CoreConnector.getInstance().getBestServer(serverMode.getSubModes(ServerSubModeType.GAME_LOBBY)
                        .stream()
                        .findFirst()
                        .get());
            }
        }

        if (NetworkModule.getInstance().getConnectedServersCount(ServerMode.HUB.getServersPrefix()) > 0)
            fallbackMode = ServerMode.HUB;

        return NetworkModule.getInstance().getConnectedServers(fallbackMode.getServersPrefix())
                .stream()
                .findFirst()
                .orElse("Limbo-1");
    }

    /**
     * Получить список подключенных серверов по
     * указанной типизации сервера
     *
     * @param serverMode - типизация.
     */
    public static Collection<String> getServers(@NonNull ServerMode serverMode) {
        return CoreConnector.getInstance().getConnectedServers(serverMode.serversPrefix);
    }


// ============================================= // MODE MANAGEMENT // ============================================= //

    public Collection<String> getActiveServers() {
        return CoreConnector.getInstance().getConnectedServers(serversPrefix);
    }

    public Collection<String> getActiveServers(String... ignorePrefixes) {
        Collection<String> activeServersList = new ArrayList<>();
        Collection<String> ignoredSubPrefixes = Stream.of(ignorePrefixes).map(String::toLowerCase).collect(Collectors.toList());

        for (String activeServer : getActiveServers()) {
            if (ignoredSubPrefixes.contains(activeServer.toLowerCase()))
                continue;

            activeServersList.add(activeServer);
        }

        return activeServersList;
    }

    public String getFirstActiveServer() {
        return getActiveServers().stream().findFirst().orElse(null);
    }

    /**
     * Получить онлайн всех подключенных серверов
     * по текущей типизации, игнорируя указанные
     * сервера по префиксам
     *
     * @param ignoreSubTypes - игнорируемые префиксы серверов.
     */
    public int getOnline(String... ignoreSubTypes) {
        int online = 0;
        Collection<String> ignoredSubPrefixes = Stream.of(ignoreSubTypes).map(String::toLowerCase).collect(Collectors.toList());

        for (ServerSubMode mode : subModes) {
            if (ignoredSubPrefixes.contains(mode.getSubPrefix().toLowerCase()))
                continue;

            online += CoreConnector.getInstance().getOnlineByPrefix(mode.getSubPrefix());
        }

        return online;
    }

    /**
     * Получить онлайн всех подключенных серверов
     * по текущей типизации
     */
    public int getOnline() {
        return CoreConnector.getInstance().getOnlineByPrefix(serversPrefix);
    }


    /**
     * Получить список подключенных подсерверов
     * текущий типизации
     *
     * @param type - типизация подсерверов, которые будут
     *             отфильтрованы в возвращаемый список.
     */
    public Collection<ServerSubMode> getSubModes(@NonNull ServerSubModeType type) {
        return subModes.stream().filter(serverSubMode -> serverSubMode.getType().equals(type)).collect(Collectors.toList());
    }

    /**
     * Получить список подключенных подсерверов
     * текущий типизации
     *
     * @param prefix - префикс подсерверов, которые будут
     *               отфильтрованы в возвращаемый список.
     */
    public Collection<ServerSubMode> getSubModes(@NonNull String prefix) {
        return subModes.stream().filter(serverSubMode -> serverSubMode.getSubPrefix().startsWith(prefix)).collect(Collectors.toList());
    }


    /**
     * Добавить новый подсервер в текущую типизацию
     *
     * @param name      - имя подсервера.
     * @param subPrefix - префикс подсервера.
     * @param type      - тип подсервера.
     */
    protected void addSubMode(@NonNull String name, @NonNull String subPrefix, @NonNull ServerSubModeType type) {
        subModes.add(new ServerSubMode(name, subPrefix, type));
    }

}
