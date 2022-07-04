package net.plazmix.coreconnector.module.type.party;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class CoreParty {

    private final String leader;
    private final List<String> members = new ArrayList<>();

    public boolean isLeader(@NonNull String playerName) {
        return leader.equalsIgnoreCase(playerName);
    }

    public boolean isMember(@NonNull String playerName) {
        return members.contains(playerName.toLowerCase());
    }

}
