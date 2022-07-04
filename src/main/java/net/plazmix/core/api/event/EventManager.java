package net.plazmix.core.api.event;

import lombok.Getter;
import lombok.NonNull;
import net.plazmix.core.api.utility.query.AsyncUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class EventManager {

    @Getter
    private final Map<EventListener, EventListenerHandler> listenerHandlers = new HashMap<>();

    /**
     * Регистрация листенера с ивентами под
     * уникальным ID
     *
     * @param eventListener - листенер
     */
    public void registerListener(@NonNull EventListener eventListener) {
        if (listenerHandlers.containsKey(eventListener)) {
            return;
        }

        listenerHandlers.put(eventListener, new EventListenerHandler(eventListener));
        registerEvents(eventListener);
    }

    public EventListenerHandler getListenerHandler(@NonNull EventListener eventListener) {
        return listenerHandlers.get(eventListener);
    }

    public void unregisterListener(@NonNull EventListener eventListener) {
        listenerHandlers.remove(eventListener);
    }

    public void unregisterEvent(@NonNull EventListener eventListener, @NonNull Class<? extends Event> eventClass) {
        EventListenerHandler listenerHandler = getListenerHandler(eventListener);

        if (listenerHandler != null) {
            listenerHandler.removeEvent(eventClass);
        }
    }

    public void unregisterEvent(@NonNull Class<? extends Event> eventClass) {
        for (EventListener eventListener : listenerHandlers.keySet()) {
            EventListenerHandler listenerHandler = getListenerHandler(eventListener);

            if (listenerHandler != null) {
                listenerHandler.removeEvent(eventClass);
            }
        }
    }


    /**
     * Регистрация ивентов, их кеширование в мапу
     *
     * @param eventListener - листенер
     */
    private void registerEvents(@NonNull EventListener eventListener) {
        EventListenerHandler listenerHandler = getListenerHandler(eventListener);

        Arrays.asList(eventListener.getClass().getMethods()).forEach(method -> {
            if (method.getDeclaredAnnotation(EventHandler.class) == null || method.getParameterCount() != 1) {
                return;
            }

            Class<?> eventClass = method.getParameterTypes()[0];

            if (eventClass.getSuperclass().isAssignableFrom(Event.class) || eventClass.getSuperclass().equals(Event.class)) {
                listenerHandler.addEventMethod((Class<? extends Event>) eventClass, method);
            }
        });
    }

    /**
     * Вызывать ивент
     *
     * @param event - ивент
     */
    public void callEvent(Event event) {
        AsyncUtil.submitAsync(() -> {

            for (EventListenerHandler listenerHandler : listenerHandlers.values()) {
                listenerHandler.fireEvent(event);
            }
        });
    }

}
