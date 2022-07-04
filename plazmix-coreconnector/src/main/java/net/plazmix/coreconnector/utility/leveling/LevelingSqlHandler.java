package net.plazmix.coreconnector.utility.leveling;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.module.type.NetworkModule;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LevelingSqlHandler {

    public static final LevelingSqlHandler INSTANCE = new LevelingSqlHandler();

    public static final String INSERT_EXPERIENCE_QUERY  = "INSERT INTO `PlayerLeveling` VALUES (?,?) ON DUPLICATE KEY UPDATE `Experience`=?";
    public static final String GET_EXPERIENCE_QUERY     = "SELECT * FROM `PlayerLeveling` WHERE `Id`=?";


    public final TIntIntMap playerExperienceMap = new TIntIntHashMap();

    public void setLevel(int playerId, int newLevel) {
        int experience = 0;

        for (int level = 0; level < newLevel; level++) {
            experience += LevelingUtil.getExpFromLevelToNext(level);
        }

        setTotalExperience(playerId, experience);
    }

    public void addLevel(int playerId, int levelToAdd) {
        int experience = 0;
        int levelsToAdd = (levelToAdd - getLevel(playerId));

        for (int level = 0; level < levelsToAdd; level++) {
            experience += LevelingUtil.getExpFromLevelToNext(level);
        }

        setTotalExperience(playerId, getTotalExperience(playerId) + experience);
    }

    public void removeLevel(int playerId, int levelToRemove) {
        int experience = 0;
        int levelsToRemove = (getLevel(playerId) - levelToRemove);

        for (int level = 0; level < levelsToRemove; level++) {
            experience += LevelingUtil.getExpFromLevelToNext(level);
        }

        setTotalExperience(playerId, getTotalExperience(playerId) - experience);
    }

    public int getLevel(int playerId) {
        return LevelingUtil.getLevel(getTotalExperience(playerId));
    }


    public int getMaxExperience(int playerId) {
        return (int) LevelingUtil.getExpFromLevelToNext(getLevel(playerId));
    }

    public int getPlayerExperience(int playerId) {
        int playerExperience = getTotalExperience(playerId);
        int totalExperienceToLevel = (int) LevelingUtil.getTotalExpToLevel(getLevel(playerId));

        return (getLevel(playerId) > 1 ? playerExperience - totalExperienceToLevel : playerExperience);
    }

    public int getTotalExperience(int playerId) {
        if (playerExperienceMap.containsKey(playerId)) {
            return playerExperienceMap.get(playerId);
        }

        int playerExperience = CoreConnector.getInstance().getMysqlConnection().executeQuery(false, GET_EXPERIENCE_QUERY, resultSet -> {

            if (!resultSet.next()) {
                CoreConnector.getInstance().getMysqlConnection().execute(true, INSERT_EXPERIENCE_QUERY, playerId, 0, 0);

                return 0;
            }

            return resultSet.getInt("Experience");
        }, playerId);

        playerExperienceMap.put(playerId, playerExperience);
        return playerExperience;
    }

    public int getTotalExperience(@NonNull String playerName) {
        return getTotalExperience(NetworkModule.getInstance().getPlayerId(playerName));
    }


    public void setTotalExperience(int playerId, int experience) {
        playerExperienceMap.put(playerId, experience);

        CoreConnector.getInstance().getMysqlConnection().execute(true, INSERT_EXPERIENCE_QUERY, playerId, experience, experience);
    }

    public void setTotalExperience(@NonNull String playerName, int experience) {
        setTotalExperience(NetworkModule.getInstance().getPlayerId(playerName), experience);
    }


    public void addTotalExperience(int playerId, int experience) {
        setTotalExperience(playerId, getTotalExperience(playerId) + experience);
    }

    public void addTotalExperience(@NonNull String playerName, int experience) {
        addTotalExperience(NetworkModule.getInstance().getPlayerId(playerName), experience);
    }


    public void removeTotalExperience(int playerId, int experience) {
        setTotalExperience(playerId, getTotalExperience(playerId) - experience);
    }

    public void removeTotalExperience(@NonNull String playerName, int experience) {
        addTotalExperience(NetworkModule.getInstance().getPlayerId(playerName), experience);
    }

}
