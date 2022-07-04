package net.plazmix.streams;

import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.CoreModuleInfo;
import net.plazmix.streams.command.StreamCommand;
import net.plazmix.streams.command.StreamsCommand;

@CoreModuleInfo(name = "PlazmixStreams", author = "Plazmix")
public class PlazmixStreams extends CoreModule {

    @Override
    protected void onEnable() {
        getManagement().registerCommand(new StreamCommand());
        getManagement().registerCommand(new StreamsCommand());
    }

    @Override
    protected void onDisable() {
        PlazmixCore.getInstance().getSchedulerManager().cancelScheduler("streams_update");
    }

}
