package net.plazmix.core.common.party;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import lombok.Getter;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.Collection;
import java.util.stream.Collectors;

public final class PartyRequestManager {

    public static final PartyRequestManager INSTANCE = new PartyRequestManager();

    @Getter
    private final Multimap<Integer, Integer> partyRequestsIds = MultimapBuilder.SetMultimapBuilder
            .linkedHashKeys()
            .linkedHashSetValues().build();


    public void addPartyRequest(int playerId, int targetId) {
        partyRequestsIds.put(targetId, playerId);
    }

    public void removePartyRequest(int playerId, int targetId) {
        partyRequestsIds.remove(targetId, playerId);
    }

    public boolean hasPartyRequest(int playerId, int targetId) {
        return partyRequestsIds.containsEntry(targetId, playerId);
    }


    public Collection<Integer> getPartiesRequestsIds(int playerId) {
        return partyRequestsIds.get(playerId);
    }

    public Collection<CorePlayer> getOfflinePartiesIds(int playerId) {
        return getPartiesRequestsIds(playerId).stream()
                .map(targetId -> PlazmixCore.getInstance().getOfflinePlayer(NetworkManager.INSTANCE.getPlayerName(targetId)))
                .collect(Collectors.toList());
    }

    public Collection<Integer> removeAll(int playerId) {
        return partyRequestsIds.removeAll(playerId);
    }
}
