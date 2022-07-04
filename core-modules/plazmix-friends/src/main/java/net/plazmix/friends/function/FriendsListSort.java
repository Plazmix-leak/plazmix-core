package net.plazmix.friends.function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.Collections;
import java.util.Comparator;

@RequiredArgsConstructor
@Getter
public enum FriendsListSort {

    BY_ONLINE("По онлайну", Comparator.comparing(corePlayer -> corePlayer.isOnline() ? 0 : 1)),
    BY_STATUS("По статусу", Collections.reverseOrder(Comparator.comparing(corePlayer -> corePlayer.getGroup().getLevel()))),
    LINKED_VK("По привязанному VK", Comparator.comparing(corePlayer -> AuthManager.INSTANCE.getAuthPlayer(corePlayer.getPlayerId()).hasVKUser() ? 0 : 1)),
    ;


    private final String sortingName;
    private final Comparator<CorePlayer> friendComparator;


    public FriendsListSort next() {
        if (ordinal() == values().length - 1)
            return values()[0];

        return values()[ordinal() + 1];
    }

    public FriendsListSort back() {
        if (ordinal() <= 0)
            return values()[values().length - 1];

        return values()[ordinal() - 1];
    }
}
