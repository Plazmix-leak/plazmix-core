package net.plazmix.core.common.friend;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import lombok.Getter;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.Collection;
import java.util.stream.Collectors;

public final class FriendRequestManager {

    public static final FriendRequestManager INSTANCE = new FriendRequestManager();

    @Getter
    private final Multimap<Integer, Integer> friendRequestsIds = MultimapBuilder.SetMultimapBuilder
            .linkedHashKeys()
            .linkedHashSetValues().build();


    public void addFriendRequest(int playerId, int targetId) {
        friendRequestsIds.put(targetId, playerId);
    }

    public void removeFriendRequest(int playerId, int targetId) {
        friendRequestsIds.remove(targetId, playerId);
    }

    public boolean hasFriendRequest(int playerId, int targetId) {
        return friendRequestsIds.containsEntry(targetId, playerId);
    }


    public Collection<Integer> getFriendsRequestsIds(int playerId) {
        return friendRequestsIds.get(playerId);
    }

    public Collection<CorePlayer> getOfflineRequestsIds(int playerId) {
        return getFriendsRequestsIds(playerId).stream()
                .map(targetId -> PlazmixCore.getInstance().getOfflinePlayer(NetworkManager.INSTANCE.getPlayerName(targetId)))
                .collect(Collectors.toList());
    }

    public Collection<Integer> removeAll(int playerId) {
        return friendRequestsIds.removeAll(playerId);
    }
}
