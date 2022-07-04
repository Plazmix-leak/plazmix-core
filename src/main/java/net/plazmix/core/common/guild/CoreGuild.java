package net.plazmix.core.common.guild;

import lombok.*;
import lombok.experimental.FieldDefaults;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.utility.JsonUtil;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@SuppressWarnings("all")
public class CoreGuild {

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GuildEconomy {

        int experience;
        int coins;
        int golds;

        public void addCoins(@NonNull int coins) {
            this.coins += coins;
        }

        public void removeCoins(@NonNull int coins) {
            this.coins -= coins;
        }

        public void addGolds(@NonNull int golds) {
            this.golds += golds;
        }

        public void removeGolds(@NonNull int golds) {
            this.golds -= golds;
        }

    }

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GuildShop {

        String tag;
        ChatColor color;
    }

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public enum GuildSettings {

        ECONOMY_AVAILABLE(Material.GOLD_INGOT, "Доступ к экономическим операциям", GuildStatus.MODER.getLevel()),
        TAG_VISIBILITY(Material.ANVIL, "Видимость тега над головой"),
        ;

        @NonNull Material icon;
        @NonNull String title;

        int bypassLevel;
    }

    @Getter
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public enum GuildStatus {

        MEMBER(0, ChatColor.GREEN, "[Участник]"),
        HELPER(1, ChatColor.AQUA, "[Помощник]"),
        MODER(2, ChatColor.RED, "[Модератор]"),
        LEADER(3, ChatColor.GOLD, "[Лидер]"),
        ;

        public static @NonNull GuildStatus fromLevel(int level) {
            for (GuildStatus status : GuildStatus.values()) {

                if (status.level == level) {
                    return status;
                }
            }

            return MEMBER;
        }

        @NonNull int level;

        @NonNull ChatColor color;
        @NonNull String name;

        public @NonNull String getDisplayName() {
            return (color.toString() + name);
        }
    }


    public static CoreGuild create(@NonNull String leaderName, @NonNull String title) {
        CoreGuild guild = CoreGuild.of(leaderName);

        if (guild != null) {
            return guild;
        }

        guild = new CoreGuild(leaderName, title);
        guild.save();

        GuildSqlHandler.INSTANCE.getGuildsCacheMap().put(guild.getLeaderId(), guild);

        return guild;
    }

    public static void delete(@NonNull CoreGuild guild) {
        GuildSqlHandler.INSTANCE.deleteGuild(guild);
    }


    public static CoreGuild of(int playerId) {
        return GuildSqlHandler.INSTANCE.getFromPlayerId(playerId);
    }

    public static CoreGuild of(@NonNull String playerName) {
        return GuildSqlHandler.INSTANCE.getFromPlayerName(playerName);
    }

    public static CoreGuild of(@NonNull CorePlayer corePlayer) {
        return GuildSqlHandler.INSTANCE.getFromPlayerId(corePlayer.getPlayerId());
    }


    @NonNull String leaderName;
    @NonNull String title;

    @NonNull Map<GuildSettings, Boolean> settingsMap = new LinkedHashMap<GuildSettings, Boolean>() {{

        for (GuildSettings guildSettings : GuildSettings.values()) {
            put(guildSettings, false);
        }
    }};

    @NonNull Map<Integer, GuildStatus> memberIdsMap = new HashMap<>();

    @NonNull GuildEconomy economy   = new GuildEconomy();
    @NonNull GuildShop shop         = new GuildShop();


    public int getLeaderId() {
        return NetworkManager.INSTANCE.getPlayerId(leaderName);
    }


    public void setSetting(@NonNull GuildSettings setting, boolean value) {
        settingsMap.put(setting, value);

        save();
    }

    public boolean getSetting(@NonNull GuildSettings setting) {
        return settingsMap.get(setting);
    }

    public void addPlayer(int playerId, @NonNull GuildStatus status) {
        memberIdsMap.put(playerId, status);

        save();
    }

    public void addPlayer(@NonNull String playerName, @NonNull GuildStatus status) {
        addPlayer(NetworkManager.INSTANCE.getPlayerId(playerName), status);
    }

    public void removePlayer(int playerId) {
        memberIdsMap.remove(playerId);

        save();
    }

    public void removePlayer(@NonNull String playerName) {
        removePlayer(NetworkManager.INSTANCE.getPlayerId(playerName));
    }

    public boolean hasPlayer(int playerId) {
        return memberIdsMap.containsKey(playerId);
    }

    public boolean hasPlayer(@NonNull String playerName) {
        return hasPlayer(NetworkManager.INSTANCE.getPlayerId(playerName));
    }


    public GuildStatus getStatus(int playerId) {
        return memberIdsMap.get(playerId);
    }

    public GuildStatus getStatus(@NonNull String playerName) {
        return getStatus(NetworkManager.INSTANCE.getPlayerId(playerName));
    }

    public void alert(@NonNull String alertMessage) {
        for (CorePlayer corePlayer : memberIdsMap.keySet().stream().map(PlazmixCore.getInstance()::getPlayer).collect(Collectors.toList())) {

            if (corePlayer != null && corePlayer.isOnline()) {
                corePlayer.sendMessage(alertMessage);
            }
        }
    }

    public @NonNull String toJson() {
        return JsonUtil.toJson(this);
    }

    public void save() {
        GuildSqlHandler.INSTANCE.save(this);
    }

}
