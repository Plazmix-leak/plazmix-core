package net.plazmix.coreconnector.module.type.achievement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.FieldDefaults;

@Getter
@Value(staticConstructor = "of")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AchievementDisplay {

    String title;
}
