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
public class CPlayerLevelUpdatePacket extends Packet<BukkitServer> {

    private String playerName;

    private int level;
    private int experience;
    private int maxExperience;

    @Override
    public void readPacket(@NonNull ByteBuf byteBuf) {
        this.playerName = BufferedQuery.readString(byteBuf);

        this.level = BufferedQuery.readVarInt(byteBuf);
        this.experience = BufferedQuery.readVarInt(byteBuf);
        this.maxExperience = BufferedQuery.readVarInt(byteBuf);
    }

    @Override
    public void handle(@NonNull BukkitServer bukkitServer) {
        bukkitServer.handle(this);
    }
}
