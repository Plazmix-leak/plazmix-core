package net.plazmix.core.connection.server.mode;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.connection.server.AbstractServer;
import net.plazmix.core.connection.server.impl.BukkitServer;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
public enum ServerMode {

    UNKNOWN(ChatColor.WHITE, "N/A", "unknown"),

    /**
     * Список основный игровых серверов,
     * благодаря которым хотя-бы можно заходить на сервер
     */
    BUNGEE(ChatColor.RED, "Bungee", "bungee") {{
        addSubMode("Bungee", "bungee", ServerSubModeType.MAIN);
    }},

    AUTH(ChatColor.YELLOW, "auth", "auth") {{
        addSubMode("auth", "auth", ServerSubModeType.MAIN);
    }},

    LIMBO(ChatColor.YELLOW, "Limbo", "limbo") {{
        addSubMode("Limbo", "limbo", ServerSubModeType.MAIN);
    }},

    HUB(ChatColor.YELLOW, "hub", "hub") {{
        addSubMode("hub", "hub", ServerSubModeType.MAIN);
    }},

    BUILD(ChatColor.YELLOW,"Build", "build") {{
        addSubMode("Build", "build", ServerSubModeType.MAIN);
    }},

    /**
     * Список серверов для выживания
     */
    MMORPG(ChatColor.LIGHT_PURPLE, "MMORPG", "mmorpg") {{
        addSubMode("MMORPG", "mmorpg", ServerSubModeType.SURVIVAL);
    }},

    ONEBLOCK(ChatColor.DARK_BLUE, "OneBlock", "oneblock") {{
        addSubMode("OneBlock", "oneblock", ServerSubModeType.SURVIVAL);
    }},

    PRISON(ChatColor.RED, "Prison", "prison") {{

        addSubMode("Prison", "prison", ServerSubModeType.SURVIVAL);
        // ...
    }},

    /**
     * Список игровых серверов
     */
    SKYWARS(ChatColor.AQUA, "SkyWars", "sw") {{
        addSubMode("SkyWars Lobby", "swlobby", ServerSubModeType.GAME_LOBBY);

        addSubMode("SkyWars Solo", "sws", ServerSubModeType.GAME_ARENA);
        addSubMode("SkyWars Doubles", "swd", ServerSubModeType.GAME_ARENA);
        addSubMode("SkyWars Team", "swt", ServerSubModeType.GAME_ARENA);

        addSubMode("SkyWars Ranked", "swr", ServerSubModeType.GAME_ARENA);
        addSubMode("SkyWars Crazy", "swc", ServerSubModeType.GAME_ARENA);
    }},

    BEDWARS(ChatColor.RED, "BedWars", "bw") {{
        addSubMode("BedWars Lobby", "bwlobby", ServerSubModeType.GAME_LOBBY);

        addSubMode("BedWars Solo", "bws", ServerSubModeType.GAME_ARENA);
        addSubMode("BedWars Doubles", "bwd", ServerSubModeType.GAME_ARENA);
        addSubMode("BedWars Team", "bwt", ServerSubModeType.GAME_ARENA);
    }},

    ARCADE(ChatColor.YELLOW, "ArcadeGames", "arcade") {{
        addSubMode("Arcade Lobby", "arcadelobby", ServerSubModeType.GAME_LOBBY);

        addSubMode("Squid", "squid", ServerSubModeType.GAME_ARENA);
        addSubMode("BuildBattle", "buildbattle", ServerSubModeType.GAME_ARENA);
        addSubMode("SpeedBuilders", "speedbuilders", ServerSubModeType.GAME_ARENA);
        addSubMode("PartyGames", "partygames", ServerSubModeType.GAME_ARENA);
        addSubMode("Parkour", "parkour", ServerSubModeType.GAME_ARENA);
        addSubMode("TntTag", "tnttag", ServerSubModeType.GAME_ARENA);
    }},

    GUNGAME(ChatColor.YELLOW, "GunGame", "gungame") {{
        addSubMode("GunGame", "gungame", ServerSubModeType.GAME_ARENA);
    }},

    UHC(ChatColor.DARK_AQUA, "UHC", "uhc") {{
        addSubMode("UHC Lobby", "uhclobby", ServerSubModeType.GAME_LOBBY);

        addSubMode("UHC Speed", "uhcspeed", ServerSubModeType.GAME_ARENA);
        addSubMode("UHC Champions", "uhcchamp", ServerSubModeType.GAME_ARENA);
        addSubMode("UHC Effects", "uhceff", ServerSubModeType.GAME_ARENA);
    }},

