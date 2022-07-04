package net.plazmix.core.api.inventory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.plazmix.core.api.inventory.handler.BaseInventoryHandler;
import net.plazmix.core.api.inventory.update.BaseInventoryUpdateTask;
import net.plazmix.core.api.scheduler.CommonScheduler;
import net.plazmix.core.api.utility.WeakObjectCache;
import net.plazmix.core.connection.player.CorePlayer;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Getter
public final class BaseInventoryManager {

    private final BaseInventoryHandlerManager inventoryHandlerManager = new BaseInventoryHandlerManager();
    private final Cache<CorePlayer, BaseInventory> inventoryCache = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build();

    private final Map<BaseInventory, BaseInventoryUpdateTask> inventoryUpdateTaskMap = new ConcurrentHashMap<>();

    /**
     * Кешировать инвентарь, открытый игроку
     *
     * @param player        - игроку
     * @param baseInventory - инвентарь
     */
    public void addInventory(@NonNull CorePlayer player, @NonNull BaseInventory baseInventory) {
        inventoryCache.put(player, baseInventory);
    }

    /**
     * Удалить из кеша инвентарь, недавно открытый игроку
     *
     * @param player        - игроку
     * @param baseInventory - инвентарь
     */
    public void removeInventory(@NonNull CorePlayer player, @NonNull BaseInventory baseInventory) {
        inventoryCache.asMap().remove(player, baseInventory);
        player.closeInventory();
    }

    /**
     * Получить открытый инвентарь игрока
     *
     * @param player - игрок
     */
    public BaseInventory getPlayerInventory(@NonNull CorePlayer player) {
        inventoryCache.cleanUp();

        return inventoryCache.asMap().get(player);
    }

    public void startInventoryUpdateTask() {
        new CommonScheduler("inventory-update-task") {
            long expireSecond;

            @Override
            public void run() {
                for (BaseInventoryUpdateTask inventoryUpdateTask : inventoryUpdateTaskMap.values()) {
                    if (expireSecond % inventoryUpdateTask.getUpdateTaskDelay() != 0) {
                        continue;
                    }

                    inventoryUpdateTask.getInventoryUpdateTask().run();
                }

                expireSecond++;
            }

        }.runTimer(0, 1, TimeUnit.SECONDS);
    }

    public void addInventoryUpdateTask(@NonNull BaseInventory baseInventory, @NonNull BaseInventoryUpdateTask inventoryUpdateTask) {
        if (inventoryUpdateTaskMap.containsKey(baseInventory)) {
            return;
        }

        inventoryUpdateTaskMap.put(baseInventory, inventoryUpdateTask);
    }

    public void removeInventoryUpdateTask(@NonNull BaseInventory baseInventory) {
        inventoryUpdateTaskMap.remove(baseInventory);
    }


    @Getter
    public static final class BaseInventoryHandlerManager {

        /**
         * Добавить новый хандлер в список
         * обработчиков инвентаря
         *
         * @param baseInventory    - инвентарь
         * @param inventoryHandler - обработчик
         */
        public <T extends BaseInventoryHandler> void add(@NonNull BaseInventory baseInventory,
                                                         @NonNull Class<T> handlerClass,
                                                         @NonNull T inventoryHandler) {

            baseInventory.getInventoryInfo().addHandler(handlerClass, inventoryHandler);
        }

        /**
         * Получить список хандлеров инвентаря
         * по указанному типу обработчика
         *
         * @param baseInventory         - инвентарь
         * @param inventoryHandlerClass - класс обработчика
         */
        public <T extends BaseInventoryHandler> Collection<T> get(@NonNull BaseInventory baseInventory,
                                                                  @NonNull Class<? extends T> inventoryHandlerClass) {

            return (Collection<T>) baseInventory.getInventoryInfo().getHandlers(inventoryHandlerClass);
        }

        /**
         * Получить первый хандлер по типу его класса
         * из списка обработчиков инвентаря
         *
         * @param baseInventory         - инвентарь
         * @param inventoryHandlerClass - класс обработчика
         */
        public <T extends BaseInventoryHandler> T getFirst(@NonNull BaseInventory baseInventory,
                                                           @NonNull Class<T> inventoryHandlerClass) {

            return baseInventory.getInventoryInfo().getFirstHandler(inventoryHandlerClass);
        }


        public void handle(@NonNull BaseInventory baseInventory,
                           @NonNull Class<? extends BaseInventoryHandler> inventoryHandlerClass,

                           WeakObjectCache weakObjectCache) {

            baseInventory.getInventoryInfo().handleHandlers(inventoryHandlerClass, weakObjectCache);
        }

    }
}
