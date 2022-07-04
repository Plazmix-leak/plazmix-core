package net.plazmix.core.common.language;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.protocol.server.SPlayerLocaleUpdatePacket;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public final class LanguageManager {

    public static final LanguageManager INSTANCE = new LanguageManager();

    @Getter
    private final Map<Integer, Integer> playerLanguageMap = new HashMap<>();


    public final LanguageType getPlayerLanguage(@NonNull String playerName) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);
        int languageId = playerLanguageMap.getOrDefault(playerId, -1);

        try {
            if (languageId < 0) {
                languageId = PlazmixCore.getInstance().getMysqlConnection().executeQuery(false, "SELECT * FROM `PlayerLanguage` WHERE `Id`=?",
                        resultSet -> resultSet.next() ? resultSet.getInt("Lang") : 0, playerId);

                playerLanguageMap.put(playerId, languageId);
            }

        } catch (Exception ex) {
            return LanguageType.RUSSIAN;
        }

        return LanguageType.VALUES[ languageId ];
    }

    public final LanguageType getPlayerLanguage(@NonNull CorePlayer corePlayer) {
        return getPlayerLanguage(corePlayer.getName());
    }

    public final void updatePlayerLanguage(@NonNull String playerName, @NonNull LanguageType languageType) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);

        PlazmixCore.getInstance().getMysqlConnection().execute(true, "INSERT INTO `PlayerLanguage` VALUES (?,?) ON DUPLICATE KEY UPDATE `Lang`=?",
                playerId, languageType.ordinal(), languageType.ordinal());

        playerLanguageMap.put(playerId, languageType.ordinal());


        CorePlayer corePlayer = PlazmixCore.getInstance().getOfflinePlayer(playerName);
        corePlayer.setLanguageType(languageType);

        if (corePlayer.isOnline()) {
            corePlayer.getBukkitServer().sendPacket( new SPlayerLocaleUpdatePacket(playerName, languageType.ordinal()) );
            corePlayer.getBungeeServer().sendPacket( new SPlayerLocaleUpdatePacket(playerName, languageType.ordinal()) );
        }
    }

    public final void updatePlayerLanguage(@NonNull CorePlayer corePlayer, @NonNull LanguageType languageType) {
        updatePlayerLanguage(corePlayer.getName(), languageType);
    }

}
