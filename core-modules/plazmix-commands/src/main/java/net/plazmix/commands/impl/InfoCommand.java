package net.plazmix.commands.impl;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.JsonChatMessage;
import net.plazmix.core.api.chat.event.ClickEvent;
import net.plazmix.core.api.chat.event.HoverEvent;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.command.sender.ConsoleCommandSender;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;

public class InfoCommand extends CommandExecutor {

    public InfoCommand() {
        super("info", "инфо", "информация");

        setMinimalGroup(Group.GALAXY);
        setOnlyAuthorized(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, String[] args) {
        CorePlayer corePlayer;

        if (args.length == 0) {

            // Это если консоль пишет
            if (commandSender instanceof ConsoleCommandSender) {
                commandSender.sendLangMessage("INFO_HELP");
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

        commandSender.sendLangMessage("INFO", "%player%", corePlayer.getDisplayName(),
                "%id%", String.valueOf(playerId),
                "%group%", corePlayer.getGroup().getColouredName(),

                "%language%", corePlayer.getLanguageType().getDisplayName(),

                "%money%", NumberUtil.spaced(corePlayer.getCoins()),
                "%plazma%", NumberUtil.spaced(corePlayer.getPlazma()),

                "%level%", NumberUtil.spaced(corePlayer.getLevel()),
                "%exp%", NumberUtil.spaced(corePlayer.getExperience()),
                "%max_exp%", NumberUtil.spaced(corePlayer.getMaxExperience()));

        if (corePlayer.isOnline()) {
            commandSender.sendLangMessage("INFO_ONLINE",
                    "%server%", corePlayer.getBukkitServer().getName(),
                    "%version%", corePlayer.getMinecraftVersionName());

            JsonChatMessage.create(" §7[ Узнать PING ]")
                    .addHover(HoverEvent.Action.SHOW_TEXT, "Нажмите, чтобы §7узнать пинг игрока")
                    .addClick(ClickEvent.Action.RUN_COMMAND, "/ping " + corePlayer.getName())

                    .sendMessage(commandSender);

            if (commandSender.getGroup().isStaff()) {
                int vkId = AuthManager.INSTANCE.getAuthPlayer(playerId).getVkId();

                if (vkId > 0) {
                    JsonChatMessage.create(" §2[ Открыть VK ]")
                            .addHover(HoverEvent.Action.SHOW_TEXT, "Нажмите, чтобы §fоткрыть ссылку на §9VK §fигрока")
                            .addClick(ClickEvent.Action.OPEN_URL, "https://vk.com/id" + vkId)

                            .sendMessage(commandSender);
                }

                if (commandSender.getGroup().isStaff()) {

                    JsonChatMessage.create(" §7[ Наблюдать за игроком ]")
                            .addHover(HoverEvent.Action.SHOW_TEXT, "Нажмите, чтобы §7наблюдать за игроком")
                            .addClick(ClickEvent.Action.RUN_COMMAND, "/watch " + corePlayer.getName())

                            .sendMessage(commandSender);

                    JsonChatMessage.create(" §a[ Замутить ]")
                            .addHover(HoverEvent.Action.SHOW_TEXT, "Нажмите, чтобы §aзамутить игрока")
                            .addClick(ClickEvent.Action.SUGGEST_COMMAND, "/mute " + corePlayer.getName() + " ")

                            .sendMessage(commandSender);

                    JsonChatMessage.create(" §e[ Кикнуть ]")
                            .addHover(HoverEvent.Action.SHOW_TEXT, "Нажмите, чтобы §eкикнуть игрока")
                            .addClick(ClickEvent.Action.SUGGEST_COMMAND, "/kick " + corePlayer.getName() + " ")

                            .sendMessage(commandSender);

                    JsonChatMessage.create(" §c[ Заблокировать ]")
                            .addHover(HoverEvent.Action.SHOW_TEXT, "Нажмите, чтобы §cзаблокировать игрока")
                            .addClick(ClickEvent.Action.SUGGEST_COMMAND, "/ban " + corePlayer.getName() + " ")

                            .sendMessage(commandSender);

                }
                if (commandSender.getGroup().isAdmin()) {
                    JsonChatMessage.create(" §9[ Узнать IP ]")
                            .addHover(HoverEvent.Action.SHOW_TEXT, "Нажмите, чтобы §6узнать IP игрока")
                            .addClick(ClickEvent.Action.RUN_COMMAND, "/айпи " + corePlayer.getName())

                            .sendMessage(commandSender);

                    JsonChatMessage.create(" §4[ Выключить клиент ]")
                            .addHover(HoverEvent.Action.SHOW_TEXT, "Нажмите, чтобы §4выключить клиент игрока")
                            .addClick(ClickEvent.Action.RUN_COMMAND, "/crash " + corePlayer.getName())

                            .sendMessage(commandSender);
                }
            }

        } else {

            commandSender.sendLangMessage("INFO_OFFLINE",
                    "%server%", corePlayer.getPlayerOfflineData().getLastServerName(),
                    "%date%", NumberUtil.getTime(System.currentTimeMillis() - corePlayer.getPlayerOfflineData().getLastOnline().getTime()));

            int vkId = AuthManager.INSTANCE.getAuthPlayer(playerId).getVkId();

            if (vkId > 0) {
                JsonChatMessage.create(" §9[ Открыть VK ]")
                        .addHover(HoverEvent.Action.SHOW_TEXT, "Нажмите, чтобы §fоткрыть ссылку на §9VK §fигрока")
                        .addClick(ClickEvent.Action.OPEN_URL, "https://vk.com/id" + vkId)

                        .sendMessage(commandSender);
            }
        }
    }
}