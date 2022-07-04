package net.plazmix.core.api.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.command.CommandManager;
import net.plazmix.core.api.command.sender.ConsoleCommandSender;

@RequiredArgsConstructor
@Log4j2
public class CoreLogger extends SimpleTerminalConsole {

    @Override
    protected boolean isRunning() {
        return PlazmixCore.getInstance().isRunning();
    }

    @Override
    protected void runCommand(String command) {
        CommandManager commandManager = PlazmixCore.getInstance().getCommandManager();

        if (!commandManager.dispatchCommand(ConsoleCommandSender.getInstance(), command)) {
            log.info(ChatColor.RED + "[!] Unknown command! :c");
        }
    }

    @Override
    protected void shutdown() {
        PlazmixCore.getInstance().shutdown();
    }

}
