package dhcoder.tool.command;

import org.junit.Test;

import static dhcoder.tool.command.CommandManager.regexSearch;
import static dhcoder.tool.command.CommandManager.toFuzzySearch;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class CommandManagerTest {

    private static class NoopCallback implements Command.RunCallback {
        @Override
        public void run() {
            // Do nothing
        }
    }

    private static final NoopCallback NOOP_CALLBACK = new NoopCallback();

    @Test
    public void testFuzzySearch() {
        CommandScope globalScope = new CommandScope("Global");
        Command command1 = new Command("test_id1", globalScope, "Test One", "Dummy desc 1", NOOP_CALLBACK);
        Command command2 = new Command("test_id2", globalScope, "Test Two", "Dummy desc 2", NOOP_CALLBACK);

        CommandManager commandManager = new CommandManager();
        commandManager.register(globalScope);

        assertThat(regexSearch(toFuzzySearch("Test One"), commandManager.allCommands()), contains(command1));
        assertThat(regexSearch(toFuzzySearch("Tw"), commandManager.allCommands()), contains(command2));
        assertThat(regexSearch(toFuzzySearch("so"), commandManager.allCommands()), contains(command1, command2));
    }

    @Test
    public void testScopedCommands() {
        final CommandScope parent1Scope = new CommandScope("Parent1");
        final CommandScope child11Scope = new CommandScope("Child11", parent1Scope);
        final CommandScope child12Scope = new CommandScope("Child12", parent1Scope);
        final CommandScope parent2Scope = new CommandScope("Parent2");
        final CommandScope child21Scope = new CommandScope("Child21", parent2Scope);
        final CommandScope extraScope = new CommandScope("Extra");

        final Command parent1_1 = new Command("p1_1", parent1Scope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Command parent1_2 = new Command("p1_2", parent1Scope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Command child11_1 = new Command("c11_1", child11Scope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Command child11_2 = new Command("c11_2", child11Scope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Command child12_1 = new Command("c12_1", child12Scope, "dumb name", "dumb desc", NOOP_CALLBACK);

        final Command parent2_1 = new Command("p2_1", parent2Scope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Command child21_1 = new Command("c21_1", child21Scope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Command child21_2 = new Command("c21_2", child21Scope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Command child21_3 = new Command("c21_3", child21Scope, "dumb name", "dumb desc", NOOP_CALLBACK);

        final Command extra_1 = new Command("ex_1", extraScope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Command extra_2 = new Command("ex_2", extraScope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Command extra_3 = new Command("ex_3", extraScope, "dumb name", "dumb desc", NOOP_CALLBACK);
        final Command extra_4 = new Command("ex_4", extraScope, "dumb name", "dumb desc", NOOP_CALLBACK);

        final CommandManager commandManager = new CommandManager();
        commandManager.register(parent1Scope);
        commandManager.register(parent2Scope);
        commandManager.register(extraScope);

        assertThat(parent1Scope.getAllCommands(),
            containsInAnyOrder(parent1_1, parent1_2, child11_1, child11_2, child12_1));
        assertThat(parent2Scope.getAllCommands(), containsInAnyOrder(parent2_1, child21_1, child21_2, child21_3));
        assertThat(extraScope.getAllCommands(), containsInAnyOrder(extra_1, extra_2, extra_3, extra_4));
        assertThat(commandManager.allCommands(),
            containsInAnyOrder(parent1_1, parent1_2, child11_1, child11_2, child12_1, parent2_1, child21_1, child21_2,
                child21_3, extra_1, extra_2, extra_3, extra_4));
    }
}