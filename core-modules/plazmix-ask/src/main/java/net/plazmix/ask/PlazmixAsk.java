package net.plazmix.ask;

import net.plazmix.ask.command.AnsCommand;
import net.plazmix.ask.command.AskCommand;
import net.plazmix.ask.listener.QuestionsListener;
import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.CoreModuleInfo;

@CoreModuleInfo(name = "PlazmixAsk", author = "Plazmix")
public class PlazmixAsk extends CoreModule {

    @Override
    protected void onEnable() {
        getManagement().registerCommand(new AnsCommand());
        getManagement().registerCommand(new AskCommand());

        getManagement().registerListener(new QuestionsListener());
    }

    @Override
    protected void onDisable() {}
}
