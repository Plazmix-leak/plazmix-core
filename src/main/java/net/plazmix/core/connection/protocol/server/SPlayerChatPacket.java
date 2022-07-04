package net.plazmix.core.connection.protocol.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.api.chat.ChatMessageType;
import net.plazmix.core.api.chat.component.BaseComponent;
import net.plazmix.core.api.chat.serializer.ComponentSerializer;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;

@NoArgsConstructor
@AllArgsConstructor
public class SPlayerChatPacket extends Packet<BukkitServer> {

    private ChatMessageType chatMessageType;

    private String playerName;
    private BaseComponent[] baseComponents;


    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) {
        BufferedQuery.writeVarInt(chatMessageType.ordinal(), byteBuf);

        BufferedQuery.writeString(playerName, byteBuf);
        BufferedQuery.writeString(ComponentSerializer.toString(baseComponents), byteBuf);
    }
}
