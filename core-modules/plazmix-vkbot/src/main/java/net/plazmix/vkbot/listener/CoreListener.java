package net.plazmix.vkbot.listener;

import net.plazmix.core.api.event.EventHandler;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.impl.CoreMessageToVkEvent;
import net.plazmix.vkbot.api.objects.message.Message;
import net.plazmix.vkbot.bot.VkBot;

public class CoreListener implements EventListener {

    @EventHandler
    public void onMessageFromCore(CoreMessageToVkEvent event) {
        int peerId = event.getPeerId();

        String message = event.getBodyMessage();

        new Message()
                .peerId(peerId)
                .body(message)

                .send(VkBot.INSTANCE);
    }

}
