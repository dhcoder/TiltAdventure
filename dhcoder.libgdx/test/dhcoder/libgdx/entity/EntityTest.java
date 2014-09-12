package dhcoder.libgdx.entity;

import dhcoder.support.time.Duration;
import org.junit.Test;

import static dhcoder.test.TestUtils.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class EntityTest {

    private final class DummyComponent extends AbstractComponent {

        private Entity owner;

        public Entity getOwner() {
            return owner;
        }

        @Override
        public void initialize(final Entity owner) {
            this.owner = owner;
        }
    }

    /**
     * This class exists only to be found by a {@link DependentComponent}
     */
    private final class SourceComponent extends AbstractComponent {}

    /**
     * This component expects to find a {@link SourceComponent} on the {@link Entity} it's attached to.
     */
    private final class DependentComponent extends AbstractComponent {

        private SourceComponent sourceComponent;

        public SourceComponent getSourceComponent() {
            return sourceComponent;
        }

        @Override
        public void initialize(final Entity owner) {
            sourceComponent = owner.requireComponent(SourceComponent.class);
        }
    }

    /**
     * This component asserts that it is the only one that exists on an entity
     */
    private final class SingletonComponent extends AbstractComponent {

        private boolean initialized;

        public boolean isInitialized() {
            return initialized;
        }

        @Override
        public void initialize(final Entity owner) {
            SingletonComponent singletonComponent = owner.requireComponent(SingletonComponent.class);
            assertThat(singletonComponent, equalTo(this));
            initialized = true;
        }

    }

    @Test
    public void componentGetsInitializedWithItsOwningEntityOnFirstUpdate() {
        DummyComponent dummyComponent = new DummyComponent();
        assertThat(dummyComponent.getOwner(), equalTo(null));

        Entity entity = new Entity();
        entity.addComponent(dummyComponent);
        assertThat(dummyComponent.getOwner(), equalTo(null));

        entity.update(Duration.zero());
        assertThat(entity, equalTo(dummyComponent.getOwner()));
    }

    @Test
    public void getComponentReturnsExpectedValues() {
        DummyComponent dummyComponent = new DummyComponent();
        SourceComponent sourceComponent = new SourceComponent();
        final Entity entity = new Entity();
        entity.addComponent(dummyComponent).addComponent(sourceComponent);
        entity.update(Duration.zero());
        assertThat(entity.requireComponent(DummyComponent.class), equalTo(dummyComponent));
        assertThat(entity.requireComponent(SourceComponent.class), equalTo(sourceComponent));
        assertException("RequireComponent throws exception if it can't find the component", IllegalStateException.class,
            new Runnable() {
                @Override
                public void run() {
                    entity.requireComponent(DependentComponent.class);
                }
            });
    }

    @Test
    public void dependantComponentCanFindOtherComponent() {
        SourceComponent sourceComponent = new SourceComponent();
        DependentComponent dependantComponent = new DependentComponent();
        Entity entity = new Entity();
        entity.addComponent(sourceComponent).addComponent(dependantComponent);
        entity.update(Duration.zero());
        assertThat(sourceComponent, equalTo(dependantComponent.getSourceComponent()));
    }

    @Test
    public void dependantComponentCanFindOtherComponent_EntityConstructedReverseOrder() {
        SourceComponent sourceComponent = new SourceComponent();
        DependentComponent dependentComponent = new DependentComponent();
        // Constructor order shouldn't matter here, even if we pass in dependantComponent first
        Entity entity = new Entity();
        entity.addComponent(dependentComponent).addComponent(sourceComponent);
        entity.update(Duration.zero());
        assertThat(sourceComponent, equalTo(dependentComponent.getSourceComponent()));
    }
}