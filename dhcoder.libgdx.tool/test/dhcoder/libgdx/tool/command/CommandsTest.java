package dhcoder.libgdx.tool.command;

import org.junit.Test;

import static dhcoder.libgdx.tool.command.Commands.regexSearch;
import static dhcoder.libgdx.tool.command.Commands.toFuzzySearch;
import static dhcoder.test.TestUtils.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class CommandsTest {

    private static class NoopCallback implements Command.RunCallback {
        @Override
        public void run() {
            // Do nothing
        }
    }

    private static final NoopCallback NOOP_CALLBACK = new NoopCallback();

    @Test
    public void testFuzzySearch() {
        CommandScope globalScope = new CommandScope();
        Command command1 = new Command("test_id1", globalScope, "Test One", "Dummy desc 1", NOOP_CALLBACK);
        Command command2 = new Command("test_id2", globalScope, "Test Two", "Dummy desc 2", NOOP_CALLBACK);

        Commands commands = new Commands();
        commands.registerCommand(command1);
        commands.registerCommand(command2);

        assertThat(regexSearch(toFuzzySearch("Test One"), commands.all()), contains(command1));
        assertThat(regexSearch(toFuzzySearch("Tw"), commands.all()), contains(command2));
        assertThat(regexSearch(toFuzzySearch("so"), commands.all()), contains(command1, command2));
    }

    @Test
    public void redudantScopesThrowsException() {
        final CommandScope parentScope = new CommandScope("Parent");
        final CommandScope childScope = new CommandScope("Child", parentScope);
        final Commands commands = new Commands();

        assertException("Redundant scope request throws exception", IllegalArgumentException.class, new Runnable() {
            @Override
            public void run() {
                commands.scoped(parentScope, childScope);
            }
        });
    }
}