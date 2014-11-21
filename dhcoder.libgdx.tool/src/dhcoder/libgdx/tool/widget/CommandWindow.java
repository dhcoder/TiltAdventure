package dhcoder.libgdx.tool.widget;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import dhcoder.libgdx.tool.command.Command;
import dhcoder.libgdx.tool.command.CommandManager;
import dhcoder.support.text.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * A modal dialog which can search through all actions registered with the current tool.
 */
public final class CommandWindow extends Table {
    private final CommandManager commandManager;
    private final ScrollPane commandsPane;
    private final TextField searchText;
    private final ListView<Command> commandList;
    private List<Command> consideredCommands;

    public CommandWindow(final CommandManager commandManager, final Skin skin) {
        super(skin);

        this.commandManager = commandManager;

        searchText = new TextField("", skin);
        commandList = new ListView<Command>(skin);
        commandsPane = new ScrollPane(commandList, skin);

        add(searchText).expandX();
        row();
        add(commandsPane).expandY();

        consideredCommands = commandManager.allCommands();

        searchText.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(final TextField textField, final char c) {
                commandList.clearItems();
                final String query = textField.getText();
                if (StringUtils.isWhitespace(query)) {
                    consideredCommands = commandManager.allCommands();
                    return;
                }

                Pattern fuzzySearch = CommandManager.toFuzzySearch(query);
                consideredCommands = CommandManager.regexSearch(fuzzySearch, consideredCommands);

                commandList.setItems((Command[])consideredCommands.toArray());
            }
        });
    }


}
