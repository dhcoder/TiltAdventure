package dhcoder.support.event;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class ArgEventTest {

    private final class IntArgs implements EventArgs {

        private final int intValue;

        private IntArgs(final int intValue) {
            this.intValue = intValue;
        }

        public int getIntValue() {
            return intValue;
        }
    }

    private final class IntEventOwner {

        private final ArgEvent<IntArgs> intEvent = new ArgEvent<IntArgs>();

        public ArgEventHandle<IntArgs> getIntEvent() {
            return intEvent.asHandle();
        }

        public void testFire(final int intValue) {
            intEvent.fire(this, new IntArgs(intValue));
        }

        public void testClear() { intEvent.clear(); }
    }

    private final class IntEventHandler implements ArgEventHandler<IntArgs> {

        private Object sender;
        private IntArgs args;

        public Object getSender() {
            return sender;
        }

        public IntArgs getArgs() {
            return args;
        }

        @Override
        public void run(final Object sender, final IntArgs args) {
            this.sender = sender;
            this.args = args;
        }
    }

    @Test
    public void testFiringAnEventWithArgs() {
        IntEventOwner intEventOwner = new IntEventOwner();
        IntEventHandler intEventHandler = new IntEventHandler();

        intEventOwner.getIntEvent().addHandler(intEventHandler);

        final int ARBITRARY_VALUE = 97531;
        intEventOwner.testFire(ARBITRARY_VALUE);

        assertThat(intEventHandler.getSender(), equalTo((Object)intEventOwner));
        assertThat(intEventHandler.getArgs().getIntValue(), equalTo(ARBITRARY_VALUE));
    }

    @Test
    public void firingAnEventTriggersAllEventHandlers() {
        IntEventOwner intEventOwner = new IntEventOwner();

        IntEventHandler intEventHandler1 = new IntEventHandler();
        IntEventHandler intEventHandler2 = new IntEventHandler();

        intEventOwner.getIntEvent().addHandler(intEventHandler1);
        intEventOwner.getIntEvent().addHandler(intEventHandler2);

        final int ARBITRARY_VALUE = 13579;
        intEventOwner.testFire(ARBITRARY_VALUE);

        assertThat(intEventHandler1.getArgs().getIntValue(), equalTo(ARBITRARY_VALUE));
        assertThat(intEventHandler2.getArgs().getIntValue(), equalTo(ARBITRARY_VALUE));
    }

    @Test
    public void removeListenerStopsEventHandlerFromBeingTriggered() {
        IntEventOwner intEventOwner = new IntEventOwner();

        IntEventHandler intEventHandler = new IntEventHandler();
        intEventOwner.getIntEvent().addHandler(intEventHandler);

        intEventOwner.testFire(1);
        assertThat(intEventHandler.getArgs().getIntValue(), equalTo(1));

        intEventOwner.getIntEvent().removeHandler(intEventHandler);
        intEventOwner.testFire(2);
        assertThat(intEventHandler.getArgs().getIntValue(), equalTo(1));
    }

    @Test
    public void clearListenersStopsEventHandlerFromBeingTriggered() {
        IntEventOwner intEventOwner = new IntEventOwner();

        IntEventHandler intEventHandler = new IntEventHandler();
        intEventOwner.getIntEvent().addHandler(intEventHandler);

        intEventOwner.testFire(1);
        assertThat(intEventHandler.getArgs().getIntValue(), equalTo(1));

        intEventOwner.testClear();
        intEventOwner.testFire(2);
        assertThat(intEventHandler.getArgs().getIntValue(), equalTo(1));
    }
}