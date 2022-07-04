package net.plazmix.core.common.question;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.Collection;
import java.util.stream.Collectors;

public final class QuestionManager {

    public static final QuestionManager INSTANCE = new QuestionManager();

    private final Multimap<String, Question> questionMap = HashMultimap.create();

    public Question createQuestion(@NonNull CorePlayer corePlayer, @NonNull String playerQuestion) {
        Question question = new Question(corePlayer.getName(), playerQuestion, System.currentTimeMillis());

        questionMap.put(corePlayer.getName().toLowerCase(), question);
        return question;
    }

    public boolean canQuestionAccept(@NonNull Question currentQuestion) {
        CorePlayer corePlayer = PlazmixCore.getInstance().getPlayer(currentQuestion.getPlayerName());
        return getPlayerQuestions(corePlayer.getName()).size() <= 1;
    }

    public Collection<Question> getPlayerQuestions(@NonNull String playerName) {
        return questionMap.get(playerName.toLowerCase());
    }

    public void removeQuestion(@NonNull Question question) {
        questionMap.remove(question.getPlayerName().toLowerCase(), question);
    }

    public Collection<Question> getActiveQuestions() {
        return questionMap.values().stream().filter(question -> (PlazmixCore.getInstance().getPlayer(question.getPlayerName()) != null))
                .collect(Collectors.toCollection(java.util.ArrayList::new));
    }
}
