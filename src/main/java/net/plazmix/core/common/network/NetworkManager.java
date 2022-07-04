package net.plazmix.core.common.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.Getter;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.group.GroupManager;

@Getter
public final class NetworkManager {

    public static final NetworkManager INSTANCE = new NetworkManager();

    private final BiMap<Integer, String> playerIdsMap = HashBiMap.create();

    /**
     * Получить ник игрока по его номеру
     *
     * @param playerId - номер игрока
     */
    public String getPlayerName(int playerId) {
        String playerName = playerIdsMap.get(playerId);

        if (playerName == null) {
            playerName = PlazmixCore.getInstance().getMysqlConnection().executeQuery(false, "SELECT * FROM `PlayerIdentifier` WHERE `Id`=?",
                    resultSet -> resultSet.next() ? resultSet.getString("Name") : null, playerId);

            if (playerName != null) {
                playerIdsMap.put(playerId, playerName);
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
        int playerId = playerIdsMap.inverse().getOrDefault(playerName, -1);

        try {
            if (playerId < 0) {
                playerId = PlazmixCore.getInstance().getMysqlConnection().executeQuery(false, "SELECT `Id`,LOWER(`Name`) FROM `PlayerIdentifier` WHERE `Name`=?",
                        resultSet -> resultSet.next() ? resultSet.getInt("Id") : -1, playerName.toLowerCase());

                if (playerId > 0) {
                    playerIdsMap.put(playerId, playerName);
                }
            }
        } catch (Exception ex) {
            return -1;
        }

        return playerId;
    }

    /**
     * Получить группу игрока по номеру го игрока
     *
     * @param playerId - номер игрока
     */
    public Group getPlayerGroup(int playerId) {
        return GroupManager.INSTANCE.getPlayerGroup(playerId);
    }

    /**
     * Получить группу игрока по нику самого игрока
     *
     * @param playerName - ник игрока
     */
    public Group getPlayerGroup(@NonNull String playerName) {
        int playerId = getPlayerId(playerName);

        return getPlayerGroup(playerId);
    }

}
