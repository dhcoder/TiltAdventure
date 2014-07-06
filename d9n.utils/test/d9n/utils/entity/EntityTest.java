package d9n.utils.entity;

import org.junit.Test;
import d9n.utils.entity.Component;
import d9n.utils.lambda.Action;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static d9n.utils.TestUtils.assertException;

public class EntityTest {

    private class DummyComponent implements Component {

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
    private class SourceComponent implements Component {

        @Override
        public void initialize(final Entity owner) { }

        @Override
        public void dispose() { }
    }

    /**
     * This class expects to find a {@link SourceComponent} on the {@link Entity} it's attached to.
     */
    private class DependentComponent implements Component {

        private SourceComponent sourceComponent;

        public SourceComponent getSourceComponent() {
            return sourceComponent;
        }

        @Override
        public void initialize(final Entity owner) {
            sourceComponent = owner.getComponent(SourceComponent.class).value();
        }

        @Override
        public void dispose() { }
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
}