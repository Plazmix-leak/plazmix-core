package net.plazmix.coreconnector.protocol.client;

import lombok.NonNull;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.protocol.AbstractServerHandler;

public class CLanguagesReloadPacket
        extends Packet<AbstractServerHandler> {

    @Override
    public void handle(@NonNull AbstractServerHandler abstractServerHandler) throws Exception {
        abstractServerHandler.handle(this);
    }

}
