package net.plazmix.coreconnector.module.type.skin;

import lombok.*;
import net.plazmix.coreconnector.utility.mojang.MojangApi;
import net.plazmix.coreconnector.utility.mojang.MojangSkin;

import java.sql.Timestamp;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class PlayerSkin {

    public static PlayerSkin create(@NonNull MojangSkin mojangSkin) {
        return new PlayerSkin(mojangSkin, new Timestamp(mojangSkin.getTimestamp()));
    }

    public static PlayerSkin create(@NonNull String skinName) {
        return new PlayerSkin(MojangApi.getMojangSkinOrDefault(skinName), new Timestamp(System.currentTimeMillis()));
    }

    private final MojangSkin skinObject;
    private Timestamp date;

    public String getSkinName() {
        return skinObject.getSkinName();
    }

}
