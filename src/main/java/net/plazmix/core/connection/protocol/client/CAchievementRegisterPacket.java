package net.plazmix.core.connection.protocol.client;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.utility.JsonUtil;
import net.plazmix.core.common.achievement.AchievementTask;
import net.plazmix.core.connection.server.impl.BukkitServer;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;

import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class CAchievementRegisterPacket extends Packet<BukkitServer> {

    private int id;

    private Material icon;

    private List<AchievementTask> tasks;
    private List<String> rewardsTitles;

    @Override
    public void readPacket(@NonNull ByteBuf byteBuf) throws Exception {
        this.id = BufferedQuery.readVarInt(byteBuf);
        this.icon = Material.getById( BufferedQuery.readVarInt(byteBuf) );

        this.tasks = Arrays.asList(BufferedQuery.readArray(byteBuf, AchievementTask[]::new,
                buf -> JsonUtil.fromJson( BufferedQuery.readString(buf), AchievementTask.class )));

        this.rewardsTitles = BufferedQuery.readStringArray(byteBuf);
    }

    @Override
    public void handle(@NonNull BukkitServer bukkitServer) throws Exception {
        bukkitServer.handle(this);
    }

}
