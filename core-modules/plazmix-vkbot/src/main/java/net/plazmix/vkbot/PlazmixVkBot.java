package net.plazmix.vkbot;

import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.CoreModuleInfo;
import net.plazmix.core.api.scheduler.CommonScheduler;
import net.plazmix.vkbot.bot.VkBot;
import net.plazmix.vkbot.command.HelpCommand;
import net.plazmix.vkbot.command.account.AccountInfoCommand;
import net.plazmix.vkbot.command.account.AccountLinkCommand;
import net.plazmix.vkbot.command.account.AccountRecoveryCommand;
import net.plazmix.vkbot.command.account.AccountUnLinkCommand;
import net.plazmix.vkbot.command.admin.*;
import net.plazmix.vkbot.command.feature.*;
import net.plazmix.vkbot.listener.AuthCodeListener;
import net.plazmix.vkbot.listener.CoreListener;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@CoreModuleInfo(name = "PlazmixVkBot", author = "Plazmix")
public class PlazmixVkBot extends CoreModule {

    public static final String VKBOT_TASK_PATTERN = "vkbot-reconnect";
    private CommonScheduler reconnectTask;


    @Override
    protected void onEnable() {
        connectToVK();

        getManagement().registerListener(new AuthCodeListener());
        getManagement().registerListener(new CoreListener());

        startReconnectTask();
    }

    @Override
    protected void onDisable() {
        VkBot.INSTANCE.stop();

        reconnectTask.cancel();
    }

    protected void startReconnectTask() {
        this.reconnectTask = new CommonScheduler(VKBOT_TASK_PATTERN) {

            @Override
            public void run() {
                VkBot.INSTANCE.stop();

                connectToVK();
            }
        };

        reconnectTask.runTimer(2, 2, TimeUnit.HOURS);
    }

    protected void connectToVK() {
        try {
            VkBot.INSTANCE.runCallbackApi();

            VkBot.INSTANCE.registerCommand(new ServerRestartCommand());
            VkBot.INSTANCE.registerCommand(new ServerStopCommand());
            VkBot.INSTANCE.registerCommand(new ServerInfoCommand());
            VkBot.INSTANCE.registerCommand(new AlertCommand());
            VkBot.INSTANCE.registerCommand(new GroupCommand());
            VkBot.INSTANCE.registerCommand(new MergeCommand());
            VkBot.INSTANCE.registerCommand(new AdminRecoveryCommand());
            VkBot.INSTANCE.registerCommand(new PlayerAddressCommand());
            VkBot.INSTANCE.registerCommand(new ModuleCommand());
            VkBot.INSTANCE.registerCommand(new EconomyPlazmaCommand());
            VkBot.INSTANCE.registerCommand(new EconomyCoinsCommand());
            VkBot.INSTANCE.registerCommand(new MuteCommand());

            VkBot.INSTANCE.registerCommand(new ByeByeCommand());
            VkBot.INSTANCE.registerCommand(new OnlineStaffCommand());
            VkBot.INSTANCE.registerCommand(new PunishmentBanCommand());
            VkBot.INSTANCE.registerCommand(new PunishmentKickCommand());
            VkBot.INSTANCE.registerCommand(new PunishmentMuteCommand());
            VkBot.INSTANCE.registerCommand(new PunishmentUnbanCommand());
            VkBot.INSTANCE.registerCommand(new PhraseCommand());
            VkBot.INSTANCE.registerCommand(new TabCompleteCommand());
            VkBot.INSTANCE.registerCommand(new ServerChatCommand());

            VkBot.INSTANCE.registerCommand(new CheckNickCommand());
            VkBot.INSTANCE.registerCommand(new PlayerFindCommand());
            VkBot.INSTANCE.registerCommand(new OnlineCommand());
            VkBot.INSTANCE.registerCommand(new TwoFactorCallbackCommand(true));
            VkBot.INSTANCE.registerCommand(new TwoFactorCallbackCommand(false));

            VkBot.INSTANCE.registerCommand(new AccountLinkCommand());
            VkBot.INSTANCE.registerCommand(new AccountUnLinkCommand());
            VkBot.INSTANCE.registerCommand(new AccountInfoCommand());
            VkBot.INSTANCE.registerCommand(new AccountRecoveryCommand());

            VkBot.INSTANCE.registerCommand(new HelpCommand());
        }

        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
