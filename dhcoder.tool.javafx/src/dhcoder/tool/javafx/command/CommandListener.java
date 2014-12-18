package dhcoder.tool.javafx.command;

import dhcoder.tool.command.Command;
import dhcoder.tool.command.CommandScope;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

/**
 * A helper class which connects to a target JavaFX component and handles executing {@link Command}s registered within
 * a {@link CommandScope}
 */
public final class CommandListener {
    final CommandScope commandScope;
    private EventHandler<KeyEvent> onKeyPressed = new EventHandler<KeyEvent>() {
        @Override
        public void handle(final KeyEvent keyEvent) {
            if (commandScope.handle(KeyUtils.toShortcut(keyEvent))) {
                keyEvent.consume();
            }
        }
    };

    public CommandListener(final CommandScope commandScope) {
        this.commandScope = commandScope;
    }

    public void install(final Scene scene) {
        scene.setOnKeyPressed(onKeyPressed);
    }

    public void install(final Node node) {
        node.setOnKeyPressed(onKeyPressed);
    }

}
