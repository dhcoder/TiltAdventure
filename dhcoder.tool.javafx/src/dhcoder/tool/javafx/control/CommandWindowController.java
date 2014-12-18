package dhcoder.tool.javafx.control;

import dhcoder.support.opt.Opt;
import dhcoder.support.text.StringUtils;
import dhcoder.tool.command.Command;
import dhcoder.tool.command.CommandManager;
import dhcoder.tool.command.CommandScope;
import dhcoder.tool.command.Shortcut;
import dhcoder.tool.javafx.command.CommandListener;
import dhcoder.tool.javafx.command.KeyCodeInt;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
            commandRowController.getPane().setBackground(Background.EMPTY);
            commandRowController.getPane()
                .setBackground(new Background(new BackgroundFill(Color.ORANGE, CornerRadii.EMPTY, Insets.EMPTY)));

            if (!empty) {
                commandRowController.getFlowCommandName().getChildren().add(new Text(command.getFullName()));
                Opt<Shortcut> shortcutOpt = command.getShortcutOpt();
                if (shortcutOpt.hasValue()) {
                    commandRowController.getLabelShortcut().setText(shortcutOpt.getValue().toString());
                }
            }

            if (isSelected()) {
                commandRowController.getPane()
                    .setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
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
            refreshSelectedCommand();
        });

        commandWindowScope.addLambdaCommand(Shortcut.noModifier(KeyCodeInt.DOWN), () -> {
            selectedCommandIndex = (selectedCommandIndex + 1) % matchedCommands.size();
            refreshSelectedCommand();
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
        listCommands.setFocusTraversable(false); // Only interact with the list indirectly!
        refreshSelectedCommand();

        textSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                final String newValue) {

                final String query = newValue;
                if (StringUtils.isWhitespace(query)) {
                    matchedCommands.setAll(allCommandsSorted);
                    return;
                }

                Pattern fuzzySearch = CommandManager.toFuzzySearch(query);
                matchedCommands.setAll(CommandManager.regexSearch(fuzzySearch, allCommandsSorted));

                if (matchedCommands.size() > 0) {
                    selectedCommandIndex = Math.min(selectedCommandIndex, matchedCommands.size() - 1);
                    refreshSelectedCommand();
                }

            }
        });
    }

    private void refreshSelectedCommand() {
        Platform.runLater(() -> listCommands.getSelectionModel().select(selectedCommandIndex));
    }

//    private void rebuildCommandsList() {
//        matchedCommands.clear();
//
//        int commandCount = matchedCommands.size();
//        for (int i = 0; i < commandCount; i++) {
//            commandsListModel.addElement(matchedCommands.get(i));
//        }
//
//        if (selectedCommandIndex < matchedCommands.size()) {
//            listCommands.setSelectedIndex(selectedCommandIndex);
//            listCommands.ensureIndexIsVisible(selectedCommandIndex);
//        }
//
//        listCommands.setVisibleRowCount(Math.min(MAX_VISIBLE_COMMANDS, matchedCommands.size()));
//        pack();
//
//    }
}
