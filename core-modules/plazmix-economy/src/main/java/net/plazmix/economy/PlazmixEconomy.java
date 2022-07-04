package net.plazmix.economy;

import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.CoreModuleInfo;
import net.plazmix.economy.command.AdminEconomyCommand;
import net.plazmix.economy.command.BalanceCommand;
import net.plazmix.economy.command.ConvertCommand;
import net.plazmix.economy.command.PayCommand;

@CoreModuleInfo(name = "PlazmixEconomy", author = "Plazmix")
public class PlazmixEconomy extends CoreModule {

    @Override
    protected void onEnable() {
        getManagement().registerCommand(new BalanceCommand());
        getManagement().registerCommand(new AdminEconomyCommand());
        getManagement().registerCommand(new PayCommand());
        getManagement().registerCommand(new ConvertCommand());
    }

    @Override
    protected void onDisable() { }

}
