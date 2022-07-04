package net.plazmix.coreconnector.protocol.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.protocol.BukkitHandler;

@AllArgsConstructor
@NoArgsConstructor
public class SPlayerChatPacket extends Packet<BukkitHandler> {

    private ChatMessageType chatMessageType;

    private String playerName;
    private BaseComponent[] baseComponents;

    @Override
    public void writePacket(ByteBuf byteBuf) {
        BufferedQuery.writeVarInt(chatMessageType.ordinal(), byteBuf);

        BufferedQuery.writeString(playerName, byteBuf);
        BufferedQuery.writeString(ComponentSerializer.toString(baseComponents), byteBuf);

        BufferedQuery.writeString(CoreConnector.getInstance().getServerName(), byteBuf);
    }

}
