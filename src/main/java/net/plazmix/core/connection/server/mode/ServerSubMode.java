package net.plazmix.core.connection.server.mode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ServerSubMode {

    private final String name;
    private final String subPrefix;

    private final ServerSubModeType type;
}
