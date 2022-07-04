package net.plazmix.core.connection.player.offline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.server.impl.BukkitServer;

import java.sql.Timestamp;
import java.util.function.Consumer;

@Getter
@Setter
@RequiredArgsConstructor
public class PlayerOfflineData {

    private final CorePlayer corePlayer;

    private String lastServerName;
    private Timestamp lastOnline;


    public BukkitServer getLastServer() {
        return PlazmixCore.getInstance().getBukkitServer(lastServerName);
    }

    public void save() {
        this.lastOnline = new Timestamp(System.currentTimeMillis());

        PlazmixCore.getInstance().getMysqlConnection()
                .execute(true, "INSERT INTO `PlayerLastOnline` (`Id`,`LastServer`,`LastOnline`) VALUES (?,?,?) ON DUPLICATE KEY UPDATE `LastServer`=?, `LastOnline`=?",
                        corePlayer.getPlayerId(),

                        lastServerName, lastOnline,
                        lastServerName, lastOnline);
    }

    public void load(Consumer<PlayerOfflineData> offlineDataConsumer) {
        PlazmixCore.getInstance().getMysqlConnection().executeQuery(false, "SELECT * FROM `PlayerLastOnline` WHERE `Id`=?",
                resultSet -> {

                    if (!resultSet.next()) {
                        this.lastServerName = "Auth-1"; // пусть по дефлоту будет эта параша
                        this.lastOnline = new Timestamp(System.currentTimeMillis());

                        return null;
                    }

                    this.lastServerName = resultSet.getString("LastServer");
                    this.lastOnline = resultSet.getTimestamp("LastOnline");

                    if (offlineDataConsumer != null) {
                        offlineDataConsumer.accept(this);
                    }

                    return null;

                }, corePlayer.getPlayerId());
    }

}
