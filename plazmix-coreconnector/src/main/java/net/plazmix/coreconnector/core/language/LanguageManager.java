package net.plazmix.coreconnector.core.language;

import lombok.Getter;
import lombok.NonNull;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.core.network.NetworkManager;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public final class LanguageManager {

    public static final LanguageManager INSTANCE    = new LanguageManager();

    @Getter
    @Deprecated
    private final Map<Integer, LanguageType> playerLanguageMap = new HashMap<>();


    @Deprecated
    public final LanguageType getPlayerLanguage(@NonNull String playerName) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);
        LanguageType languageType = playerLanguageMap.get(playerId);

        try {
            if (languageType == null) {
                int languageOrdinal = CoreConnector.getInstance().getMysqlConnection().executeQuery(false, "SELECT * FROM `PlayerLanguage` WHERE `Id`=?",
                        resultSet -> resultSet.next() ? resultSet.getInt("Lang") : 0, playerId);

                playerLanguageMap.put(playerId, languageType = LanguageType.VALUES[ languageOrdinal ]);
            }

        } catch (Exception exception) {
            languageType = LanguageType.RUSSIAN;
        }

        return languageType;
    }

}
