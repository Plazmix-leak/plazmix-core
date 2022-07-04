package net.plazmix.core.common.quest;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.plazmix.core.PlazmixCore;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class QuestTask {

    int id;

    @NonNull
    QuestGroup group;

    @NonNull
    Quest.QuestDisplay display;

    @NonNull
    QuestProgress progress;

    @NonNull
    TIntObjectMap<Quest.QuestTaskData> playerDataMap = new TIntObjectHashMap<>();


    public Quest.QuestTaskData newPlayerData(Quest quest, int playerId, long timeGetMillis) {
        Quest.QuestTaskData playerData = playerDataMap.get(playerId);

        if (playerData != null) {
            return playerData;
        }

        playerData = Quest.QuestTaskData.fromQuest(quest, id, playerId, timeGetMillis);
        playerDataMap.put(playerId, playerData);

        return playerData;
    }

    void updatePlayerData(Quest quest, int playerId, Quest.QuestTaskData playerData) {
        PlazmixCore.getInstance().getMysqlConnection().execute(true, "INSERT INTO `PlayerQuests` VALUES (?,?,?,?)",
                playerId, quest.getId(), id, playerData.toJson());

        playerDataMap.put(playerId, playerData);
    }

    private Quest.QuestTaskData initPlayerData(Quest quest, int playerId) {
        return PlazmixCore.getInstance().getMysqlConnection().executeQuery(false,
                "SELECT * FROM `PlayerQuests` WHERE `Id`=? AND `QuestId`=? AND `TaskId`=?",
                resultSet -> {

                    if (!resultSet.next()) {
                        Quest.QuestTaskData newPlayerData = newPlayerData(quest, playerId, System.currentTimeMillis());
                        updatePlayerData(quest, playerId, newPlayerData);

                        return newPlayerData;
                    }

                    return Quest.QuestTaskData.fromJson(resultSet.getString("TaskJson"));

                }, playerId, quest.getId(), id);
    }

    public Quest.QuestTaskData getCachedPlayerData(Quest quest, int playerId) {
        if (!playerDataMap.containsKey(playerId)) {

            Quest.QuestTaskData playerData = initPlayerData(quest, playerId);
            playerDataMap.put(playerId, playerData);

            return playerData;
        }

        return playerDataMap.get(playerId);
    }
}