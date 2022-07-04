package net.plazmix.core.api.module.execute;

import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.ServerConnectedEvent;

public final class ModuleExecuteListener implements EventListener {

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        PlazmixCore.getInstance().getModuleExecuteQueries().values().forEach(query -> query.execute(event.getServer()));
    }

}
