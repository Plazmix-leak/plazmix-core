package net.plazmix.core.common.group;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.event.impl.PlayerGroupChangeEvent;
import net.plazmix.core.api.module.execute.ModuleExecuteType;
import net.plazmix.core.api.sounds.SoundType;
import net.plazmix.core.common.coloredprefix.ColoredPrefixSqlHandler;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.protocol.server.SPlayerGroupUpdatePacket;
import net.plazmix.core.connection.server.impl.BukkitServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public final class GroupManager {

    public static final GroupManager INSTANCE = new GroupManager();


    private final TIntObjectMap<Group> playerGroupMap = new TIntObjectHashMap<>();

    /**
     * Установить новую группу игроку
     *
     * @param corePlayer - игрок
     * @param group      - новая группа
     */
    public void setGroupToPlayer(@NonNull CorePlayer corePlayer,
                                 @NonNull Group group) {

        if (corePlayer.getGroup().equals(group)) {
            return;
        }

        callEvent(corePlayer.getName(), group, corePlayer.getGroup());

        playerGroupMap.put(corePlayer.getPlayerId(), group);

        PlazmixCore.getInstance().getMysqlConnection().execute(true, "INSERT INTO `PlayerGroup` (`Id`,`Group`) VALUES (?,?) ON DUPLICATE KEY UPDATE `Group`=?",
                corePlayer.getPlayerId(), group.getLevel(), group.getLevel());

        if (group.isDonate()) {
            corePlayer.sendMessage("§d§lPlazmix §8:: §fСтатус " + group.getPrefix() + " §fбыл успешно приобретен!");
            corePlayer.sendMessage(" §fПриносим огромную §aблагодарность §fза покупку и желаем §eприятной игры§f!");
            corePlayer.playSound(SoundType.BLOCK_ANVIL_USE, 1, 1);
        }

        if (corePlayer.isOnline()) {
            corePlayer.disconnect("§eВаши игровые данные были обновлены" +
                    "\n§eПожалуйста, перезайдите на сервер!");

            sendUpdateStatusPacket(corePlayer.getName(), group);
        }

        PlazmixCore.getInstance().executeBroadcast(ModuleExecuteType.INSERT, "TynixGroups", "PLAYER_GROUP_" + corePlayer.getName(), group.getLevel());
    }

    /**
     * Установить новую группу игроку
     *
     * @param playerName - ник игрока
     * @param group      - новая группа
     */
    public void setGroupToPlayer(@NonNull String playerName,
                                 @NonNull Group group) {

        CorePlayer corePlayer = PlazmixCore.getInstance().getOfflinePlayer(playerName);

        //if (corePlayer.getGroup().equals(group)) {
        //    return;
        //}

        callEvent(playerName, group, getPlayerGroup(playerName));

        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);
        playerGroupMap.put(playerId, group);

        PlazmixCore.getInstance().getMysqlConnection().execute(true, "INSERT INTO `PlayerGroup` (`Id`,`Group`) VALUES (?,?) ON DUPLICATE KEY UPDATE `Group`=?",
                playerId, group.getLevel(), group.getLevel());

        ColoredPrefixSqlHandler.INSTANCE.setPrefixColor(playerId, null);
        sendUpdateStatusPacket(playerName, group);

        if (corePlayer.isOnline()) {
            corePlayer.disconnect("§eВаши игровые данные были обновлены" +
                    "\n§eПожалуйста, перезайдите на сервер!");

            sendUpdateStatusPacket(corePlayer.getName(), group);
        }

        PlazmixCore.getInstance().executeBroadcast(ModuleExecuteType.INSERT, "TynixGroups", "PLAYER_GROUP_" + playerName, group.getLevel());
    }

    /**
     * Получить группу игрока по его номеру
     *
     * @param playerId - номеру игрока
     */
    public Group getPlayerGroup(int playerId) {
        Group group = playerGroupMap.get(playerId);

        if (group == null) {
            group = PlazmixCore.getInstance().getMysqlConnection().executeQuery(false, "SELECT * FROM `PlayerGroup` WHERE `Id`=?",
                    resultSet -> resultSet.next() ? Group.getGroupByLevel(resultSet.getInt("Group")) : null, playerId);

            if (group != null) {
                playerGroupMap.put(playerId, group);
            }
        }

        return group;
    }

    /**
     * Получить группу игрока по его нику
     *
     * @param playerName - ник игрока
     */
    public Group getPlayerGroup(@NonNull String playerName) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);

        return getPlayerGroup(playerId);
    }

    /**
     * Получить группу игрока
     *
     * @param corePlayer - игрок
     */
    public Group getPlayerGroup(@NonNull CorePlayer corePlayer) {
        return getPlayerGroup(corePlayer.getName());
    }


    private final Cache<Group, Collection<CorePlayer>> offlinePlayersByGroupCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();

    /**
     * Получить список оффлайн игроков по группе
     *
     * @param group - группа
     */
    public Collection<CorePlayer> getOfflinePlayersByGroup(@NonNull Group group) {
        offlinePlayersByGroupCache.cleanUp();

        Collection<CorePlayer> offlinePlayersCollection = offlinePlayersByGroupCache.asMap().get(group);
        if (offlinePlayersCollection == null) {

            offlinePlayersCollection = PlazmixCore.getInstance().getMysqlConnection().executeQuery(true, "SELECT * FROM `PlayerGroup` WHERE `Group`=?",
                    resultSet -> {

                        Collection<CorePlayer> corePlayerCollection = new ArrayList<>();
                        while (resultSet.next()) {

                            int playerId = resultSet.getInt("Id");
                            String playerName = NetworkManager.INSTANCE.getPlayerName(playerId);

                            corePlayerCollection.add(PlazmixCore.getInstance().getOfflinePlayer(playerName));
                        }

                        return corePlayerCollection;
                    }, group.getLevel());

            offlinePlayersByGroupCache.put(group, offlinePlayersCollection);
        }

        return offlinePlayersCollection;
    }


    protected void sendUpdateStatusPacket(@NonNull String playerName, @NonNull Group playerGroup) {
        SPlayerGroupUpdatePacket playerStatusUpdatePacket = new SPlayerGroupUpdatePacket(playerName, playerGroup);

        for (BukkitServer bukkitServer : PlazmixCore.getInstance().getServerManager().getBukkitServers().values()) {
            bukkitServer.sendPacket(playerStatusUpdatePacket);
        }
    }

    protected void callEvent(@NonNull String playerName,
                             @NonNull Group currentGroup,
                             Group previousGroup) {

        PlayerGroupChangeEvent playerGroupChangeEvent = new PlayerGroupChangeEvent(
                PlazmixCore.getInstance().getOfflinePlayer(playerName), currentGroup, previousGroup
        );

        PlazmixCore.getInstance().getEventManager().callEvent(playerGroupChangeEvent);

    }
}
