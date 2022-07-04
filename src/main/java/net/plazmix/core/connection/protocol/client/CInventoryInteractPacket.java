package net.plazmix.core.connection.protocol.client;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.api.inventory.MouseAction;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CInventoryInteractPacket extends Packet<BukkitServer> {

    private String playerName;
    private int inventorySlot;

    private MouseAction mouseAction;

    @Override
    public void readPacket(@NonNull ByteBuf byteBuf) throws Exception {
        this.playerName = BufferedQuery.readString(byteBuf);
        this.inventorySlot = BufferedQuery.readVarInt(byteBuf);

        this.mouseAction = MouseAction.getMouseAction(BufferedQuery.readString(byteBuf));
    }

    @Override
    public void handle(@NonNull BukkitServer bukkitServer) throws Exception {
        bukkitServer.handle(this);
    }

}
