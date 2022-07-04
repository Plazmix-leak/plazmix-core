package net.plazmix.core.api.event;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.plazmix.core.api.utility.ReflectionUtil;

import java.lang.reflect.Method;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Log4j2
public final class EventListenerHandler {

    private final EventListener eventListener;
    private final Multimap<Class<? extends Event>, Method> eventMethods = ArrayListMultimap.create();

    public void addEventMethod(@NonNull Class<? extends Event> event, @NonNull Method method) {
        eventMethods.put(event, method);
    }

    public void removeEventMethod(@NonNull Class<? extends Event> event, @NonNull Method method) {
        eventMethods.remove(event, method);
    }

    public void removeEvent(@NonNull Class<? extends Event> event) {
        eventMethods.removeAll(event);
    }

    public void fireEvent(@NonNull Event event) {
        if (!eventMethods.containsKey(event.getClass())) {
            return;
        }

        Class<? extends EventListener> listenerClass = eventListener.getClass();

        for (Method method : eventMethods.get(event.getClass())) {
            try {
                if (!ReflectionUtil.hasMethod(listenerClass, method)) {
                    return;
                }

                method.invoke(eventListener, event);
            }

            catch (Exception exception) {

                log.error("", new EventException());
                log.error("", exception);
            }
        }
    }

}
