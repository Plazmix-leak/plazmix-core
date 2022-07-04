package net.plazmix.coreconnector.module.type.party;

import net.plazmix.coreconnector.core.network.NetworkManager;
import net.plazmix.coreconnector.module.BaseServerModule;

public final class PartyModule extends BaseServerModule {

    public static final String PLAYER_PARTY_KEY = "PLAYER_PARTY";

    public PartyModule() {
        super("TynixParty");

        container.setOnReadPacketKey(PLAYER_PARTY_KEY, CoreParty.class);
    }


    public CoreParty getParty(int playerId) {
        String playerName = NetworkManager.INSTANCE.getPlayerName(playerId);
        return playerName == null ? null : getParty(playerName);
    }

    public CoreParty getParty(String playerName) {
        return container.read(CoreParty.class, PLAYER_PARTY_KEY + "_" + playerName);
    }

}
