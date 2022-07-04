package net.plazmix.core.common.friend;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CoreFriend {

    public static final TIntObjectMap<CoreFriend> CORE_FRIEND_MAP = new TIntObjectHashMap<>();

    public static final String INJECT_QUERY = "SELECT * FROM `CoreFriends` WHERE `Id`=?";
    public static final String ADD_FRIEND_QUERY = "INSERT INTO `CoreFriends` VALUES (?, ?)";
    public static final String REMOVE_FRIEND_QUERY = "DELETE FROM `CoreFriends` WHERE `Id`=? AND `FriendId`=?";


    public static CoreFriend of(int playerId) {
        CoreFriend coreFriend = CORE_FRIEND_MAP.get(playerId);

        if (coreFriend == null) {
            CORE_FRIEND_MAP.put(playerId, coreFriend = new CoreFriend(playerId));
        }

        return coreFriend;
    }

    public static CoreFriend of(@NonNull String playerName) {
        return of(NetworkManager.INSTANCE.getPlayerId(playerName));
    }

    public static CoreFriend of(@NonNull CorePlayer corePlayer) {
        return of(corePlayer.getName());
    }


    @Getter
    private final int playerId;

    private final TIntList friendsIds = new TIntArrayList();


    public void inject(Consumer<CoreFriend> coreFriendConsumer) {
        friendsIds.clear();

        PlazmixCore.getInstance().getMysqlConnection().executeQuery(false, INJECT_QUERY,
                resultSet -> {

            while (resultSet.next()) {
                friendsIds.add( resultSet.getInt("FriendId") );
            }

            if (coreFriendConsumer != null) {
                coreFriendConsumer.accept(CoreFriend.this);
            }

            return null;
        }, playerId);
    }


    public String getName() {
        return NetworkManager.INSTANCE.getPlayerName(playerId);
    }

    public CorePlayer getOfflinePlayer() {
        return PlazmixCore.getInstance().getOfflinePlayer(getName());
    }


    public List<String> getFriendsPlayerNames() {
        return Arrays.stream(friendsIds.toArray()).mapToObj(NetworkManager.INSTANCE::getPlayerName)
                .collect(Collectors.toList());
    }

    public List<CoreFriend> getCoreFriends() {
        return Arrays.stream(friendsIds.toArray()).mapToObj(CoreFriend::of)
                .collect(Collectors.toList());
    }

    public List<CorePlayer> getFriendsOfflinePlayers(Predicate<CorePlayer> filter) {
       List<CorePlayer> friendsList = Arrays.stream(friendsIds.toArray())
                .mapToObj(playerId -> PlazmixCore.getInstance().getOfflinePlayer( NetworkManager.INSTANCE.getPlayerName(playerId) ))
                .collect(Collectors.toList());

       if (filter != null)
           return friendsList.stream().filter(filter).collect(Collectors.toList());

       return friendsList;
    }

    public List<CorePlayer> getFriendsOfflinePlayers() {
        return getFriendsOfflinePlayers(null);
    }


    public void addFriend(int friendId) {
        CoreFriend.of(friendId).friendsIds.add(playerId);
        friendsIds.add(friendId);

        PlazmixCore.getInstance().getMysqlConnection().execute(false, ADD_FRIEND_QUERY, playerId, friendId);
        PlazmixCore.getInstance().getMysqlConnection().execute(false, ADD_FRIEND_QUERY, friendId, playerId);
    }

    public void addFriend(@NonNull String playerName) {
        addFriend( NetworkManager.INSTANCE.getPlayerId(playerName) );
    }


    public void removeFriend(int friendId) {
        CoreFriend.of(friendId).friendsIds.remove(playerId);
        friendsIds.remove(friendId);

        PlazmixCore.getInstance().getMysqlConnection().execute(false, REMOVE_FRIEND_QUERY, playerId, friendId);
        PlazmixCore.getInstance().getMysqlConnection().execute(false, REMOVE_FRIEND_QUERY, friendId, playerId);
    }

    public void removeFriend(@NonNull String playerName) {
        removeFriend( NetworkManager.INSTANCE.getPlayerId(playerName) );
    }


    public boolean hasFriend(int playerId) {
        return friendsIds.contains(playerId);
    }

    public boolean hasFriend(@NonNull String playerName) {
        return hasFriend( NetworkManager.INSTANCE.getPlayerId(playerName) );
    }


    public int getFriendsCount() {
        return friendsIds.size();
    }

}
