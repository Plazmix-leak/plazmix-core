package net.plazmix.core;

import lombok.SneakyThrows;
import net.plazmix.core.api.log.CoreLogger;

public class PlazmixCoreStarter {

    @SneakyThrows
    public static void main(String[] args) {
        PlazmixCore plazmixCore = PlazmixCore.getInstance();
        plazmixCore.launch();

        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("io.netty.selectorAutoRebuildThreshold", "0");

        new CoreLogger().start();
    }

}
