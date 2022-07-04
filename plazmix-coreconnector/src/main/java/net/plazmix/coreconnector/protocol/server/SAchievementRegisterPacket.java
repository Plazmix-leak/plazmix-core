package net.plazmix.core.connection.protocol.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.core.protocol.BufferedQuery;
import net.plazmix.core.protocol.Packet;
import net.plazmix.coreconnector.module.type.achievement.AchievementTask;
import net.plazmix.coreconnector.protocol.BukkitHandler;
import net.plazmix.coreconnector.utility.JsonUtil;
import org.bukkit.Material;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class SAchievementRegisterPacket extends Packet<BukkitHandler> {

    private int id;

    private Material icon;

    private List<AchievementTask> tasks;
    private List<String> rewardsTitles;

    @Override
    public void writePacket(@NonNull ByteBuf byteBuf) throws Exception {
        BufferedQuery.writeVarInt(id, byteBuf);
        BufferedQuery.writeVarInt(icon.getId(), byteBuf);

        BufferedQuery.writeArray(tasks.toArray(new AchievementTask[0]), byteBuf,
                (task, buf) -> BufferedQuery.writeString( JsonUtil.toJson(task), buf ));

        BufferedQuery.writeStringArray(rewardsTitles, byteBuf);
    }

}
