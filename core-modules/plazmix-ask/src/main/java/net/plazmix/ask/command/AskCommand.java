package net.plazmix.ask.command;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatMessageType;
import net.plazmix.core.api.chat.JsonChatMessage;
import net.plazmix.core.api.chat.event.ClickEvent;
import net.plazmix.core.api.chat.event.HoverEvent;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.module.execute.ModuleExecuteType;
import net.plazmix.core.common.question.Question;
import net.plazmix.core.common.question.QuestionManager;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.Collection;

public class AskCommand extends CommandExecutor {

    public AskCommand() {
        super("question", "ask", "вопрос", "баг", "квестион");

        setOnlyAuthorized(true);
        setOnlyPlayers(true);
    }

    @Override
    protected void executeCommand(@NonNull CommandSender commandSender, @NonNull String[] args) {
        CorePlayer corePlayer = ((CorePlayer) commandSender);

        if (args.length == 0) {
            commandSender.sendMessage("§d§lPlazmix §8:: §fИспользуйте - §d/ask <вопрос>");
            return;
        }

        String playerQuestion = Joiner.on(" ").join(args);
        Question question = QuestionManager.INSTANCE.createQuestion(corePlayer, playerQuestion);

        if (!QuestionManager.INSTANCE.canQuestionAccept(question)) {
            commandSender.sendMessage("§d§lPlazmix §8:: §cОшибка, Вы привысили лимит по вопросам за раз: 1");

            QuestionManager.INSTANCE.removeQuestion(question);
            return;
        }

        Collection<CorePlayer> staffOnlineCollection = PlazmixCore.getInstance().getOnlinePlayers(corePlayer1 -> corePlayer1.getGroup().isStaff());
        corePlayer.sendMessage("§d§lPlazmix §8:: §fВаш вопрос был §aуспешно §fсоздан и отправлен персоналу проекта!");

        for (CorePlayer staffOnline : staffOnlineCollection) {

            staffOnline.sendMessage(ChatMessageType.CHAT, JsonChatMessage.create("§d§lPlazmix §8:: " + corePlayer.getDisplayName() + " §fзадал вопрос: §e" + question.getPlayerQuestion())
                    .addHover(HoverEvent.Action.SHOW_TEXT, "§eНажмите, чтобы ответить на вопрос")
                    .addClick(ClickEvent.Action.SUGGEST_COMMAND, "/ans " + commandSender.getName() + " ")
                    .build());
        }

        if (staffOnlineCollection.isEmpty())
            corePlayer.sendMessage("§cНа данный момент нет активного персонала на сервере, придется подождать, пока кто-то увидит!");

        PlazmixCore.getInstance().executeBroadcast(ModuleExecuteType.INSERT, "TynixAsk", "ACTIVE_QUESTIONS_COUNT",
                QuestionManager.INSTANCE.getActiveQuestions().size());
    }
}
