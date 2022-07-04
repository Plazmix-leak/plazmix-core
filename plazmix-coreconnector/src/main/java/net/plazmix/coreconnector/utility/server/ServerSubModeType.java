package net.plazmix.coreconnector.utility.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ServerSubModeType {

    GAME_ARENA("Игровая арена"),
    GAME_LOBBY("Игровое лобби"),
    SURVIVAL("Выживание"),
    MAIN("Главное"),
    ;

    private final String displayName;
}
