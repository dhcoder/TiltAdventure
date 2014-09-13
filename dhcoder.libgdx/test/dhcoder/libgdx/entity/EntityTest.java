package dhcoder.libgdx.entity;

import dhcoder.support.time.Duration;
import org.junit.Test;

import static dhcoder.test.TestUtils.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class EntityTest {

    private static final class DummyComponent extends AbstractComponent {
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
    private static final class SourceComponent extends AbstractComponent {}

    /**
     * This component expects to find a {@link SourceComponent} on the {@link Entity} it's attached to.
     */
    private static final class DependentComponent extends AbstractComponent {

        private SourceComponent sourceComponent;

        public SourceComponent getSourceComponent() {
            return sourceComponent;
        }

        @Override
        public void initialize(final Entity owner) {
            sourceComponent = owner.requireComponent(SourceComponent.class);
        }
    }

    @Test
    public void managerCanCreateEntities() {
        EntityManager manager = new EntityManager(1);
        Entity entity = manager.newEntity();
        assertThat(entity.getManager(), equalTo(manager));
    }

    @Test
    public void componentGetsInitializedWithItsOwningEntityOnFirstUpdate() {
        EntityManager manager = new EntityManager(1);

        DummyComponent dummyComponent = manager.newComponent(DummyComponent.class);
        assertThat(dummyComponent.getOwner(), equalTo(null));

        Entity entity = manager.newEntity();
        entity.addComponent(dummyComponent);
        assertThat(dummyComponent.getOwner(), equalTo(null));

        entity.update(Duration.zero());
        assertThat(entity, equalTo(dummyComponent.getOwner()));
    }

    @Test
    public void requireComponentReturnsExpectedValues() {
        EntityManager manager = new EntityManager(1);

        DummyComponent dummyComponent = manager.newComponent(DummyComponent.class);
        SourceComponent sourceComponent = manager.newComponent(SourceComponent.class);

        final Entity entity = manager.newEntity();
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
        EntityManager manager = new EntityManager(1);

        SourceComponent sourceComponent = manager.newComponent(SourceComponent.class);
        DependentComponent dependantComponent = manager.newComponent(DependentComponent.class);
        Entity entity = manager.newEntity();

        entity.addComponent(sourceComponent).addComponent(dependantComponent);
        entity.update(Duration.zero());
        assertThat(sourceComponent, equalTo(dependantComponent.getSourceComponent()));
    }

    @Test
    public void dependantComponentCanFindOtherComponent_EntityConstructedReverseOrder() {
        EntityManager manager = new EntityManager(1);

        SourceComponent sourceComponent = manager.newComponent(SourceComponent.class);
        DependentComponent dependantComponent = manager.newComponent(DependentComponent.class);

        // Component order shouldn't matter here, even if we pass in dependantComponent first
        Entity entity = manager.newEntity();
        entity.addComponent(dependantComponent).addComponent(sourceComponent);
        entity.update(Duration.zero());
        assertThat(sourceComponent, equalTo(dependantComponent.getSourceComponent()));
    }
}