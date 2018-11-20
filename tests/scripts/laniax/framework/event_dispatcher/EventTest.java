package scripts.laniax.framework.event_dispatcher;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Laniax
 */
public class EventTest {

    private Event event;

    @Before
    public void setUp() throws Exception {
        event = new Event();
    }

    @After
    public void tearDown() throws Exception {
        event = null;
    }

    @Test
    public void isPropagationStopped() throws Exception {
        Assert.assertFalse(event.isPropagationStopped());
    }

    @Test
    public void stopPropagation() throws Exception {
        event.stopPropagation();
        Assert.assertTrue(event.isPropagationStopped());
    }

}