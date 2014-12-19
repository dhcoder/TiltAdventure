package dhcoder.tool.javafx.fxutils;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Helper font utility methods.
 */
public final class FontUtils {
    public static Font cloneBold(final Font font) {
        return Font.font(font.getFamily(), FontWeight.BOLD, font.getSize());
    }
    private FontUtils() {}

}
