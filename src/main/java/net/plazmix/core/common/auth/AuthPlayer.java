package net.plazmix.core.common.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.event.impl.PlayerAuthCompleteEvent;
import net.plazmix.core.api.event.impl.PlayerAuthSendCodeEvent;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.protocol.server.SPlayerAuthCompletePacket;

import java.net.InetSocketAddress;
import java.sql.Timestamp;

@RequiredArgsConstructor
@Getter
@Setter
public class AuthPlayer {

    private final String playerName;
    private String playerPassword;

    private int vkId = -1;
    private String mail;

    private Timestamp registerDate;
    private Timestamp expireSessionTime;

    private InetSocketAddress registerAddress;
    private InetSocketAddress lastAddress;

    private boolean license;


    public void initialize() {
        PlazmixCore.getInstance().getMysqlConnection().executeQuery(true, "SELECT * FROM `PlayerAuth` WHERE `Id`=?", resultSet -> {
            if (!resultSet.next()) {
                return null;
            }

            // Initialize authorized player data.
            setVkId(resultSet.getInt("VkId"));

            setPlayerPassword(resultSet.getString("Password"));
            setMail(resultSet.getString("Mail"));

            setRegisterDate(resultSet.getTimestamp("RegisterDate"));
            setExpireSessionTime(resultSet.getTimestamp("ExpireSessionTime"));

            setRegisterAddress(new InetSocketAddress(resultSet.getString("RegisterAddress"), 0));
            setLastAddress(new InetSocketAddress(resultSet.getString("LastAddress"), 0));

            setLicense(resultSet.getBoolean("License"));
            return null;

        }, NetworkManager.INSTANCE.getPlayerId(playerName));
    }

    public void setNewPassword(String newPlayerPassword) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);

        if (newPlayerPassword == null) {
            logout();

            PlazmixCore.getInstance().getMysqlConnection().execute(true, "DELETE FROM `PlayerAuth` WHERE `Id`=?", playerId);
            return;
        }

        this.playerPassword = AuthManager.INSTANCE.hashPassword(newPlayerPassword);
        PlazmixCore.getInstance().getMysqlConnection().execute(true, "UPDATE `PlayerAuth` SET `Password`=? WHERE `Id`=?", playerPassword, playerId);
    }

    public void setNewMail(String mail) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);

        this.mail = mail;

        PlazmixCore.getInstance().getMysqlConnection().execute(true, "UPDATE `PlayerAuth` SET `Mail`=? WHERE `Id`=?", mail, playerId);
    }

    public void updateLastAddress() {
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);

        this.lastAddress = getHandle().getInetSocketAddress();

        PlazmixCore.getInstance().getMysqlConnection().execute(true, "UPDATE `PlayerAuth` SET `LastAddress`=? WHERE `Id`=?", lastAddress.getHostName(), playerId);
    }

    public void updateVkId() {
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);

        PlazmixCore.getInstance().getMysqlConnection().execute(true, "UPDATE `PlayerAuth` SET `VkId`=? WHERE `Id`=?", vkId, playerId);
    }

    public void updateSessionTime() {
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);

        this.expireSessionTime = new Timestamp(System.currentTimeMillis() + AuthManager.EXPIRE_SESSION_MILLIS);

        PlazmixCore.getInstance().getMysqlConnection().execute(true, "UPDATE `PlayerAuth` SET `ExpireSessionTime`=? WHERE `Id`=?", expireSessionTime, playerId);
    }

    public void unregister() {
        setNewPassword(null);
    }

    public boolean equalsPassword(String password) {
        return playerPassword != null && AuthManager.INSTANCE.hashPassword(password).equals(playerPassword);
    }

    public CorePlayer getHandle() {
        return PlazmixCore.getInstance().getPlayer(playerName);
    }

    public void logout() {
        CorePlayer corePlayer = getHandle();
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);

        if (corePlayer != null) {

            AuthManager.INSTANCE.removeSession(corePlayer);
            corePlayer.disconnect("§cВы вышли из аккаунта\n" +
                    "§cПерезайдите на сервер для повторной сессии!\n\n" + "§dwww.Plazmix.net");
        }

        this.expireSessionTime = null;

        PlazmixCore.getInstance().getMysqlConnection()
                .execute(true, "UPDATE `PlayerAuth` SET `ExpireSessionTime`=? WHERE `Id`=?", expireSessionTime, playerId);
    }

    public void complete() {
        AuthManager.INSTANCE.cacheSession(getHandle());

        getHandle().connectToAnyServer("hub");

        updateSessionTime();
        updateLastAddress();

        PlazmixCore.getInstance().getEventManager().callEvent(new PlayerAuthCompleteEvent(getHandle()));
    }

    public boolean hasVKUser() {
        return (vkId > 0);
    }

    public void completeWithTwofactorCode() {
        PlazmixCore.getInstance().broadcastBukkitPacket(new SPlayerAuthCompletePacket(playerName));

        if (hasVKUser()) {
            AuthManager.INSTANCE.addTwofactorSession(playerName);

            getHandle().connectToAnyServer("auth");
            PlazmixCore.getInstance().getEventManager().callEvent(new PlayerAuthSendCodeEvent(this));

        } else {

            this.complete();
        }
    }
}