    DUELS(ChatColor.GREEN, "Duels", "duels") {{
        addSubMode("Duels Lobby", "duelslobby", ServerSubModeType.GAME_LOBBY);

        addSubMode("MlgRush 1vs1", "duelsmlgrush", ServerSubModeType.GAME_ARENA);
        addSubMode("GApple 1vs1", "duelsgapple", ServerSubModeType.GAME_ARENA);
        addSubMode("Nodebuff 1vs1", "duelsnodebuff", ServerSubModeType.GAME_ARENA);
        addSubMode("Sumo 1vs1", "duelssumo", ServerSubModeType.GAME_ARENA);
        addSubMode("BuildUHC 1vs1", "duelsbuilduhc", ServerSubModeType.GAME_ARENA);
        addSubMode("Classic 1vs1", "duelsclassic", ServerSubModeType.GAME_ARENA);
        addSubMode("Spleef 1vs1", "duelsspleef", ServerSubModeType.GAME_ARENA);
        addSubMode("BattleRush 1vs1", "duelsbattlerush", ServerSubModeType.GAME_ARENA);
    }},

    LUCKYWARS(ChatColor.GREEN, "LuckyWars", "lw") {{
        addSubMode("LuckyWars Lobby", "lwl", ServerSubModeType.GAME_LOBBY);

        addSubMode("LuckyWars 1x8", "lw-1x8", ServerSubModeType.GAME_ARENA);
        addSubMode("LuckyWars 2x8", "lw-2x8", ServerSubModeType.GAME_ARENA);
        addSubMode("LuckyWars 4x8", "lw-4x8", ServerSubModeType.GAME_ARENA);
    }},

    SAVECHRISTMAS(ChatColor.GREEN,"SaveChristmas", "savechristmas") {{
        addSubMode("SaveChristmas", "savechristmas", ServerSubModeType.GAME_ARENA);
    }},
    ;

    private final ChatColor chatColor;

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
     * Получить подтипизацию сервера
     *
     * @param abstractServer - сервер.
     */
    public static ServerSubMode getSubMode(@NonNull AbstractServer abstractServer) {
        return getSubMode(abstractServer.getName());
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

    /**
     * Получить типизацию сервера
     *
     * @param abstractServer - сервер.
     */
    public static ServerMode getMode(@NonNull AbstractServer abstractServer) {
        return getMode(abstractServer.getName());
    }


    /**
     * Проверить типизацию подсерверов
     *
     * @param server - префикс или имя сервера.
     * @param type   - типизация, на равность которой проверяем.
     */
    public static boolean isTyped(@NonNull String server, @NonNull ServerSubModeType type) {
        return getMode(server).getSubModes(type)
                .stream().anyMatch(serverSubMode -> server.toLowerCase().startsWith(serverSubMode.getSubPrefix().toLowerCase()));
    }

    /**
     * Проверить типизацию подсерверов
     *
     * @param server - сервер.
     * @param type   - типизация, на равность которой проверяем.
     */
    public static boolean isTyped(@NonNull AbstractServer server, @NonNull ServerSubModeType type) {
        return getMode(server).getSubModes(type).stream()
                .anyMatch(serverSubMode -> server.getName().toLowerCase().startsWith(serverSubMode.getSubPrefix().toLowerCase()));
    }


    /**
     * Проверить типизацию сервера
     *
     * @param server - префикс или имя сервера.
     * @param mode   - типизация, на равность которой проверяем.
     */
    public static boolean isTyped(@NonNull String server, @NonNull ServerMode mode) {
        return getMode(server).equals(mode);
    }

    /**
     * Проверить типизацию сервера
     *
     * @param server - сервер.
     * @param mode   - типизация, на равность которой проверяем.
     */
    public static boolean isTyped(@NonNull AbstractServer server, @NonNull ServerMode mode) {
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
    public static BukkitServer getFallbackLobby(@NonNull String server) {
        ServerMode serverMode = getMode(server);
        if (serverMode.getSubModes(ServerSubModeType.GAME_LOBBY).isEmpty()) {

            return getServers(ServerMode.HUB).stream().findFirst().orElse(
                    getServers(ServerMode.LIMBO).stream().findFirst().orElse(null)
            );
        }

        ServerSubMode serverLobby = serverMode.getSubModes(ServerSubModeType.GAME_LOBBY)
                .stream()
                .findFirst()
                .get();

        return PlazmixCore.getInstance().getBestServer(false, serverLobby);
    }

    /**
     * Получить список подключенных серверов по
     * указанной типизации сервера
     *
     * @param serverMode - типизация.
     */
    public static Collection<BukkitServer> getServers(@NonNull ServerMode serverMode) {
        return PlazmixCore.getInstance().getConnectedServers(serverMode.serversPrefix);
    }


// ============================================= // MODE MANAGEMENT // ============================================= //

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

            online += PlazmixCore.getInstance().getOnlineByServerPrefix(mode.getSubPrefix());
        }

        return online;
    }

    /**
     * Получить онлайн всех подключенных серверов
     * по текущей типизации
     */
    public int getOnline() {
        return PlazmixCore.getInstance().getOnlineByServerPrefix(serversPrefix);
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
