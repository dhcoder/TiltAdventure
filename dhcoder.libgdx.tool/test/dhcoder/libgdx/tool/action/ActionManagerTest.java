package dhcoder.libgdx.tool.action;

import org.junit.Test;

import static dhcoder.libgdx.tool.action.ActionManager.regexSearch;
import static dhcoder.libgdx.tool.action.ActionManager.toFuzzySearch;
import static dhcoder.test.TestUtils.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class ActionManagerTest {

    private static class NoopCallback implements Action.RunCallback {
        @Override
        public void run() {
            // Do nothing
        }
    }

    private static final NoopCallback NOOP_CALLBACK = new NoopCallback();

    @Test
    public void testFuzzySearch() {
        ActionScope globalScope = new ActionScope();
        Action action1 = new Action("test_id1", globalScope, "Test One", "Dummy desc 1", NOOP_CALLBACK);
        Action action2 = new Action("test_id2", globalScope, "Test Two", "Dummy desc 2", NOOP_CALLBACK);

        ActionManager actionManager = new ActionManager();
        actionManager.register(action1);
        actionManager.register(action2);

        assertThat(regexSearch(toFuzzySearch("Test One"), actionManager.allActions()), contains(action1));
        assertThat(regexSearch(toFuzzySearch("Tw"), actionManager.allActions()), contains(action2));
        assertThat(regexSearch(toFuzzySearch("so"), actionManager.allActions()), contains(action1, action2));
    }

    @Test
    public void testScopedActions() {
        final ActionScope parent1Scope = new ActionScope("Parent1");
        final ActionScope child11Scope = new ActionScope("Child11", parent1Scope);
        final ActionScope child12Scope = new ActionScope("Child12", parent1Scope);
        final ActionScope parent2Scope = new ActionScope("Parent2");
        final ActionScope child21Scope = new ActionScope("Child21", parent2Scope);
        final ActionScope extraScope = new ActionScope("Extra");

        final Action parent1_1 = new Action("p1_1", parent1Scope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Action parent1_2 = new Action("p1_2", parent1Scope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Action child11_1 = new Action("c11_1", child11Scope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Action child11_2 = new Action("c11_2", child11Scope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Action child12_1 = new Action("c12_1", child12Scope, "dumb name", "dumb desc", NOOP_CALLBACK);

        final Action parent2_1 = new Action("p2_1", parent2Scope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Action child21_1 = new Action("c21_1", child21Scope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Action child21_2 = new Action("c21_2", child21Scope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Action child21_3 = new Action("c21_3", child21Scope, "dumb name", "dumb desc", NOOP_CALLBACK);

        final Action extra_1 = new Action("ex_1", extraScope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Action extra_2 = new Action("ex_2", extraScope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Action extra_3 = new Action("ex_3", extraScope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Action extra_4 = new Action("ex_4", extraScope, "dumb name", "dumb desc", NOOP_CALLBACK);

        final ActionManager actionManager = new ActionManager();
        actionManager.register(parent1_1);
        actionManager.register(parent1_2);
        actionManager.register(child11_1);
        actionManager.register(child11_2);
        actionManager.register(child12_1);

        actionManager.register(parent2_1);
        actionManager.register(child21_1);
        actionManager.register(child21_2);
        actionManager.register(child21_3);

        actionManager.register(extra_1);
        actionManager.register(extra_2);
        actionManager.register(extra_3);
        actionManager.register(extra_4);

        assertThat(actionManager.scopedActions(parent1Scope),
            contains(parent1_1, parent1_2, child11_1, child11_2, child12_1));
        assertThat(actionManager.scopedActions(child21Scope), contains(child21_1, child21_2, child21_3));
        assertThat(actionManager.scopedActions(extraScope), contains(extra_1, extra_2, extra_3, extra_4));
        assertThat(actionManager.scopedActions(parent1Scope, child21Scope, extraScope),
            contains(parent1_1, parent1_2, child11_1, child11_2, child12_1, child21_1, child21_2, child21_3, extra_1,
                extra_2, extra_3, extra_4));
    }

    @Test
    public void redudantScopesThrowsException() {
        final ActionScope parentScope = new ActionScope("Parent");
        final ActionScope childScope = new ActionScope("Child", parentScope);
        final ActionManager actionManager = new ActionManager();

        assertException("Redundant scope request throws exception", IllegalArgumentException.class, new Runnable() {
            @Override
            public void run() {
                actionManager.scopedActions(parentScope, childScope);
            }
        });
    }
}