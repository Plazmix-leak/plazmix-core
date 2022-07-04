package net.plazmix.quiter;

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
public enum QuiterMessageCategory {

    CLASSICAL(21, "§aКлассические", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTk3M2ZmYWMyOGVmYzMzNGVmYWFjZjYxZmVjNTcyMmNmZjBjOTg1OTUxZTVkMjBhNjIyOWNkMTU0YjdlMTljIn19fQ==") {{

        addJoinMessage(1, 2, "&c✘ %player% &6полетел покупать донат");
        addJoinMessage(2, 5, "&c✘ %player% &6решил нас покинуть");
        addJoinMessage(3, 5, "&c✘ %player% &6ушёл не попращавшись");
        addJoinMessage(4, 5, "&c✘ %player% &6пошел выносить мусор");
        addJoinMessage(5, 5, "&c✘ %player% &6ливнул после потной катки");
        addJoinMessage(6, 4, "&c✘ %player% &6ушел помогать маме");
        addJoinMessage(7, 5, "&c✘ %player% &6пошел делать уроки");
        addJoinMessage(8, 5, "&c✘ %player% &6растворился в темноте");
        addJoinMessage(9, 5, "&c✘ %player% &6ливнул с сервера");
        addJoinMessage(10, 5, "&c✘ %player% &6ушёл зарабатывать на донат");
        addJoinMessage(11, 5, "&c✘ %player% &6пошел учить уроки");
        addJoinMessage(12, 3, "&c✘ %player% &6наигрался и пошел спать");
        addJoinMessage(13, 5, "&c✘ %player% &6отошёл в туалет");
        addJoinMessage(14, 5, "&c✘ %player% &6отправился за хлебом");
        addJoinMessage(15, 5, "&c✘ %player% &6ушел играть в футбол");
        addJoinMessage(16, 6, "&c✘ %player% &6отправился кушать");
        addJoinMessage(17, 5, "&c✘ %player% &6ушёл в школу");
        addJoinMessage(18, 5, "&c✘ %player% &6пошёл отдыхать");
        addJoinMessage(19, 5, "&c✘ %player% &6покинул нас, но обещал вернуться...");
        addJoinMessage(20, 10, "&c✘ %player% &6ушёл говнокодить");
    }},
    ;

    private final int inventorySlot;

    private final String displayName;
    private final String texture;

    private final TIntObjectMap<QuitMessage> messagesMap = new TIntObjectHashMap<>();


    protected void addJoinMessage(int messageId, int golds, @NonNull String message) {
        messagesMap.put(messageId, new QuitMessage(messageId, ordinal(), golds, ChatColor.translateAlternateColorCodes('&', message)));
    }

    public QuitMessage getJoinMessage(int messageId) {
        return messagesMap.get(messageId);
    }


    public ItemStack getItemStack(@NonNull CorePlayer corePlayer) {
        ItemBuilder itemBuilder = ItemBuilder.newBuilder(Material.SKULL_ITEM);
        itemBuilder.setDurability(3);

        itemBuilder.setDisplayName(displayName);
        itemBuilder.setPlayerSkull(texture);

        int purchasedMessagesCount = (int) QuiterManager.INSTANCE.getPurchasedMessages(corePlayer.getName())
                .stream()
                .filter(quitMessage -> quitMessage.getCategoryId() == ordinal() && quitMessage.isPurchased(corePlayer))
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
