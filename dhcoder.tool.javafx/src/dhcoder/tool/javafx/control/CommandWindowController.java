package dhcoder.tool.javafx.control;

import dhcoder.support.opt.Opt;
import dhcoder.tool.command.Command;
import dhcoder.tool.command.CommandScope;
import dhcoder.tool.command.Shortcut;
import dhcoder.tool.javafx.command.CommandListener;
import dhcoder.tool.javafx.command.KeyCodeInt;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CommandWindowController {
    private CommandWindow commandWindow;

    @FXML private TextField textSearch;
    @FXML private ListView<Command> listCommands;

    private List<Command> allCommandsSorted;
    private ObservableList<Command> matchedCommands;
    private int selectedCommandIndex;

    private class CommandRowCell extends ListCell<Command> {

        private CommandRowController commandRowController;

        public CommandRowCell() {
            FXMLLoader loader = new FXMLLoader(CommandWindow.class.getResource("CommandRowView.fxml"));
            try {
                loader.load();
                commandRowController = loader.getController();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override

        protected void updateItem(final Command command, final boolean empty) {
            commandRowController.getFlowCommandName().getChildren().clear();
            commandRowController.getLabelShortcut().setText("");

            if (!empty) {
                commandRowController.getFlowCommandName().getChildren().add(new Text(command.getFullName()));
                Opt<Shortcut> shortcutOpt = command.getShortcutOpt();
                if (shortcutOpt.hasValue()) {
                    commandRowController.getLabelShortcut().setText(shortcutOpt.getValue().toString());
                }
            }

            setGraphic(commandRowController.getPane());
        }
    }

    public void setCommandWindow(final CommandWindow commandWindow) {
        this.commandWindow = commandWindow;

        allCommandsSorted = new ArrayList<>(commandWindow.getCommandManager().searchableCommands());
        allCommandsSorted.sort((command1, command2) -> command1.getFullName().compareTo(command2.getFullName()));
        matchedCommands = FXCollections.observableArrayList(allCommandsSorted);

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

        listCommands.setCellFactory(param -> new CommandRowCell());
        listCommands.setItems(matchedCommands);
        listCommands.setFocusTraversable(false);
    }

    private void rebuildCommandsList() {}
}
