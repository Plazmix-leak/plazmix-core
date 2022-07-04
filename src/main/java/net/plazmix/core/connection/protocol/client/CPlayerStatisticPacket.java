package net.plazmix.core.connection.protocol.client;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.api.utility.Statistic;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CPlayerStatisticPacket extends Packet<BukkitServer> {

    private String playerName;
    private Statistic statistic;

    private int value;

    @Override
    public void readPacket(@NonNull ByteBuf byteBuf) throws Exception {
        this.playerName = BufferedQuery.readString(byteBuf);

        this.statistic = Statistic.VALUES[BufferedQuery.readVarInt(byteBuf)];
        this.value = BufferedQuery.readVarInt(byteBuf);
    }

    @Override
    public void handle(@NonNull BukkitServer bukkitServer) throws Exception {
        bukkitServer.handle(this);
    }

}
