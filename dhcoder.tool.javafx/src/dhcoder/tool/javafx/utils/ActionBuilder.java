package dhcoder.tool.javafx.utils;

import javafx.scene.input.KeyCombination;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionGroup;

import java.util.function.Predicate;

/**
 * A flat, searchable collection of {@link Action}s.
 */
public final class ActionBuilder {
    private String id;
    private ActionGroup parent;
    private String text;
    private String longText;
    private Runnable onAction;
    private Predicate<Void> actionTest;
    private KeyCombination accelerator;

    public ActionBuilder setId(final String id) {
        this.id = id;
        return this;
    }

    public ActionBuilder setParent(final ActionGroup parent) {
        this.parent = parent;
        return this;
    }

    public ActionBuilder setText(final String text, final String longText) {
        this.text = text;
        this.longText = longText;
        return this;
    }

    public ActionBuilder setOnAction(final Runnable onAction) {
        this.onAction = onAction;
        return this;
    }

    public ActionBuilder setActionTest(final Predicate<Void> actionTest) {
        this.actionTest = actionTest;
        return this;
    }

    public ActionBuilder setAccelerator(final KeyCombination accelerator) {
        this.accelerator = accelerator;
        return this;
    }

    public Action build() {

        if (onAction == null) {
            throw new IllegalStateException("Can't create an action without logic");
        }

        Action action = new Action(event -> {
            if (actionTest != null) {
                if (!actionTest.test(null)) {
                    return;
                }
            }

            onAction.run();
            event.consume();
        });

        if (id != null) {
            ActionCollection.setId(action, id);
        }

        if (text != null) {
            action.setText(text);
        }

        if (longText != null) {
            action.setLongText(longText);
        }

        if (accelerator != null) {
            action.setAccelerator(accelerator);
        }

        if (parent != null) {
            parent.getActions().add(action);
        }

        return action;
    }
}
