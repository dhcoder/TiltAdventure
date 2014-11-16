package dhcoder.libgdx.tool.command;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class CommandsTest {

    private static class NoopRunnable implements Runnable {
        @Override
        public void run() {
            // Do nothing
        }
    }

    @Test
    public void testFuzzySearch() throws Exception {
        Command command1 = new Command("test_id1", "Test One", "Just a dummy command", new NoopRunnable());
        Command command2 = new Command("test_id2", "Test Two", "Just a dummy command", new NoopRunnable());

        Commands commands = new Commands();
        commands.registerCommand(command1);
        commands.registerCommand(command2);

        assertThat(commands.fuzzySearch("Test One"), contains(command1));
        assertThat(commands.fuzzySearch("Tw"), contains(command2));
        assertThat(commands.fuzzySearch("sO"), contains(command1, command2));
    }
}