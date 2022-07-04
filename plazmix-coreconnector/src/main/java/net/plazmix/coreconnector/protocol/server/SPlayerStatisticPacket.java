package net.plazmix.coreconnector.protocol.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Statistic;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.protocol.BukkitHandler;

@AllArgsConstructor
@NoArgsConstructor
public class SPlayerStatisticPacket extends Packet<BukkitHandler> {

    private String playerName;
    private Statistic statistic;

    private int value;

    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) {
        BufferedQuery.writeString(playerName, byteBuf);

        BufferedQuery.writeVarInt(statistic.ordinal(), byteBuf);
        BufferedQuery.writeVarInt(value, byteBuf);
    }

}
