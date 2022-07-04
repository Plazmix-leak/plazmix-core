package net.plazmix.core.common.language;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LanguageType {

    RUSSIAN("Русский", new LocalizationResource("https://gitlab.com/itzstonlex/plazmix-translations/-/raw/main/lang_ru.yml").initResources()),
    ENGLISH("English", new LocalizationResource("https://gitlab.com/itzstonlex/plazmix-translations/-/raw/main/lang_en.yml").initResources()),
    UKRAINE("Український", new LocalizationResource("https://gitlab.com/itzstonlex/plazmix-translations/-/raw/main/lang_ua.yml").initResources()),
    ;

    public static LanguageType[] VALUES = values();

    private final String displayName;
    private final LocalizationResource resource;
}