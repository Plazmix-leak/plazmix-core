package net.plazmix.coreconnector.direction.bukkit.listener;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.direction.bukkit.BukkitConnectorPlugin;
import net.plazmix.coreconnector.protocol.server.SPlayerChatPacket;
import net.plazmix.coreconnector.protocol.server.SPlayerCommandPacket;
import net.plazmix.coreconnector.protocol.server.SPlayerServerRedirectPacket;
import net.plazmix.coreconnector.utility.server.ServerMode;

public final class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().replaceFirst("/", "");

        if (commandIsExists(command)) {
            CoreConnector.getInstance().sendPacket(new SPlayerCommandPacket(player.getName(), command));

            event.setCancelled(true);
        }

        else if (ServerMode.isCurrentTyped(ServerMode.AUTH) || ServerMode.isCurrentTyped(ServerMode.LIMBO)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        SPlayerChatPacket playerMessagePacket = new SPlayerChatPacket(ChatMessageType.CHAT, player.getName(), TextComponent.fromLegacyText(event.getMessage()));
        CoreConnector.getInstance().sendPacket(playerMessagePacket);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String serverName = CoreConnector.getInstance().getServerName();

        new BukkitRunnable() {

            @Override
            public void run() {
                CoreConnector.getInstance().sendPacket(new SPlayerServerRedirectPacket(player.getName(), serverName, false));
            }

        }.runTaskLater(BukkitConnectorPlugin.getInstance(), 20);
    }

    /**
     * Проверяет, зарегистрирована ли команда
     *
     * @param commandName - имя команды
     */
    private boolean commandIsExists(String commandName) {
        String[] commandArg = commandName.replaceFirst("/", "").split(" ", -1);
        return BukkitConnectorPlugin.getCommandList().contains(commandArg[0].toLowerCase());
    }

}
