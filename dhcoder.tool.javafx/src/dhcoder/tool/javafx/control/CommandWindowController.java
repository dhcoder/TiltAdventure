package dhcoder.tool.javafx.control;

import dhcoder.tool.command.Command;
import dhcoder.tool.command.CommandScope;
import dhcoder.tool.command.Shortcut;
import dhcoder.tool.javafx.command.CommandListener;
import dhcoder.tool.javafx.command.KeyCodeInt;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public final class CommandWindowController {
    private CommandWindow commandWindow;

    @FXML private TextField textSearch;
    @FXML private ListView listCommands;

    private List<Command> allCommandsSorted;
    private List<Command> matchedCommands;
    private int selectedCommandIndex;

    public void setCommandWindow(final CommandWindow commandWindow) {
        this.commandWindow = commandWindow;

        allCommandsSorted = new ArrayList<>(commandWindow.getCommandManager().searchableCommands());

        CommandScope commandWindowScope = new CommandScope("CommandWindow");
        commandWindowScope.addLambdaCommand(Shortcut.noModifier(KeyCodeInt.UP), () -> {
            selectedCommandIndex--;
            if (selectedCommandIndex < 0) {
                selectedCommandIndex = matchedCommands.size() - 1;
            }
            rebuildCommandsList();
        });

        commandWindowScope.addLambdaCommand(Shortcut.noModifier(KeyCodeInt.DOWN), () -> {
            selectedCommandIndex = (selectedCommandIndex + 1) % matchedCommands.size();
            rebuildCommandsList();
        });

        commandWindowScope.addLambdaCommand(Shortcut.noModifier(KeyCodeInt.ENTER), () -> {
            final Command command = matchedCommands.get(selectedCommandIndex);
            commandWindow.hide();
            command.run();
        }).setActiveCallback(() -> (selectedCommandIndex < matchedCommands.size()));

        commandWindowScope.addLambdaCommand(Shortcut.noModifier(KeyCodeInt.ESCAPE), () -> {
            commandWindow.hide();
        });

        CommandListener commandListener = new CommandListener(commandWindowScope);
        commandListener.install(textSearch);
    }

    private void rebuildCommandsList() {}
}
