package net.plazmix.core.common.auth;

import com.google.common.hash.Hashing;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.protocol.server.SPlayerAuthCompletePacket;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
public final class AuthManager {

    public static final AuthManager INSTANCE = new AuthManager();

    public static long EXPIRE_SESSION_MILLIS = TimeUnit.DAYS.toMillis(3);


    private final TIntObjectMap<AuthPlayer> authPlayerMap   = new TIntObjectHashMap<>();
    private final List<String> twoFactorLoginList           = new LinkedList<>();

    public boolean hasAuthSession(@NonNull CorePlayer corePlayer) {
        AuthPlayer authPlayer = getAuthPlayer(corePlayer.getPlayerId());

        if (authPlayer == null) {
            return false;
        }

        return authPlayer.getLastAddress() != null && authPlayer.getLastAddress().getHostName().equals(corePlayer.getInetSocketAddress().getHostName()) &&

                authPlayer.getExpireSessionTime() != null &&
                authPlayer.getExpireSessionTime().getTime() - System.currentTimeMillis() > 0;
    }

    public void cacheSession(@NonNull CorePlayer corePlayer) {
        getAuthPlayer(corePlayer.getPlayerId()).updateSessionTime();
    }

    public void removeSession(@NonNull CorePlayer corePlayer) {
        authPlayerMap.remove(corePlayer.getPlayerId());
    }


    public boolean hasTwofactorSession(@NonNull String playerName) {
        return twoFactorLoginList.contains(playerName.toLowerCase());
    }

    public void addTwofactorSession(@NonNull String playerName) {
        twoFactorLoginList.add(playerName.toLowerCase());
    }

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

    public AuthPlayer getAuthPlayer(String playerName) {
        return getAuthPlayer( NetworkManager.INSTANCE.getPlayerId(playerName) );
    }

    @SuppressWarnings("all")
    public String hashPassword(@NonNull String password) {
        String salt = "f0e6%c7880e4wr7d3$4ce^%b^b3872ac47d*f570fer1^6";

        String level1 = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString() + salt;
        String level2 = Hashing.sha256().hashString(level1, StandardCharsets.UTF_8).toString();

        return level2;
    }

    public void registerPlayer(@NonNull String playerName,
                               @NonNull String playerPassword,

                               boolean playerLicense) {

        AuthPlayer authPlayer = getAuthPlayer(playerName);

        authPlayer.setPlayerPassword(playerPassword = hashPassword(playerPassword));
        authPlayer.setLicense(playerLicense);

        InetSocketAddress inetSocketAddress = authPlayer.getHandle().getInetSocketAddress();

        PlazmixCore.getInstance().getMysqlConnection().execute(true, "INSERT INTO `PlayerAuth` VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                NetworkManager.INSTANCE.getPlayerId(playerName),

                playerPassword, authPlayer.getVkId(), authPlayer.getMail(),
                new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis() + EXPIRE_SESSION_MILLIS),
                inetSocketAddress.getHostName(), inetSocketAddress.getHostName(), playerLicense);

        authPlayer.complete();
        PlazmixCore.getInstance().broadcastBukkitPacket(new SPlayerAuthCompletePacket(playerName));
    }

}
