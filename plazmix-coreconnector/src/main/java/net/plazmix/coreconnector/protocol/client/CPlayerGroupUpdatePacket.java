package net.plazmix.coreconnector.protocol.client;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.core.group.Group;
import net.plazmix.coreconnector.protocol.BukkitHandler;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class CPlayerGroupUpdatePacket extends Packet<BukkitHandler> {

    private String playerName;
    private Group playerGroup;

    @Override
    public void readPacket(@NonNull ByteBuf byteBuf) throws Exception {
        this.playerName = BufferedQuery.readString(byteBuf);
        this.playerGroup = Group.getGroupByLevel( BufferedQuery.readVarInt(byteBuf) );
    }

    @Override
    public void handle(@NonNull BukkitHandler bukkitHandler) throws Exception {
        bukkitHandler.handle(this);
    }
}
