package net.plazmix.core.common.quest.impl;

import lombok.NonNull;
import net.plazmix.core.api.chat.ChatColor;
import net.plazmix.core.api.inventory.itemstack.Material;
import net.plazmix.core.common.group.Group;
import net.plazmix.core.common.quest.*;

import java.util.concurrent.TimeUnit;

// TODO: После ознакомления прошу удалить это говно пжпжпжпжп
//  Адресат: фроттер
//  От: итзслоникс
public class QuestExample extends WrappedQuest {

    public QuestExample() {
        super(0, Material.BARRIER, Quest.QuestRequirement.of(

                // Minimal group use.
                Group.GALAXY,

                // Time millis for quest processing.
                TimeUnit.DAYS.toMillis(1),

                // Time millis for quest refresh.
                TimeUnit.DAYS.toMillis(7)
        ));
    }

    @Override
    protected void addTasks(@NonNull TaskAppender appender) {
        appender.add(new QuestTask(1, QuestGroup.MAIN, Quest.QuestDisplay.of("250 раз унизить краша85"), new QuestProgress(1, 250)));
        appender.add(new QuestTask(2, QuestGroup.SKYWARS, Quest.QuestDisplay.of("Уебать 10 камней с ноги на SkyWars"), new QuestProgress(2, 10)));
    }

    @Override
    protected void addRewards(@NonNull RewardAppender appender) {
        appender.add( Quest.QuestReward.of(ChatColor.YELLOW + "10 мочи", corePlayer -> corePlayer.addCoins(10)) );
        appender.add( Quest.QuestReward.of(ChatColor.LIGHT_PURPLE + "2 пакета говна", corePlayer -> corePlayer.addPlazma(2)) );
    }

}
