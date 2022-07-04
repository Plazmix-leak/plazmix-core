package net.plazmix.party;

import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.CoreModuleInfo;
import net.plazmix.party.command.PartyCommand;
import net.plazmix.party.listener.PartyHavingListener;
import net.plazmix.party.listener.PartyWarpListener;

@CoreModuleInfo(name = "PlazmixParty", author = "Plazmix")
public class PlazmixParty extends CoreModule {

    @Override
    protected void onEnable() {
        getManagement().registerCommand(new PartyCommand());

        getManagement().registerListener(new PartyHavingListener());
        getManagement().registerListener(new PartyWarpListener());
    }

    @Override
    protected void onDisable() {
    }

}
