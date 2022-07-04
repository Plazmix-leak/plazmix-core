package net.plazmix.ask.command;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.plazmix.core.common.question.QuestionManager;
import net.plazmix.core.common.question.Question;
import net.plazmix.ask.inventory.QuestionsMenu;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.Arrays;

public class AnsCommand extends CommandExecutor {

    public AnsCommand() {
        super("answer", "ans");

        setMinimalGroup(Group.JR_MODER);

        setOnlyAuthorized(true);
        setOnlyPlayers(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        CorePlayer corePlayer = (CorePlayer) commandSender;

        if (args.length == 0) {
            new QuestionsMenu().openInventory(corePlayer);
            return;
        }

        if (args.length < 2) {
            commandSender.sendMessage("§d§lPlazmix §8:: §fИспользуйте - §d/ans <игрок> <ответ>");
            return;
        }

        Question question = QuestionManager.INSTANCE.getPlayerQuestions(args[0]).stream().findFirst().orElse(null);

        if (question == null) {
            commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, вопрос данного игрока не найден!");
            return;
        }

        question.answer(corePlayer, ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(Arrays.copyOfRange((Object[]) args, 1, args.length))));
    }
}
