package net.plazmix.core.api.module;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.PlazmixCore;
import net.plazmix.core.api.command.CommandExecutor;
import net.plazmix.core.api.command.CommandManager;
import net.plazmix.core.api.event.EventListener;
import net.plazmix.core.api.event.EventManager;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
@Getter
public class CoreModuleManagement {

    private final CoreModule coreModule;

    private final Collection<EventListener> listenerCollection  = new ArrayList<>();
    private final Collection<CommandExecutor> commandCollection = new ArrayList<>();


    public void registerCommand(@NonNull CommandExecutor commandExecutor) {
        commandCollection.add(commandExecutor);

        PlazmixCore.getInstance().getCommandManager().registerCommand(commandExecutor);
    }

    public void registerListener(@NonNull EventListener eventListener) {
        listenerCollection.add(eventListener);

        PlazmixCore.getInstance().getEventManager().registerListener(eventListener);
    }

    public void unregisterCommands() {
        CommandManager commandManager = PlazmixCore.getInstance().getCommandManager();

        for (CommandExecutor commandExecutor : commandCollection) {
            commandManager.getCommandMap().remove(commandExecutor.getCommand().toLowerCase());

            for (String alias : commandExecutor.getAliases())
                commandManager.getCommandMap().remove(alias.toLowerCase());

        }

        commandCollection.clear();
    }

    public void unregisterListeners() {
        EventManager eventManager = PlazmixCore.getInstance().getEventManager();

        for (EventListener eventListener : listenerCollection) {
            eventManager.unregisterListener(eventListener);
        }

        listenerCollection.clear();
    }
}
