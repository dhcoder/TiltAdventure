package tiltadv.game.components;

import org.junit.Test;
import tiltadv.game.Entity;
import tiltadv.util.lambda.Action0;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static tiltadv.TestUtils.assertException;

public final class ComponentGroupTest {

    private class Component1 implements Component {

        private Entity owner;

        public Entity getOwner() {
            return owner;
        }

        @Override
        public void initialize(final Entity owner) {
            this.owner = owner;
        }

        @Override
        public void dispose() { }
    }

    private class ComponentGroup1 extends ComponentGroup<Component1> {
        private ComponentGroup1(final Component1... components) {
            super(components);
        }
    }

    private class Component2 implements Component {
        @Override
        public void initialize(final Entity owner) { }

        @Override
        public void dispose() {
        }
    }

    private class ComponentGroup2 extends ComponentGroup<Component2> {
        private ComponentGroup2(final Component2... components) {
            super(components);
        }
    }

    @Test
    public void componentGroupsInitializeMultipleComponents() {
        Component1 component1 = new Component1();
        Component1 component2 = new Component1();
        ComponentGroup1 componentGroup1 = new ComponentGroup1(component1, component2);
        assertThat(component1.getOwner(), nullValue());
        assertThat(component2.getOwner(), nullValue());

        Entity entity = new Entity(componentGroup1);
        assertThat(component1.getOwner(), equalTo(entity));
        assertThat(component2.getOwner(), equalTo(entity));
    }

    @Test
    public void entityCanHandleAddingDifferentComponentGroups() {
        Component1 component1 = new Component1();
        Component2 component2 = new Component2();
        ComponentGroup1 componentGroup1 = new ComponentGroup1(component1);
        ComponentGroup2 componentGroup2 = new ComponentGroup2(component2);

        Entity entity = new Entity(componentGroup1, componentGroup2);
        assertThat(entity.getExpectedComponent(ComponentGroup1.class), equalTo(componentGroup1));
        assertThat(entity.getExpectedComponent(ComponentGroup2.class), equalTo(componentGroup2));
    }

    @Test
    public void duplicateComponentsThrowsException() {
        final Component1 component1 = new Component1();
        assertException("Duplicates not allowed in component group", IllegalArgumentException.class, new Action0() {
            @Override
            public void run() {
                new ComponentGroup1(component1, component1);
            }
        });
    }
}