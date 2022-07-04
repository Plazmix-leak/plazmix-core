package net.plazmix.coreconnector.core.group;

import lombok.NonNull;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.core.network.NetworkManager;

@Deprecated
public final class GroupManager {

    public static final GroupManager INSTANCE = new GroupManager();

    /**
     * Получить группу игрока по его нику
     *
     * @param playerName - ник игрока
     */
    @Deprecated
    public Group getPlayerGroup(@NonNull String playerName) {
        NetworkManager networkModuleManager = CoreConnector.getInstance().getCoreManager().getNetworkManager();
        int playerId = networkModuleManager.getPlayerId(playerName);

        return networkModuleManager.getPlayerGroup(playerId);
    }

}
