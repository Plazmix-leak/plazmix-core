package net.plazmix.core.common.streams.platform;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.NonNull;
import net.plazmix.core.common.streams.detail.AbstractStreamDetails;
import net.plazmix.core.common.streams.exception.StreamException;

public interface StreamPlatform<T extends AbstractStreamDetails> {

    //для десериализации
    Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    /**
     * Вернуть новый объект информации о стриме по указанной ссылке на него
     */
    T parseStreamUrl(@NonNull String streamUrl);

    /**
     * Вернуть красивый URL (ну крч просто URL для пользователя)
     */
    String makeBeautifulUrl(@NonNull AbstractStreamDetails streamDetails);

    /**
     * Получить URL для отправки запроса
     */
    JsonObject makeRequest(@NonNull String streamId);

    /**
     * Обновить инфо о стриме в зависимости от полученного ответа
     */
    void updateStreamDetails(@NonNull AbstractStreamDetails streamDetails,
                             @NonNull JsonObject jsonObject) throws StreamException;

    /**
     * Получить отображаемое имя платформы (для игроков)
     */
    String getDisplayName();

}
