package dhcoder.support.event;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ArgEventTest {

    private class IntArgs implements EventArgs {

        private final int intValue;

        private IntArgs(final int intValue) {
            this.intValue = intValue;
        }

        public int getIntValue() {
            return intValue;
        }
    }

    private class IntEventOwner {

        private final ArgEvent<IntArgs> intEvent = new ArgEvent<IntArgs>();

        public ArgEventHandle<IntArgs> getIntEvent() {
            return intEvent.asHandle();
        }

        public void testFire(final int intValue) {
            intEvent.fire(this, new IntArgs(intValue));
        }
    }

    private class IntEventHandler implements ArgEventHandler<IntArgs> {

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
}