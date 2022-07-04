package net.plazmix.core.connection.server.mode;

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
