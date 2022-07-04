package net.plazmix.joiner;

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
public final class JoinerManager {

    public static final JoinerManager INSTANCE = new JoinerManager();


    public static final String SET_SELECTED_MESSAGE_QUERY   = "INSERT INTO `SelectedJoinMessages` VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `Category`=?, `MessageId`=?";
    public static final String ADD_PURCHASED_MESSAGE_QUERY  = "INSERT INTO `PurchasedJoinMessages` VALUES (?, ?, ?)";

    public static final String INJECT_PURCHASED_QUERY       = "SELECT * FROM `PurchasedJoinMessages` WHERE `Id`=?";
    public static final String INJECT_SELECTED_QUERY        = "SELECT * FROM `SelectedJoinMessages` WHERE `Id`=?";
    

    private final Multimap<Integer, JoinMessage> purchasedMessagesMap   = HashMultimap.create();
    private final TIntObjectMap<JoinMessage> selectedMessagesMap        = new TIntObjectHashMap<>();


    public void setSelectedMessage(int messageId, int categoryOrdinal, @NonNull String playerName) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);

        selectedMessagesMap.put(playerId, JoinMessageCategory.values()[categoryOrdinal].getJoinMessage(messageId));

        PlazmixCore.getInstance().getMysqlConnection().execute(true, SET_SELECTED_MESSAGE_QUERY, playerId, categoryOrdinal, messageId, categoryOrdinal, messageId);
    }

    public void addPurchasedMessage(int messageId, int categoryOrdinal, @NonNull String playerName) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);

        purchasedMessagesMap.put(playerId, JoinMessageCategory.values()[categoryOrdinal].getJoinMessage(messageId));

        PlazmixCore.getInstance().getMysqlConnection().execute(true, ADD_PURCHASED_MESSAGE_QUERY, playerId, categoryOrdinal, messageId);
    }
    
    
    public boolean isPurchased(int messageId, int categoryId, @NonNull String playerName) {
        return getPurchasedMessages(playerName).stream().anyMatch(joinMessage -> joinMessage.getMessageId() == messageId && joinMessage.getCategoryId() == categoryId);
    }
    
    public boolean isSelected(int messageId, int categoryId, @NonNull String playerName) {
        JoinMessage joinMessage = getSelectedMessage(playerName);

        return joinMessage != null && joinMessage.getMessageId() == messageId && joinMessage.getCategoryId() == categoryId;
    }


    public Collection<JoinMessage> getPurchasedMessages(@NonNull String playerName) {
        return purchasedMessagesMap.get(NetworkManager.INSTANCE.getPlayerId(playerName));
    }

    public JoinMessage getSelectedMessage(@NonNull String playerName) {
        return selectedMessagesMap.get(NetworkManager.INSTANCE.getPlayerId(playerName));
    }


    public void injectPlayer(@NonNull CorePlayer corePlayer) {
        int playerId = corePlayer.getPlayerId();

        PlazmixCore.getInstance().getMysqlConnection().executeQuery(true, INJECT_PURCHASED_QUERY, resultSet -> {

            while (resultSet.next()) {
                JoinMessageCategory messageCategory = JoinMessageCategory.values()[resultSet.getInt("Category")];

                purchasedMessagesMap.put(playerId, messageCategory.getJoinMessage( resultSet.getInt("MessageId") ));
            }

            return null;
        }, playerId);

        PlazmixCore.getInstance().getMysqlConnection().executeQuery(true, INJECT_SELECTED_QUERY, resultSet -> {

            if (!resultSet.next()) {
                return null;
            }

            JoinMessageCategory messageCategory = JoinMessageCategory.values()[resultSet.getInt("Category")];
            selectedMessagesMap.put(playerId, messageCategory.getJoinMessage( resultSet.getInt("MessageId") ));

            return null;
        }, playerId);
    }
    
}
