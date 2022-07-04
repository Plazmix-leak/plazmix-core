package net.plazmix.guilds;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import lombok.Getter;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.Collection;
import java.util.stream.Collectors;

public final class GuildRequestManager {

    public static final GuildRequestManager INSTANCE = new GuildRequestManager();

    @Getter
    private final Multimap<Integer, Integer> guildRequestsIds = MultimapBuilder.SetMultimapBuilder
            .linkedHashKeys()
            .linkedHashSetValues().build();


    public void addGuildRequest(int playerId, int targetId) {
        guildRequestsIds.put(targetId, playerId);
    }

    public void removeGuildRequest(int playerId, int targetId) {
        guildRequestsIds.remove(targetId, playerId);
    }

    public boolean hasGuildRequest(int playerId, int targetId) {
        return guildRequestsIds.containsEntry(targetId, playerId);
    }


    public Collection<Integer> getGuildsRequestsIds(int playerId) {
        return guildRequestsIds.get(playerId);
    }

    public Collection<CorePlayer> getOfflineGuildIds(int playerId) {
        return getGuildsRequestsIds(playerId).stream()
                .map(targetId -> PlazmixCore.getInstance().getOfflinePlayer(NetworkManager.INSTANCE.getPlayerName(targetId)))
                .collect(Collectors.toList());
    }

    public Collection<Integer> removeAll(int playerId) {
        return guildRequestsIds.removeAll(playerId);
    }
}