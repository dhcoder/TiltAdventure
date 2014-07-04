package tiltadv.util;

import org.junit.Test;
import tiltadv.game.Entity;
import tiltadv.game.components.Component;
import tiltadv.util.lambda.Action0;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsEqual.equalTo;
import static tiltadv.TestUtils.assertException;
import static tiltadv.util.ComponentUtils.requireComponents;
import static tiltadv.util.ComponentUtils.requireSingleInstance;

public class ComponentUtilsTest {

    private class DummyComponent implements Component {

        @Override
        public void initialize(final Entity owner) {}

        @Override
        public void dispose() {}
    }

    private class SingletonComponent implements Component {

        private boolean initialized;

        public boolean isInitialized() {
            return initialized;
        }

        @Override
        public void initialize(final Entity owner) {
            SingletonComponent singletonComponent = requireSingleInstance(owner, SingletonComponent.class);
            assertThat(singletonComponent, equalTo(this));
            initialized = true;
        }

        @Override
        public void dispose() {}
    }

    @Test
    public void testRequireSingleInstance() {
        SingletonComponent singletonComponent = new SingletonComponent();
        Entity entity = new Entity(singletonComponent);

        // Some testing is done in SingletonComponent.initialize(). This quick check here verifies that those tests
        // completed.
        assertThat(singletonComponent.isInitialized(), equalTo(true));
    }

    @Test
    public void testRequireComponents() {
        DummyComponent component1 = new DummyComponent();
        DummyComponent component2 = new DummyComponent();

        Entity entity = new Entity(component1, component2);

        assertThat(requireComponents(entity, DummyComponent.class), containsInAnyOrder(component1, component2));
    }

    @Test
    public void requireSingleInstanceThrowsExceptionIfNoInstances() {
        DummyComponent dummyComponent = new DummyComponent();
        final Entity entity = new Entity(dummyComponent);

        assertException("requireSingleInstance fails if no instances", IllegalStateException.class, new Action0() {
            @Override
            public void run() {
                requireSingleInstance(entity, SingletonComponent.class);
            }
        });
    }

    @Test
    public void requireSingleInstanceThrowsExceptionIfMultipleInstances() {
        final SingletonComponent singletonComponent1 = new SingletonComponent();
        final SingletonComponent singletonComponent2 = new SingletonComponent();

        assertException("requireSingleInstance fails if multiple instances", IllegalStateException.class,
            new Action0() {
                @Override
                public void run() {
                    Entity entity = new Entity(singletonComponent1, singletonComponent2);
                    fail("Shouldn't get here - SingletonComponent should have thrown an exception on initialization.");
                }
            });
    }

    @Test
    public void requireComponentsThrowsExceptionIfNoInstances() {
        SingletonComponent singletonComponent = new SingletonComponent();
        final Entity entity = new Entity(singletonComponent);

        assertException("requireSingleInstance fails if no instances", IllegalStateException.class, new Action0() {
            @Override
            public void run() {
                requireComponents(entity, DummyComponent.class);
            }
        });
    }

}