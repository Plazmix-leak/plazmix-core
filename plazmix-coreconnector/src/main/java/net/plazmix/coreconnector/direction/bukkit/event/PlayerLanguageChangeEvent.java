package net.plazmix.coreconnector.direction.bukkit.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.coreconnector.core.language.LanguageType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class PlayerLanguageChangeEvent extends Event {

    private final String playerName;

    private final LanguageType languageFrom;
    private final LanguageType languageTo;


    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
