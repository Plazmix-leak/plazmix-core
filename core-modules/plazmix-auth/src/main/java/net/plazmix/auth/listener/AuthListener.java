package net.plazmix.auth.listener;

import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatMessageType;
import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.PlayerJoinEvent;
import net.plazmix.core.api.event.impl.PlayerLeaveEvent;
import net.plazmix.core.api.scheduler.CommonScheduler;
import net.plazmix.core.common.auth.AuthManager;
import net.plazmix.core.common.auth.AuthPlayer;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class AuthListener implements EventListener {

    /**
     * Список аккаунтов для их
     * защиты по IP адресу
     */
    protected static final Map<String, String> guardAccountsMap = new HashMap<String, String>() {{

        // Когда будут статические IP, тогда и поставим, нечего постоянно трогать
        // и без этого заебанный модуль ауча.
    }};

    public static void openAuthSession(@NonNull CorePlayer corePlayer) {
        AuthPlayer authPlayer = AuthManager.INSTANCE.getAuthPlayer(corePlayer.getPlayerId());

        String securedAccountAddress = guardAccountsMap.get(corePlayer.getName().toLowerCase());

        if (securedAccountAddress != null && !securedAccountAddress.equals(corePlayer.getInetSocketAddress().getHostName())) {
            corePlayer.disconnect("§cАккаунт вам не пренадлежит!");
            return;
        }

        AuthManager.INSTANCE.removeTwofactorSession(corePlayer.getName());

        if (AuthManager.INSTANCE.hasAuthSession(corePlayer)) {
            authPlayer.completeWithTwofactorCode();

        } else {

            PlazmixCore.getInstance().getLogger().info("§e[Auth] Session for " + corePlayer.getName() + " was success created!");
            corePlayer.connectToAnyServer("auth");

            startLoginFlood(corePlayer);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        openAuthSession(event.getCorePlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerLeaveEvent event) {
        CorePlayer corePlayer = event.getCorePlayer();

        if (AuthManager.INSTANCE.hasTwofactorSession(corePlayer.getName())) {
            AuthManager.INSTANCE.removeTwofactorSession(corePlayer.getName());
        }
    }

    private static void startLoginFlood(@NonNull CorePlayer corePlayer) {
        new CommonScheduler("auth_flood_" + corePlayer.getPlayerId()) {

            private int secondsCounter = 0;

            @Override
            public void run() {
                if (!corePlayer.isOnline()) {
                    cancel();
                    return;
                }

                if (AuthManager.INSTANCE.hasAuthSession(corePlayer)) {
                    cancel();
                    return;
                }

                if (AuthManager.INSTANCE.hasTwofactorSession(corePlayer.getName())) {
                    cancel();
                    return;
                }

                if (AuthManager.INSTANCE.hasPlayerAccount(corePlayer.getPlayerId())) {
                    corePlayer.sendMessage(ChatMessageType.ACTION_BAR, "§fИспользуйте - §d/login <пароль>");
                    corePlayer.sendLangMessage("LOGIN_USAGE");

                } else {

                    corePlayer.sendLangMessage("REGISTER_USAGE");
                    corePlayer.sendMessage(ChatMessageType.ACTION_BAR, "§fИспользуйте - §d/register [пароль] [пароль]");
                }

                secondsCounter++;
                if (secondsCounter >= 12) {  // 2 минуты

                    corePlayer.disconnect("§cВы не успели авторизироваться!");
                    cancel();
                }
            }

        }.runTimer(1, 10, TimeUnit.SECONDS);
    }

}
