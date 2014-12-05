package dhcoder.tool.swing.command;

import dhcoder.support.opt.Opt;
import dhcoder.tool.command.Command;
import dhcoder.tool.command.CommandScope;
import dhcoder.tool.command.Shortcut;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;

/**
 * Register this listener via {@link JComponent#addKeyListener(KeyListener)}
 */
public final class CommandListener {
    final CommandScope commandScope;

    public CommandListener(final CommandScope commandScope) {
        this.commandScope = commandScope;
    }

    /**
     * Listen to the target component and all of its children.
     */
    public void registerUmbrellaListener(final JComponent component) {
        Collection<Command> allCommands = commandScope.getAllCommands();
        for (final Command command : allCommands) {
            Opt<Shortcut> shortcutOpt = command.getShortcutOpt();
            if (shortcutOpt.hasValue()) {
                component.getInputMap().put(KeystrokeUtils.fromShortcut(shortcutOpt.getValue()), command);
                component.getActionMap().put(command, new AbstractAction() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        command.run();
                    }
                });
            }
        }
    }

    /**
     * Listen to just the target component, especially useful for leaf components. Unlike {@link
     * CommandListener#registerUmbrellaListener(JComponent)}, this has a chance to listen to key events first.
     */
    public void registerListener(final JComponent component) {
        component.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (commandScope.handle(KeystrokeUtils.toShortcut(e))) {
                    e.consume();
                }
            }
        });
    }

}
