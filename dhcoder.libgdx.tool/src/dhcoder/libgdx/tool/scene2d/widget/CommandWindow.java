package dhcoder.libgdx.tool.scene2d.widget;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import dhcoder.libgdx.tool.command.Command;
import dhcoder.libgdx.tool.command.CommandManager;
import dhcoder.libgdx.tool.command.CommandScope;
import dhcoder.libgdx.tool.command.Shortcut;
import dhcoder.libgdx.tool.scene2d.CommandListener;
import dhcoder.support.text.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A modal dialog which can search through all actions registered with the current tool.
 *
 * Requires the following font styles:
 *   - bold
 *   - default
 *   - italic-xs
 */
public final class CommandWindow extends Table {
    public static final int MAX_COMMAND_COUNT = 30;
    private final Table commandsTable;
    private final ScrollPane commandsPane;
    private final TextField searchText;
    private final List<Command> allCommandsSorted;
    private List<Command> matchedCommands;
    private int selectedCommandIndex;
    private Actor lastFocus;

    public CommandWindow(final CommandManager commandManager, final Skin skin) {
        super(skin);

        searchText = new TextField("", skin);
        commandsTable = new Table();
        commandsPane = new ScrollPane(commandsTable, skin);
        commandsPane.setFadeScrollBars(false);
        commandsPane.setVisible(false);

        add(searchText).expandX().fillX();
        row();
        add(commandsPane).expand().fillX().maxHeight(400f).top();

        allCommandsSorted = commandManager.searchableCommands();
        allCommandsSorted.sort(new Comparator<Command>() {
            @Override
            public int compare(final Command o1, final Command o2) {
                return o1.getFullName().compareTo(o2.getFullName());
            }
        });

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

        CommandScope commandWindowScope = new CommandScope();
        commandWindowScope.addLambdaCommand(Shortcut.noModifier(Keys.UP), new Command.RunCallback() {
            @Override
            public void run() {
                selectedCommandIndex--;
                if (selectedCommandIndex < 0) {
                    selectedCommandIndex = matchedCommands.size() - 1;
                }
                rebuildCommandsTable(skin);
            }
        });

        commandWindowScope.addLambdaCommand(Shortcut.noModifier(Keys.DOWN), new Command.RunCallback() {
            @Override
            public void run() {
                selectedCommandIndex = (selectedCommandIndex + 1) % matchedCommands.size();
                rebuildCommandsTable(skin);
            }
        });

        commandWindowScope.addLambdaCommand(Shortcut.noModifier(Keys.ENTER), new Command.RunCallback() {
            @Override
            public void run() {
                final Command command = matchedCommands.get(selectedCommandIndex);
                command.run();
                hide(true);
            }
        }).setActiveCallback(new Command.ActiveCallback() {
            @Override
            public boolean isActive() {
                return (selectedCommandIndex < matchedCommands.size());
            }
        });

        commandWindowScope.addLambdaCommand(Shortcut.noModifier(Keys.ESCAPE), new Command.RunCallback() {
            @Override
            public void run() {
                hide(true);
            }
        });

        addListener(new CommandListener(commandWindowScope));
        setVisible(false);
    }

    public void show() {
        lastFocus = getStage().getKeyboardFocus();
        getStage().setKeyboardFocus(searchText);
        getStage().setScrollFocus(commandsPane);
        setVisible(true);
        matchedCommands = allCommandsSorted;
        selectedCommandIndex = 0;
    }

    private void hide(final boolean restoreFocus) {
        setVisible(false);
        if (restoreFocus) {
            getStage().setKeyboardFocus(lastFocus);
        }
        getStage().setScrollFocus(null);
        commandsTable.clearChildren();
        searchText.setText("");
        lastFocus = null;
    }

    private void rebuildCommandsTable(final Skin skin) {
        commandsTable.clearChildren();
        commandsPane.setVisible(false);

        int commandCount = Math.min(MAX_COMMAND_COUNT, matchedCommands.size());
        for (int i = 0; i < commandCount; i++) {
            if (i > 0) {
                commandsTable.row();
            }

            final Command command = matchedCommands.get(i);
            final Label commandLabel =
                new Label(getFormattedCommandName(command), skin, i == selectedCommandIndex ? "bold" : "default");
            commandLabel.setEllipse(true);
            final Color defaultColor = commandLabel.getColor().cpy();
            commandLabel.setColor(defaultColor);
            commandLabel.addListener(new ClickListener() {
                @Override
                public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
                    final int button) {
                    command.run();
                    hide(false);
                    return true;
                }

                @Override
                public void enter(final InputEvent event, final float x, final float y, final int pointer,
                    final Actor fromActor) {
                    commandLabel.setColor(Color.YELLOW);
                }

                @Override
                public void exit(final InputEvent event, final float x, final float y, final int pointer,
                    final Actor toActor) {
                    commandLabel.setColor(defaultColor);
                }
            });
            commandsTable.add(commandLabel).expandX().fillX().pad(0f, 10f, 0f, 10f);

            if (command.getShortcutOpt().hasValue()) {
                Label shortcutLabel = new Label(command.getShortcutOpt().getValue().toString(), skin, "italic-xs");
                commandsTable.add(shortcutLabel).pad(0f, 0f, 0f, 10f);
            }
        }

        if (commandCount > 0) {
            // Ensure the current command is visible
            float rowHeight = commandsTable.getHeight() / commandCount;
            float ensureVisibleLowerY = rowHeight * selectedCommandIndex;
            float ensureVisibleUpperY = ensureVisibleLowerY + rowHeight;

            if (commandsPane.getScrollY() >= ensureVisibleLowerY) {
                // Scroll UP to the active command
                commandsPane.setScrollY(ensureVisibleLowerY);
            }
            else if (commandsPane.getScrollY() + commandsPane.getScrollHeight() <= ensureVisibleLowerY) {
                // Scroll DOWN to the active command
                commandsPane.setScrollY(ensureVisibleUpperY - commandsPane.getScrollHeight());
            }
            commandsPane.setVisible(true);
        }
    }

    private String getFormattedCommandName(final Command command) {
        String query = searchText.getText();
        if (StringUtils.isWhitespace(query)) {
            return command.getFullName();
        }
        String name = command.getFullName();
        StringBuilder stringBuilder = new StringBuilder(name.length() + 2 * query.length()); // Extra for parens

        boolean inParens = false;
        int queryIndex = 0;
        for (int nameIndex = 0; nameIndex < name.length(); nameIndex++) {
            char nameChar = name.charAt(nameIndex);

            if (queryIndex < query.length()) {
                char queryChar = query.charAt(queryIndex);

                if (Character.toLowerCase(nameChar) == Character.toLowerCase(queryChar)) {
                    if (!inParens) {
                        if (queryChar != ' ') {
                            stringBuilder.append('(');
                            inParens = true;
                        }
                    }
                    else {
                        if (queryChar == ' ') {
                            stringBuilder.append(')');
                            inParens = false;
                        }
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
