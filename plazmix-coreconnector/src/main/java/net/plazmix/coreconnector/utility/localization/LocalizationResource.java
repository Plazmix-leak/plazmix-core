package net.plazmix.coreconnector.utility.localization;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.SerializationUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
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
    public synchronized void initResources() {
        localizationMessages.clear();

        try (InputStreamReader inputStream = new InputStreamReader(new URL(resourceURL).openStream())) {
            localizationMessages.putAll(newYaml().loadAs(inputStream, LinkedHashMap.class));
        }

    }

    /**
     * Получить локализированное сообщение, преобразованное
     * в строку по ключу
     *
     * @param messageKey - ключ локализированного сообщения
     */
    public synchronized String getText(@NonNull String messageKey) {
        return SerializationUtils.clone(localizationMessages.getOrDefault(messageKey, ChatColor.RED + messageKey).toString());
    }

    /**
     * Получить локализированное сообщение, преобразованное
     * в список строк по ключу
     *
     * @param messageKey - ключ локализированного сообщения
     */
    public synchronized List<String> getTextList(@NonNull String messageKey) {
        return new ArrayList<>((List<String>) localizationMessages.get(messageKey));
    }

    /**
     * Получить локализированное сообщение
     *
     * @param messageKey - ключ локализированного сообщения
     */
    public synchronized LocalizedMessage getMessage(@NonNull String messageKey) {
        return LocalizedMessage.create(this, messageKey);
    }


    /**
     * Проверить наличие локализированного сообщения
     * в списке загруженных
     *
     * @param messageKey - ключ локализированного сообщения
     */
    public synchronized boolean hasMessage(@NonNull String messageKey) {
        return localizationMessages.containsKey(messageKey);
    }

    /**
     * Проверить наличие локализированного сообщения
     * в списке загруженных
     *
     * @param messageKey - ключ локализированного сообщения
     */
    public synchronized boolean isText(@NonNull String messageKey) {
        return hasMessage(messageKey) && (localizationMessages.get(messageKey) instanceof String);
    }

    /**
     * Проверить наличие локализированного сообщения
     * в списке загруженных
     *
     * @param messageKey - ключ локализированного сообщения
     */
    public  synchronized boolean isList(@NonNull String messageKey) {
        return hasMessage(messageKey) && (localizationMessages.get(messageKey) instanceof List);
    }

}
