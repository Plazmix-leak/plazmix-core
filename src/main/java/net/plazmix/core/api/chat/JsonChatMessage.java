package net.plazmix.core.api.chat;

import lombok.*;
import net.plazmix.core.api.chat.component.BaseComponent;
import net.plazmix.core.api.chat.component.ComponentBuilder;
import net.plazmix.core.api.chat.component.TextComponent;
import net.plazmix.core.api.chat.event.ClickEvent;
import net.plazmix.core.api.chat.event.HoverEvent;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.common.language.LocalizationResource;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonChatMessage {

    public static JsonChatMessage create() {
        return new JsonChatMessage(new ComponentBuilder(""));
    }

    public static JsonChatMessage create(@NonNull String text) {
        return new JsonChatMessage(new ComponentBuilder(text));
    }

    public static JsonChatMessage createLocalized(@NonNull LocalizationResource localizationResource, @NonNull String key) {
        return new JsonChatMessage(new ComponentBuilder(localizationResource.getMessage(key)));
    }


    private final ComponentBuilder componentBuilder;


    public JsonChatMessage addHover(@NonNull HoverEvent.Action action, @NonNull String hover) {
        HoverEvent hoverEvent = new HoverEvent(action, TextComponent.fromLegacyText(hover));

        componentBuilder.event(hoverEvent);
        return this;
    }

    public JsonChatMessage addHoverLocalized(@NonNull HoverEvent.Action action, @NonNull LocalizationResource localizationResource, @NonNull String hoverKey) {
        HoverEvent hoverEvent = new HoverEvent(action, TextComponent.fromLegacyText(localizationResource.getMessage(hoverKey)));

        componentBuilder.event(hoverEvent);
        return this;
    }

    public JsonChatMessage addClick(@NonNull ClickEvent.Action action, @NonNull String command) {
        ClickEvent clickEvent = new ClickEvent(action, command);

        componentBuilder.event(clickEvent);
        return this;
    }


    public JsonChatMessage addText(@NonNull String text) {
        componentBuilder.appendLegacy(text);
        return this;
    }

    public JsonChatMessage addTextLocalized(@NonNull LocalizationResource localizationResource, @NonNull String key) {
        componentBuilder.appendLegacy(localizationResource.getMessage(key));
        return this;
    }


    public JsonChatMessage addJoiner(@NonNull ComponentBuilder.Joiner joiner) {
        componentBuilder.append(joiner);
        return this;
    }

    public JsonChatMessage addColor(@NonNull ChatColor chatColor) {
        componentBuilder.append(chatColor.toString());
        return this;
    }

    public JsonChatMessage setColor(@NonNull ChatColor chatColor) {
        componentBuilder.color(chatColor);
        return this;
    }

    public JsonChatMessage addComponents(BaseComponent... baseComponents) {
        componentBuilder.append(baseComponents);
        return this;
    }


    public BaseComponent[] build() {
        return componentBuilder.create();
    }

    public void sendMessage(@NonNull ChatMessageType chatMessageType, @NonNull CommandSender commandSender) {
        commandSender.sendMessage(chatMessageType, build());
    }

    public void sendMessage(@NonNull CommandSender commandSender) {
        sendMessage(ChatMessageType.CHAT, commandSender);
    }

}
