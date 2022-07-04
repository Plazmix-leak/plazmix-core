package net.plazmix.core.common.economy.service;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.common.economy.EconomyService;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.protocol.server.SPlayerEconomyUpdatePacket;
import net.plazmix.core.connection.server.impl.BukkitServer;

import java.net.InetSocketAddress;
import java.sql.Timestamp;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlazmaEconomyService implements EconomyService {

    @Getter
    private static final PlazmaEconomyService instance = new PlazmaEconomyService();
    static {
        instance.initTable();
    }

    private static final String SAVE_TO_OUTLAY = ("INSERT INTO `PlayerEconomyOutlay` VALUES (?,?,?,?,?,?)");

    private static final String INSERT = ("INSERT INTO `PlazmaServiceData` (`Id`, `Value`) VALUES (?, ?)");
    private static final String SELECT = ("SELECT `Value` FROM `PlazmaServiceData` WHERE `Id`=?");

    private final TIntIntMap loadedUsers = new TIntIntHashMap();

    private void initTable() {
        PlazmixCore.getInstance().getMysqlConnection().createTable(false, "PlazmaServiceData",
                "`Id` INT NOT NULL, `Value` INT NOT NULL");
    }

    private void saveEconomyOutlay(int difference, int playerID, String playerName) {
        CorePlayer offlinePlayer = PlazmixCore.getInstance().getOfflinePlayer(playerName);

        InetSocketAddress playerAddress = offlinePlayer.getInetSocketAddress();
        BukkitServer playerBukkitServer = offlinePlayer.getBukkitServer();

        PlazmixCore.getInstance().getMysqlConnection().execute(true, SAVE_TO_OUTLAY, playerID, "PLAZMA",
                difference,

                (playerBukkitServer != null ? playerBukkitServer.getName() : null),
                (playerAddress != null ? playerAddress.getHostString() : null),

                new Timestamp(System.currentTimeMillis()));
    }

    private void sendEconomyUpdatePacket(String playerName) {

        for (BukkitServer bukkitServer : PlazmixCore.getInstance().getBukkitServers()) {
            bukkitServer.sendPacket(new SPlayerEconomyUpdatePacket(playerName, -1, this.get(playerName)));
        }
    }

    public void updateCacheData(int playerID, int value) {
        loadedUsers.put(playerID, value);
    }

    @Override
    public int get(String playerName) {
        int playerID = NetworkManager.INSTANCE.getPlayerId(playerName);

        if (loadedUsers.containsKey(playerID)) {
            return loadedUsers.get(playerID);
        }

        int value = PlazmixCore.getInstance().getMysqlConnection().executeQuery(false, SELECT,
                response -> response.next() ? response.getInt("Value") : 0, playerID);

        this.updateCacheData(playerID, value);
        return value;
    }

    @Override
    public void set(String playerName, int value) {
        int playerID = NetworkManager.INSTANCE.getPlayerId(playerName);
        int difference = (value - this.get(playerName));

        if (difference == 0) {
            return;
        }

        PlazmixCore.getInstance().getMysqlConnection().execute(true, INSERT, playerID, value);

        this.saveEconomyOutlay(difference, playerID, playerName);
        this.updateCacheData(playerID, value);
        this.sendEconomyUpdatePacket(playerName);
    }

    @Override
    public void add(String playerName, int value) {
        this.set(playerName, this.get(playerName) + value);
    }

    @Override
    public void take(String playerName, int value) {
        this.add(playerName, -value);
    }

    @Override
    public void multiply(String playerName, int value) {
        this.set(playerName, this.get(playerName) * value);
    }

    @Override
    public void divide(String playerName, int value) {
        this.set(playerName, this.get(playerName) / value);
    }

    @Override
    public void increment(String playerName) {
        this.add(playerName, 1);
    }

    @Override
    public void decrement(String playerName) {
        this.take(playerName, 1);
    }

}
