package tiltadv.game;

import com.sun.tracing.dtrace.DependencyClass;
import org.hamcrest.Matchers;
import org.junit.Test;
import tiltadv.game.components.Component;
import tiltadv.game.components.SingletonComponent;
import tiltadv.util.lambda.Action0;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static tiltadv.TestUtils.assertException;

public class EntityTest {

    private class DummyComponent implements SingletonComponent {

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
    private class SourceComponent implements SingletonComponent {
        @Override
        public void initialize(final Entity owner) {
        }
    }

    /**
     * This class expects to find a {@link SourceComponent} on the {@link Entity} it's attached to.
     */
    private class DependentComponent implements SingletonComponent {

        private SourceComponent sourceComponent;

        public SourceComponent getSourceComponent() {
            return sourceComponent;
        }

        @Override
        public void initialize(final Entity owner) {
            sourceComponent = owner.getComponent(SourceComponent.class);
        }
    }

    @Test
    public void componentGetsInitializedWithItsOwningEntity() {
        DummyComponent dummyComponent = new DummyComponent();
        assertThat(dummyComponent.getOwner(), nullValue());

        Entity entity = new Entity(dummyComponent);
        assertThat(entity, equalTo(dummyComponent.getOwner()));
    }

    @Test
    public void getComponentReturnsExpectedValues() {
        DummyComponent dummyComponent = new DummyComponent();
        DependentComponent dependentComponent = new DependentComponent();

        Entity entity = new Entity(dummyComponent, dependentComponent);
        assertThat(entity.getComponent(DummyComponent.class), equalTo(dummyComponent));
        assertThat(entity.getComponent(DependentComponent.class), equalTo(dependentComponent));
        assertThat(entity.getComponent(SourceComponent.class), nullValue());
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
    public void moreThanOneComponentOfTheSameTypeThrowsException() {

        final DummyComponent dummy1 = new DummyComponent();
        final DummyComponent dummy2 = new DummyComponent();

        assertException("Duplicate component types not allowed", IllegalArgumentException.class, new Action0() {
            @Override
            public void run() {
                Entity entity = new Entity(dummy1, dummy2);
            }
        });
    }
}