package net.plazmix.core.common.group;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.chat.ChatColor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum Group {

    /**
     * Стандартная группа, которая выдается всем
     * при первом заходе на сервер.
     */
    DEFAULT(0, ChatColor.GRAY, "Y", "Игрок", "§7", "§7"),

    /**
     * Привилегилированные группы. Они имеют дополнительные права,
     * имеют шанс выпасть в донат-кейсе, также можно купить на сайте
     */
    STAR(1, ChatColor.YELLOW, "S", "STAR", "§e§lSTAR§e", "§7"),
    COSMO(2, ChatColor.GREEN, "R", "COSMO", "§a§lCOSMO§a", "§7"),

    QA(3, ChatColor.GOLD, "M", "Тестер", "§6§lQA§6", "§7"),

    GALAXY(4, ChatColor.AQUA, "Q", "GALAXY", "§b§lGALAXY§b", "§7"),
    UNIVERSE(5, ChatColor.LIGHT_PURPLE, "P", "UNIVERSE", "§d§lUNIVERSE§d", "§7"),
    LUXURY(6, ChatColor.GOLD, "O", "LUXURY", "§6§lLUXURY§6", "§7"),
    SPECIAL(7, ChatColor.RED, "N", "SPECIAL", "§c§lSPECIAL§6", "§7"),

    /**
     * Универсальные группы. В данную категорию входят такие статусы,
     * как MEDIA, MEDIA+, BUILDER.
     */
    MEDIA(10, ChatColor.RED, "M", "Медиа", "§c§lMEDIA§c", "§f"),
    MEDIA_PLUS(11, ChatColor.RED, "L", "Медиа +", "§c§lMEDIA§c", "§f"),
    BUILDER(12, ChatColor.DARK_GREEN, "K", "Строитель", "§2§lBUILD§2", "§f"),
    SR_BUILDER(13, ChatColor.DARK_GREEN, "J", "Ст.Строитель", "§2§lSR.BUILD§2", "§f"),

    /**
     * Персонал. Данные группы выдаются только тем, кто является
     * лицом и персоналом проекта, их нельзя получить
     * за какие-то достижения.
     */
    JR_MODER(30, ChatColor.BLUE, "I", "Мл.Модератор", "§9§lJR.MOD§9", "§f"),
    MODER(35, ChatColor.BLUE, "H", "Модератор", "§9§lMOD§9", "§f"),
    SR_MODER(40, ChatColor.BLUE, "G", "Ст.Модератор", "§9§lSR.MOD§9", "§f"),

    /**
     * Администраторы и разработчики проекта.
     */
    STAFF(90, ChatColor.AQUA, "F", "Стафф", "§b§lSTAFF§b", "§f"),
    ADMIN(92, ChatColor.RED, "E", "Администратор", "§c§lADMIN§c", "§f"),
    DEVELOPER(93, ChatColor.DARK_AQUA, "D", "Разработчик", "§3§lDEV§3", "§f"),
    SR_DEVELOPER(94, ChatColor.DARK_AQUA, "C", "Ст.Разработчик", "§3§lSR.DEV§3", "§f"),
    PROJECT_MANAGER(95, ChatColor.DARK_BLUE, "B", "Менеджер", "§1§lMANAGER§1", "§f"),
    OWNER(100, ChatColor.DARK_RED, "A", "Владелец", "§4§lOWNER§4", "§f"),

    /**
     * Уникальная группа, которая выдается очень "хорошим" людям
     */
    ABOBA(-1, ChatColor.AQUA, "Z", "Абоба", "§b§lABOBA §c+§b", "§7");


    private final int level;

    private final ChatColor color;

    private final String tagPriority;
    private final String name;
    private final String prefix;
    private final String suffix;

    public static final Group[] GROUPS_ARRAY
            = values();


    /**
     * Получение статуса по его уровню
     *
     * @param level - уровень группы
     */
    public static Group getGroupByLevel(int level) {
        return Arrays.stream(GROUPS_ARRAY).filter(group -> group.getLevel() == level)
                .findFirst().orElse(null);
    }

    /**
     * Получение статуса по его имени
     *
     * @param groupName - имя группы
     */
    public static Group getGroupByName(String groupName) {
        return Arrays.stream(GROUPS_ARRAY).filter(group -> group.name().equalsIgnoreCase(groupName))
                .findFirst().orElse(null);
    }


    /**
     * Получение цветного наименования статуса
     */
    public String getColouredName() {
        return getColor() + name;
    }


    /**
     * Является ли статус игроком
     */
    public boolean isPidoras() {
        return level < 0;
    }

    /**
     * Является ли статус игроком
     */
    public boolean isDefault() {
        return level <= 0;
    }

    /**
     * Является ли статус персоналом
     */
    public boolean isStaff() {
        return level >= 30;
    }

    /**
     * Является ли статус донатом
     */
    public boolean isDonate() {
        return level >= 1 && level < 10;
    }

    /**
     * Является ли статус донатом
     */
    public boolean isBuilder() {
        return level >= 13 && level < 15;
    }

    /**
     * Является ли статус администратором
     */
    public boolean isAdmin() {
        return level >= 90;
    }

    /**
     * Является ли статус универсальным
     */
    public boolean isUniversal() {
        return level >= 10 && level <= 20;
    }
}
