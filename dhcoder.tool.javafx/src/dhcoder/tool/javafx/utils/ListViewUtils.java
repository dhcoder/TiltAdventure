package dhcoder.tool.javafx.utils;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 * Helper ListView utility methods.
 */
public final class ListViewUtils {
    private static final double ROW_HEIGHT = 24;
    private static final double LIST_BORDER = 2;

    public static void forceRefresh(final ListView listView) {
        ObservableList items = listView.getItems();
        listView.setItems(null);
        listView.setItems(items);
    }

    /**
     * Set the target list so it sizes itself to its contents (and continues to do so).
     *
     * You must do this after setting the ListView to its contents list.
     */
    public static void sizeToContents(final ListView listView) {
        listView.setPrefHeight(listView.getItems().size() * ROW_HEIGHT + LIST_BORDER);
        listView.getItems().addListener((ListChangeListener.Change c) -> listView
            .setPrefHeight(listView.getItems().size() * ROW_HEIGHT + LIST_BORDER));
    }

    private ListViewUtils() {}
}
