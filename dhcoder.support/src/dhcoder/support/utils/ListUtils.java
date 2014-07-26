package dhcoder.support.utils;

import java.util.List;

public final class ListUtils {

    /**
     * Take the current item and swap it to the end of the list before removing it. This is a useful operation
     * because removing something from the end of a list is often a much more lightweight operation than removing from the middle.
     *
     * @throws IllegalArgumentException if the specified item is not in the list.
     */
    public static <T> void swapToEndAndRemove(final List<T> list, final T item) {
        int itemIndex = list.indexOf(item);
        if (itemIndex < 0) {
            throw new IllegalArgumentException("Trying to remove an item that's not in the list");
        }

        // Swap item to the end before removing (since removing from the end avoids shifting elements)
        int lastIndex = list.size() - 1;
        if (lastIndex > 0 && itemIndex != lastIndex) {
            T temp = list.get(lastIndex);
            list.set(lastIndex, list.get(itemIndex));
            list.set(itemIndex, temp);
        }

        list.remove(lastIndex);
    }
}
