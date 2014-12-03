package dhcoder.tool.swing.widget;

import dhcoder.tool.command.Command;

import javax.swing.*;
import java.awt.*;

/**
 * Renderer for the list displayed in the {@link CommandWindow}
 */
public final class CommandRowRenderer extends JPanel implements ListCellRenderer<Command> {
    private final JLabel commandLabel;
    private final JLabel shortcutLabel;

    public CommandRowRenderer() {
        super(new BorderLayout());

        setOpaque(false);

        commandLabel = new JLabel();
        shortcutLabel = new JLabel();

        Font defaultFont = shortcutLabel.getFont();
        Font commandFont = new Font(defaultFont.getName(), Font.PLAIN, 16);
        Font shortcutFont = new Font(defaultFont.getName(), Font.ITALIC, 10);

        commandLabel.setFont(commandFont);
        shortcutLabel.setFont(shortcutFont);

        add(commandLabel, BorderLayout.CENTER);
        add(shortcutLabel, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(final JList<? extends Command> list, final Command value,
        final int index, final boolean isSelected, final boolean cellHasFocus) {
        commandLabel.setText(value.getFullName());
        if (value.getShortcutOpt().hasValue()) {
            shortcutLabel.setText(value.getShortcutOpt().getValue().toString());
        }

        return this;
    }
}
