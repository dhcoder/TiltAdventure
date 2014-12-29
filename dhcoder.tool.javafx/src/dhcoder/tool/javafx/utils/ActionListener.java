package dhcoder.tool.javafx.utils;

import dhcoder.support.text.StringUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionGroup;

import java.util.ArrayList;
import java.util.Collection;

public final class ActionListener {

    Collection<Action> actions;

    private final EventHandler<KeyEvent> onKeyPressed = new EventHandler<KeyEvent>() {
        @Override
        public void handle(final KeyEvent keyEvent) {
            ActionEvent actionEvent = new ActionEvent();
            for (Action action : actions) {
                System.out.println(StringUtils.format("Matching {0} against {1} ({2})", keyEvent, action.getAccelerator(), action.getText()));

                if (!action.getAccelerator().match(keyEvent)) {
                    continue;
                }

                System.out.println("MATCH!");

                action.handle(actionEvent);
                if (actionEvent.isConsumed()) {
                    keyEvent.consume();
                    break;
                }
            }
        }
    };

    public ActionListener(final Action... actions) {
        this.actions = new ArrayList<>();

        for (Action action : actions) {
            flatten(action, this.actions);
        }
    }

    private void flatten(final Action action, final Collection<Action> actions) {
        if (action instanceof ActionGroup) {
            ActionGroup actionGroup = (ActionGroup)action;
            for (Action childAction : actionGroup.getActions()) {
                flatten(childAction, actions);
            }
        }
        else {
            actions.add(action);
        }
    }

    public void install(final Scene scene) {
        scene.setOnKeyPressed(onKeyPressed);
    }

    public void install(final Node node) {
        node.setOnKeyPressed(onKeyPressed);
    }

}