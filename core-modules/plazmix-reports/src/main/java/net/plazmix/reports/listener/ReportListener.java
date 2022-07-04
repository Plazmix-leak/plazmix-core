package net.plazmix.reports.listener;

import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.PlayerAuthCompleteEvent;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.common.report.ReportManager;

public final class ReportListener implements EventListener {

    @EventHandler
    public void onPlayerJoin(PlayerAuthCompleteEvent event) {
        CorePlayer corePlayer = event.getCorePlayer();

        if (corePlayer.getGroup().isStaff() && !ReportManager.INSTANCE.getReportMap().isEmpty()) {

            corePlayer.sendMessage("\n§d§lPlazmix §8:: §fСейчас на сервере актуально §a"
                    + NumberUtil.formatting(ReportManager.INSTANCE.getReportMap().size(), "§fрепорт", "§fрепорта", "§fрепортов") + " от игроков!");

            corePlayer.sendMessage(" §cПостарайтесь ответить на них в ближайшее время!");
            corePlayer.sendMessage(" §7Открыть список жалоб - §d/reports\n");
        }
    }

}
