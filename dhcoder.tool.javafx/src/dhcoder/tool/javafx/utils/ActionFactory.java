package dhcoder.tool.javafx.utils;

import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionGroup;

/**
 * A flat, searchable collection of {@link Action}s.
 */
public final class ActionFactory {
    public static Action create(final String id, final ActionGroup parent, final String text, final String longText,
        final Runnable onAction) {
        Action action = new Action(text, actionEvent -> {
            onAction.run();
            actionEvent.consume();
        });
        action.setLongText(longText);
        parent.getActions().add(action);
        ActionCollection.setId(action, id);

        return action;
    }
}
