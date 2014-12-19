package dhcoder.tool.javafx.fxutils;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 * Helper font utility methods.
 */
public final class ListUtils {
    public static void forceRefresh(final ListView listView) {
        ObservableList items = listView.getItems();
        listView.setItems(null);
        listView.setItems(items);
    }

    private ListUtils() {}
}
