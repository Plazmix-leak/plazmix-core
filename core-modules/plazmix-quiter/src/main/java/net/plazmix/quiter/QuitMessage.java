package net.plazmix.quiter;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.connection.player.CorePlayer;

@RequiredArgsConstructor
@Getter
public class QuitMessage {

    private final int messageId;
    private final int categoryId;

    private final int goldsCost;

    private final String message;


    public boolean canPurchase(@NonNull CorePlayer corePlayer) {
        return corePlayer.getPlazma() >= goldsCost;
    }

    public boolean isSelected(@NonNull CorePlayer corePlayer) {
        return QuiterManager.INSTANCE.isSelected(messageId, categoryId, corePlayer.getName());
    }

    public boolean isPurchased(@NonNull CorePlayer corePlayer) {
        return QuiterManager.INSTANCE.isPurchased(messageId, categoryId, corePlayer.getName());
    }

    public String parse(@NonNull CorePlayer corePlayer) {
        return message.replace("%player%", corePlayer.getDisplayName());
    }

}
