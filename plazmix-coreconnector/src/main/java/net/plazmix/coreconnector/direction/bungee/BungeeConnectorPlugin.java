package net.plazmix.coreconnector.direction.bungee;

import de.dytanic.cloudnet.wrapper.Wrapper;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.core.language.LanguageType;
import net.plazmix.coreconnector.direction.bungee.listener.*;
import net.plazmix.coreconnector.module.type.skin.PlayerSkin;
import net.plazmix.coreconnector.utility.MinecraftVersion;
import net.plazmix.coreconnector.utility.mojang.MojangApi;
import net.plazmix.coreconnector.utility.mojang.MojangSkin;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class BungeeConnectorPlugin extends Plugin {

    public static final SkinSetter SKIN_SETTER = new SkinSetter();

    @Getter
    private static BungeeConnectorPlugin instance; {
        instance = this;
    }

    @Override
    public void onEnable() {
        // saveResource();

        // Load languages.
        for (LanguageType languageType : LanguageType.VALUES) {
            languageType.getResource().initResources();

            System.out.println("[LanguageManager] :: Type " + languageType + " was success loaded!");
        }

        for (ListenerInfo listenerInfo : ProxyServer.getInstance().getConfigurationAdapter().getListeners()) {
            InetSocketAddress inetSocketAddress = listenerInfo.getHost();

            CoreConnector.getInstance().createConnection(true, getServerName(), listenerInfo.getMotd(), inetSocketAddress.getHostName(), inetSocketAddress.getPort(),
                    MinecraftVersion.V_1_12_2.getVersionId());
        }

        getProxy().registerChannel("tc:setskin");
        // getProxy().registerChannel("tc:loadtextures");
        // getProxy().registerChannel("tc:setresources");

        // getProxy().getPluginManager().registerListener(this, new TexturePackListener());
        getProxy().getPluginManager().registerListener(this, new TablistListener());
        getProxy().getPluginManager().registerListener(this, new SkinsListener());
        getProxy().getPluginManager().registerListener(this, new ConnectionListener());
        getProxy().getPluginManager().registerListener(this, new ChatListener());
        getProxy().getPluginManager().registerListener(this, new KickListener());
    }

    @Override
    public void onDisable() {
        CoreConnector.getInstance().getChannelWrapper().close(null);
    }

    private void saveResource() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        try {
            InputStream inputStream = CoreConnector.class.getClassLoader().getResourceAsStream("config.properties");
            Path path = getDataFolder().toPath().resolve("config.properties");

            if (inputStream == null) {
                return;
            }

            if (Files.notExists(path)) {
                Files.copy(inputStream, path);
            }
        }

        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private String getServerName() {
        return Wrapper.getInstance().getServiceId().getTaskName() + "-" + Wrapper.getInstance().getServiceId().getTaskServiceId();
    }

    public static final class SkinSetter {

        private static final Field PROFILE_FIELD;

        static {
            Field field = null;

            try {
                field = InitialHandler.class.getDeclaredField("loginProfile");
                field.setAccessible(true);

            } catch (Throwable t) {
                t.printStackTrace();
            }

            PROFILE_FIELD = field;
        }

        private LoginResult.Property toProperty(MojangSkin mojangSkin) {
            return new LoginResult.Property("textures", mojangSkin.getValue(), mojangSkin.getSignature());
        }

        private void setTexturesProperty(@NonNull ProxiedPlayer player, @NonNull PlayerSkin skin) {
            try {
                InitialHandler handler = (InitialHandler) player.getPendingConnection();
                LoginResult profile = (LoginResult) PROFILE_FIELD.get(handler);

                if (profile == null) {
                    profile = new LoginResult(player.getUUID(), player.getName(), new LoginResult.Property[]{toProperty(skin.getSkinObject())});

                    PROFILE_FIELD.set(handler, profile);
                } else {

                    List<LoginResult.Property> props = new ArrayList<>(Arrays.asList(profile.getProperties()));

                    props.removeIf(p -> p.getName().equals("textures"));
                    props.add(toProperty(skin.getSkinObject()));

                    profile.setProperties(props.toArray(new LoginResult.Property[0]));
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }


        public void updateSkin(ProxiedPlayer player, @NonNull PlayerSkin skin) {
            if (player == null) {
                return;
            }

            setTexturesProperty(player, skin);
        }

        public void updateSkin(@NonNull ProxiedPlayer player, @NonNull MojangSkin mojangSkin) {
            updateSkin(player, PlayerSkin.create(mojangSkin));
        }

        public void updateSkin(@NonNull ProxiedPlayer player, @NonNull String playerName) {
            updateSkin(player, PlayerSkin.create(playerName));
        }

        public void updateSkin(@NonNull ProxiedPlayer player, @NonNull String value, @NonNull String signature) {
            updateSkin(player, MojangApi.createSkinObject(value, signature));
        }
    }

}
