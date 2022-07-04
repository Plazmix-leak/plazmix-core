package net.plazmix.economy.command;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.utility.ValidateUtil;
import net.plazmix.core.common.economy.EconomyManager;
import net.plazmix.core.connection.player.CorePlayer;

public class PayCommand extends CommandExecutor {

    public PayCommand() {
        super("pay", "передать", "заплатить");
        setOnlyAuthorized(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender,
                                  @NonNull String... args) {
        CorePlayer corePlayer = ((CorePlayer) commandSender);
        if (args.length < 2) {
            commandSender.sendMessage("§d§lPlazmix §8:: §fИспользуйте - §d/pay <игрок> <кол-во>");
            return;
        }

        CorePlayer targetPlayer = PlazmixCore.getInstance().getPlayer(args[0]);
        if (targetPlayer == null || targetPlayer.getPlayerId() < 0) {
            commandSender.sendLangMessage("NO_PLAYER");
            return;
        }

        if (corePlayer.getLevel() < 5) {
            commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, передача коинов доступна с 5 уровня");
            return;
        }

        if (targetPlayer.getName().equals(commandSender.getName())) {
            commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, Вы не можете перевести деньги себе");
            return;
        }

        if (!ValidateUtil.isNumber(args[1])) {
            commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, указанное количество не является числом!");
            return;
        }

        int payMoney = Integer.parseInt(args[1]);
        if (payMoney > corePlayer.getCoins()) {
            commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, у Вас недостаточно коинов!");
            return;
        }

        EconomyManager.INSTANCE.changePlayerCoins(targetPlayer.getName(), targetPlayer.getCoins() + payMoney);
        EconomyManager.INSTANCE.changePlayerCoins(corePlayer.getName(), corePlayer.getCoins() - payMoney);

        commandSender.sendMessage("§d§lPlazmix §8:: §fВы успешно переделали коины игроку " + targetPlayer.getDisplayName() + " §fв размере §a" + payMoney + " §fкоинов");
        targetPlayer.sendMessage("§d§lPlazmix §8:: §fВы получили перевод от игрока " + corePlayer.getDisplayName() + " §fв размере §a" + payMoney + " §fкоинов");
    }
}
