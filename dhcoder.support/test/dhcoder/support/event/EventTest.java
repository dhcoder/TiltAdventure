package dhcoder.support.event;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class EventTest {

    private final class EventOwner {

        private final Event event = new Event();

        public EventHandle getEvent() {
            return event.asHandle();
        }

        public void testFire() {
            event.fire(this);
        }

        public void testClear() {
            event.clear();
        }
    }

    private final class EventFiredCounter implements EventHandler {

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
        eventOwner.getEvent().addHandler(eventFiredCounter);

        assertThat(eventFiredCounter.getCount(), equalTo(0));

        eventOwner.testFire();
        assertThat(eventFiredCounter.getCount(), equalTo(1));
    }

    @Test
    public void firingAnEventTriggersAllEventHandlers() {
        EventOwner eventOwner = new EventOwner();

        EventFiredCounter eventFiredCounter1 = new EventFiredCounter();
        EventFiredCounter eventFiredCounter2 = new EventFiredCounter();

        eventOwner.getEvent().addHandler(eventFiredCounter1);
        eventOwner.getEvent().addHandler(eventFiredCounter2);

        eventOwner.testFire();
        assertThat(eventFiredCounter1.getCount(), equalTo(1));
        assertThat(eventFiredCounter2.getCount(), equalTo(1));
    }

    @Test
    public void removeListenerStopsEventHandlerFromBeingTriggered() {
        EventOwner eventOwner = new EventOwner();
        EventFiredCounter eventFiredCounter = new EventFiredCounter();
        eventOwner.getEvent().addHandler(eventFiredCounter);

        eventOwner.testFire();
        assertThat(eventFiredCounter.getCount(), equalTo(1));

        eventOwner.getEvent().removeHandler(eventFiredCounter);
        eventOwner.testFire();
        assertThat(eventFiredCounter.getCount(), equalTo(1));
    }

    @Test
    public void clearListenersStopsEventHandlerFromBeingTriggered() {
        EventOwner eventOwner = new EventOwner();
        EventFiredCounter eventFiredCounter = new EventFiredCounter();
        eventOwner.getEvent().addHandler(eventFiredCounter);

        eventOwner.testFire();
        assertThat(eventFiredCounter.getCount(), equalTo(1));

        eventOwner.testClear();
        eventOwner.testFire();
        assertThat(eventFiredCounter.getCount(), equalTo(1));
    }
}