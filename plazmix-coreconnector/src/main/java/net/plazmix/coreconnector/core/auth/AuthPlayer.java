package net.plazmix.coreconnector.core.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.core.network.NetworkManager;

import java.net.InetSocketAddress;
import java.sql.Timestamp;

@RequiredArgsConstructor
@Getter
@Setter
@Deprecated
public class AuthPlayer {

    @Deprecated private final String playerName;
    @Deprecated private String playerPassword;

    @Deprecated private int vkId = -1;
    @Deprecated private String mail;

    @Deprecated private Timestamp registerDate;
    @Deprecated private Timestamp expireSessionTime;

    @Deprecated private InetSocketAddress registerAddress;
    @Deprecated private InetSocketAddress lastAddress;

    @Deprecated private boolean license;


    @Deprecated
    public void initialize() {
        CoreConnector.getInstance().getMysqlConnection().executeQuery(true, "SELECT * FROM `PlayerAuth` WHERE `Id`=?", resultSet -> {
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

    @Deprecated
    public boolean equalsPassword(String password) {
        return playerPassword != null && AuthManager.INSTANCE.hashPassword(password).equals(playerPassword);
    }

    @Deprecated
    public boolean hasVKUser() {
        return (vkId > 0);
    }
}
