package net.plazmix.economy.command;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.utility.ValidateUtil;
import net.plazmix.core.common.economy.EconomyManager;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.Arrays;

public class AdminEconomyCommand extends CommandExecutor {

    protected static final String DEFAULT_COINS_ARGUMENT = "coins";
    protected static final String DEFAULT_PLAZMA_ARGUMENT = "plazma";

    public AdminEconomyCommand() {
        super("economy", "econ", "eco", "эко", "экономика");

        setOnlyAuthorized(true);
        setMinimalGroup(Group.ADMIN);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        if (args.length == 0) {
            sendHelpMessage(commandSender, null);

            return;
        }

        String lowerArgument = (args[0].toLowerCase());
        switch (lowerArgument) {

            case "money":
            case "moneys":
            case "coin":
            case "coins": {

                executeArgument(commandSender, DEFAULT_COINS_ARGUMENT, Arrays.copyOfRange(args, 1, args.length));
                break;
            }

            case "plazma":
            case "плазма": {

                executeArgument(commandSender, DEFAULT_PLAZMA_ARGUMENT, Arrays.copyOfRange(args, 1, args.length));
                break;
            }

            default:
                sendHelpMessage(commandSender, null);
        }
    }

    protected void sendHelpMessage(@NonNull CommandSender commandSender, String argument) {
        commandSender.sendMessage("§d§lEconomy §8:: §fСписок доступных команд:");

        if (argument == null) {
            commandSender.sendMessage(" §8- §7Управление монетами - §d/eco coins");
            commandSender.sendMessage(" §8- §7Управление плазмой - §d/eco plazma");

        } else {

            commandSender.sendMessage(String.format(" §8- §7Узнать баланс игрока - §d/eco %s get <игрок>", argument));
            commandSender.sendMessage(String.format(" §8- §7Установить баланс игроку - §d/eco %s set <игрок> <кол-во>", argument));
            commandSender.sendMessage(String.format(" §8- §7Добавить к балансу игрока - §d/eco %s give <игрок> <кол-во>", argument));
            commandSender.sendMessage(String.format(" §8- §7Вычесть из баланса игрока - §d/eco %s take <игрок> <кол-во>", argument));
        }
    }

    protected void executeArgument(@NonNull CommandSender commandSender,
                                   @NonNull String argument,
                                   @NonNull String... args) {

        if (args.length == 0) {
            sendHelpMessage(commandSender, argument);

            return;
        }

        switch (args[0].toLowerCase()) {

            case "get": {
                if (args.length < 2) {
                    commandSender.sendMessage(String.format("§d§lEconomy §8:: §fИспользуйте - §d/eco %s get <игрок>", argument));
                    return;
                }

                CorePlayer corePlayer = PlazmixCore.getInstance().getOfflinePlayer(args[1]);

                if (corePlayer == null || corePlayer.getPlayerId() < 0) {
                    commandSender.sendLangMessage("NO_PLAYER");
                    return;
                }

                executeArgumentAction(EconomyArgumentAction.BALANCE_GET, argument, commandSender, corePlayer, -1);
                break;
            }

            case "set": {
                if (args.length < 3) {
                    commandSender.sendMessage(String.format("§d§lEconomy §8:: §fИспользуйте - §d/eco %s set <игрок> <кол-во>", argument));
                    return;
                }

                CorePlayer corePlayer = PlazmixCore.getInstance().getOfflinePlayer(args[1]);

                if (corePlayer == null || corePlayer.getPlayerId() < 0) {
                    commandSender.sendLangMessage("NO_PLAYER");
                    return;
                }

                if (ValidateUtil.isNumber(args[2])) {

                    executeArgumentAction(EconomyArgumentAction.BALANCE_SET, argument, commandSender, corePlayer, Integer.parseInt(args[2]));
                    break;
                }

                commandSender.sendMessage("§d§lEconomy §8:: §cОшибка, указанное количество не является числом!");
                break;
            }

            case "add":
            case "give": {
                if (args.length < 3) {
                    commandSender.sendMessage(String.format("§d§lEconomy §8:: §fИспользуйте - §d/eco %s give <игрок> <кол-во>", argument));
                    return;
                }

                CorePlayer corePlayer = PlazmixCore.getInstance().getOfflinePlayer(args[1]);

                if (corePlayer == null || corePlayer.getPlayerId() < 0) {
                    commandSender.sendLangMessage("NO_PLAYER");
                    return;
                }

                if (ValidateUtil.isNumber(args[2])) {

                    executeArgumentAction(EconomyArgumentAction.BALANCE_GIVE, argument, commandSender, corePlayer, Integer.parseInt(args[2]));
                    break;
                }

                commandSender.sendMessage("§d§lEconomy §8:: §cОшибка, указанное количество не является числом!");
                break;
            }

            case "remove":
            case "take": {

                if (args.length < 3) {
                    commandSender.sendMessage(String.format("§d§lEconomy §8:: §fИспользуйте - §d/eco %s take <игрок> <кол-во>", argument));
                    return;
                }

                CorePlayer corePlayer = PlazmixCore.getInstance().getOfflinePlayer(args[1]);

                if (corePlayer == null || corePlayer.getPlayerId() < 0) {
                    commandSender.sendLangMessage("NO_PLAYER");
                    return;
                }

                if (ValidateUtil.isNumber(args[2])) {

                    executeArgumentAction(EconomyArgumentAction.BALANCE_TAKE, argument, commandSender, corePlayer, Integer.parseInt(args[2]));
                    break;
                }

                commandSender.sendMessage("§6§lEconomy §8:: §cОшибка, указанное количество не является числом!");
                break;
            }

            default:
                sendHelpMessage(commandSender, argument);
        }
    }

