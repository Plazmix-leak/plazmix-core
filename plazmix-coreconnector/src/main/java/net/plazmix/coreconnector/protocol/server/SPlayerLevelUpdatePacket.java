package net.plazmix.coreconnector.protocol.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.protocol.BukkitHandler;

@AllArgsConstructor
@NoArgsConstructor
public class SPlayerLevelUpdatePacket extends Packet<BukkitHandler> {

    private String playerName;

    private int level;
    private int experience;
    private int maxExperience;

    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) {
        BufferedQuery.writeString(playerName, byteBuf);

        BufferedQuery.writeVarInt(level, byteBuf);
        BufferedQuery.writeVarInt(experience, byteBuf);
        BufferedQuery.writeVarInt(maxExperience, byteBuf);
    }

}
