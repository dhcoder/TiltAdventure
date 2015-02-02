package dhcoder.tool.javafx.utils;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

/**
 * Miscellaneous helper functions which extend the various *Pane JavaFX classes.
 */
public final class PaneUtils {
    public static void setAnchors(final Node n, final double top, final double right, final double bottom,
        final double left) {
        AnchorPane.setTopAnchor(n, top);
        AnchorPane.setRightAnchor(n, right);
        AnchorPane.setBottomAnchor(n, bottom);
        AnchorPane.setLeftAnchor(n, left);
    }

    public static void setAnchors(final Node n, final double topBottom, final double leftRight) {
        setAnchors(n, topBottom, leftRight, topBottom, leftRight);
    }

    public static void setAnchors(final Node n, final double value) {
        setAnchors(n, value, value, value, value);
    }
}
