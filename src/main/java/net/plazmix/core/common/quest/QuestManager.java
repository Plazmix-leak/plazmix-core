package net.plazmix.core.common.quest;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Collection;

public final class QuestManager {

    public static final QuestManager INSTANCE = new QuestManager();

    private final TIntObjectMap<Quest> questsMap = new TIntObjectHashMap<>();


    public Collection<Quest> getRegisteredQuests() {
        return questsMap.valueCollection();
    }

    public void registerQuest(Quest quest) {
        questsMap.put(quest.getId(), quest);
    }

    public Quest quest(int questID) {
        return questsMap.get(questID);
    }

}
