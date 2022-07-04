package net.plazmix.core.api.command.sender;

import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.chat.ChatMessageType;
import net.plazmix.core.api.chat.component.BaseComponent;
import net.plazmix.core.api.chat.component.TextComponent;
import net.plazmix.core.api.command.CommandSender;
import net.plazmix.core.api.command.CommandSendingType;
import net.plazmix.core.api.event.impl.CoreMessageToVkEvent;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.language.LanguageType;
import net.plazmix.core.common.language.LocalizationResource;

@Getter
public class VkbotCommandSender implements CommandSender {

    @Getter
    private static final VkbotCommandSender instance = new VkbotCommandSender();

    @Setter private int lastUserId;


    private final String name                           = "Бот ВКонтакте";

    private final CommandSendingType commandSendingType = CommandSendingType.VKBOT;
    private final LanguageType languageType             = LanguageType.RUSSIAN;
    private final Group group                           = Group.ADMIN;


    @Override
    public String getDisplayName() {
        return ChatColor.AQUA + getName();
    }

    @Override
    public void sendMessage(@NonNull ChatMessageType messageType, BaseComponent[] baseComponents) {
        PlazmixCore.getInstance().getEventManager()
                .callEvent(new CoreMessageToVkEvent(lastUserId, TextComponent.toLegacyText(baseComponents)));
    }

    @Override
    public void sendLangMessage(@NonNull String messageKey, @NonNull String... placeholders) {
        LocalizationResource localizationResource = languageType.getResource();

        if (!localizationResource.hasMessage(messageKey)) {
            sendMessage("§c§nUnknown " + languageType.name().toLowerCase() + " localization key: " + messageKey);
            return;
        }

        String message;

        if (localizationResource.isText(messageKey)) {
            message = localizationResource.getMessage(messageKey);

        } else {

            message = Joiner.on("\n").join(localizationResource.getMessageList(messageKey));
        }

        if (placeholders.length > 0 && placeholders.length % 2 == 0) {
            for (int i = 0; i < placeholders.length; i += 2) {

                String placeholder = placeholders[i];
                String value = placeholders[i + 1];

                message = message.replace(placeholder, value);
            }
        }

        sendMessage(message);
    }

}
