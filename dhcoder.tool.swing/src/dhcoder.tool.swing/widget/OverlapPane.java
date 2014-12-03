package dhcoder.tool.swing.widget;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

/**
 * An overlap pane is basically a {@link JLayeredPane} which automatically sizes all children to fit. This is a pretty
 * simple way to have multiple panels which all share the same space, and which you can toggle various layers on or off
 * at will.
 * <p/>
 * This class lets you emulate having a {@link CardLayout} in a way that's a lot more intuitive (just toggle panel
 * visibilities instead of naming things with arbitrary card names).
 */
public final class OverlapPane extends JLayeredPane implements ComponentListener {
    List<JComponent> components = new ArrayList<JComponent>();

    public OverlapPane() {
        super();
        setLayout(null);
        addComponentListener(this);
    }

    public void addComponent(final JComponent component) {
        components.add(component);
        component.setBounds(0, 0, getWidth(), getHeight());
        add(component, components.size());
    }

    @Override
    public void componentResized(final ComponentEvent e) {
        for (JComponent component : components) {
            component.setBounds(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    public void componentMoved(final ComponentEvent e) {}

    @Override
    public void componentShown(final ComponentEvent e) {}

    @Override
    public void componentHidden(final ComponentEvent e) {}

}
