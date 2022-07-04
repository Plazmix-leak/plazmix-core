package net.plazmix.quiter;

import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.CoreModuleInfo;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.quiter.command.QuiterCommand;
import net.plazmix.quiter.listener.PlayerListener;

@CoreModuleInfo(name = "PlazmixQuiter", author = "Plazmix")
public class PlazmixQuiter extends CoreModule {

    @Override
    protected void onEnable() {
        getManagement().registerCommand(new QuiterCommand());
        getManagement().registerListener(new PlayerListener());

        getCore().getMysqlConnection().createTable(true, "PurchasedQuitMessages", "`Id` INT NOT NULL, `Category` INT NOT NULL, `MessageId` INT NOT NULL");
        getCore().getMysqlConnection().createTable(true, "SelectedQuitMessages", "`Id` INT NOT NULL PRIMARY KEY, `Category` INT NOT NULL, `MessageId` INT NOT NULL");

        for (CorePlayer corePlayer : getCore().getOnlinePlayers()) {
            QuiterManager.INSTANCE.injectPlayer(corePlayer);
        }
    }

    @Override
    protected void onDisable() { }
}
