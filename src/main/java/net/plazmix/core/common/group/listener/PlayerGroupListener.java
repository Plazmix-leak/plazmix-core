package net.plazmix.core.common.group.listener;

import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.PlayerGroupChangeEvent;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;

public class PlayerGroupListener implements EventListener {

    private static final String[] ALERT_MESSAGES = new String[]{
            "§8■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■\n" +
                    "\n" +
                    "            §cВНИМАНИЕ!\n" +
                    "  §fИгрок %player% §fкупил привилегию %group%\n" +
                    "  §bПоздравим §fего с покупкой!\n" +
                    "  §fХочешь так же? §dwww.Plazmix.net §7[жми]\n" +
                    "\n" +
                    "§8■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■",

            "§8■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■\n" +
                    "\n" +
                    "            §cВНИМАНИЕ!\n" +
                    "  §fУ нас новый счастливчик!\n" +
                    "  §fПоздравляем %player% §fс покупкой %group%\n" +
                    "\n" +
                    " §fЕсли Вы хотите, чтобы о Вас узнали также, то\n" +
                    " §fприобретите любую привилегию на нашем сайте!\n" +
                    "\n" +
                    " §fМагазин возможностей - §dwww.Plazmix.net\n" +
                    "\n" +
                    "§8■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■",

            "§8■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■\n" +
                    " \n" +
                    "            §cВНИМАНИЕ!\n" +
                    "  §6§lНОВЫЙ ДОНАТЕР §f- §6§lСПАСИБО ТЕБЕ\n" +
                    "  %player% §fкупил %group%\n" +
                    "  §fна сайте §dwww.Plazmix.net\n" +
                    "\n" +
                    "§8■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■",

            "§8■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■\n" +
                    "\n" +
                    "            §cВНИМАНИЕ!\n" +
                    "  §fИгрок %player% §fкупил привилегию %group%\n" +
                    "  §bКто не рискует, тот не пьёт шампанское!\n" +
                    "  §fХочешь так же? §dwww.Plazmix.net §7[жми]\n" +
                    "\n" +
                    "§8■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■"
    };

    @EventHandler
    public void onGroupChange(PlayerGroupChangeEvent event) {
        CorePlayer corePlayer = event.getCorePlayer();

        Group currentGroup = event.getCurrentGroup();
        Group previousGroup = event.getPreviousGroup();

        if (previousGroup == null || (!previousGroup.isDonate() && !previousGroup.isDefault()) || !currentGroup.isDonate() || currentGroup.getLevel() == Group.QA.getLevel()) {
            return;
        }

        String randomMessage = ALERT_MESSAGES[NumberUtil.randomInt(0, ALERT_MESSAGES.length - 1)]
                .replace("%player%", previousGroup.getPrefix() + " " + corePlayer.getName())
                .replace("%group%", currentGroup.getColouredName());

        for (CorePlayer onlinePlayer : PlazmixCore.getInstance().getOnlinePlayers()) {
            onlinePlayer.sendMessage(randomMessage);
        }
    }

}
