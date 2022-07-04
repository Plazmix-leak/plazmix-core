package net.plazmix.streams.inventory;

import gnu.trove.map.TIntObjectMap;
import lombok.NonNull;
import net.plazmix.core.api.chat.JsonChatMessage;
import net.plazmix.core.api.chat.event.ClickEvent;
import net.plazmix.core.api.chat.event.HoverEvent;
import net.plazmix.core.api.inventory.BaseInventoryMarkup;
import net.plazmix.core.api.inventory.impl.CorePaginatedInventory;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.api.inventory.itemstack.builder.ItemBuilder;
import net.plazmix.core.api.inventory.markup.BaseInventoryBlockMarkup;
import net.plazmix.core.api.utility.NumberUtil;
import net.plazmix.core.common.network.NetworkManager;
import net.plazmix.core.common.streams.StreamManager;
import net.plazmix.core.common.streams.detail.AbstractStreamDetails;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.concurrent.atomic.AtomicInteger;

public class StreamsInventory extends CorePaginatedInventory {

    public StreamsInventory() {
        super(5, "Актуальные стримы");
    }

    @Override
    public void drawInventory(@NonNull CorePlayer player) {

        // Set inventory markup.
        BaseInventoryMarkup inventoryMarkup = new BaseInventoryBlockMarkup(inventoryRows);

        inventoryMarkup.addHorizontalRow(2, 2);
        inventoryMarkup.addHorizontalRow(3, 2);
        inventoryMarkup.addHorizontalRow(4, 2);

        setInventoryMarkup(inventoryMarkup);

        // Add streams items.
        TIntObjectMap<AbstractStreamDetails> streamDetailsMap = StreamManager.INSTANCE.getActiveStreams();

        AtomicInteger streamCounter = new AtomicInteger(1);
        streamDetailsMap.forEachEntry((playerId, streamDetails) -> {

            if (!streamDetails.isActual()) {
                return true;
            }

            String streamUrl = streamDetails.getStreamPlatform().makeBeautifulUrl(streamDetails);


            ItemBuilder itemBuilder = ItemBuilder.newBuilder(Material.SKULL_ITEM);
            itemBuilder.setDurability(3);

            itemBuilder.setDisplayName("§eСтрим #" + streamCounter.getAndIncrement());
            itemBuilder.setPlayerSkull(NetworkManager.INSTANCE.getPlayerName(playerId));

            itemBuilder.addLore("");
            itemBuilder.addLore("§7Ссылка: §e" + streamUrl);
            itemBuilder.addLore("");
            itemBuilder.addLore("§7Название: §f" + (streamDetails.getTitle().length() > 48 ? streamDetails.getTitle().substring(0, 48) + "..." : streamDetails.getTitle()));
            itemBuilder.addLore("§7Зрителей: §b" + NumberUtil.spaced(streamDetails.getViewers(), '.'));
            itemBuilder.addLore("");
            itemBuilder.addLore("§7Платформа: " + streamDetails.getStreamPlatform().getDisplayName());
            itemBuilder.addLore("");
            itemBuilder.addLore("§e▸ Нажмите, чтобы получить ссылку в чате");

            addItemToMarkup(itemBuilder.build(), (baseInventory, event) -> {

                JsonChatMessage jsonChatMessage = JsonChatMessage.create("§d§lPlazmix §8:: §fСсылка на стрим: §a" + streamUrl + "\n" +
                        " §e▸ §7Нажмите, чтобы перейти на трансляцию!");

                jsonChatMessage.addHover(HoverEvent.Action.SHOW_TEXT, "§aПерейти на трансляцию");
                jsonChatMessage.addClick(ClickEvent.Action.OPEN_URL, streamUrl);

                jsonChatMessage.sendMessage(player);
                player.closeInventory();
            });

            return true;
        });

        // Add not found item.
        if (streamCounter.get() <= 1) {
            addItem(23, ItemBuilder.newBuilder(Material.GLASS_BOTTLE)
                    .setDisplayName("§cНа сервере отсутствуют активные трансляции")
                    .addLore("§7В данный момент на сервере")
                    .addLore("§7Отсутствуют активные трансляции")
                    .build());
        }
    }

}
