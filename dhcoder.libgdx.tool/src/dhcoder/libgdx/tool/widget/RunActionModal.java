package dhcoder.libgdx.tool.widget;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import dhcoder.libgdx.tool.action.Action;
import dhcoder.libgdx.tool.action.ActionManager;
import dhcoder.support.text.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * A modal dialog which can search through all actions registered with the current tool.
 */
public final class RunActionModal extends Table {
    private final ActionManager actionManager;
    private final ScrollPane actionsPane;
    private final TextField searchText;
    private final ListView<Action> actionsList;
    private List<Action> consideredActions;

    public RunActionModal(final ActionManager actionManager, final Skin skin) {
        super(skin);

        this.actionManager = actionManager;

        searchText = new TextField("", skin);
        actionsList = new ListView<Action>(skin);
        actionsPane = new ScrollPane(actionsList, skin);

        add(searchText).expandX();
        row();
        add(actionsPane).expandY();

        consideredActions = actionManager.allActions();

        searchText.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(final TextField textField, final char c) {
                actionsList.clearItems();
                final String query = textField.getText();
                if (StringUtils.isWhitespace(query)) {
                    consideredActions = actionManager.allActions();
                    return;
                }

                Pattern fuzzySearch = ActionManager.toFuzzySearch(query);
                consideredActions = ActionManager.regexSearch(fuzzySearch, consideredActions);

                actionsList.setItems((Action[])consideredActions.toArray());
            }
        });
    }


}
