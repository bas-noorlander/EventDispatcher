package scripts.laniax.framework.event_dispatcher;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Laniax
 */
public class DispatcherTest {

    private Dispatcher dispatcher;

    @Before
    public void setUp() throws Exception {
        dispatcher = Dispatcher.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        dispatcher.destroy();
        dispatcher = null;
    }

    @Test
    public void dispatch() throws Exception {

        Assert.assertEquals(0, dispatcher.getListeners().size());

        List<String> invoked = new ArrayList<>();

        EventListener<Event> listener1 = new EventListener<>(event -> invoked.add("first"));
        EventListener<Event> listener2 = new EventListener<>(event -> invoked.add("second"));
        EventListener<Event> listener3 = new EventListener<>(event -> invoked.add("third"));

        dispatcher.addListener(Event.class, listener1, -10);
        dispatcher.addListener(Event.class, listener2);
        dispatcher.addListener(Event.class, listener3, 10);

        Event event = new Event();

        Assert.assertEquals(event, dispatcher.dispatch(event));

        Assert.assertEquals("third", invoked.get(0));
        Assert.assertEquals("second", invoked.get(1));
        Assert.assertEquals("first", invoked.get(2));
    }

    @Test
    public void getAllListeners() throws Exception {

        Assert.assertEquals(0, dispatcher.getListeners().size());

        // Check priority
        EventListener<Event> listener1 = new EventListener<>(event -> System.out.println("Hello, first event"), 2);
        EventListener<Event> listener2 = new EventListener<>(event -> System.out.println("Hello, second event"));
        EventListener<Event> listener3 = new EventListener<>(event -> System.out.println("Hello, third event"));

        dispatcher.addListener(Event.class, listener1);
        dispatcher.addListener(Event.class, listener2, 3);
        dispatcher.addListener(Event.class, listener3, 1);

        List<EventListener<? extends Event>> listeners = dispatcher.getListeners(Event.class);

        Assert.assertEquals(listener2, listeners.get(0));
        Assert.assertEquals(listener1, listeners.get(1));
        Assert.assertEquals(listener3, listeners.get(2));
    }

    @Test
    public void getSpecificListeners() throws Exception {

        Assert.assertEquals(0, dispatcher.getListeners().size());

        // Check priority
        EventListener<Event> listener1 = new EventListener<>(event -> System.out.println("Hello, first event"), 2);
        EventListener<Event> listener2 = new EventListener<>(event -> System.out.println("Hello, second event"));
        EventListener<Event> listener3 = new EventListener<>(event -> System.out.println("Hello, third event"));
        EventListener<Event> listener4 = new EventListener<>(event -> System.out.println("Hello, fourth event"));

        class OtherEvent extends Event {}

        dispatcher.addListener(Event.class, listener1);
        dispatcher.addListener(Event.class, listener2, 3);
        dispatcher.addListener(Event.class, listener3, 1);
        dispatcher.addListener(Event.class, listener4, 4);

        dispatcher.addListener(OtherEvent.class, listener1, 3);
        dispatcher.addListener(OtherEvent.class, listener2, 1);
        dispatcher.addListener(OtherEvent.class, listener3, 2);

        List<EventListener<? extends Event>> listeners = dispatcher.getListeners(OtherEvent.class);

        Assert.assertEquals(3, listeners.size());

        Assert.assertEquals(listener1, listeners.get(0));
        Assert.assertEquals(listener3, listeners.get(1));
        Assert.assertEquals(listener2, listeners.get(2));
    }

    @Test
    public void addListener() throws Exception {

        Assert.assertEquals(0, dispatcher.getListeners().size());

        EventListener<Event> listener = new EventListener<>(event -> System.out.println("Hello, first event"));

        dispatcher.addListener(Event.class, listener);

        Assert.assertEquals(1, dispatcher.getListeners().size());
        Assert.assertTrue(dispatcher.getListeners().contains(listener));

        dispatcher.addListener(Event.class, listener); // adding the same listener for the same event type should do nothing

        Assert.assertEquals(1, dispatcher.getListeners().size());
        Assert.assertTrue(dispatcher.getListeners().contains(listener));

        EventListener<Event> listener2 = new EventListener<>(event -> System.out.println("Hello, second event"));

        Assert.assertNotEquals(listener, listener2);

        dispatcher.addListener(Event.class, listener2);

        Assert.assertEquals(2, dispatcher.getListeners().size());
    }

    @Test
    public void removeListener() throws Exception {

        Assert.assertEquals(0, dispatcher.getListeners().size());

        EventListener<Event> listener = new EventListener<>(event -> System.out.println("Hello, first event"));
        EventListener<Event> listener2 = new EventListener<>(event -> System.out.println("Hello, second event"));
        dispatcher.addListener(Event.class, listener);
        dispatcher.addListener(Event.class, listener2);

        Assert.assertEquals(2, dispatcher.getListeners().size());
        Assert.assertTrue(dispatcher.getListeners().contains(listener));

        dispatcher.removeListener(Event.class, listener);

        Assert.assertEquals(1, dispatcher.getListeners().size());
        Assert.assertFalse(dispatcher.getListeners().contains(listener));

        dispatcher.removeListener(Event.class, listener2);

        Assert.assertEquals(0, dispatcher.getListeners().size());
        Assert.assertFalse(dispatcher.getListeners().contains(listener2));
    }
}
