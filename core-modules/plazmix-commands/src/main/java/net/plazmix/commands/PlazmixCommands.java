package net.plazmix.commands;

import net.plazmix.commands.impl.*;
import net.plazmix.commands.inventory.donate.*;
import net.plazmix.commands.inventory.other.*;
import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.CoreModuleInfo;
import net.plazmix.core.common.group.command.GroupCommand;

@CoreModuleInfo(name = "PlazmixCommands", author = "Plazmix")
public class PlazmixCommands extends CoreModule {

    @Override
    protected void onEnable() {
        getManagement().registerCommand(new FindCommand());
        getManagement().registerCommand(new AlertCommand());
        getManagement().registerCommand(new HubCommand());
        getManagement().registerCommand(new BuildCommand());
        getManagement().registerCommand(new ServerCommand());
        getManagement().registerCommand(new CoreCommand());
        getManagement().registerCommand(new GroupCommand());
        getManagement().registerCommand(new OnlineCommand());
        getManagement().registerCommand(new StaffCommand());
        getManagement().registerCommand(new SendCommand());
        getManagement().registerCommand(new InfoCommand());
        getManagement().registerCommand(new WatchCommand());
        // getManagement().registerCommand(new LanguageCommand());
        getManagement().registerCommand(new ProfileCommand());
        getManagement().registerCommand(new FastMessageCommand());
        getManagement().registerCommand(new ModeCommand());
        getManagement().registerCommand(new DonateMenuCommand());
        getManagement().registerCommand(new IpCommand());
        getManagement().registerCommand(new BroadcastCommand());
        getManagement().registerCommand(new DonateListCommand());
        getManagement().registerCommand(new DonateInfoCommand());
        getManagement().registerCommand(new DonatePlazmaCommand());
        getManagement().registerCommand(new ThrowCommand());
        getManagement().registerCommand(new SelectorCommand());
    }

    @Override
    protected void onDisable() { }

}
