package net.plazmix.coreconnector.protocol.client;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.protocol.BukkitHandler;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class CPlayerChatPacket extends Packet<BukkitHandler> {

    private ChatMessageType chatMessageType;

    private String playerName;
    private BaseComponent[] baseComponents;

    @Override
    public void readPacket(@NonNull ByteBuf byteBuf) {
        this.chatMessageType = ChatMessageType.values()[BufferedQuery.readVarInt(byteBuf)];

        this.playerName = BufferedQuery.readString(byteBuf);
        this.baseComponents = ComponentSerializer.parse(BufferedQuery.readString(byteBuf));
    }

    @Override
    public void handle(@NonNull BukkitHandler bukkitHandler) throws Exception {
        bukkitHandler.handle(this);
    }
}
