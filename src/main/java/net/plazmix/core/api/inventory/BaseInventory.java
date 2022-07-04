package net.plazmix.core.api.inventory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.plazmix.core.api.inventory.handler.BaseInventoryHandler;
import net.plazmix.core.api.inventory.handler.impl.BaseInventoryDisplayableHandler;
import net.plazmix.core.api.inventory.handler.impl.BaseInventoryUpdateHandler;
import net.plazmix.core.api.utility.WeakObjectCache;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.ArrayList;
import java.util.Collection;

public interface BaseInventory {

    /**
     * Добавить новый обработчик
     * событий данному инвентарю
     *
     * @param inventoryHandler - обработчик событий
     */
    default <T extends BaseInventoryHandler> void addHandler(@NonNull Class<T> handlerClass, @NonNull T inventoryHandler) {
        getInventoryInfo().addHandler(handlerClass, inventoryHandler);
    }


    /**
     * Открыть инвентарь игроку
     */
    void openInventory(@NonNull CorePlayer player);

    /**
     * Открыть инвентарь игроку, используя
     * обработчик открытия и закрытия данного инвентаря
     *
     * @param inventoryDisplayableHandler - обработчик закрытия и открытия инвентаря
     */
    void openInventory(@NonNull CorePlayer player, @NonNull BaseInventoryDisplayableHandler inventoryDisplayableHandler);


    /**
     * Единаразово очистить инвентарь
     */
    void clearInventory(@NonNull CorePlayer player);


    /**
     * Единоразово обновить инвентарь и
     * все предметы внутри игроку
     */
    void updateInventory(@NonNull CorePlayer player);

    /**
     * Единоразово обновить инвентарь и
     * все предметы внутри игроку, используя
     * обработчик обновления инвентаря
     *
     * @param player                 - игрок
     * @param inventoryUpdateHandler - обработчик обновления инвентаря
     */
    void updateInventory(@NonNull CorePlayer player, @NonNull BaseInventoryUpdateHandler inventoryUpdateHandler);

    /**
     * Включить цикличное автообновление инвентаря
     * до закрытия его игроком
     *
     * @param inventoryUpdateHandler - обработчик обновления инвентаря
     */
    void enableAutoUpdate(@NonNull CorePlayer player, BaseInventoryUpdateHandler inventoryUpdateHandler, long duration);


    /**
     * Переопределяющийся метод
     * <p>
     * Отрисовка и настройка инвентаря, установка
     * предметов и разметка
     *
     * @param player - игрок
     */
    void drawInventory(@NonNull CorePlayer player);

    /**
     * Закрыть инвентарь игроку, вызывав
     * при наличии обработчик закрытия инвентаря
     */
    void closeInventory(@NonNull CorePlayer player);


    /**
     * Отрисовать предмет в инвентаре
     *
     * @param baseInventoryItem - предмет и его функции
     */
    void addItem(@NonNull BaseInventoryItem baseInventoryItem);


    /**
     * Установить разметку инвентаря для
     * разрешенных мест в установке предметов
     *
     * @param baseInventoryMarkup - разметка инвентаря
     */
    void setInventoryMarkup(@NonNull BaseInventoryMarkup baseInventoryMarkup);


    /**
     * Получить обработчик информации инвентаря,
     * который хранит в себе как базовые поля,
     * так и различные списки предметов, хандлеров и т.д.
     */
    BaseInventoryInfo getInventoryInfo();

    BaseInventorySettings getInventorySettings();


    @Getter
    @RequiredArgsConstructor
    class BaseInventoryInfo {

        private final BaseInventory baseInventory;

        private final String inventoryTitle;
        private final int inventorySize, inventoryRows;

        private final Multimap<Class<? extends BaseInventoryHandler>, BaseInventoryHandler> inventoryHandlerMap = HashMultimap.create();
        private final TIntObjectMap<BaseInventoryItem> inventoryItemMap = new TIntObjectHashMap<>();


        public <T extends BaseInventoryHandler> void addHandler(@NonNull Class<T> handlerClass,
                                                                @NonNull T inventoryHandler) {

            inventoryHandlerMap.put(handlerClass, inventoryHandler);
        }

        public void addItem(int itemSlot, @NonNull BaseInventoryItem baseInventoryItem) {
            inventoryItemMap.put(itemSlot, baseInventoryItem);
        }


        public <T extends BaseInventoryHandler> T getFirstHandler(@NonNull Class<T> inventoryHandlerClass) {
            return getHandlers(inventoryHandlerClass).stream().findFirst().orElse(null);
        }

        public <T extends BaseInventoryHandler> Collection<T> getHandlers(@NonNull Class<T> inventoryHandlerClass) {
            Class<T> handlerClassKey = (Class<T>) inventoryHandlerMap.keySet().stream()
                    .filter(handlerClass -> handlerClass.isAssignableFrom(inventoryHandlerClass))
                    .findFirst()
                    .orElse(null);

            if (handlerClassKey == null) {
                return new ArrayList<>();
            }

            return (Collection<T>) inventoryHandlerMap.get(handlerClassKey);
        }

        public BaseInventoryItem getItem(int itemSlot) {
            return inventoryItemMap.get(itemSlot);
        }

        public <T extends BaseInventoryHandler> void handleHandlers(@NonNull Class<T> inventoryHandlerClass,
                                                                    WeakObjectCache objectCache) {

            Collection<T> inventoryHandlerCollection = getHandlers(inventoryHandlerClass);

            for (BaseInventoryHandler inventoryHandler : inventoryHandlerCollection) {
                inventoryHandler.handle(baseInventory, objectCache);
            }
        }
    }

    @Getter
    @Setter
    class BaseInventorySettings {

        protected Group minimalGroup = Group.ABOBA;
        protected boolean useOnlyCacheItems = false;
    }

}
