package net.plazmix.coreconnector.core.auth;

import com.google.common.hash.Hashing;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.core.network.NetworkManager;
import net.plazmix.coreconnector.utility.server.ServerMode;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Deprecated
@Getter
public final class AuthManager {

    public static final AuthManager INSTANCE = new AuthManager();

    public static long EXPIRE_SESSION_MILLIS = TimeUnit.DAYS.toMillis(3);

    @Deprecated private final TIntObjectMap<AuthPlayer> authPlayerMap   = new TIntObjectHashMap<>();
    @Deprecated private final List<String> twoFactorLoginList           = new LinkedList<>();


    @Deprecated
    public boolean hasAuthSession(int playerId) {
        AuthPlayer authPlayer = getAuthPlayer(playerId);

        if (authPlayer == null) {
            return false;
        }

        if (ServerMode.isTyped(CoreConnector.getInstance().getServerName(), ServerMode.BUNGEE)) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(authPlayer.getPlayerName());

            if (player != null) {
                return authPlayer.getLastAddress() != null && authPlayer.getLastAddress().getHostName().equals(player.getAddress().getHostName()) &&

                        authPlayer.getExpireSessionTime() != null &&
                        authPlayer.getExpireSessionTime().getTime() - System.currentTimeMillis() > 0;
            } else {

                return authPlayer.getExpireSessionTime() != null &&
                        authPlayer.getExpireSessionTime().getTime() - System.currentTimeMillis() > 0;
            }

        } else {

            Player player = Bukkit.getPlayer(NetworkManager.INSTANCE.getPlayerName(playerId));

            if (player != null) {
                return authPlayer.getLastAddress() != null && authPlayer.getLastAddress().getHostName().equals(player.getAddress().getHostName()) &&

                        authPlayer.getExpireSessionTime() != null &&
                        authPlayer.getExpireSessionTime().getTime() - System.currentTimeMillis() > 0;
            } else {

                return authPlayer.getExpireSessionTime() != null &&
                        authPlayer.getExpireSessionTime().getTime() - System.currentTimeMillis() > 0;
            }
        }
    }

    @Deprecated
    public void removeSession(int playerId) {
        authPlayerMap.remove(playerId);
    }


    @Deprecated
    public boolean hasTwofactorSession(@NonNull String playerName) {
        return twoFactorLoginList.contains(playerName.toLowerCase());
    }

    @Deprecated
    public void addTwofactorSession(@NonNull String playerName) {
        twoFactorLoginList.add(playerName.toLowerCase());
    }

    @Deprecated
    public void removeTwofactorSession(@NonNull String playerName) {
        twoFactorLoginList.remove(playerName.toLowerCase());
    }


    public boolean hasPlayerAccount(int playerId) {
        return getAuthPlayer(playerId) != null && getAuthPlayer(playerId).getPlayerPassword() != null;
    }

    public AuthPlayer getAuthPlayer(int playerId) {
        AuthPlayer authPlayer = authPlayerMap.get(playerId);

        if (authPlayer != null) {
            return authPlayerMap.get(playerId);
        }

        authPlayer = new AuthPlayer( NetworkManager.INSTANCE.getPlayerName(playerId) );
        authPlayer.initialize();

        authPlayerMap.put(playerId, authPlayer);
        return authPlayer;
    }

    public AuthPlayer getAuthPlayer(@NonNull String playerName) {
        return getAuthPlayer( NetworkManager.INSTANCE.getPlayerId(playerName) );
    }

    public AuthPlayer findPlayer(@NonNull String address) {
        AuthPlayer authPlayer = authPlayerMap.valueCollection()
                .stream()
                .filter(player -> player.getLastAddress() != null && player.getLastAddress().getHostName().equals(address))
                .findFirst()
                .orElse(null);

        if (authPlayer != null) {
            return authPlayer;
        }

        return CoreConnector.getInstance().getMysqlConnection().executeQuery(false, "SELECT * FROM `PlayerAuth` WHERE `LastAddress`=?", resultSet -> {
            if (!resultSet.next()) {
                return null;
            }

            int playerId = resultSet.getInt("Id");

            AuthPlayer player = new AuthPlayer(NetworkManager.INSTANCE.getPlayerName(playerId));
            player.initialize();

            authPlayerMap.put(playerId, player);

            return player;
        }, address);
    }

    @SuppressWarnings("all")
    public String hashPassword(@NonNull String password) {
        return Hashing.sha512().hashString(Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString(), StandardCharsets.UTF_8).toString();
    }

}
