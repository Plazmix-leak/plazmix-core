package net.plazmix.joiner;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.inventory.itemstack.ItemStack;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.utility.ProgressBar;
import net.plazmix.core.connection.player.CorePlayer;

@RequiredArgsConstructor
@Getter
public enum JoinMessageCategory {

    CLASSICAL(21, "§aКлассические", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTk3M2ZmYWMyOGVmYzMzNGVmYWFjZjYxZmVjNTcyMmNmZjBjOTg1OTUxZTVkMjBhNjIyOWNkMTU0YjdlMTljIn19fQ==") {{

        addJoinMessage(1, 1, "&a▸ %player% &fзашел на сервер");
        addJoinMessage(2, 4, "&a▸ %player% &fзашел-вышел, шишел-мышел");
        addJoinMessage(3, 3, "&a▸ %player% &fшишел-мышел, зашел-вышел");
        addJoinMessage(4, 4, "&a▸ %player% &fзалетел без стука");
        addJoinMessage(5, 2, "&a▸ %player% &fвернулся из далёкого плавания! Поприветствуем же его!");
        addJoinMessage(6, 2, "&a▸ %player% &fвзялся за кирку, опять работа?");
        addJoinMessage(7, 3, "&a▸ %player% &fприлетел первым рейсом, давно не виделись!");
        addJoinMessage(8, 4, "&a▸ %player% &fвышел из-за завесы дыма, ну что, теперь эффектно?!");
        addJoinMessage(9, 4, "&a▸ %player% &fпостучался. Разрешите войти?");
        addJoinMessage(10, 5, "&a▸ %player%&f, тебя заждались! Где ты был столько времени?");
        addJoinMessage(11, 3, "&a▸ &fзашёл к нам, как обычно? %player%&f?");
        addJoinMessage(12, 5, "&a▸ &fГлазам своим не верю, это же %player%");
        addJoinMessage(13, 4, "&a▸ %player% &fтут, а мы и не заметили!");
        addJoinMessage(14, 2, "&a▸ %player% &fзаскочил на сервер. Кенгуруу!");
        addJoinMessage(15, 3, "&a▸ %player% &fпринес пиццу");
        addJoinMessage(16, 4, "&a▸ %player% &fзаряжен и готов к бою!");
        addJoinMessage(17, 2, "&a▸ %player% &fзашёл. Нет, блин, вышел...");
        addJoinMessage(18, 3, "&a▸ %player% &fукрал все наши конфеты...");
        addJoinMessage(19, 3, "&a▸ %player% &fзасекречен. Всем спрятаться!");
        addJoinMessage(20, 5, "&a▸ %player% &fконечно, тебя же не кто не заметит...");
        addJoinMessage(21, 5, "&a▸ &fВы не видели %player%&f? А, вот же он!");
        addJoinMessage(22, 5, "&a▸ &fВнимание! %player% &fочень силён, нам нужна помощь!");
        addJoinMessage(22, 5, "&a▸ %player% &fприлетел на сервер");
        addJoinMessage(23, 5, "&a▸ %player% &fприплыл на сервер");
        addJoinMessage(24, 5, "&a▸ %player% &fприбежал на сервер");
        addJoinMessage(25, 5, "&a▸ %player% &fприсоединился к нам");
        addJoinMessage(26, 5, "&a▸ %player% &fдополз до сервера еле живым");
        addJoinMessage(27, 5, "&a▸ %player% &fвышел из преисподней");
        addJoinMessage(28, 5, "&a▸ &fИ вот уже очередной раз %player% &fзаходит на сервер");
    }},

    ELITE(22, "§6Элитные", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzg3YjQ0OWM2MGZhNjE5YWZiNzJkYjhhOGUxZmNmMzczMWUyMzRkNTc2ZTEyMDhmYmRlZDQ0MWEzOGJmMmVmZSJ9fX0=") {{

        addJoinMessage(1, 6, "&a▸ &fВеликий %player%&f, пришел завоевывать мир!");
        addJoinMessage(2, 8, "&a▸ &fНаше величество %player% &fрешил проведать нас");
        addJoinMessage(3, 5, "&a▸ &eИмператор %player% &fрешил заглянуть на сервер");
        addJoinMessage(4, 8, "&a▸ %player% &fпришёл за кодом элемента...");
        addJoinMessage(5, 5, "&a▸ %player% &fприлетел с &eМарса");
        addJoinMessage(6, 8, "&a▸ %player% &fпришел со школы!");
        addJoinMessage(7, 6, "&a▸ %player% &fпришел с работы!");
        addJoinMessage(8, 6, "&a▸ %player% &fзашёл на сервер... &aПовезло, повезло&f!");
        addJoinMessage(9, 8, "&a▸ %player% &fмедленно входит на сервер и попивает свой чаек");
        addJoinMessage(10, 6, "&a▸ %player% &fзашёл на свой &6любимый сервер&f!");
        addJoinMessage(11, 6, "&a▸ %player% &fпришёл проведать свой любимый сервер!");
        addJoinMessage(12, 7, "&a▸ %player% &fпрыгнул в бассейн!");
        addJoinMessage(13, 6, "&a▸ %player% &fпоцеловал свою 2-ю половинку &c<3");
        addJoinMessage(14, 7, "&a▸ %player% &fон невидимый убийца. Всем прятаться!");
        addJoinMessage(15, 7, "&a▸ %player% &fпопросил подержать колу!");
        addJoinMessage(16, 6, "&a▸ &fРаз, два, три, четыре пять - %player% &fвышел погулять!");
        addJoinMessage(17, 6, "&a▸ %player% &fзабежал купить шаурмы!");
        addJoinMessage(18, 8, "&a▸ %player% &fсегодня прекрасен, впрочем, как и всегда");
        addJoinMessage(19, 8, "&a▸ %player% &fВот па-па-поворот, его ужалил пчело-бав урод...");
        addJoinMessage(20, 12, "&a▸ %player% &fбосс этой качалки!");
        addJoinMessage(21, 6, "&a▸ %player% &fпришёл устроить &cпожилой флекс!");
        addJoinMessage(22, 8, "&a▸ &fКого-кого, а %player% &fмы не ожидали тут увидеть.");
        addJoinMessage(23, 6, "&a▸ &fЭто ракета? Это самолёт? А, не, это %player%");
        addJoinMessage(24, 6, "&a▸ %player% &fзашёл показать свой скилл");
        addJoinMessage(25, 5, "&a▸ %player% &fпошёл тестить сервер. Ой.. сломал..");
        addJoinMessage(26, 5, "&a▸ &fА кто это у нас на репортах сидит? %player% вай какой молодец");
        addJoinMessage(27, 5, "&a▸ %player% &fвлетел на крыльях ночи");
        addJoinMessage(28, 7, "&a▸ %player% &fпришёл рвать всех и вся.");
        addJoinMessage(29, 7, "&a▸ %player% &fприлетел со своей джессикой");
        addJoinMessage(30, 7, "&a▸ %player% &fзашёл, чтобы выйти");
        addJoinMessage(31, 5, "&a▸ %player% &fзашёл на чашечку чая");
        addJoinMessage(32, 6, "&a▸ %player% &fснова с нами");
        addJoinMessage(33, 6, "&a▸ %player% &fвосстал из пепла");
        addJoinMessage(34, 7, "&a▸ %player% &fзашёл на сервер, но забыл выключить утюг");
        addJoinMessage(35, 7, "&a▸ %player% &fзашёл выпить кофе");
        addJoinMessage(36, 5, "&a▸ %player% &fискривил пространство и зашёл на сервер");
        addJoinMessage(37, 5, "&a▸ %player% &fприземлился на сервер");
        addJoinMessage(38, 5, "&a▸ %player% &fнеожиданно зашёл на сервер");
        addJoinMessage(39, 5, "&a▸ %player% &fвоскрес и зашёл на сервер");
    }},

    ANIME(23, "§dАниме", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTAyY2M2YWJmNzc5ZGYxYzc5MGJlMWNhNzE1YTcxNjMxM2MxNzk0NWJhZGMyMzY5ZDBjZjA3OTMzNmE1MjQyMSJ9fX0=") {{

        addJoinMessage(1, 9, "&a▸ %player% &dтантэ баё!");
        addJoinMessage(2, 12, "&a▸ %player% &dкрутой, прям как наруто");
        addJoinMessage(3, 12, "&a▸ %player% &dмикаса... как бы");
        addJoinMessage(4, 12, "&a▸ %player% &dямете кудасай..");
    }},

    MEMES(24, "§eМ§aе§9м§bы", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjFlYmM3YWFkNWE2NTZkNTg0MmQ0ODExNjdiNWI3Yjk4ZWFmOWQ5MjRjMmRiYjkzNDhhMzEyMDMzMzAyNjMifX19") {{

        addJoinMessage(1, 5, "&a▸ %player% &fты горишь как огония...");
        addJoinMessage(2, 8, "&a▸ %player% &fкчау!");
        addJoinMessage(3, 5, "&a▸ %player% &fя тут, я там. ");
        addJoinMessage(4, 8, "&a▸ %player% &fah shit, here we go again.");
        addJoinMessage(5, 15, "&a▸ %player% &fвы думаете что я вас не переиграю? Я вас не уничтожу? Я вас уничтожу!");
        addJoinMessage(6, 8, "&a▸ %player% &fугадай, &9где я? &fя дома! угадай, &cкак я? &fя в норме");
        addJoinMessage(7, 7, "&a▸ %player% &fотличный план, надежный как швейцарские часы");
        addJoinMessage(8, 7, "&a▸ %player% &fкак живой но, блин, не живой");
        addJoinMessage(9, 7, "&a▸ %player% &fрешил устроить спидран по серверу");
    }},

    UKRAINE(25, "§9Украина", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjhiOWY1MmUzNmFhNWM3Y2FhYTFlN2YyNmVhOTdlMjhmNjM1ZThlYWM5YWVmNzRjZWM5N2Y0NjVmNWE2YjUxIn19fQ==") {{

        addJoinMessage(1, 3, "&a▸ %player% &9прiшёл iз украiнi!");
        addJoinMessage(2, 5, "&a▸ %player% &9выйды, розбійник");
        addJoinMessage(3, 8, "&a▸ %player% &9український безпілотник");
        addJoinMessage(4, 8, "&a▸ %player% &9о Житомир, це не місто не село..");
        addJoinMessage(5, 8, "&a▸ %player% &9вам повістка");
        addJoinMessage(6, 8, "&a▸ %player% &9йой най буде");

    }},
    ;

    private final int inventorySlot;

    private final String displayName;
    private final String texture;

    private final TIntObjectMap<JoinMessage> messagesMap = new TIntObjectHashMap<>();


    protected void addJoinMessage(int messageId, int golds, @NonNull String message) {
        messagesMap.put(messageId, new JoinMessage(messageId, ordinal(), golds, ChatColor.translateAlternateColorCodes('&', message)));
    }

    public JoinMessage getJoinMessage(int messageId) {
        return messagesMap.get(messageId);
    }


    public ItemStack getItemStack(@NonNull CorePlayer corePlayer) {
        ItemBuilder itemBuilder = ItemBuilder.newBuilder(Material.SKULL_ITEM);
        itemBuilder.setDurability(3);

        itemBuilder.setDisplayName(displayName);
        itemBuilder.setPlayerSkull(texture);

        int purchasedMessagesCount = (int) JoinerManager.INSTANCE.getPurchasedMessages(corePlayer.getName())
                .stream()
                .filter(joinMessage -> joinMessage.getCategoryId() == ordinal() && joinMessage.isPurchased(corePlayer))
                .count();

        itemBuilder.addLore("");
        itemBuilder.addLore("§7Сообщений открыто: §e" + purchasedMessagesCount + "§f/§c" + messagesMap.size());

        ProgressBar progressBar = new ProgressBar(purchasedMessagesCount, messagesMap.size(), 10, "§a", "§7", "●");
        itemBuilder.addLore(" §a↪ " + progressBar.getProgressBar() + " §f" + progressBar.getPercent());

        itemBuilder.addLore("");
        itemBuilder.addLore("§a▸ Нажмите, чтобы перейти!");

        return itemBuilder.build();
    }

}
