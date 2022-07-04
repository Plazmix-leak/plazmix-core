package net.plazmix.reports;

import net.plazmix.core.api.module.CoreModule;
import net.plazmix.core.api.module.CoreModuleInfo;
import net.plazmix.reports.command.ReportListCommand;
import net.plazmix.reports.command.ReportSendCommand;
import net.plazmix.reports.listener.ReportListener;

@CoreModuleInfo(name = "PlazmixReports", author = "Plazmix")
public class PlazmixReports extends CoreModule {

    @Override
    protected void onEnable() {
        getManagement().registerCommand(new ReportSendCommand());
        getManagement().registerCommand(new ReportListCommand());

        getManagement().registerListener(new ReportListener());
    }

    @Override
    protected void onDisable() {}
}
