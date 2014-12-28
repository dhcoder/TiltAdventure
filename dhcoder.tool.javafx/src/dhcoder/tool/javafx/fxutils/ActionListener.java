package dhcoder.tool.javafx.fxutils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import org.controlsfx.control.action.Action;

import java.util.Arrays;
import java.util.Collection;

public final class ActionListener {

    Collection<Action> actions;

    private final EventHandler<KeyEvent> onKeyPressed = new EventHandler<KeyEvent>() {
        @Override
        public void handle(final KeyEvent keyEvent) {
            ActionEvent actionEvent = new ActionEvent();
            for (Action action : actions) {
                if (!action.getAccelerator().match(keyEvent)) {
                    continue;
                }

                action.handle(actionEvent);
                if (actionEvent.isConsumed()) {
                    keyEvent.consume();
                    break;
                }
            }
        }
    };

    public ActionListener(final Action... actions) {
        this.actions = Arrays.asList(actions);
    }

    public void install(final Scene scene) {
        scene.setOnKeyPressed(onKeyPressed);
    }

    public void install(final Node node) {
        node.setOnKeyPressed(onKeyPressed);
    }

}