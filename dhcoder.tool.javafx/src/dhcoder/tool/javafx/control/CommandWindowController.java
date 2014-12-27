package dhcoder.tool.javafx.control;

import dhcoder.support.opt.Opt;
import dhcoder.support.text.StringUtils;
import dhcoder.tool.command.Command;
import dhcoder.tool.command.CommandManager;
import dhcoder.tool.command.CommandScope;
import dhcoder.tool.command.Shortcut;
import dhcoder.tool.javafx.command.CommandListener;
import dhcoder.tool.javafx.command.KeyCodeInt;
import dhcoder.tool.javafx.fxutils.FontUtils;
import dhcoder.tool.javafx.fxutils.FxController;
import dhcoder.tool.javafx.fxutils.ListViewUtils;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class CommandWindowController extends FxController {



    private class CommandRowCell extends ListCell<Command> {

        private CommandRowController commandRowController;

        public CommandRowCell() {
            commandRowController = FxController.load(CommandRowController.class);
        }

        @Override
        protected void updateItem(final Command command, final boolean empty) {
            super.updateItem(command, empty);

            commandRowController.getFlowCommandName().getChildren().clear();
            commandRowController.getLabelShortcut().setText("");
            commandRowController.getPane().setBackground(Background.EMPTY);

            if (!empty) {
                updateName(command);
                Opt<Shortcut> shortcutOpt = command.getShortcutOpt();
                if (shortcutOpt.hasValue()) {
                    commandRowController.getLabelShortcut().setText(shortcutOpt.getValue().toString());
                }
            }

            setGraphic(commandRowController.getPane());
        }

        private void updateName(final Command command) {
            String name = command.getFullName();

            final String query = textSearch.getText();
            if (StringUtils.isWhitespace(query)) {
                addText(name);
                return;
            }

            StringBuilder stringBuilder = new StringBuilder();

            boolean inBoldSection = false;
            int queryIndex = 0;
            for (int nameIndex = 0; nameIndex < name.length(); nameIndex++) {
                char nameChar = name.charAt(nameIndex);

                if (queryIndex < query.length()) {
                    char queryChar = query.charAt(queryIndex);

                    if (Character.toLowerCase(nameChar) == Character.toLowerCase(queryChar)) {
                        if (!inBoldSection) {
                            if (queryChar != ' ') {
                                addTextSoFar(stringBuilder, inBoldSection);
                                inBoldSection = true;
                            }
                        }
                        else {
                            if (queryChar == ' ') {
                                addTextSoFar(stringBuilder, inBoldSection);
                                inBoldSection = false;
                            }
                        }
                        queryIndex++;
                    }
                    else {
                        if (inBoldSection) {
                            addTextSoFar(stringBuilder, inBoldSection);
                            inBoldSection = false;
                        }
                    }
                }
                else {
                    if (inBoldSection) {
                        addTextSoFar(stringBuilder, inBoldSection);
                        inBoldSection = false;
                    }
                }
                stringBuilder.append(nameChar);
            }

            addTextSoFar(stringBuilder, inBoldSection);
        }

        private void addText(final String text) {
            commandRowController.getFlowCommandName().getChildren().add(new Text(text));
        }

        private void addTextSoFar(final StringBuilder stringBuilder, final boolean isBold) {
            if (stringBuilder.length() == 0) {
                return;
            }

            Text text = new Text(stringBuilder.toString());
            stringBuilder.setLength(0);

            if (isBold) {
                text.setFont(FontUtils.cloneBold(text.getFont()));
            }
            commandRowController.getFlowCommandName().getChildren().add(text);
        }
    }

    private static final int MAX_COMMANDS = 30;

    @FXML private Parent rootPane;
    @FXML private TextField textSearch;
    @FXML private ListView<Command> listCommands;
    private List<Command> allCommandsSorted;
    private ObservableList<Command> matchedCommands;
    private int selectedCommandIndex;

    public void setCommandWindow(final CommandWindow commandWindow) {

        commandWindow.setOnShown(event -> {
            textSearch.clear();
            selectedCommandIndex = 0;
            updateSelection();
        });

        allCommandsSorted = new ArrayList<>(commandWindow.getCommandManager().searchableCommands());
        allCommandsSorted.sort((command1, command2) -> command1.getFullName().compareTo(command2.getFullName()));
        matchedCommands = FXCollections.observableArrayList();
        matchedCommands.addListener((ListChangeListener<Command>)c -> {
            if (matchedCommands.size() > MAX_COMMANDS) {
                matchedCommands.remove(MAX_COMMANDS, matchedCommands.size());
            }
        });
        matchedCommands.addAll(allCommandsSorted);

        CommandScope commandWindowScope = new CommandScope("CommandWindow");
        commandWindowScope.addLambdaCommand(Shortcut.of(KeyCodeInt.UP).build(), () -> {
            selectedCommandIndex--;
            if (selectedCommandIndex < 0) {
                selectedCommandIndex = matchedCommands.size() - 1;
            }
            updateSelection();
        });

        commandWindowScope.addLambdaCommand(Shortcut.of(KeyCodeInt.DOWN).build(), () -> {
            selectedCommandIndex = (selectedCommandIndex + 1) % matchedCommands.size();
            updateSelection();
        });

        commandWindowScope.addLambdaCommand(Shortcut.of(KeyCodeInt.ENTER).build(), () -> {
            final Command command = matchedCommands.get(selectedCommandIndex);
            commandWindow.hide();
            command.run();
        }).setActiveCallback(() -> (selectedCommandIndex < matchedCommands.size()));

        commandWindowScope.addLambdaCommand(Shortcut.of(KeyCodeInt.ESCAPE).build(), commandWindow::hide);

        CommandListener commandListener = new CommandListener(commandWindowScope);
        commandListener.install(textSearch);

        listCommands.setCellFactory(param -> new CommandRowCell());
        listCommands.setItems(matchedCommands);
        ListViewUtils.sizeToContents(listCommands);

        updateSelection();

        textSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            final String query = newValue;
            if (StringUtils.isWhitespace(query)) {
                matchedCommands.setAll(allCommandsSorted);
            }
            else {
                Pattern fuzzySearch = CommandManager.toFuzzySearch(query);
                matchedCommands.setAll(CommandManager.regexSearch(fuzzySearch, allCommandsSorted));
            }

            if (matchedCommands.size() > 0) {
                selectedCommandIndex = Math.min(selectedCommandIndex, matchedCommands.size() - 1);
                updateSelection();
            }

            ListViewUtils.forceRefresh(listCommands);
        });
    }

    @Override
    public Parent getRoot() {
        return rootPane;
    }

    private void updateSelection() {
        listCommands.getSelectionModel().select(selectedCommandIndex);
    }
}
