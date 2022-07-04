package net.plazmix.core.common.punishment;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.connection.player.CorePlayer;

@RequiredArgsConstructor
@Getter
public class Punishment {

    private final String punishmentIntruder;
    private final String punishmentOwner;

    private final String punishmentReason;

    private final PunishmentType punishmentType;

    private final long punishmentTime;


    /**
     * Проверить, истек ли срок наказания
     */
    public boolean isPunishmentExpired() {
        return punishmentTime > 0 && System.currentTimeMillis() > punishmentTime;
    }

    public boolean isPermanent() {
        return punishmentTime < 0;
    }

    public void giveToPlayer(@NonNull CorePlayer corePlayer) {
        CorePlayer offlinePlayerOwner = PlazmixCore.getInstance().getOfflinePlayer(punishmentOwner);

        switch (punishmentType) {
            case TEMP_BAN: {
                if (isPunishmentExpired()) {

                    break;
                }

                corePlayer.disconnect("§cВаш аккаунт был заблокирован!\n" +
                        "\n" +
                        "§fЗаблокировал: " + offlinePlayerOwner.getDisplayName() + "\n" +
                        "§fПричина: §d" + punishmentReason + "\n" +
                        "§fБлокировка спадёт через: §d" + NumberUtil.getTime(punishmentTime - System.currentTimeMillis()) + "\n" +
                        "\n" +
                        "§fХотите обжаловать?\n" +
                        "§fПрежде, рекомендуем ознакомиться с правилами проекта - §dhttps://plazmix.net/page/rules\n" +
                        "\n" +
                        "§fНекорректная блокировка аккаунта?\n" +
                        "§fПопробуйте подать аппеляцию на сайте §dhttps://team.plazmix.net/form/apeal\n" +
                        "§fДополнительная информация в нашей VK группе §dhttps://plzm.xyz/vk");

                break;
            }

            case PERMANENT_BAN: {
                corePlayer.disconnect("§cВаш аккаунт был заблокирован §lНАВСЕГДА§c!\n" +
                        "\n" +
                        "§fЗаблокировал: " + offlinePlayerOwner.getDisplayName() + "\n" +
                        "§fПричина: §d" + punishmentReason + "\n" +
                        "\n" +
                        "§fХотите обжаловать?\n" +
                        "§fПрежде, рекомендуем ознакомиться с правилами проекта - §dhttps://plazmix.net/page/rules\n" +
                        "\n" +
                        "§fНекорректная блокировка аккаунта?\n" +
                        "§fПопробуйте подать аппеляцию на сайте §dhttps://team.plazmix.net/form/apeal\n" +
                        "§fДополнительная информация в нашей VK группе §dhttps://plzm.xyz/vk");

                break;
            }
        }
    }
}
