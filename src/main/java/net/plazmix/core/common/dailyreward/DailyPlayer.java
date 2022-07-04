package net.plazmix.core.common.dailyreward;

import com.google.common.base.Joiner;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatMessageType;
import net.plazmix.core.api.chat.component.TextComponent;
import net.plazmix.core.api.module.execute.ModuleExecuteType;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;

import java.sql.Timestamp;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public final class DailyPlayer {

    public static final TIntObjectMap<DailyPlayer> DAILY_PLAYERS_MAP = new TIntObjectHashMap<>();

    public static DailyPlayer of(@NonNull String playerName) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);
        DailyPlayer dailyPlayer = DAILY_PLAYERS_MAP.get(playerId);

        if (dailyPlayer == null) {
            dailyPlayer = new DailyPlayer(playerId);

            DAILY_PLAYERS_MAP.put(playerId, dailyPlayer);
        }

        return dailyPlayer;
    }


    private final int playerId;

    private int lastRewardDay;
    private Timestamp lastRewardTimestamp;

    public void injectPlayer(Consumer<DailyPlayer> dailyPlayerConsumer) {
        PlazmixCore.getInstance().getMysqlConnection().executeQuery(true, "SELECT * FROM `PlayerRewards` WHERE `Id`=?",
                resultSet -> {

                    if (!resultSet.next()) {
                        this.lastRewardDay = 0;
                        this.lastRewardTimestamp = new Timestamp(0);

                    } else {

                        this.lastRewardDay = resultSet.getInt("LastReward");
                        this.lastRewardTimestamp = resultSet.getTimestamp("Date");
                    }

                    if (dailyPlayerConsumer != null)
                        dailyPlayerConsumer.accept(this);

                    return null;

                }, playerId);
    }

    public void addReward(@NonNull DailyRewardInfo dailyRewardInfo) {
        CorePlayer corePlayer = getCoreHandle();

        corePlayer.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(
                "§8§l■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■\n" +
                        "\n" +
                        "  §d§lPlazmix ▸ ЕЖЕДНЕВНЫЕ НАГРАДЫ\n" +
                        "\n" +
                        "  §7Вы забрали награду за §b" + dailyRewardInfo.getDayIndex() + " День\n" +
                        "   §7Вы получили: " + Joiner.on("§f, ").join(dailyRewardInfo.getDailyRewards().stream().map(DailyReward::getDisplay).collect(Collectors.toList())) + "\n" +
                        "\n" +
                        "§8§l■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■"));


        this.lastRewardTimestamp = new Timestamp(System.currentTimeMillis());
        this.lastRewardDay = dailyRewardInfo.getDayIndex();

        PlazmixCore.getInstance().getMysqlConnection().execute(true, "INSERT INTO `PlayerRewards` VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `LastReward`=?, `Date`=?",
                playerId,
                lastRewardDay, lastRewardTimestamp,
                lastRewardDay, lastRewardTimestamp);

        PlazmixCore.getInstance().executeBroadcast(ModuleExecuteType.INSERT, "TynixRewards", "PASS_REWARD", playerId);

        for (DailyReward dailyReward : dailyRewardInfo.getDailyRewards()) {
            dailyReward.getPlayerConsumer().accept(corePlayer);
        }
    }

    public CorePlayer getCoreHandle() {
        return PlazmixCore.getInstance().getOfflinePlayer(NetworkManager.INSTANCE.getPlayerName(playerId));
    }

}
