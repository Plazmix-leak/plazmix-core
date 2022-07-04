package net.plazmix.core.common.punishment;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.NonNull;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.connection.player.CorePlayer;
import net.plazmix.core.connection.protocol.server.SPlayerMutePacket;
import net.plazmix.core.connection.protocol.server.SPlayerUnmutePacket;
import net.plazmix.core.connection.server.impl.BungeeServer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

public final class PunishmentManager {

    public static final PunishmentManager INSTANCE = new PunishmentManager();

    @Getter
    private final Multimap<String, Punishment> punishmentMap = HashMultimap.create();


    /**
     * Временно забанить игрока
     *
     * @param ownerPlayer    - модератор, который выдал бан
     * @param intruderPlayer - нарушитель, которому выдали бан
     * @param reason         - причина бана
     * @param millisTime     - время бана в миллисекундах
     */
    public void tempBanPlayer(@NonNull CorePlayer ownerPlayer,
                              @NonNull CorePlayer intruderPlayer,

                              @NonNull String reason,

                              long millisTime) {

        if (hasPunishmentToPlayer(intruderPlayer.getName(), PunishmentType.TEMP_BAN)) {
            return;
        }

        savePunishmentToHistory(ownerPlayer, intruderPlayer, reason, PunishmentType.TEMP_BAN, millisTime)
                .giveToPlayer(intruderPlayer);
    }

    /**
     * Забанить игрока навсегда
     *
     * @param ownerPlayer    - модератор, который выдал бан
     * @param intruderPlayer - наушитель, которому выдали бан
     * @param reason         - причина бана
     */
    public void banPlayer(@NonNull CorePlayer ownerPlayer,
                          @NonNull CorePlayer intruderPlayer,

                          @NonNull String reason) {

        savePunishmentToHistory(ownerPlayer, intruderPlayer, reason, PunishmentType.PERMANENT_BAN, -1)
                .giveToPlayer(intruderPlayer);
    }

    /**
     * Снять бан игрока
     *
     * @param intruderName - ник игрока
     */
    public void unbanPlayer(@NonNull String intruderName) {
        removePunishmentToData(intruderName, PunishmentType.PERMANENT_BAN);
        removePunishmentToData(intruderName, PunishmentType.TEMP_BAN);
    }


    /**
     * Временно заблокировать игроку чат
     *
     * @param ownerPlayer    - модератор, который заблокировал чат
     * @param intruderPlayer - нарушитель, который инициировал блокировку
     * @param reason         - причина блокировки
     * @param millisTime     - время блокировки
     */
    public void tempMutePlayer(@NonNull CorePlayer ownerPlayer,
                               @NonNull CorePlayer intruderPlayer,

                               @NonNull String reason,

                               long millisTime) {

        if (hasPunishmentToPlayer(intruderPlayer.getName(), PunishmentType.TEMP_MUTE)) {
            return;
        }

        Punishment punishment = savePunishmentToHistory(ownerPlayer, intruderPlayer, reason, PunishmentType.TEMP_MUTE, millisTime);

        intruderPlayer.getBungeeServer().sendPacket(new SPlayerMutePacket(punishment.getPunishmentOwner(), punishment.getPunishmentIntruder(),
                        punishment.getPunishmentReason(), punishment.getPunishmentTime())
        );
    }

    /**
     * Снять блокировку чата у игрока
     *
     * @param intruderName - ник игрока
     */
    public void unmutePlayer(@NonNull String intruderName) {
        removePunishmentToData(intruderName, PunishmentType.PERMANENT_MUTE);
        removePunishmentToData(intruderName, PunishmentType.TEMP_MUTE);

        for (BungeeServer bungeeServer : PlazmixCore.getInstance().getBungeeServers()) {
            bungeeServer.sendPacket(new SPlayerUnmutePacket(intruderName));
        }
    }


    /**
     * Выкинуть игрока с сервера
     *
     * @param ownerPlayer    - тот, кто кикнул
     * @param intruderPlayer - ник нарушителя
     * @param reason         - причина кика
     */
    public void kickPlayer(@NonNull CorePlayer ownerPlayer,
                           @NonNull CorePlayer intruderPlayer,
                           @NonNull String reason) {

        if (!intruderPlayer.isOnline()) {
            return;
        }

        intruderPlayer.disconnect("§cВы были кикнуты с сервера!\n" +
                "\n" +
                "§7Кикнул: " + ownerPlayer.getDisplayName() + "\n" +
                "§7Причина: §f" + reason + "\n" +
                "\n" +
                "§7Если Вы считаете, что данное действие было совершено ошибочно,\n" +
                "§7то Вы можете обжаловать это в группе ВК - §d§nvk.me/plazmixnetwork");

        for (CorePlayer staffCorePlayer : PlazmixCore.getInstance().getOnlinePlayers(corePlayer -> corePlayer.getGroup().isStaff())) {
            staffCorePlayer.sendMessage("§d§lPlazmix §8:: " + ownerPlayer.getDisplayName()
                    + " §7кикнул игрока " + intruderPlayer.getDisplayName()
                    + " §7по причине: §f" + reason);
        }
    }


    /**
     * Проверить игрока на наличие текущего наказания определенного типа
     *
     * @param intruderName   - ник игрока
     * @param punishmentType - тип наказания
     */
    public boolean hasPunishmentToPlayer(@NonNull String intruderName,
                                         @NonNull PunishmentType punishmentType) {

        return getPlayerPunishment(intruderName, punishmentType) != null;
    }

