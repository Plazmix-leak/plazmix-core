package net.plazmix.core.common.language;

import lombok.NonNull;

public interface LocalizedSender {

    LanguageType getLanguageType();

    void sendLangMessage(@NonNull String messageKey, @NonNull String... placeholders);
}
