package net.plazmix.core.connection.protocol.client;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.plazmix.core.api.chat.ChatMessageType;
import net.plazmix.core.api.chat.component.BaseComponent;
import net.plazmix.core.api.chat.serializer.ComponentSerializer;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CPlayerChatPacket extends Packet<BukkitServer> {

    private ChatMessageType chatMessageType;

    private String playerName;
    private BaseComponent[] baseComponents;

    private String serverName;

    @Override
    public void readPacket(ByteBuf byteBuf) throws Exception {
        this.chatMessageType = ChatMessageType.values()[BufferedQuery.readVarInt(byteBuf)];

        this.playerName = BufferedQuery.readString(byteBuf);
        this.baseComponents = ComponentSerializer.parse(BufferedQuery.readString(byteBuf));

        this.serverName = BufferedQuery.readString(byteBuf);
    }

    @Override
    public void handle(BukkitServer bukkitServer) throws Exception {
        bukkitServer.handle(this);
    }
}
