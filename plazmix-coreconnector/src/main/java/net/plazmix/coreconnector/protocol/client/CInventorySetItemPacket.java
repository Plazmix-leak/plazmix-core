package net.plazmix.coreconnector.protocol.client;

import io.netty.buffer.ByteBuf;
import lombok.*;
import org.bukkit.inventory.ItemStack;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.direction.bukkit.inventory.itemstack.CoreItemStack;
import net.plazmix.coreconnector.protocol.BukkitHandler;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CInventorySetItemPacket extends Packet<BukkitHandler> {

    private String playerName;

    private int slot;
    private ItemStack itemStack;

    @Override
    public void readPacket(@NonNull ByteBuf byteBuf) throws Exception {
        this.playerName = BufferedQuery.readString(byteBuf);
        this.slot = BufferedQuery.readVarInt(byteBuf);

        this.itemStack = CoreItemStack.parse(byteBuf).toBukkitItem();
    }

    @Override
    public void handle(@NonNull BukkitHandler bukkitHandler) throws Exception {
        bukkitHandler.handle(this);
    }
}
