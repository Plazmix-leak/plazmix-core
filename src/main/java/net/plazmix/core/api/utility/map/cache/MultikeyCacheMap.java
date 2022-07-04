package net.plazmix.core.api.utility.map.cache;

import net.plazmix.core.api.utility.map.MultikeyMap;

public interface MultikeyCacheMap<I> extends MultikeyMap<I> {

    void cleanUp();
}
