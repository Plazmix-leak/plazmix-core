package net.plazmix.gameselector;

import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.CoreModuleInfo;
import net.plazmix.gameselector.command.GameSelectorCommand;
import net.plazmix.gameselector.command.GameSpectatorCommand;

@CoreModuleInfo(name = "PlazmixGameSelector", author = "Plazmix")
public class PlazmixGameSelector extends CoreModule {

    @Override
    protected void onEnable() {
        getManagement().registerCommand(new GameSelectorCommand());
        getManagement().registerCommand(new GameSpectatorCommand());
    }

    @Override
    protected void onDisable() { }

}
