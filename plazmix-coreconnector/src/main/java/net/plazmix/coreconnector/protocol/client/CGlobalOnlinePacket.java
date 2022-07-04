package net.plazmix.coreconnector.protocol.client;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.protocol.AbstractServerHandler;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class CGlobalOnlinePacket extends Packet<AbstractServerHandler> {

    private Map<String, Integer> serversMap = new HashMap<>();


    @Override
    public void readPacket(@NonNull ByteBuf byteBuf) {

        // Bukkit servers
        int bukkitCount = BufferedQuery.readVarInt(byteBuf);

        for (int i = 0 ; i < bukkitCount ; i++) {
            String bukkitName = BufferedQuery.readString(byteBuf);
            int bukkitOnline = BufferedQuery.readVarInt(byteBuf);

            serversMap.put(bukkitName, bukkitOnline);
        }

        // Bungee servers
        int bungeeCount = BufferedQuery.readVarInt(byteBuf);

        for (int i = 0 ; i < bungeeCount ; i++) {
            String bungeeName = BufferedQuery.readString(byteBuf);
            int bungeeOnline = BufferedQuery.readVarInt(byteBuf);

            serversMap.put(bungeeName, bungeeOnline);
        }
    }

    @Override
    public void handle(@NonNull AbstractServerHandler connectionHandler) {
        connectionHandler.handle(this);
    }
}
