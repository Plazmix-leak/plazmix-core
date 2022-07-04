package net.plazmix.friends.function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.function.Predicate;

@RequiredArgsConstructor
@Getter
public enum FriendsListFilter {

    BY_DONATE_GROUP("Только с платным статусом", corePlayer -> corePlayer.getGroup().isDonate()),
    BY_UNIVERSAL_GROUP("Только с универсальным статусом", corePlayer -> corePlayer.getGroup().isUniversal()),

    NO_FILTER("Без фильтра", corePlayer -> true),
    ;


    private final String sortingName;
    private final Predicate<CorePlayer> itemFilter;


    public FriendsListFilter next() {
        if (ordinal() == values().length - 1)
            return values()[0];

        return values()[ordinal() + 1];
    }

    public FriendsListFilter back() {
        if (ordinal() <= 0)
            return values()[values().length - 1];

        return values()[ordinal() - 1];
    }
}
