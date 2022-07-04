package net.plazmix.guilds;

import lombok.extern.log4j.Log4j2;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.CoreModuleInfo;
import net.plazmix.core.common.guild.GuildSqlHandler;
import net.plazmix.guilds.command.GuildCommand;
import net.plazmix.guilds.listener.PlayerListener;

@Log4j2
@CoreModuleInfo(name = "PlazmixGuilds", author = "Plazmix")
public final class PlazmixGuilds extends CoreModule {

    @Override
    protected void onEnable() {

        // Load guilds.
        log.info(ChatColor.YELLOW + "[PlazmixGuilds] :: Loading all guilds from MySql...");
        GuildSqlHandler.INSTANCE.loadGuilds();

        log.info(ChatColor.GREEN + "[PlazmixGuilds] :: Success loaded " + GuildSqlHandler.INSTANCE.getGuildsCacheMap().size() + " guilds!");

        // Register commands.
        getManagement().registerCommand(new GuildCommand());

        // Register listeners.
        getManagement().registerListener(new PlayerListener());
    }

    @Override
    protected void onDisable() {
    }
}
