package scripts.laniax.framework.event_dispatcher;

/**
 * Event is the base class for classes containing event data.
 *
 * This class contains no event data. It is used by events that do not pass
 * state information to an event handler when an event is raised.
 *
 * You can call the method stopPropagation() to abort the execution of
 * further listeners in your event listener.
 *
 * @author Laniax
 */
public class Event {

    private boolean isPropagationStopped = false;

    /**
     * Returns whether further event listeners should be triggered.
     * See {@link this#stopPropagation} for more info.
     *
     * @return Whether propagation was already stopped for this event
     */
    public boolean isPropagationStopped() {
        return isPropagationStopped;
    }

    /**
     * Stops the propagation of the event to further event listeners.
     *
     * If multiple event listeners are connected to the same event, no
     * further event listener will be triggered once any trigger calls
     * stopPropagation().
     */
    public void stopPropagation() {
        isPropagationStopped = true;
    }

}
