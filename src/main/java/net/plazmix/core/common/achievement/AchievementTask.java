package net.plazmix.core.common.achievement;

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
public class AchievementTask {

    int id;

    @NonNull
    AchievementGroup group;

    @NonNull
    Achievement.AchievementDisplay display;

    @NonNull
    AchievementProgress progress;

    @NonNull
    TIntObjectMap<Achievement.AchievementTaskData> playerDataMap = new TIntObjectHashMap<>();


    public Achievement.AchievementTaskData newPlayerData(Achievement achievement, int playerId) {
        Achievement.AchievementTaskData playerData = playerDataMap.get(playerId);

        if (playerData != null) {
            return playerData;
        }

        playerData = Achievement.AchievementTaskData.fromAchievement(achievement, id, playerId);
        playerDataMap.put(playerId, playerData);

        return playerData;
    }

    void updatePlayerData(Achievement achievement, int playerId, Achievement.AchievementTaskData playerData) {
        PlazmixCore.getInstance().getMysqlConnection().execute(true, "INSERT INTO `PlayerAchievements` VALUES (?,?,?,?)",
                playerId, achievement.getId(), id, playerData.toJson());

        playerDataMap.put(playerId, playerData);
    }

    private Achievement.AchievementTaskData initPlayerData(Achievement achievement, int playerId) {
        return PlazmixCore.getInstance().getMysqlConnection().executeQuery(false,
                "SELECT * FROM `PlayerAchievements` WHERE `Id`=? AND `AchievementId`=? AND `TaskId`=?",
                resultSet -> {

                    if (!resultSet.next()) {
                        Achievement.AchievementTaskData newPlayerData = newPlayerData(achievement, playerId);
                        updatePlayerData(achievement, playerId, newPlayerData);

                        return newPlayerData;
                    }

                    return Achievement.AchievementTaskData.fromJson(resultSet.getString("TaskJson"));

                }, playerId, achievement.getId(), id);
    }

    public Achievement.AchievementTaskData getCachedPlayerData(Achievement achievement, int playerId) {
        if (!playerDataMap.containsKey(playerId)) {

            Achievement.AchievementTaskData playerData = initPlayerData(achievement, playerId);
            playerDataMap.put(playerId, playerData);

            return playerData;
        }

        return playerDataMap.get(playerId);
    }
}