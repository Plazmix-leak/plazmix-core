package net.plazmix.core.common.group.command;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.sounds.SoundType;
import net.plazmix.core.api.utility.ValidateUtil;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.group.GroupManager;
import net.plazmix.core.connection.player.CorePlayer;

public class GroupCommand extends CommandExecutor {

    public GroupCommand() {
        super("group", "coregroup", "groups");

        setMinimalGroup(Group.SR_DEVELOPER);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, String[] args) {
        if (args.length == 0) {
            sendHelpMessage(commandSender);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "list":
                commandSender.sendMessage("§eСписок доступных групп:");

                StringBuilder stringBuilder = new StringBuilder();

                for (Group group : Group.GROUPS_ARRAY) {
                    stringBuilder.append(" - ")
                            .append(group.getColouredName())

                            .append(" §7(Level:")
                            .append(group.getLevel())
                            .append(", Name:")
                            .append(group.name())
                            .append(", Color:")
                            .append(group.getColor().getName().toUpperCase())
                            .append(")")

                            .append("\n");
                }

                commandSender.sendMessage(stringBuilder.toString());
                break;

            case "get":
                if (args.length < 2) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §fИспользуйте - §d/group get <ник игрока>");

                    break;
                }

                Group playerGroup = GroupManager.INSTANCE.getPlayerGroup(args[1]);

                if (playerGroup == null) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, группа данного игрока не найдена!");

                    return;
                }

                commandSender.sendMessage("§d§lPlazmix §8:: §fГруппа данного игрока - " + playerGroup.getColouredName());
                break;

            case "set": {
                if (args.length < 3) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §fИспользуйте - §d/group set <ник игрока> <группа (номер или имя)>");

                    break;
                }

                String currentPlayerName = args[1];

                CorePlayer targetPlayer = PlazmixCore.getInstance().getOfflinePlayer(currentPlayerName);

                if (targetPlayer == null || !targetPlayer.hasIdentifier()) {
                    commandSender.sendLangMessage("NO_PLAYER");
                    return;
                }

                Group groupToSet = ValidateUtil.isNumber(args[2]) ? Group.getGroupByLevel(Integer.parseInt(args[2])) : Group.getGroupByName(args[2]);

                if (groupToSet == null) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, данной группы не существует!");

                    break;
                }

                GroupManager.INSTANCE.setGroupToPlayer(currentPlayerName, groupToSet);
                targetPlayer.playSound(SoundType.BLOCK_ANVIL_USE, 1, 1);

                commandSender.sendMessage("§d§lPlazmix §8:: §fГруппа " + groupToSet.getColouredName() + " §fбыла выдана игроку §e" + currentPlayerName);
                break;
            }

            case "merge":
                if (args.length < 3) {
                    commandSender.sendMessage("§d§lPlazmix §8:: §fИспользуйте - §d/group merge <ник игрока> <новый ник игрока>");

                    break;
                }

                String currentPlayerName = args[1];
                String targetPlayerName = args[2];

                Group groupToMerge = GroupManager.INSTANCE.getPlayerGroup(currentPlayerName);
                Group secondGroupToMerge = GroupManager.INSTANCE.getPlayerGroup(targetPlayerName);

                if (groupToMerge.isDefault()) {
                    commandSender.sendMessage("§cОру, а какой смысл переносить группу игрока?");

                    break;
                }

                GroupManager.INSTANCE.setGroupToPlayer(currentPlayerName, secondGroupToMerge);
                GroupManager.INSTANCE.setGroupToPlayer(targetPlayerName, groupToMerge);

                commandSender.sendMessage("§d§lPlazmix §8:: §fГруппа " + groupToMerge.getColouredName() + " §fбыла перенесена игроку §e" + targetPlayerName);
                break;

            default:
                sendHelpMessage(commandSender);
        }
    }

    private void sendHelpMessage(@NonNull CommandSender commandSender) {
        commandSender.sendMessage("§d§lPlazmix §8:: §fПомощь по командам");
        commandSender.sendMessage("§fУстановить группу игроку - §b/group set <ник игрока> <группа (уровень или имя)>");
        commandSender.sendMessage("§fПеренести группу игроку - §b/group merge <ник игрока> <новый ник игрока>");
        commandSender.sendMessage("§fПосмотреть список групп - §b/group list");
        commandSender.sendMessage("§fУзнать группу игрока - §b/group get <ник игрока>");
    }

}
