package net.plazmix.coreconnector.utility.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ServerSubMode {

    private final String name;
    private final String subPrefix;

    private final ServerSubModeType type;
}
