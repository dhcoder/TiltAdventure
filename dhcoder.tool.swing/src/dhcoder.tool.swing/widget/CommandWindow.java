package dhcoder.tool.swing.widget;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import dhcoder.support.text.StringUtils;
import dhcoder.tool.command.Command;
import dhcoder.tool.command.CommandManager;
import dhcoder.tool.command.CommandScope;
import dhcoder.tool.command.Shortcut;
import dhcoder.tool.swing.command.CommandListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_UP;

/**
 * A widget which can search through all actions registered with the current tool.
 */
public final class CommandWindow extends JDialog {
    public static final int MAX_VISIBLE_COMMANDS = 16;

    class CommandRowContext {
        public String getQuery() {
            return textSearch.getText();
        }
    }

    private JTextField textSearch;
    private JList<Command> listCommands;
    private JScrollPane scrollCommands;
    private JPanel panelRoot;

    private final List<Command> allCommandsSorted;
    private List<Command> matchedCommands;
    private int selectedCommandIndex;

    String getQuery() {
        return textSearch.getText();
    }

    int getSelectedCommandIndex() {
        return selectedCommandIndex;
    }

    public CommandWindow(final Frame owner, final CommandManager commandManager) {
        super(owner);

        allCommandsSorted = new ArrayList<Command>(commandManager.searchableCommands());
        Collections.sort(allCommandsSorted, new Comparator<Command>() {
            @Override
            public int compare(final Command o1, final Command o2) {
                return o1.getFullName().compareTo(o2.getFullName());
            }
        });
        matchedCommands = allCommandsSorted;

        setContentPane(panelRoot);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setUndecorated(true);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(final ComponentEvent componentEvent) {
                textSearch.setText("");
                textSearch.grabFocus();
                rebuildCommandsList();
            }
        });

        this.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(final WindowEvent e) {
                //do nothing
            }

            @Override
            public void windowLostFocus(final WindowEvent e) {
                if (e.getOppositeWindow() == null) {
                    return;
                }
                if (SwingUtilities.isDescendingFrom(e.getOppositeWindow(), CommandWindow.this)) {
                    return;
                }
                setVisible(false);
            }
        });

        CommandScope commandWindowScope = new CommandScope("CommandWindow");
        commandWindowScope.addLambdaCommand(Shortcut.noModifier(VK_UP), new Command.RunCallback() {
            @Override
            public void run() {
                selectedCommandIndex--;
                if (selectedCommandIndex < 0) {
                    selectedCommandIndex = matchedCommands.size() - 1;
                }
                rebuildCommandsList();
            }
        });

        commandWindowScope.addLambdaCommand(Shortcut.noModifier(VK_DOWN), new Command.RunCallback() {
            @Override
            public void run() {
                selectedCommandIndex = (selectedCommandIndex + 1) % matchedCommands.size();
                rebuildCommandsList();
            }
        });

        commandWindowScope.addLambdaCommand(Shortcut.noModifier(VK_ENTER), new Command.RunCallback() {
            @Override
            public void run() {
                final Command command = matchedCommands.get(selectedCommandIndex);
                setVisible(false);
                command.run();
            }
        }).setActiveCallback(new Command.ActiveCallback() {
            @Override
            public boolean isActive() {
                return (selectedCommandIndex < matchedCommands.size());
            }
        });

        commandWindowScope.addLambdaCommand(Shortcut.noModifier(VK_ESCAPE), new Command.RunCallback() {
            @Override
            public void run() {
                setVisible(false);
            }
        });

        CommandListener commandListener = new CommandListener(commandWindowScope);
        commandListener.registerListener(textSearch);

        listCommands.setCellRenderer(new CommandRowRenderer());
        listCommands.setModel(new DefaultListModel<Command>());
        listCommands.putClientProperty("context", new CommandRowContext()); // For handoff to CommandRowRenderer

        textSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                updateMatches();
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                updateMatches();
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                updateMatches();
            }

            private void updateMatches() {
                final String query = textSearch.getText();
                if (StringUtils.isWhitespace(query)) {
                    matchedCommands = allCommandsSorted;
                    rebuildCommandsList();
                    return;
                }

                Pattern fuzzySearch = CommandManager.toFuzzySearch(query);
                matchedCommands = CommandManager.regexSearch(fuzzySearch, allCommandsSorted);

                if (matchedCommands.size() > 0) {
                    selectedCommandIndex = Math.min(selectedCommandIndex, matchedCommands.size() - 1);
                }
                rebuildCommandsList();
            }
        });
    }

    private void rebuildCommandsList() {
        DefaultListModel<Command> commandsListModel = (DefaultListModel<Command>)listCommands.getModel();
        commandsListModel.clear();

        int commandCount = matchedCommands.size();
        for (int i = 0; i < commandCount; i++) {
            commandsListModel.addElement(matchedCommands.get(i));
        }

        if (selectedCommandIndex < matchedCommands.size()) {
            listCommands.setSelectedIndex(selectedCommandIndex);
            listCommands.ensureIndexIsVisible(selectedCommandIndex);
        }

        listCommands.setVisibleRowCount(Math.min(MAX_VISIBLE_COMMANDS, matchedCommands.size()));
        pack();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelRoot = new JPanel();
        panelRoot.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        textSearch = new JTextField();
        panelRoot.add(textSearch,
            new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(400, -1),
                new Dimension(400, -1), null, 0, false));
        scrollCommands = new JScrollPane();
        panelRoot.add(scrollCommands,
            new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0,
                false));
        listCommands = new JList();
        listCommands.setVisibleRowCount(1);
        scrollCommands.setViewportView(listCommands);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() { return panelRoot; }
}
