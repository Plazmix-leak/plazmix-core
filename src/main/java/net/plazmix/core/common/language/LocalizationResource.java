package net.plazmix.core.common.language;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import net.plazmix.core.api.chat.ChatColor;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class LocalizationResource {

    private final String resourceURL;

    @Getter
    private final Map<String, Object> localizationMessages = new LinkedHashMap<>();

    private synchronized Yaml newYaml() {
        Constructor constructor = new Constructor();

        PropertyUtils propertyUtils = new PropertyUtils();
        propertyUtils.setSkipMissingProperties(true);

        constructor.setPropertyUtils(propertyUtils);
        return new Yaml(constructor);
    }

    @SneakyThrows
    public synchronized LocalizationResource initResources() {
        localizationMessages.clear();

        try (InputStreamReader inputStream = new InputStreamReader(new URL(resourceURL).openStream())) {
            localizationMessages.putAll(newYaml().loadAs(inputStream, LinkedHashMap.class));
        }

        return this;
    }

    public synchronized String getMessage(@NonNull String messageKey) {
        return new String(localizationMessages.getOrDefault(messageKey, ChatColor.RED + messageKey).toString().getBytes());
    }

    public synchronized List<String> getMessageList(@NonNull String messageKey) {
        return new ArrayList<>((List<String>) localizationMessages.get(messageKey));
    }

    public synchronized boolean hasMessage(@NonNull String messageKey) {
        return localizationMessages.containsKey(messageKey);
    }

    public synchronized boolean isText(@NonNull String messageKey) {
        return localizationMessages.get(messageKey) instanceof String;
    }

    public synchronized boolean isList(@NonNull String messageKey) {
        return localizationMessages.get(messageKey) instanceof List;
    }

}