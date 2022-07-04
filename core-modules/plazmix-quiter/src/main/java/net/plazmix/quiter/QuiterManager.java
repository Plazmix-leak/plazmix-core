package net.plazmix.quiter;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QuiterManager {

    public static final QuiterManager INSTANCE = new QuiterManager();


    public static final String SET_SELECTED_MESSAGE_QUERY   = "INSERT INTO `SelectedQuitMessages` VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `Category`=?, `MessageId`=?";
    public static final String ADD_PURCHASED_MESSAGE_QUERY  = "INSERT INTO `PurchasedQuitMessages` VALUES (?, ?, ?)";

    public static final String INJECT_PURCHASED_QUERY       = "SELECT * FROM `PurchasedQuitMessages` WHERE `Id`=?";
    public static final String INJECT_SELECTED_QUERY        = "SELECT * FROM `SelectedQuitMessages` WHERE `Id`=?";
    

    private final Multimap<Integer, QuitMessage> purchasedMessagesMap   = HashMultimap.create();
    private final TIntObjectMap<QuitMessage> selectedMessagesMap        = new TIntObjectHashMap<>();


    public void setSelectedMessage(int messageId, int categoryOrdinal, @NonNull String playerName) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);

        selectedMessagesMap.put(playerId, QuiterMessageCategory.values()[categoryOrdinal].getJoinMessage(messageId));

        PlazmixCore.getInstance().getMysqlConnection().execute(true, SET_SELECTED_MESSAGE_QUERY, playerId, categoryOrdinal, messageId, categoryOrdinal, messageId);
    }

    public void addPurchasedMessage(int messageId, int categoryOrdinal, @NonNull String playerName) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);

        purchasedMessagesMap.put(playerId, QuiterMessageCategory.values()[categoryOrdinal].getJoinMessage(messageId));

        PlazmixCore.getInstance().getMysqlConnection().execute(true, ADD_PURCHASED_MESSAGE_QUERY, playerId, categoryOrdinal, messageId);
    }
    
    
    public boolean isPurchased(int messageId, int categoryId, @NonNull String playerName) {
        return getPurchasedMessages(playerName).stream().anyMatch(quitMessage -> quitMessage.getMessageId() == messageId && quitMessage.getCategoryId() == categoryId);
    }
    
    public boolean isSelected(int messageId, int categoryId, @NonNull String playerName) {
        QuitMessage quitMessage = getSelectedMessage(playerName);

        return quitMessage != null && quitMessage.getMessageId() == messageId && quitMessage.getCategoryId() == categoryId;
    }


    public Collection<QuitMessage> getPurchasedMessages(@NonNull String playerName) {
        return purchasedMessagesMap.get(NetworkManager.INSTANCE.getPlayerId(playerName));
    }

    public QuitMessage getSelectedMessage(@NonNull String playerName) {
        return selectedMessagesMap.get(NetworkManager.INSTANCE.getPlayerId(playerName));
    }


    public void injectPlayer(@NonNull CorePlayer corePlayer) {
        int playerId = corePlayer.getPlayerId();

        PlazmixCore.getInstance().getMysqlConnection().executeQuery(true, INJECT_PURCHASED_QUERY, resultSet -> {

            while (resultSet.next()) {
                QuiterMessageCategory messageCategory = QuiterMessageCategory.values()[resultSet.getInt("Category")];

                purchasedMessagesMap.put(playerId, messageCategory.getJoinMessage( resultSet.getInt("MessageId") ));
            }

            return null;
        }, playerId);

        PlazmixCore.getInstance().getMysqlConnection().executeQuery(true, INJECT_SELECTED_QUERY, resultSet -> {

            if (!resultSet.next()) {
                return null;
            }

            QuiterMessageCategory messageCategory = QuiterMessageCategory.values()[resultSet.getInt("Category")];
            selectedMessagesMap.put(playerId, messageCategory.getJoinMessage( resultSet.getInt("MessageId") ));

            return null;
        }, playerId);
    }
    
}
