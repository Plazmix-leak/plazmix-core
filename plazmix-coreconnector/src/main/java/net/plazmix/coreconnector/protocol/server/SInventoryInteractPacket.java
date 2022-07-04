package net.plazmix.coreconnector.protocol.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.event.inventory.ClickType;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.protocol.BukkitHandler;

@AllArgsConstructor
@NoArgsConstructor
@Setter
public class SInventoryInteractPacket extends Packet<BukkitHandler> {

    private String playerName;
    private int inventorySlot;

    private ClickType clickType;

    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) throws Exception {
        BufferedQuery.writeString(playerName, byteBuf);
        BufferedQuery.writeVarInt(inventorySlot, byteBuf);

        BufferedQuery.writeString(clickType.name(), byteBuf);
    }

}
