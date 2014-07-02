package tiltadv.game.components;

import org.junit.Test;
import tiltadv.game.Entity;
import tiltadv.util.lambda.Action0;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static tiltadv.TestUtils.assertException;

public class ComponentGroupTest {

    private class DummyComponent implements Component {

        private Entity owner;

        public Entity getOwner() {
            return owner;
        }

        @Override
        public void initialize(final Entity owner) {
            this.owner = owner;
        }
    }

    private class DummyComponents extends ComponentGroup<DummyComponent> {
        private DummyComponents(final DummyComponent... components) {
            super(components);
        }
    }

    @Test
    public void componentGroupsInitializeMultipleComponents() {
        DummyComponent dummy1 = new DummyComponent();
        DummyComponent dummy2 = new DummyComponent();
        DummyComponents dummyComponents = new DummyComponents(dummy1, dummy2);
        assertThat(dummy1.getOwner(), nullValue());
        assertThat(dummy2.getOwner(), nullValue());

        Entity entity = new Entity(dummyComponents);
        assertThat(dummy1.getOwner(), equalTo(entity));
        assertThat(dummy2.getOwner(), equalTo(entity));
    }

    @Test
    public void duplicateComponentsThrowsException() {
        final DummyComponent dummyComponent = new DummyComponent();
        assertException("Duplicates not allowed in component group", IllegalArgumentException.class, new Action0() {
            @Override
            public void run() {
                DummyComponents dummyComponents = new DummyComponents(dummyComponent, dummyComponent);
            }
        });
    }
}