package dhcoder.tool.javafx.control;

import dhcoder.support.text.StringUtils;
import dhcoder.tool.command.CommandManager;
import dhcoder.tool.javafx.fxutils.ActionListener;
import dhcoder.tool.javafx.fxutils.FontUtils;
import dhcoder.tool.javafx.fxutils.FxController;
import dhcoder.tool.javafx.fxutils.ListViewUtils;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.text.Text;
import org.controlsfx.control.action.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class CommandWindowController extends FxController {

    private class CommandRowCell extends ListCell<Action> {

        private CommandRowController commandRowController;

        public CommandRowCell() {
            commandRowController = FxController.load(CommandRowController.class);
        }

        @Override
        protected void updateItem(final Action action, final boolean empty) {
            super.updateItem(action, empty);

            commandRowController.getFlowCommandName().getChildren().clear();
            commandRowController.getLabelShortcut().setText("");
            commandRowController.getPane().setBackground(Background.EMPTY);

            if (!empty) {
                updateName(action);
                if (action.getAccelerator() != null) {
                    commandRowController.getLabelShortcut().setText(action.getAccelerator().getDisplayText());
                }
            }

            setGraphic(commandRowController.getPane());
        }

        private void updateName(final Action command) {
            String name = command.getText();

            final String query = textSearch.getText();
            if (StringUtils.isWhitespace(query)) {
                addText(name); // Whitespace only means no filtering
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

    @FXML private TextField textSearch;
    @FXML private ListView<Action> listCommands;
    private List<Action> allCommandsSorted;
    private ObservableList<Action> matchedCommands;
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
        matchedCommands.addListener((ListChangeListener<Action>)c -> {
            if (matchedCommands.size() > MAX_COMMANDS) {
                matchedCommands.remove(MAX_COMMANDS, matchedCommands.size());
            }
        });
        matchedCommands.addAll(allCommandsSorted);

        Action prevCommand = new Action(actionEvent -> {
            if (matchedCommands.size() <= 0) {return;}

            selectedCommandIndex--;
            if (selectedCommandIndex < 0) {
                selectedCommandIndex = matchedCommands.size() - 1;
            }
            updateSelection();

            actionEvent.consume();
        });
        prevCommand.setAccelerator(new KeyCodeCombination(KeyCode.UP));

        Action nextCommand = new Action(actionEvent -> {
            if (matchedCommands.size() <= 0) {return;}

            selectedCommandIndex = (selectedCommandIndex + 1) % matchedCommands.size();
            updateSelection();

            actionEvent.consume();
        });
        nextCommand.setAccelerator(new KeyCodeCombination(KeyCode.DOWN));

        Action acceptCommand = new Action(actionEvent -> {
            if (selectedCommandIndex < 0 || selectedCommandIndex >= matchedCommands.size()) {return;}

            final Action action = matchedCommands.get(selectedCommandIndex);
            commandWindow.hide();
            action.handle(new ActionEvent());

            actionEvent.consume();
        });
        acceptCommand.setAccelerator(new KeyCodeCombination(KeyCode.ENTER));

        Action closeWindow = new Action(actionEvent -> {
            commandWindow.hide();
            actionEvent.consume();
        });
        closeWindow.setAccelerator(new KeyCodeCombination(KeyCode.ESCAPE));

        ActionListener actionListener = new ActionListener(nextCommand, prevCommand, acceptCommand, closeWindow);
        actionListener.install(textSearch);

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
