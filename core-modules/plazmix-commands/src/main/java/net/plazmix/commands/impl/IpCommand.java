package net.plazmix.commands.impl;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.command.sender.ConsoleCommandSender;
import net.plazmix.core.api.utility.IpAddressUtil;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;

public class IpCommand extends CommandExecutor {

    public IpCommand() {
        // /IP какого то хуя блочится самим бомжекордом
        super("ip", "айпи", "ип");

        setOnlyAuthorized(true);
        setOnlyPlayers(true);
        setMinimalGroup(Group.ADMIN);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        CorePlayer corePlayer;

        if (args.length == 0) {

            // Это если консоль пишет
            if (commandSender instanceof ConsoleCommandSender) {
                commandSender.sendLangMessage("IP_HELP");
                return;
            }

            corePlayer = ((CorePlayer) commandSender);

        } else {

            corePlayer = PlazmixCore.getInstance().getOfflinePlayer(args[0]);
        }

        int playerId = corePlayer.getPlayerId();

        if (playerId < 0) {
            commandSender.sendLangMessage("NO_PLAYER");
            return;
        }

        commandSender.sendMessage("§d§lPlazmix §8:: §fIP игрока " + corePlayer.getDisplayName() + " - §c" + corePlayer.getInetSocketAddress().getHostString());

        IpAddressUtil.getAddressStats(corePlayer.getPlayerOfflineData().getCorePlayer().getInetSocketAddress(),
                (result, error) -> {

                    if (error != null) {
                        commandSender.sendMessage(ChatColor.RED + error.getCause().toString() + ": " + error.getMessage());
                        return;
                    }

                    commandSender.sendMessage("§d§lPlazmix §8:: §fСтатистика IP адреса игрока:");
                    commandSender.sendMessage(" §7Тип адреса: §e" + result.type);
                    commandSender.sendMessage(" §7Город: §e" + result.city);
                    commandSender.sendMessage(" §7Страна: §e" + result.country_name + " (" + result.location.capital + ")");
                    commandSender.sendMessage(" §7Координаты провайдера: §e" + result.latitude + ", " + result.longitude);
                });
    }
}