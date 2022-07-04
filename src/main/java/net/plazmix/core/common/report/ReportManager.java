package net.plazmix.core.common.report;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.Collection;

public final class ReportManager {
    public static final ReportManager INSTANCE = new ReportManager();

    public Multimap<String, Report> getReportMap() {
        return this.reportMap;
    }

    private final Multimap<String, Report> reportMap = HashMultimap.create();

    public void createReport(@NonNull CorePlayer intruderPlayer, @NonNull CorePlayer ownerPlayer, @NonNull String reportReason) {
        Report report = new Report(intruderPlayer.getName(), ownerPlayer.getName(), reportReason, System.currentTimeMillis());

        reportMap.put(intruderPlayer.getName().toLowerCase(), report);

        for (CorePlayer onlineStaff : PlazmixCore.getInstance().getOnlinePlayers(corePlayer -> corePlayer.getGroup().isStaff()))
            onlineStaff.sendMessage("§d§lЖалобы §8:: §fНа игрока " + intruderPlayer.getDisplayName() + " §fбыла подана новая жалоба! §7(" + reportMap.get(intruderPlayer.getName().toLowerCase()).size() + ")");
    }

    public Collection<Report> getReportsByIntruder(@NonNull String intruderName) {
        return this.reportMap.get(intruderName.toLowerCase());
    }

    public boolean hasReport(@NonNull String ownerName, @NonNull String intruderName) {
        return this.reportMap.values().stream().anyMatch(report -> (report.getReportIntruder().equalsIgnoreCase(intruderName) && report.getReportOwner().equalsIgnoreCase(ownerName)));
    }
}
