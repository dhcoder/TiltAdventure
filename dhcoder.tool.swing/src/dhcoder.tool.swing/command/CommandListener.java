package dhcoder.tool.swing.command;

import dhcoder.tool.command.CommandScope;
import dhcoder.tool.command.Shortcut;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Register this listener via {@link JComponent#addKeyListener(KeyListener)}
 */
public final class CommandListener extends KeyAdapter {
    final CommandScope commandScope;

    public CommandListener(final CommandScope commandScope) {
        this.commandScope = commandScope;
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        Shortcut shortcut = new Shortcut(e.isControlDown(), e.isAltDown(), e.isShiftDown(), e.getKeyCode());
        commandScope.handle(shortcut);
    }
}
