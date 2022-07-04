package net.plazmix.coreconnector.module.type.economy;

import net.plazmix.coreconnector.core.network.NetworkManager;
import net.plazmix.coreconnector.module.BaseServerModule;

public final class EconomyModule extends BaseServerModule {

    public static final String PLAYER_COINS_KEY = "PLAYER_COINS";
    public static final String PLAYER_GOLDS_KEY = "PLAYER_GOLDS";

    public EconomyModule() {
        super("TynixEconomy");

        container.setOnReadPacketKey(PLAYER_COINS_KEY, int.class);
        container.setOnReadPacketKey(PLAYER_GOLDS_KEY, int.class);
    }


    public int getCoins(int playerId) {
        String playerName = NetworkManager.INSTANCE.getPlayerName(playerId);

        return playerName == null ? 0 : getCoins(playerName);
    }

    public int getCoins(String playerName) {
        return container.readInt(PLAYER_COINS_KEY + "_" + playerName);
    }


    public int getGolds(int playerId) {
        String playerName = NetworkManager.INSTANCE.getPlayerName(playerId);

        return playerName == null ? 0 : getGolds(playerName);
    }

    public int getGolds(String playerName) {
        return container.readInt(PLAYER_GOLDS_KEY + "_" + playerName);
    }

}
