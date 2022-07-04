package net.plazmix.coreconnector.core.language;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.coreconnector.utility.localization.LocalizationResource;

@Deprecated
@RequiredArgsConstructor
@Getter
public enum LanguageType {

    RUSSIAN("Русский", new LocalizationResource("https://gitlab.com/itzstonlex/plazmix-translations/-/raw/main/lang_ru.yml")),
    ENGLISH("English", new LocalizationResource("https://gitlab.com/itzstonlex/plazmix-translations/-/raw/main/lang_en.yml")),
    UKRAINE("Український", new LocalizationResource("https://gitlab.com/itzstonlex/plazmix-translations/-/raw/main/lang_ua.yml")),
    ;

    @Deprecated
    public static LanguageType[] VALUES = values();

    @Deprecated private final String displayName;
    @Deprecated private final LocalizationResource resource;
}
