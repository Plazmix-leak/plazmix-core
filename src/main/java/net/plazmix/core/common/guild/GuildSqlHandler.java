package net.plazmix.core.common.guild;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.utility.JsonUtil;
import net.plazmix.core.common.network.NetworkManager;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class GuildSqlHandler {

// ============================================================================================================================= //

    public static final GuildSqlHandler INSTANCE = new GuildSqlHandler();

// ============================================================================================================================= //

    private static final String INSERT_GUILD_QUERY   = "INSERT INTO `CoreGuilds` VALUES (?, ?) ON DUPLICATE KEY UPDATE `Json`=?";
    private static final String DELETE_GUILD_QUERY   = "DELETE FROM `CoreGuilds` WHERE `Id`=?";

// ============================================================================================================================= //

    @Getter
    TIntObjectMap<CoreGuild> guildsCacheMap = new TIntObjectHashMap<>();

    public void loadGuilds() {
        PlazmixCore.getInstance().getMysqlConnection().executeQuery(true, "SELECT * FROM `CoreGuilds`;", resultSet -> {

            while (resultSet.next()) {

                CoreGuild guild = JsonUtil.fromJson(resultSet.getString("Json"), CoreGuild.class);
                guildsCacheMap.put(guild.getLeaderId(), guild);
            }

            return null;
        });
    }

    public void save(@NonNull CoreGuild coreGuild) {
        String json = JsonUtil.toJson(coreGuild);
        PlazmixCore.getInstance().getMysqlConnection().execute(true, INSERT_GUILD_QUERY, coreGuild.getLeaderId(), json, json);
    }

    public void deleteGuild(@NonNull CoreGuild coreGuild) {
        guildsCacheMap.remove(coreGuild.getLeaderId());

        PlazmixCore.getInstance().getMysqlConnection().execute(true, DELETE_GUILD_QUERY, coreGuild.getLeaderId());
    }

    public CoreGuild getFromPlayerId(int playerId) {
        for (CoreGuild coreGuild : guildsCacheMap.valueCollection()) {

            if (coreGuild.hasPlayer(playerId)) {
                return coreGuild;
            }
        }

        return null;
    }

    public CoreGuild getFromPlayerName(@NonNull String playerName) {
        return getFromPlayerId(NetworkManager.INSTANCE.getPlayerId(playerName));
    }

    public CoreGuild getFromTitle(@NonNull String guildTitle) {
        for (CoreGuild coreGuild : guildsCacheMap.valueCollection()) {

            if (coreGuild.getTitle().equalsIgnoreCase(guildTitle)) {
                return coreGuild;
            }
        }

        return null;
    }

}
