package net.plazmix.core.common.party;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PartyManager {

    public static final PartyManager INSTANCE = new PartyManager();

    private final Map<String, Party> partyMap = new HashMap<>();


    public Set<Party> getAllParties() {
        return new HashSet<>(partyMap.values());
    }

    public void addMemberToParty(@NonNull Party coreParty, @NonNull CorePlayer corePlayer) {
        partyMap.put(corePlayer.getName().toLowerCase(), coreParty);
    }

    public void removeMemberToParty(@NonNull CorePlayer corePlayer) {
        partyMap.remove(corePlayer.getName().toLowerCase());
    }

    public Party getParty(@NonNull CorePlayer corePlayer) {
        return partyMap.get(corePlayer.getName().toLowerCase());
    }

    public Party createParty(@NonNull CorePlayer corePlayer) {
        Party party = new Party(corePlayer.getName());
        party.addMember(corePlayer);

        partyMap.put(corePlayer.getName().toLowerCase(), party);

        return party;
    }

    public void deleteParty(@NonNull Party party) {

        for (CorePlayer memberPlayer : party.getMembers().stream().map(PlazmixCore.getInstance()::getOfflinePlayer).collect(Collectors.toSet())) {
            party.removeMember(memberPlayer);
        }
    }

    public boolean hasParty(@NonNull CorePlayer corePlayer) {
        return getParty(corePlayer) != null;
    }

}
