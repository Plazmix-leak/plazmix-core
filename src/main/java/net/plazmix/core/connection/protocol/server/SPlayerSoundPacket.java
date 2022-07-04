package net.plazmix.core.connection.protocol.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.api.sounds.SoundType;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;

@AllArgsConstructor
@NoArgsConstructor
public class SPlayerSoundPacket extends Packet<BukkitServer> {

    private String playerName;
    private SoundType soundType;
    private int pitch;
    private int volume;

    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) {
        BufferedQuery.writeString(playerName, byteBuf);
        BufferedQuery.writeEnum(soundType, byteBuf);
        BufferedQuery.writeVarInt(pitch, byteBuf);
        BufferedQuery.writeVarInt(volume, byteBuf);
    }

}
