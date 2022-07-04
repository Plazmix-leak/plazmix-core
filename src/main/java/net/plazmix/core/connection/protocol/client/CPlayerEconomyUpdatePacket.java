package net.plazmix.core.connection.protocol.client;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CPlayerEconomyUpdatePacket extends Packet<BukkitServer> {

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
    public void handle(@NonNull BukkitServer bukkitServer) {
        bukkitServer.handle(this);
    }

}
