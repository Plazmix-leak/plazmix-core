package net.plazmix.coreconnector.module;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public final class BaseModuleManager {

    public static BaseModuleManager INSTANCE = new BaseModuleManager();

    private final Map<String, BaseServerModule> baseModulesMap = new HashMap<>();


    public void registerModule(@NonNull BaseServerModule module) {
        baseModulesMap.put(module.getName().toLowerCase(), module);
    }

    public <T extends BaseServerModule> T find(@NonNull Class<T> moduleType) {
        return ((T) baseModulesMap.values()
                .stream()
                .filter(module -> moduleType.equals(module.getClass()))
                .findFirst()
                .orElse(null));
    }

    public BaseServerModule find(@NonNull String module) {
        return baseModulesMap.get(module.toLowerCase());
    }

}
