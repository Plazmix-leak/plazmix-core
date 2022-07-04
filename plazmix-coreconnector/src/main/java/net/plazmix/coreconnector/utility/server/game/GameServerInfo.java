package net.plazmix.coreconnector.utility.server.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.coreconnector.utility.JsonUtil;

@RequiredArgsConstructor
@Getter
public class GameServerInfo {

    private final String map;
    private final String mode;

    private final boolean available;

    private final int alivePlayers;
    private final int maxPlayers;

    public String toServerMotd() {
        return ("Game" + ("@") + JsonUtil.toJson(this));
    }

}
