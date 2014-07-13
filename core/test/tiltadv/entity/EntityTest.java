package tiltadv.entity;

import dhcoder.support.lambda.Action;
import org.junit.Test;

import static dhcoder.test.TestUtils.assertException;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsEqual.equalTo;

public class EntityTest {

    private class DummyComponent extends AbstractComponent {

        private Entity owner;
        private boolean disposed;

        public boolean isDisposed() {
            return disposed;
        }

        public Entity getOwner() {
            return owner;
        }

        @Override
        public void initialize(final Entity owner) {
            this.owner = owner;
        }

        @Override
        public void dispose() {
            disposed = true;
        }
    }

    /**
     * This class exists only to be found by a {@link DependentComponent}
     */
    private class SourceComponent extends AbstractComponent {}

    /**
     * This component expects to find a {@link SourceComponent} on the {@link Entity} it's attached to.
     */
    private class DependentComponent extends AbstractComponent {

        private SourceComponent sourceComponent;

        public SourceComponent getSourceComponent() {
            return sourceComponent;
        }

        @Override
        public void initialize(final Entity owner) {
            sourceComponent = owner.getComponent(SourceComponent.class).value();
        }
    }

    /**
     * This component asserts that it is the only one that exists on an entity
     */
    private class SingletonComponent extends AbstractComponent {

        private boolean initialized;

        public boolean isInitialized() {
            return initialized;
        }

        @Override
        public void initialize(final Entity owner) {
            SingletonComponent singletonComponent = owner.requireSingleInstance(SingletonComponent.class);
            assertThat(singletonComponent, equalTo(this));
            initialized = true;
        }

        @Override
        public void dispose() {}
    }

    @Test
    public void componentGetsInitializedWithItsOwningEntity() {
        DummyComponent dummyComponent = new DummyComponent();
        assertThat(dummyComponent.getOwner(), nullValue());
        Entity entity = new Entity(dummyComponent);
        assertThat(entity, equalTo(dummyComponent.getOwner()));
    }

    @Test
    public void componentGetsDisposedWithEntity() {
        DummyComponent dummyComponent = new DummyComponent();
        Entity entity = new Entity(dummyComponent);
        assertThat(dummyComponent.isDisposed(), equalTo(false));

        entity.dispose();
        assertThat(dummyComponent.isDisposed(), equalTo(true));
    }

    @Test
    public void getComponentReturnsExpectedValues() {
        DummyComponent dummyComponent = new DummyComponent();
        SourceComponent sourceComponent = new SourceComponent();
        Entity entity = new Entity(dummyComponent, sourceComponent);
        assertThat(entity.getComponent(DummyComponent.class).value(), equalTo(dummyComponent));
        assertThat(entity.getComponent(SourceComponent.class).value(), equalTo(sourceComponent));
        assertThat(entity.getComponent(DependentComponent.class).hasValue(), equalTo(false));
    }

    @Test
    public void dependantComponentCanFindOtherComponent() {
        SourceComponent sourceComponent = new SourceComponent();
        DependentComponent dependantComponent = new DependentComponent();
        Entity entity = new Entity(sourceComponent, dependantComponent);
        assertThat(sourceComponent, equalTo(dependantComponent.getSourceComponent()));
    }

    @Test
    public void dependantComponentCanFindOtherComponent_EntityConstructedReverseOrder() {
        SourceComponent sourceComponent = new SourceComponent();
        DependentComponent dependentComponent = new DependentComponent();
        // Constructor order shouldn't matter here, even if we pass in dependantComponent first
        Entity entity = new Entity(dependentComponent, sourceComponent);
        assertThat(sourceComponent, equalTo(dependentComponent.getSourceComponent()));
    }

    @Test
    public void emptyEntityThrowsException() {
        assertException("Empty entities are not allowed", IllegalArgumentException.class, new Action() {
            @Override
            public void run() {
                Entity entity = new Entity();
            }
        });
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
    public void testGetComponents() {
        DummyComponent component1 = new DummyComponent();
        DummyComponent component2 = new DummyComponent();

        Entity entity = new Entity(component1, component2);

        assertThat(entity.getComponents(DummyComponent.class), containsInAnyOrder(component1, component2));
    }

    @Test
    public void requireSingleInstanceThrowsExceptionIfNoInstances() {
        DummyComponent dummyComponent = new DummyComponent();
        final Entity entity = new Entity(dummyComponent);

        assertException("requireSingleInstance fails if no instances", IllegalStateException.class, new Action() {
            @Override
            public void run() {
                entity.requireSingleInstance(SingletonComponent.class);
            }
        });
    }

    @Test
    public void requireSingleInstanceThrowsExceptionIfMultipleInstances() {
        final SingletonComponent singletonComponent1 = new SingletonComponent();
        final SingletonComponent singletonComponent2 = new SingletonComponent();

        assertException("requireSingleInstance fails if multiple instances", IllegalStateException.class, new Action() {
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

        assertException("requireSingleInstance fails if no instances", IllegalStateException.class, new Action() {
            @Override
            public void run() {
                entity.requireComponents(DummyComponent.class);
            }
        });
    }

}