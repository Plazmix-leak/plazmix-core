package net.plazmix.coreconnector.protocol.client;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.protocol.BukkitHandler;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class CPlayerLocaleUpdatePacket extends Packet<BukkitHandler> {

    private String playerName;
    private int languageIndex;

    @Override
    public void readPacket(ByteBuf byteBuf) {
        this.playerName = BufferedQuery.readString(byteBuf);
        this.languageIndex = BufferedQuery.readVarInt(byteBuf);
    }

    @Override
    public void handle(@NonNull BukkitHandler bukkitHandler) throws Exception {
        bukkitHandler.handle(this);
    }
}
