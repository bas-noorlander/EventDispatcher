package scripts.laniax.framework.event_dispatcher;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This is the dispatcher that dispatches various events to any subscribed listener.
 *
 * @author Laniax
 */
public class Dispatcher {

    private static volatile Dispatcher instance;
    private HashMap<Class<? extends Event>, List<EventListener<? extends Event>>> listeners;

    private Dispatcher() {
        listeners = new HashMap<>();
    }

    public static Dispatcher getInstance() {
        if (instance == null) {
            synchronized (Dispatcher.class) {
                if (instance == null) {
                    instance = new Dispatcher();
                }
            }
        }
        return instance;
    }

    public void destroy() {
        listeners = null;
        instance = null;
    }

    /**
     * Dispatches an event to all registered listeners.
     * The listeners are executed on the same thread the event is dispatched.
     *
     * @param event The event to dispatch
     * @param <T>
     * @return the dispatched event
     */
    public <T extends Event> T dispatch(T event) {

        List<EventListener<? extends Event>> listeners = getListeners(event.getClass());

        if (listeners.size() > 0) {

            for (Iterator<EventListener<? extends Event>> iterator = listeners.iterator(); iterator.hasNext(); ) {
                EventListener listener = iterator.next();

                if (event.isPropagationStopped())
                    break;

                listener.invoke(event);
            }
        }

        return event;
    }

    /**
     * Get all the event listeners registered to ANY event.
     * @return
     */
    public List<EventListener<? extends Event>> getListeners() {
        return getListeners(null);
    }

    /**
     * Gets all the event listener registered to the given event.
     *
     * @param event
     * @return
     */
    public List<EventListener<? extends Event>> getListeners(Class<? extends Event> event) {

        if (event != null) {

            List<EventListener<? extends Event>> currentListenersSet = listeners.get(event);

            if (currentListenersSet == null) {
                return new ArrayList<>();
            }

            currentListenersSet.sort(Comparator.<EventListener<? extends Event>>comparingInt(EventListener::getPriority).reversed());
            return currentListenersSet;
        }

        List<EventListener<? extends Event>> result = new CopyOnWriteArrayList<>();
        listeners.values().forEach(result::addAll);

        result.sort(Comparator.<EventListener<? extends Event>>comparingInt(EventListener::getPriority).reversed());

        return result;
    }

    /**
     * Registers a listener for an event.
     * @param event
     * @param listener
     */
    public Dispatcher addListener(Class<? extends Event> event, EventListener<? extends Event> listener) {
        return addListener(event, listener, null);
    }

    /**
     * Registers a listener for an event.
     * @param event
     * @param listener
     */
    public Dispatcher addListener(Class<? extends Event> event, EventListener<? extends Event> listener, Integer priority) {

        if (priority != null) {
            listener.setPriority(priority);
        }

        List<EventListener<? extends Event>> currentListenersSet = listeners.get(event);

        if (currentListenersSet == null) {
            currentListenersSet = new CopyOnWriteArrayList<>();
        } else if (currentListenersSet.contains(listener))
            return this;

        currentListenersSet.add(listener);

        listeners.put(event, currentListenersSet);

        return this;
    }

    /**
     * Removes a listener for an event.
     * @param event
     * @param listener
     */
    public Dispatcher removeListener(Class<? extends Event> event, EventListener<? extends Event> listener) {

        List<EventListener<? extends Event>> currentListenersSet = listeners.get(event);

        if (currentListenersSet == null)
            return this;

        currentListenersSet.remove(listener);

        if (currentListenersSet.size() == 0) {
            listeners.remove(event);
        } else {
            listeners.put(event, currentListenersSet);
        }

        return this;
    }
}
