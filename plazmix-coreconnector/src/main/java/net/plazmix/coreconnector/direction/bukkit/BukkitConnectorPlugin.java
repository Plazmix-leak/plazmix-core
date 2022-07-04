package net.plazmix.coreconnector.direction.bukkit;

import com.comphenix.protocol.utility.MinecraftProtocolVersion;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.dytanic.cloudnet.wrapper.Wrapper;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.plazmix.coreconnector.direction.bukkit.listener.ShapedCoreInventoryListener;
import net.plazmix.coreconnector.direction.bukkit.listener.SkinsListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.direction.bukkit.listener.PlayerListener;
import net.plazmix.coreconnector.module.type.skin.PlayerSkin;
import net.plazmix.coreconnector.utility.mojang.MojangApi;
import net.plazmix.coreconnector.utility.mojang.MojangSkin;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class BukkitConnectorPlugin extends JavaPlugin {

    public static final SkinSetter SKIN_SETTER = new SkinSetter();

    @Setter
    @Getter
    private static List<String> commandList = new ArrayList<>();

    @Getter
    private static BukkitConnectorPlugin instance; {
        instance = this;
    }

    @Override
    public void onEnable() {
        // saveResource("config.properties", false);

        // TexturePackListener texturePackListener = new TexturePackListener();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "tc:setskin");
        // getServer().getMessenger().registerOutgoingPluginChannel(this, "tc:loadtextures");
        // getServer().getMessenger().registerIncomingPluginChannel(this, "tc:setresources", texturePackListener);

        CoreConnector.getInstance().createConnection(false, getServerName(), Bukkit.getMotd(), Bukkit.getIp(), Bukkit.getPort(),
                MinecraftProtocolVersion.getCurrentVersion());

        getServer().getPluginManager().registerEvents(new ShapedCoreInventoryListener(), this);
        // getServer().getPluginManager().registerEvents(new AfkListener(), this);
        // getServer().getPluginManager().registerEvents(texturePackListener, this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new SkinsListener(), this);
        // getServer().getPluginManager().registerEvents(new TabCompleteListener(), this);
    }

    @Override
    public void onDisable() {

        if (CoreConnector.getInstance().isConnected()) {
            CoreConnector.getInstance().getChannelWrapper().close(null);
        }
    }

    private String getServerName() {
        return Wrapper.getInstance().getServiceId().getTaskName() + "-" + Wrapper.getInstance().getServiceId().getTaskServiceId();
    }

    public static final class SkinSetter {

        public void updateSkin(@NonNull Player player, @NonNull PlayerSkin playerSkin) {
            updateSkin(player, playerSkin.getSkinObject());
        }

        public void updateSkin(@NonNull Player player, @NonNull MojangSkin mojangSkin) {
            updateSkin(player, mojangSkin.getValue(), mojangSkin.getSignature());
        }

        public void updateSkin(@NonNull Player player, @NonNull String skin) {
            MojangSkin mojangSkin = MojangApi.getMojangSkinOrDefault(skin);

            updateSkin(player, mojangSkin.getValue(), mojangSkin.getSignature());
        }

        public void updateSkin(@NonNull Player player, @NonNull String value, @NonNull String signature) {
            updateSkin(player.getName(), value, signature);
        }

        public void updateSkin(@NonNull String playerName, @NonNull String value, @NonNull String signature) {
            ByteArrayDataOutput output = ByteStreams.newDataOutput();

            output.writeUTF(playerName);
            output.writeUTF(value);
            output.writeUTF(signature);

            Bukkit.getServer().sendPluginMessage(BukkitConnectorPlugin.getInstance(), "tc:setskin", output.toByteArray());
        }
    }

}
