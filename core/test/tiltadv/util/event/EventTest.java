package tiltadv.util.event;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class EventTest {

    private class EventOwner {

        private Event testEvent = new Event();

        public EventListener getTestEvent() {
            return testEvent;
        }

        public void testFire() {
            testEvent.fire(this);
        }
    }

    private class EventFiredCounter implements EventHandler {

        private Object sender;
        private int eventFiredCount;

        public Object getSender() {
            return sender;
        }

        public int getCount() {
            return eventFiredCount;
        }

        @Override
        public void run(final Object sender) {
            this.sender = sender;
            eventFiredCount++;
        }
    }

    @Test
    public void firingAnEventTriggersAnEventHandler() {
        EventOwner eventOwner = new EventOwner();

        EventFiredCounter eventFiredCounter = new EventFiredCounter();
        eventOwner.getTestEvent().addListener(eventFiredCounter);

        assertThat(eventFiredCounter.getCount(), equalTo(0));

        eventOwner.testFire();
        assertThat(eventFiredCounter.getCount(), equalTo(1));
    }

    @Test
    public void firingAnEventTriggersAllEventHandlers() {
        EventOwner eventOwner = new EventOwner();

        EventFiredCounter eventFiredCounter1 = new EventFiredCounter();
        EventFiredCounter eventFiredCounter2 = new EventFiredCounter();

        eventOwner.getTestEvent().addListener(eventFiredCounter1);
        eventOwner.getTestEvent().addListener(eventFiredCounter2);

        eventOwner.testFire();
        assertThat(eventFiredCounter1.getCount(), equalTo(1));
        assertThat(eventFiredCounter2.getCount(), equalTo(1));
    }

    @Test
    public void removeListenerStopsEventHandlerFromBeingTriggered() {
        EventOwner eventOwner = new EventOwner();
        EventFiredCounter eventFiredCounter = new EventFiredCounter();
        eventOwner.getTestEvent().addListener(eventFiredCounter);

        eventOwner.testFire();
        assertThat(eventFiredCounter.getCount(), equalTo(1));

        eventOwner.getTestEvent().removeListener(eventFiredCounter);
        eventOwner.testFire();
        assertThat(eventFiredCounter.getCount(), equalTo(1));
    }
}