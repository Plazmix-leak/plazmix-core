package net.plazmix.coreconnector.core;

import lombok.Getter;
import lombok.NonNull;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.core.auth.AuthManager;
import net.plazmix.coreconnector.core.economy.EconomyManager;
import net.plazmix.coreconnector.core.group.GroupManager;
import net.plazmix.coreconnector.core.network.NetworkManager;
import net.plazmix.coreconnector.protocol.server.SPlayerCommandPacket;
import net.plazmix.coreconnector.protocol.server.SPlayerServerRedirectPacket;

@Deprecated
@Getter
public final class CoreManager {

    @Deprecated private final GroupManager groupManager         = GroupManager.INSTANCE;
    @Deprecated private final NetworkManager networkManager     = NetworkManager.INSTANCE;
    @Deprecated private final AuthManager authManager           = AuthManager.INSTANCE;
    @Deprecated private final EconomyManager economyManager     = EconomyManager.INSTANCE;


    @Deprecated
    public void dispatchCommand(@NonNull String playerName,
                                @NonNull String command) {

        SPlayerCommandPacket playerCommandPacket = new SPlayerCommandPacket(playerName, command);
        CoreConnector.getInstance().sendPacket(playerCommandPacket);
    }

    @Deprecated
    public void redirect(@NonNull String playerName,
                         @NonNull String serverName) {

        SPlayerServerRedirectPacket serverRedirectPacket = new SPlayerServerRedirectPacket(playerName, serverName, true);
        CoreConnector.getInstance().sendPacket(serverRedirectPacket);
    }

}
