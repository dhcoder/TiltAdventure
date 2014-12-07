package dhcoder.tool.swing.widget;

import dhcoder.support.text.StringUtils;
import dhcoder.tool.command.Command;

import javax.swing.*;
import java.awt.*;

/**
 * Renderer for the list displayed in the {@link CommandWindow}
 */
public final class CommandRowRenderer extends JPanel implements ListCellRenderer<Command> {
    private static final class AlphaLabel extends JLabel {

        @Override
        protected void paintComponent(final Graphics g) {
//            Composite alphaComposite =
//                AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getForeground().getAlpha() / 255.0f);
//
//            Graphics2D g2d = (Graphics2D)g.create();
//            g2d.setComposite(alphaComposite);
//
            super.paintComponent(g);
        }
    }

    private final AlphaLabel commandLabel;
    private final AlphaLabel shortcutLabel;

    public CommandRowRenderer() {
        super(new BorderLayout());

        setOpaque(false);

        commandLabel = new AlphaLabel();
        shortcutLabel = new AlphaLabel();

        Font defaultFont = shortcutLabel.getFont();
        Font commandFont = new Font(defaultFont.getName(), Font.PLAIN, 16);
        Font shortcutFont = new Font(defaultFont.getName(), Font.ITALIC, 12);

        commandLabel.setFont(commandFont);
        shortcutLabel.setFont(shortcutFont);

        add(commandLabel, BorderLayout.CENTER);
        add(shortcutLabel, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(final JList<? extends Command> listCommands, final Command command,
        final int index, final boolean isSelected, final boolean cellHasFocus) {

        commandLabel.setText("");
        shortcutLabel.setText("");
        commandLabel.setForeground(listCommands.getForeground());
        shortcutLabel.setForeground(listCommands.getForeground());

        setOpaque(false);
        if (isSelected) {
            setOpaque(true);
            setBackground(listCommands.getSelectionBackground());
            commandLabel.setForeground(listCommands.getSelectionForeground());
            shortcutLabel.setForeground(listCommands.getSelectionForeground());
        }

        if (!command.isEnabled()) {
            setOpaque(false);
            Color disabledCommandColor = new Color(0x80000000 & getBackground().getRGB(), true);
            setBackground(disabledCommandColor);
        }

        CommandWindow.CommandRowContext context =
            (CommandWindow.CommandRowContext)listCommands.getClientProperty("context");
        commandLabel.setText(getFormattedCommandName(context.getQuery(), command));
        if (command.getShortcutOpt().hasValue()) {
            shortcutLabel
                .setText(command.getShortcutOpt().getValue().toString() + " "); // Add space to prevent clipping
        }

        return this;
    }

    private String getFormattedCommandName(final String query, final Command command) {
        if (StringUtils.isWhitespace(query)) {
            return command.getFullName();
        }

        String name = command.getFullName();
        // Allocate extra for <html></html> and <b></b>
        int maxLength = name.length() + 13 + 7 * query.length();
        StringBuilder stringBuilder = new StringBuilder(maxLength);
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

        assert stringBuilder.toString().length() <= maxLength;
        return stringBuilder.toString();
    }

}
