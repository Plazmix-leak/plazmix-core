package net.plazmix.auth;

import net.plazmix.auth.command.*;
import net.plazmix.auth.listener.AuthListener;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.CoreModuleInfo;
import net.plazmix.core.api.scheduler.CommonScheduler;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.concurrent.TimeUnit;

@CoreModuleInfo(name = "PlazmixAuth", author = "Plazmix")
public class PlazmixAuth extends CoreModule {

    @Override
    protected void onEnable() {
        getManagement().registerListener(new AuthListener());

        getManagement().registerCommand(new LoginCommand());
        getManagement().registerCommand(new LogoutCommand());
        getManagement().registerCommand(new ChangePasswordCommand());
        getManagement().registerCommand(new RegisterCommand());
        getManagement().registerCommand(new UnregisterCommand());

        new CommonScheduler() {

            @Override
            public void run() {

                for (CorePlayer corePlayer : PlazmixCore.getInstance().getOnlinePlayers()) {

                    if (!AuthManager.INSTANCE.hasAuthSession(corePlayer)) {
                        AuthListener.openAuthSession(corePlayer);
                    }
                }
            }

        }.runLater(1, TimeUnit.SECONDS);
    }

    @Override
    protected void onDisable() { }

}
