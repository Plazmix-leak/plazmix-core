package net.plazmix.coreconnector.protocol.client;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.protocol.BukkitHandler;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CPlayerEconomyUpdatePacket extends Packet<BukkitHandler> {

    private String playerName;

    private int coins;
    private int golds;

    @Override
    public void readPacket(@NonNull ByteBuf byteBuf) {
        this.playerName = BufferedQuery.readString(byteBuf);

        this.coins = BufferedQuery.readVarInt(byteBuf);
        this.golds = BufferedQuery.readVarInt(byteBuf);
    }

    @Override
    public void handle(BukkitHandler bukkitHandler) throws Exception {
        bukkitHandler.handle(this);
    }
}
