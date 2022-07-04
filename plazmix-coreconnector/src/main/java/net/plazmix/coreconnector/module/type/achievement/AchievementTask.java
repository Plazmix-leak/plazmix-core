package net.plazmix.coreconnector.module.type.achievement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AchievementTask {

    int id;

    @NonNull
    AchievementGroup group;

    @NonNull
    AchievementDisplay display;

}