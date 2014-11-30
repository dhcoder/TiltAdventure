package dhcoder.libgdx.tool.swing.widget;

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: HEADER COMMENT HERE.
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
