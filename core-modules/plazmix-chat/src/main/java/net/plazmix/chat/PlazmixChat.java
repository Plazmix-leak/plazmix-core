package net.plazmix.chat;

import net.plazmix.chat.command.chat.DonatechatCommand;
import net.plazmix.chat.command.chat.StaffchatCommand;
import net.plazmix.chat.command.post.IgnoreAllCommand;
import net.plazmix.chat.command.post.IgnoreCommand;
import net.plazmix.chat.command.post.PostMessageCommand;
import net.plazmix.chat.command.post.ReplyCommand;
import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.CoreModuleInfo;

@CoreModuleInfo(name = "PlazmixChat", author = "Plazmix")
public class PlazmixChat extends CoreModule {

    @Override
    protected void onEnable() {
        getManagement().registerCommand(new DonatechatCommand());
        getManagement().registerCommand(new StaffchatCommand());

        getManagement().registerCommand(new IgnoreAllCommand());
        getManagement().registerCommand(new IgnoreCommand());
        getManagement().registerCommand(new PostMessageCommand());
        getManagement().registerCommand(new ReplyCommand());
    }

    @Override
    protected void onDisable() {
    }

}
