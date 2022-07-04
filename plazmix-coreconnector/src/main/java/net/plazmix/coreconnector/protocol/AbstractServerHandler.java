package net.plazmix.coreconnector.protocol;

import lombok.NonNull;
import net.plazmix.core.protocol.ChannelWrapper;
import net.plazmix.core.protocol.handler.BothHandler;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.core.language.LanguageType;
import net.plazmix.coreconnector.module.BaseModuleManager;
import net.plazmix.coreconnector.module.BaseServerModule;
import net.plazmix.coreconnector.protocol.client.*;
import net.plazmix.coreconnector.utility.CoreReconnector;

public abstract class AbstractServerHandler implements BothHandler {

    protected ChannelWrapper channelWrapper;

    @Override
    public void channelActive(ChannelWrapper wrapper) {
        this.channelWrapper = wrapper;
        CoreConnector.getInstance().setMotd( CoreConnector.getInstance().getServerMotd() );

        CoreReconnector.disableReconnect();
    }

    @Override
    public void channelInactive() {
        CoreConnector.getInstance().setConnected(false);

        CoreReconnector.enableReconnect();
    }

    public void handle(@NonNull CGlobalOnlinePacket packet) {
        CoreConnector.getInstance().getServersOnlineMap().clear();
        CoreConnector.getInstance().getServersOnlineMap().putAll(packet.getServersMap());

        CoreConnector.getNetworkInstance().getServersOnlineMap().clear();
        CoreConnector.getNetworkInstance().getServersOnlineMap().putAll(packet.getServersMap());
    }

    public void handle(@NonNull CLanguagesReloadPacket packet) {

        for (LanguageType languageType : LanguageType.VALUES = LanguageType.values()) {
            languageType.getResource().initResources();
        }
    }

    public void handle(@NonNull CModuleDataUpdatePacket packet) {
        BaseServerModule serverModule = BaseModuleManager.INSTANCE.find(packet.getModule());

        if (serverModule != null) {
            serverModule.getContainer().handlePacket(packet);
        }
    }

    public void handle(@NonNull CRestartServerPacket packet) {
        // handle in to implements.
    }

    public void handle(@NonNull CPlayerLocaleUpdatePacket packet) {
        // handle in to implements.
    }

}
