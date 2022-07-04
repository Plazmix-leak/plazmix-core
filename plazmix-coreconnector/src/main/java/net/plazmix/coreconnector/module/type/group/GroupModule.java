package net.plazmix.coreconnector.module.type.group;

import lombok.NonNull;
import net.plazmix.coreconnector.core.network.NetworkManager;
import net.plazmix.coreconnector.module.BaseServerModule;

public final class GroupModule extends BaseServerModule {

    public static final String GET_GROUP_KEY = "PLAYER_GROUP";

    public GroupModule() {
        super("TynixGroups");

        container.setOnReadPacketKey(GET_GROUP_KEY, int.class);
    }

    public Group getGroup(@NonNull String playerName) {
        return Group.fromLevel(container.readInt(GET_GROUP_KEY + "_" + playerName));
    }

    public Group getGroup(int playerId) {
        String playerName = NetworkManager.INSTANCE.getPlayerName(playerId);

        return playerName == null ? Group.DEFAULT : getGroup(playerName);
    }

}
