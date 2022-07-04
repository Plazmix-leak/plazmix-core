package net.plazmix.core.api.module.execute;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.connection.protocol.server.SModuleDataUpdatePacket;
import net.plazmix.core.connection.server.AbstractServer;

@RequiredArgsConstructor
@Getter
public class ModuleExecuteQuery {

    private final String moduleName;

    private final String key;
    private final Object value;

    private final ModuleExecuteType type;


    private SModuleDataUpdatePacket createPacket() {
        return new SModuleDataUpdatePacket(type, this);
    }

    public void executeBroadcast() {
        PlazmixCore.getInstance().broadcastPacket(createPacket());
    }

    public void execute(@NonNull AbstractServer server) {
        server.sendPacket(createPacket());
    }
}
