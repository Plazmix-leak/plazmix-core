package net.plazmix.myserver;

import lombok.Getter;
import lombok.SneakyThrows;
import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.CoreModuleInfo;
import net.plazmix.myserver.command.MyServerCommand;
import net.plazmix.myserver.listener.MyServerListener;
import net.plazmix.myserver.type.MyServerManager;
import net.plazmix.myserver.type.MyServerType;
import net.plazmix.myserver.type.PlayerMyServer;

import java.nio.file.Files;

@CoreModuleInfo(name = "PlazmixMyServer", author = "Plazmix")
public class PlazmixMyServer extends CoreModule {

    @Getter
    private static PlazmixMyServer instance; {
        instance = this;
    }

    @Override
    protected void onEnable() {
        createServersDirectories();

        getManagement().registerListener(new MyServerListener());
        getManagement().registerCommand(new MyServerCommand());
    }

    @Override
    protected void onDisable() {
        for (PlayerMyServer playerMyServer : MyServerManager.INSTANCE.getActiveServers()) {
            playerMyServer.shutdown();
        }
    }


    @SneakyThrows
    private void createServersDirectories() {
        for (MyServerType myServerType : MyServerType.SERVER_TYPES) {

            if (!Files.exists(myServerType.getServersFolder()))
                Files.createDirectories(myServerType.getServersFolder());
        }

        Files.createDirectories(getModuleFolder().toPath().resolve("RunningServers"));
    }

}
