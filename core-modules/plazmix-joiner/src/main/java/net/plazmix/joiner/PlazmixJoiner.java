package net.plazmix.joiner;

import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.CoreModuleInfo;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.joiner.command.JoinerCommand;
import net.plazmix.joiner.listener.PlayerListener;

@CoreModuleInfo(name = "PlazmixJoiner", author = "Plazmix")
public class PlazmixJoiner extends CoreModule {

    @Override
    protected void onEnable() {
        getManagement().registerCommand(new JoinerCommand());
        getManagement().registerListener(new PlayerListener());

        getCore().getMysqlConnection().createTable(true, "PurchasedJoinMessages", "`Id` INT NOT NULL, `Category` INT NOT NULL, `MessageId` INT NOT NULL");
        getCore().getMysqlConnection().createTable(true, "SelectedJoinMessages", "`Id` INT NOT NULL PRIMARY KEY, `Category` INT NOT NULL, `MessageId` INT NOT NULL");

        for (CorePlayer corePlayer : getCore().getOnlinePlayers()) {
            JoinerManager.INSTANCE.injectPlayer(corePlayer);
        }
    }

    @Override
    protected void onDisable() { }
}
