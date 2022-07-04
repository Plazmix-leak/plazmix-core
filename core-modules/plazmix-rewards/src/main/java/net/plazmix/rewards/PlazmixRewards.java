package net.plazmix.rewards;

import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.CoreModuleInfo;
import net.plazmix.core.api.utility.DateUtil;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.group.GroupManager;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.rewards.command.RewardsCommand;
import net.plazmix.rewards.listener.PlayerListener;
import net.plazmix.core.common.dailyreward.DailyPlayer;
import net.plazmix.core.common.dailyreward.DailyReward;
import net.plazmix.core.common.dailyreward.DailyRewardManager;

import java.sql.Timestamp;
import java.util.TimeZone;

@CoreModuleInfo(name = "PlazmixRewards", author = "Plazmix")
public class PlazmixRewards extends CoreModule {

    @Override
    protected void onEnable() {
        addRewardsTypes();

        getCore().getMysqlConnection().createTable(true, "PlayerRewards", "`Id` INT NOT NULL PRIMARY KEY, `LastReward` INT NOT NULL, `Date` TIMESTAMP NOT NULL");
        cleanDatabase();

        getManagement().registerCommand(new RewardsCommand());
        getManagement().registerListener(new PlayerListener());

        for (CorePlayer corePlayer : PlazmixCore.getInstance().getOnlinePlayers()) {
            DailyPlayer.of(corePlayer.getName()).injectPlayer(null);
        }
    }

    @Override
    protected void onDisable() {
    }


    private void cleanDatabase() {
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis() + TimeZone.getTimeZone("Europe/Moscow").getOffset(System.currentTimeMillis()));

            if (Integer.parseInt(DateUtil.formatTime(timestamp.getTime(), "dd")) <= 1) {
                getCore().getMysqlConnection().execute(true, "DELETE FROM `PlayerRewards`");
            }

        } catch (Exception ignored) {
        }
    }

    private void addRewardsTypes() {

        // TODO: Убрать/заменить начисление монеток
        DailyRewardManager.INSTANCE.addDailyReward(1,
                new DailyReward("§fМонеты §7(§e50§7)", corePlayer -> corePlayer.addCoins(50)),
                new DailyReward("§fПлазма §7(§d1§7)", corePlayer -> corePlayer.addPlazma(1)));

        DailyRewardManager.INSTANCE.addDailyReward(2,
                new DailyReward("§fМонеты §7(§e80§7)", corePlayer -> corePlayer.addCoins(80)));

        DailyRewardManager.INSTANCE.addDailyReward(3,
                new DailyReward("§fМонеты §7(§e120§7)", corePlayer -> corePlayer.addCoins(120)));

        DailyRewardManager.INSTANCE.addDailyReward(4,
                new DailyReward("§fМонеты §7(§e48§7)", corePlayer -> corePlayer.addCoins(140)));

        DailyRewardManager.INSTANCE.addDailyReward(5,
                new DailyReward("§fМонеты §7(§e180§7)", corePlayer -> corePlayer.addCoins(180)));

        DailyRewardManager.INSTANCE.addDailyReward(6,
                new DailyReward("§fМонеты §7(§e300§7)", corePlayer -> corePlayer.addCoins(300)));

        DailyRewardManager.INSTANCE.addDailyReward(7,
                new DailyReward("§fМонеты §7(§e550§7)", corePlayer -> corePlayer.addCoins(550)));

        DailyRewardManager.INSTANCE.addDailyReward(8,
                new DailyReward("§fМонеты §7(§e610§7)", corePlayer -> corePlayer.addCoins(610)));

        DailyRewardManager.INSTANCE.addDailyReward(9,
                new DailyReward("§fМонеты §7(§e750§7)", corePlayer -> corePlayer.addCoins(750)));

        DailyRewardManager.INSTANCE.addDailyReward(10,
                new DailyReward("§fМонеты §7(§e900§7)", corePlayer -> corePlayer.addCoins(900)),
                new DailyReward("§fПлазма §7(§d2§7)", corePlayer -> corePlayer.addPlazma(2)));

        DailyRewardManager.INSTANCE.addDailyReward(11,
                new DailyReward("§fМонеты §7(§e1,200§7)", corePlayer -> corePlayer.addCoins(1200)));

        DailyRewardManager.INSTANCE.addDailyReward(12,
                new DailyReward("§fМонеты §7(§e2,500§7)", corePlayer -> corePlayer.addCoins(2500)));

        DailyRewardManager.INSTANCE.addDailyReward(13,
                new DailyReward("§fМонеты §7(§e2,850§7)", corePlayer -> corePlayer.addCoins(2850)));

        DailyRewardManager.INSTANCE.addDailyReward(14,
                new DailyReward("§fМонеты §7(§e3,450§7)", corePlayer -> corePlayer.addCoins(3450)));

        DailyRewardManager.INSTANCE.addDailyReward(15,
                new DailyReward("§fМонеты §7(§e4,000§7)", corePlayer -> corePlayer.addCoins(4000)));

        DailyRewardManager.INSTANCE.addDailyReward(16,
                new DailyReward("§fМонеты §7(§e5,200§7)", corePlayer -> corePlayer.addCoins(5200)),
                new DailyReward("§fПлазма §7(§d3§7)", corePlayer -> corePlayer.addPlazma(3)));

        DailyRewardManager.INSTANCE.addDailyReward(17,
                new DailyReward("§fМонеты §7(§e6,300§7)", corePlayer -> corePlayer.addCoins(6300)));

        DailyRewardManager.INSTANCE.addDailyReward(18,
                new DailyReward("§fМонеты §7(§e7,850§7)", corePlayer -> corePlayer.addCoins(7850)));

        DailyRewardManager.INSTANCE.addDailyReward(19,
                new DailyReward("§fМонеты §7(§e8,800§7)", corePlayer -> corePlayer.addCoins(8800)));

        DailyRewardManager.INSTANCE.addDailyReward(20,
                new DailyReward("§fМонеты §7(§e9,400§7)", corePlayer -> corePlayer.addCoins(9400)),
                new DailyReward("§fПлазма §7(§d5§7)", corePlayer -> corePlayer.addPlazma(5)));

        DailyRewardManager.INSTANCE.addDailyReward(21,
                new DailyReward("§fМонеты §7(§e10,000§7)", corePlayer -> corePlayer.addCoins(10_000)),
                new DailyReward("§fПлазма §7(§d10§7)", corePlayer -> corePlayer.addPlazma(10)));
    }

}
