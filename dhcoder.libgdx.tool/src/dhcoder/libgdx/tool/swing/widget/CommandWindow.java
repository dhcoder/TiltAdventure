package dhcoder.libgdx.tool.swing.widget;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import dhcoder.libgdx.tool.command.Command;
import dhcoder.libgdx.tool.command.CommandManager;
import dhcoder.support.text.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A widget which can search through all actions registered with the current tool.
 */
public final class CommandWindow extends JPanel {
    public static final int MAX_COMMAND_COUNT = 30;

    private JTextField textSearch;
    private JList listCommands;
    private JScrollPane scrollCommands;
    private JPanel panelRoot;

    private final List<Command> allCommandsSorted;
    private List<Command> matchedCommands;
    private int selectedCommandIndex;

    public CommandWindow(final CommandManager commandManager) {
        super();

        allCommandsSorted = commandManager.searchableCommands();
        Collections.sort(allCommandsSorted, new Comparator<Command>() {
            @Override
            public int compare(final Command o1, final Command o2) {
                return o1.getFullName().compareTo(o2.getFullName());
            }
        });

        //            final Label commandLabel =
//                new Label(getFormattedCommandName(command), skin, i == selectedCommandIndex ? "bold" : "default");
//            commandLabel.setEllipse(true);
//            final Color defaultColor = commandLabel.getColor().cpy();
//            commandLabel.setColor(defaultColor);
//            commandLabel.addListener(new ClickListener() {
//                @Override
//                public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
//                    final int button) {
//                    hide();
//                    command.run();
//                    return true;
//                }
//
//                @Override
//                public void enter(final InputEvent event, final float x, final float y, final int pointer,
//                    final Actor fromActor) {
//                    commandLabel.setColor(Color.YELLOW);
//                }
//
//                @Override
//                public void exit(final InputEvent event, final float x, final float y, final int pointer,
//                    final Actor toActor) {
//                    commandLabel.setColor(defaultColor);
//                }
//            });
//            commandsTable.add(commandLabel).expandX().fillX().pad(0f, 10f, 0f, 10f);
//
//            if (command.getShortcutOpt().hasValue()) {
//                Label shortcutLabel = new Label(command.getShortcutOpt().getValue().toString(), skin, "italic-xs");
//                commandsTable.add(shortcutLabel).pad(0f, 0f, 0f, 10f).right();
//            }

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
        listCommands.removeAll();

        DefaultListModel<Command> commandsListModel = new DefaultListModel<Command>();
        int commandCount = Math.min(MAX_COMMAND_COUNT, matchedCommands.size());
        for (int i = 0; i < commandCount; i++) {
            commandsListModel.addElement(matchedCommands.get(i));
        }

        if (commandCount > 0) {
//            scrollCommands.scroll
//            // Ensure the current command is visible
//            float rowHeight = commandsTable.getHeight() / commandCount;
//            float ensureVisibleLowerY = rowHeight * selectedCommandIndex;
//            float ensureVisibleUpperY = ensureVisibleLowerY + rowHeight;
//
//            if (commandsPane.getScrollY() > ensureVisibleLowerY) {
//                // Scroll UP to the active command
//                commandsPane.setScrollY(ensureVisibleLowerY);
//            }
//            else if (commandsPane.getScrollY() + commandsPane.getScrollHeight() < ensureVisibleLowerY) {
//                // Scroll DOWN to the active command
//                commandsPane.setScrollY(ensureVisibleUpperY - commandsPane.getScrollHeight());
//            }
//            commandsPane.setVisible(true);
        }
    }

    private String getFormattedCommandName(final Command command) {
        String query = textSearch.getText();
        if (StringUtils.isWhitespace(query)) {
            return command.getFullName();
        }

        String name = command.getFullName();
        // Allocate extra for <html></html> and <b></b>
        StringBuilder stringBuilder = new StringBuilder(name.length() + 13 + 7 * query.length());
        stringBuilder.append("<html>");

        boolean inBoldSection = false;
        int queryIndex = 0;
        for (int nameIndex = 0; nameIndex < name.length(); nameIndex++) {
            char nameChar = name.charAt(nameIndex);

            if (queryIndex < query.length()) {
                char queryChar = query.charAt(queryIndex);

                if (Character.toLowerCase(nameChar) == Character.toLowerCase(queryChar)) {
                    if (!inBoldSection) {
                        if (queryChar != ' ') {
                            stringBuilder.append("<b>");
                            inBoldSection = true;
                        }
                    }
                    else {
                        if (queryChar == ' ') {
                            stringBuilder.append("</b>");
                            inBoldSection = false;
                        }
                    }
                    queryIndex++;
                }
                else {
                    if (inBoldSection) {
                        stringBuilder.append("</b>");
                        inBoldSection = false;
                    }
                }
            }
            else {
                if (inBoldSection) {
                    stringBuilder.append("</b>");
                    inBoldSection = false;
                }
            }
            stringBuilder.append(nameChar);
        }

        if (inBoldSection) {
            stringBuilder.append("</b>");
        }

        stringBuilder.append("</html>");

        return stringBuilder.toString();
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
                GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1),
                null, 0, false));
        scrollCommands = new JScrollPane();
        panelRoot.add(scrollCommands,
            new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0,
                false));
        listCommands = new JList();
        scrollCommands.setViewportView(listCommands);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() { return panelRoot; }
}