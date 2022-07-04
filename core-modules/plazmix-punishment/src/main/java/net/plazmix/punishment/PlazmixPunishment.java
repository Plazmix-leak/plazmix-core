package net.plazmix.punishment;

import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.CoreModuleInfo;
import net.plazmix.core.api.scheduler.CommonScheduler;
import net.plazmix.core.common.punishment.PunishmentManager;
import net.plazmix.punishment.command.*;
import net.plazmix.punishment.listener.BanListener;
import net.plazmix.punishment.listener.MuteListener;

import java.util.concurrent.TimeUnit;

@CoreModuleInfo(name = "PlazmixPunishment", author = "Plazmix")
public class PlazmixPunishment extends CoreModule {

    protected CommonScheduler punishmentCleaner;

    @Override
    protected void onEnable() {
        createPunishmentCleaner();

        getManagement().registerCommand(new KickCommand());
        getManagement().registerCommand(new BanCommand());
        getManagement().registerCommand(new MuteCommand());
        getManagement().registerCommand(new UnbanCommand());
        getManagement().registerCommand(new UnmuteCommand());
        getManagement().registerCommand(new BanInfoCommand());

        getManagement().registerListener(new BanListener());
        getManagement().registerListener(new MuteListener());
    }

    @Override
    protected void onDisable() {
        if (punishmentCleaner != null) {
            punishmentCleaner.cancel();
        }
    }

    protected void createPunishmentCleaner() {
        this.punishmentCleaner = new CommonScheduler("punishmentCleaner228") {

            @Override
            public void run() {
                PunishmentManager.INSTANCE.getPunishmentMap().clear();
            }
        };

        punishmentCleaner.runTimer(0, 15, TimeUnit.MINUTES);
    }

}
