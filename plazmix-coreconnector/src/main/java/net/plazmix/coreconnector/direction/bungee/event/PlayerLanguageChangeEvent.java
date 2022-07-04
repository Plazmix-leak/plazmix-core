package net.plazmix.coreconnector.direction.bungee.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;
import net.plazmix.coreconnector.core.language.LanguageType;

@RequiredArgsConstructor
@Getter
public class PlayerLanguageChangeEvent extends Event {

    private final ProxiedPlayer player;

    private final LanguageType languageFrom;
    private final LanguageType languageTo;
}
