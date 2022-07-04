package net.plazmix.core.api.utility;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Placeholders {

    private final String handle;
    private final Map<String, Object> placeholdersMap = new HashMap<>();

    public static Placeholders wrapOf(String text) {
        return new Placeholders(text);
    }

    public static Placeholders newInstance() {
        return new Placeholders(null);
    }

    public Placeholders replace(@NonNull String placeholder, Object value) {
        placeholdersMap.put(placeholder, value == null ? "null" : value);
        return this;
    }

    public String applyAndGet() {
        return applyFor(handle);
    }

    public String applyFor(String handle) {
        if (handle != null) {

            for (Map.Entry<String, Object> placeholderEntry : placeholdersMap.entrySet()) {
                handle = handle.replace(placeholderEntry.getKey(), placeholderEntry.getValue().toString());
            }

            return handle;
        }

        return null;
    }

}