    protected void executeArgumentAction(@NonNull EconomyArgumentAction economyArgumentAction,
                                         @NonNull String argument,

                                         @NonNull CommandSender commandSender,
                                         @NonNull CorePlayer targetPlayer,

                                         int amount) {

        EconomyManager economyManager = EconomyManager.INSTANCE;

        switch (argument.toLowerCase()) {
            case DEFAULT_COINS_ARGUMENT: {

                switch (economyArgumentAction) {
                    case BALANCE_GET: {
                        commandSender.sendMessage("§d§lEconomy §8:: §7Баланс данного игрока: §e" + economyManager.getPlayerCoins(targetPlayer.getName()) + " монет");
                        break;
                    }

                    case BALANCE_SET: {
                        economyManager.changePlayerCoins(targetPlayer.getName(), amount);
                        commandSender.sendMessage("§d§lEconomy §8:: §7Баланс игрока был изменен на §e" + economyManager.getPlayerCoins(targetPlayer) + " монет");

                        break;
                    }

                    case BALANCE_GIVE: {
                        economyManager.changePlayerCoins(targetPlayer.getName(),economyManager.getPlayerCoins(targetPlayer.getName()) + amount);
                        commandSender.sendMessage("§d§lEconomy §8:: §7Баланс игрока был изменен на §e" + economyManager.getPlayerCoins(targetPlayer) + " монет");

                        break;
                    }

                    case BALANCE_TAKE: {
                        economyManager.changePlayerCoins(targetPlayer.getName(), economyManager.getPlayerCoins(targetPlayer.getName()) - amount);
                        commandSender.sendMessage("§d§lEconomy §8:: §7Баланс игрока был изменен на §e" + economyManager.getPlayerCoins(targetPlayer) + " монет");

                        break;
                    }
                }

                break;
            }

            case DEFAULT_PLAZMA_ARGUMENT: {

                switch (economyArgumentAction) {
                    case BALANCE_GET: {
                        commandSender.sendMessage("§d§lEconomy §8:: §7Баланс данного игрока: §e" + economyManager.getPlayerPlazma(targetPlayer.getName()) + " плазмы");

                        break;
                    }

                    case BALANCE_SET: {
                        economyManager.changePlayerPlazma(targetPlayer.getName(), amount);
                        commandSender.sendMessage("§d§lEconomy §8:: §7Баланс игрока был изменен на §e" + economyManager.getPlayerPlazma(targetPlayer) + " плазмы");

                        break;
                    }

                    case BALANCE_GIVE: {
                        economyManager.changePlayerPlazma(targetPlayer.getName(),economyManager.getPlayerPlazma(targetPlayer.getName()) + amount);
                        commandSender.sendMessage("§d§lEconomy §8:: §7Баланс игрока был изменен на §e" + economyManager.getPlayerPlazma(targetPlayer) + " плазмы");

                        break;
                    }

                    case BALANCE_TAKE: {
                        economyManager.changePlayerPlazma(targetPlayer.getName(), economyManager.getPlayerPlazma(targetPlayer.getName()) - amount);
                        commandSender.sendMessage("§d§lEconomy §8:: §7Баланс игрока был изменен на §e" + economyManager.getPlayerPlazma(targetPlayer) + " плазмы");

                        break;
                    }
                }

                break;
            }

            default:
                sendHelpMessage(commandSender, argument);
        }
    }


    protected enum EconomyArgumentAction {

        BALANCE_GET,
        BALANCE_SET,
        BALANCE_GIVE,
        BALANCE_TAKE
    }

}
