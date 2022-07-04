package net.plazmix.coreconnector.core.network;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.core.group.Group;

import java.util.HashMap;
import java.util.Map;

@Deprecated
@Getter
public final class NetworkManager {

    public static final NetworkManager INSTANCE = new NetworkManager();

    @Deprecated private final Map<Integer, String> playerNameByIdsMap = new HashMap<>();
    @Deprecated private final Map<String, Integer> playerIdByNamesMap = new HashMap<>();

    @Deprecated private final TIntObjectMap<Group> playerGroupMap = new TIntObjectHashMap<>();


    /**
     * Получить ник игрока по его номеру
     *
     * @param playerId - номер игрока
     */
    @Deprecated
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
    @Deprecated
    public int getPlayerId(String playerName) {
        if (playerName == null)
            return -1;

        int playerId = playerIdByNamesMap.getOrDefault(playerName.toLowerCase(), -1);

        if (playerId < 0) {
            playerId = CoreConnector.getInstance().getMysqlConnection().executeQuery(false, "SELECT `Id`,LOWER(`Name`) FROM `PlayerIdentifier` WHERE `Name`=?",
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

    /**
     * Получить группу игрока по номеру самого игрока
     *
     * @param playerId - номер игрока
     */
    @Deprecated
    public Group getPlayerGroup(int playerId) {
        Group group = playerGroupMap.get(playerId);

        if (group == null) {
            group = CoreConnector.getInstance().getMysqlConnection().executeQuery(false, "SELECT * FROM `PlayerGroup` WHERE `Id`=?",
                    resultSet -> resultSet.next() ? Group.getGroupByLevel(resultSet.getInt("Group")) : null, playerId);

            if (group != null) {
                playerGroupMap.put(playerId, group);
            }
        }

        return group;
    }

}
