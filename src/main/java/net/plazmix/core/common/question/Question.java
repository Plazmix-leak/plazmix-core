package net.plazmix.core.common.question;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.module.execute.ModuleExecuteType;
import net.plazmix.core.connection.player.CorePlayer;

@RequiredArgsConstructor
@Getter
public class Question {

    private final String playerName;
    private final String playerQuestion;

    private final long questionDate;


    public void answer(@NonNull CorePlayer corePlayer, @NonNull String playerAnswer) {
        QuestionManager.INSTANCE.removeQuestion(this);
        CorePlayer offlineAuthor = PlazmixCore.getInstance().getOfflinePlayer(this.playerName);

        corePlayer.sendMessage("§d§lPlazmix §8:: §fОтвет на вопрос " + offlineAuthor.getDisplayName() + " был успешно отправлен!");
        offlineAuthor.sendMessage("§d§lPlazmix §8:: §fНа ваш вопрос ответил " + corePlayer.getDisplayName() + "§f: " +
                "\n §7Ответ: §e" + playerAnswer);

        for (CorePlayer staffOnline : PlazmixCore.getInstance().getOnlinePlayers(corePlayer1 -> (!corePlayer1.getName().equalsIgnoreCase(corePlayer.getName()) && corePlayer1.getGroup().isStaff()))) {
            staffOnline.sendMessage("§d§lPlazmix §8:: " + corePlayer.getDisplayName() + " §fответил на вопрос игрока " + offlineAuthor.getDisplayName() + " - §e" + playerAnswer);
        }

        PlazmixCore.getInstance().executeBroadcast(ModuleExecuteType.INSERT, "TynixAsk", "ACTIVE_QUESTIONS_COUNT",
                QuestionManager.INSTANCE.getActiveQuestions().size());
    }
}
