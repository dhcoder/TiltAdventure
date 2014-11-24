package dhcoder.libgdx.tool.widget;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import dhcoder.libgdx.tool.command.Command;
import dhcoder.libgdx.tool.command.CommandManager;
import dhcoder.support.text.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A modal dialog which can search through all actions registered with the current tool.
 */
public final class CommandWindow extends Table {
    public static final int MAX_COMMAND_COUNT = 30;
    private final Table commandsTable;
    private final ScrollPane commandsPane;
    private final TextField searchText;
    private final List<Command> allCommandsSorted;
    private List<Command> matchedCommands;
    private int selectedCommandIndex;

    public CommandWindow(final CommandManager commandManager, final Skin skin) {
        super(skin);

        searchText = new TextField("", skin);
        commandsTable = new Table();
        commandsPane = new ScrollPane(commandsTable, skin);
        commandsPane.setVisible(false);

        add(searchText).expandX().fillX();
        row();
        add(commandsPane).expand().maxHeight(400f).fillX().top();

        allCommandsSorted = commandManager.searchableCommands();
        allCommandsSorted.sort(new Comparator<Command>() {
            @Override
            public int compare(final Command o1, final Command o2) {
                return o1.getFullName().compareTo(o2.getFullName());
            }
        });
        matchedCommands = allCommandsSorted;

        searchText.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(final TextField textField, final char c) {
                final String query = textField.getText();
                if (StringUtils.isWhitespace(query)) {
                    matchedCommands = allCommandsSorted;
                    rebuildCommandsTable(skin);
                    return;
                }

                Pattern fuzzySearch = CommandManager.toFuzzySearch(query);
                matchedCommands = CommandManager.regexSearch(fuzzySearch, allCommandsSorted);

                if (matchedCommands.size() > 0) {
                    selectedCommandIndex = Math.min(selectedCommandIndex, matchedCommands.size() - 1);
                }
                rebuildCommandsTable(skin);
            }
        });

        addListener(new InputListener() {
            @Override
            public boolean keyDown(final InputEvent event, final int keycode) {
                if (keycode == Keys.UP) {
                    selectedCommandIndex--;
                    if (selectedCommandIndex < 0) {
                        selectedCommandIndex = matchedCommands.size() - 1;
                    }
                    rebuildCommandsTable(skin);
                    return true;
                }
                else if (keycode == Keys.DOWN) {
                    selectedCommandIndex = (selectedCommandIndex + 1) % matchedCommands.size();
                    rebuildCommandsTable(skin);
                    return true;
                }
                return false;
            }
        });

        setVisible(false);
    }

    public void show() {
        getStage().setKeyboardFocus(searchText);
        setVisible(true);
    }

    @Override
    public void layout() {
        super.layout();
    }

    private void rebuildCommandsTable(final Skin skin) {
        commandsTable.reset();
        commandsPane.setVisible(false);

        int commandCount = Math.min(MAX_COMMAND_COUNT, matchedCommands.size());
        for (int i = 0; i < commandCount; i++) {
            Command command = matchedCommands.get(i);
            Label commandLabel =
                new Label(getFormattedCommandName(command), skin, i == selectedCommandIndex ? "bold" : "default");
            commandsTable.add(commandLabel).expandX().fillX().pad(0f, 10f, 0f, 10f);
            commandsTable.row();
        }

        if (commandCount > 0) {
            commandsPane.setVisible(true);
        }
    }

    private String getFormattedCommandName(final Command command) {
        String name = command.getFullName();
        String query = searchText.getText();
        StringBuilder stringBuilder = new StringBuilder(name.length() + 2 * query.length()); // Extra for parens

        boolean inParens = false;
        int queryIndex = 0;
        for (int nameIndex = 0; nameIndex < name.length(); nameIndex++) {
            char nameChar = name.charAt(nameIndex);

            if (queryIndex < query.length()) {
                char queryChar = query.charAt(queryIndex);
                if (Character.toLowerCase(nameChar) == Character.toLowerCase(queryChar)) {
                    if (!inParens) {
                        stringBuilder.append('(');
                        inParens = true;
                    }
                    queryIndex++;
                }
                else {
                    if (inParens) {
                        stringBuilder.append(')');
                        inParens = false;
                    }
                }
            }
            else {
                if (inParens) {
                    stringBuilder.append(')');
                    inParens = false;
                }
            }
            stringBuilder.append(nameChar);
        }

        if (inParens) {
            stringBuilder.append(')');
        }

        return stringBuilder.toString();
    }
}
