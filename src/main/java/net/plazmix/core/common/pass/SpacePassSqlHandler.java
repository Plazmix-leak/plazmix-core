package net.plazmix.core.common.pass;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpacePassSqlHandler {

    public static final SpacePassSqlHandler INSTANCE
            = new SpacePassSqlHandler();

    public static final String SELECT_PASS_QUERY            = "SELECT * FROM `PlayerPass` WHERE `Id`=?";
    public static final String INSERT_PASS_QUERY            = "INSERT INTO `PlayerPass` VALUES (?, ?, ?, ?)";
    public static final String PURCHASE_ACTIVATION_QUERY    = "UPDATE `PlayerPass` SET `Activation`=? WHERE `Id`=?";
    public static final String ADD_EXP_PASS_QUERY           = "UPDATE `PlayerPass` SET `Experience`=(`Experience`+?) WHERE `Id`=?";

    private final TIntObjectMap<SpacePass> playerPassesMap = new TIntObjectHashMap<>();

    public void cleanDatabase() {
        try {

            if (System.currentTimeMillis() > SpacePass.END_SEASON_MILLIS) {
                PlazmixCore.getInstance().getMysqlConnection().execute(true, "DELETE FROM `PlayerPass`");
            }

        } catch (Exception ignored) {
        }
    }

    public void purchaseActivation(int playerId) {
        PlazmixCore.getInstance().getMysqlConnection().execute(true, PURCHASE_ACTIVATION_QUERY, true, playerId);
    }

    public SpacePass getPlayerPass(int playerId) {
        if (playerId < 0) {
            return null;
        }

        SpacePass pass = playerPassesMap.get(playerId);

        if (pass == null) {
            pass = PlazmixCore.getInstance().getMysqlConnection().executeQuery(false, SELECT_PASS_QUERY, (resultSet) -> {

                if (resultSet.next()) {
                    return new SpacePass(playerId, resultSet.getTimestamp("Date"), resultSet.getInt("Experience"), resultSet.getBoolean("Activation"));
                }

                SpacePass sqlPass = new SpacePass(playerId, new Timestamp(System.currentTimeMillis()), 0, false);

                PlazmixCore.getInstance().getMysqlConnection().execute(true, INSERT_PASS_QUERY,
                        playerId, sqlPass.getPurchaseDate(), sqlPass.getExperience(), sqlPass.isActivated());

                return sqlPass;
            }, playerId);

            playerPassesMap.put(playerId, pass);
        }

        return pass;
    }

    public SpacePass getPlayerPass(@NonNull String playerName) {
        return getPlayerPass(NetworkManager.INSTANCE.getPlayerId(playerName));
    }

    public SpacePass getPlayerPass(@NonNull CorePlayer player) {
        return getPlayerPass(player.getName());
    }

}
