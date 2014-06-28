package tiltadv.game;

import org.junit.Test;
import tiltadv.game.components.Component;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;

public class EntityTest {

    private class DummyComponent implements Component {

        private Entity owner;

        @Override
        public void initialize(Entity owner) {
            this.owner = owner;
        }

        public Entity getOwner() {
            return owner;
        }
    }

    /**
     * This class exists only to be found by a {@link DependentComponent}
     */
    private class SourceComponent implements Component {
        @Override
        public void initialize(Entity owner) {
        }
    }

    /**
     * This class expects to find a {@link SourceComponent} on the {@link Entity} it's attached to.
     */
    private class DependentComponent implements Component {

        private SourceComponent sourceComponent;

        @Override
        public void initialize(Entity owner) {
            sourceComponent = owner.getComponent(SourceComponent.class);
        }

        public SourceComponent getSourceComponent() {
            return sourceComponent;
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
}