    /**
     * Получить текущее наказание игрока (при его наличии, естественно)
     *
     * @param intruderName   - ник игрока
     * @param punishmentType - тип наказания
     */
    public Punishment getPlayerPunishment(@NonNull String intruderName,
                                          @NonNull PunishmentType punishmentType) {

        Punishment currentPunishment = punishmentMap.get(intruderName.toLowerCase())
                .stream()
                .filter(punishment -> punishment.getPunishmentType().equals(punishmentType) && !punishment.isPunishmentExpired())
                .findFirst().orElse(null);

        if (currentPunishment == null) {
            Punishment punishment = PlazmixCore.getInstance().getMysqlConnection().executeQuery(false, "SELECT * FROM `PunishmentData` WHERE `Intruder`=? AND `Type`=?",
                    resultSet -> {

                        if (!resultSet.next()) {
                            return null;
                        }

                        String punishmentOwner = NetworkManager.INSTANCE.getPlayerName(resultSet.getInt("Owner"));
                        String punishmentReason = resultSet.getString("Reason");

                        Timestamp punishmentTime = resultSet.getTimestamp("Time");

                        Punishment punishmentData = new Punishment(intruderName, punishmentOwner, punishmentReason, punishmentType, punishmentTime == null ? -1 : punishmentTime.getTime());
                        if (punishmentData.isPunishmentExpired()) {

                            punishmentMap.remove(intruderName.toLowerCase(), punishmentData);

                            PlazmixCore.getInstance().getMysqlConnection().execute(true, "DELETE FROM `PunishmentData` WHERE `Intruder`=? AND `Type`=?",
                                    NetworkManager.INSTANCE.getPlayerId(intruderName), punishmentType.ordinal());

                            return null;
                        }

                        return punishmentData;

                    }, NetworkManager.INSTANCE.getPlayerId(intruderName), punishmentType.ordinal());

            if (punishment != null) {
                punishmentMap.put(intruderName.toLowerCase(), currentPunishment = punishment);
            }
        }

        if (currentPunishment != null && currentPunishment.isPunishmentExpired()) {
            removePunishmentToData(currentPunishment.getPunishmentIntruder(), currentPunishment.getPunishmentType());
            return null;
        }

        return currentPunishment;
    }

    /**
     * Получить историю наказаний игрока
     *
     * @param intruderName - ник игрока
     */
    public Collection<Punishment> getPlayerPunishmentHistory(@NonNull String intruderName) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(intruderName);

        return PlazmixCore.getInstance().getMysqlConnection().executeQuery(true, "SELECT * FROM `PunishmentHistory` WHERE `Intruder`=?",
                resultSet -> {

                    Collection<Punishment> punishmentCollection = new ArrayList<>();

                    while (resultSet.next()) {
                        String punishmentOwner = NetworkManager.INSTANCE.getPlayerName(resultSet.getInt("Owner"));
                        String punishmentReason = resultSet.getString("Reason");

                        PunishmentType punishmentType = PunishmentType.PUNISHMENT_TYPES[resultSet.getInt("Type")];

                        Timestamp punishmentTime = resultSet.getTimestamp("Time");

                        punishmentCollection.add(
                                new Punishment(intruderName, punishmentOwner, punishmentReason, punishmentType, punishmentTime == null ? -1 : punishmentTime.getTime())
                        );
                    }

                    return punishmentCollection;

                }, playerId);
    }


    /**
     * Сохранить наказание игрока в базу данных истории
     *
     * @param ownerPlayer    - модератор, который выдал наказание
     * @param intruderPlayer - нарушитель, который получил по жопе
     * @param reason         - причина наказания
     * @param punishmentType - тип наказания
     * @param millisTime     - время наказания
     */
    private Punishment savePunishmentToHistory(@NonNull CorePlayer ownerPlayer,
                                               @NonNull CorePlayer intruderPlayer,

                                               @NonNull String reason,
                                               @NonNull PunishmentType punishmentType,

                                               long millisTime) {

        Punishment punishment = new Punishment(
                intruderPlayer.getName(), ownerPlayer.getName(), reason, punishmentType, millisTime >= 0 ? System.currentTimeMillis() + millisTime : -1);

        punishmentMap.put(intruderPlayer.getName().toLowerCase(), punishment);


        PlazmixCore.getInstance().getMysqlConnection()
                .execute(true, "INSERT INTO `PunishmentHistory` VALUES (?, ?, ?, ?, ?)",

                        intruderPlayer.getPlayerId(), ownerPlayer.getPlayerId(), punishmentType.ordinal(), reason, millisTime > 0 ? new Timestamp(punishment.getPunishmentTime()) : null);

        PlazmixCore.getInstance().getMysqlConnection()
                .execute(true, "INSERT INTO `PunishmentData` VALUES (?, ?, ?, ?, ?)",

                        intruderPlayer.getPlayerId(), ownerPlayer.getPlayerId(), punishmentType.ordinal(), reason, millisTime > 0 ? new Timestamp(punishment.getPunishmentTime()) : null);

        // возвращаем только что созданное наказание
        return punishment;
    }

    /**
     * Удалить наказание из текущих
     *
     * @param intruderName   - ник нарушителя
     * @param punishmentType - тип наказания
     */
    private void removePunishmentToData(@NonNull String intruderName,
                                        @NonNull PunishmentType punishmentType) {

        Punishment punishment = getPlayerPunishment(intruderName, punishmentType);

        if (punishment == null) {
            return;
        }

        punishmentMap.remove(intruderName.toLowerCase(), punishment);

        PlazmixCore.getInstance().getMysqlConnection().execute(true, "DELETE FROM `PunishmentData` WHERE `Intruder`=? AND `Type`=?",
                NetworkManager.INSTANCE.getPlayerId(intruderName), punishmentType.ordinal());
    }

}
