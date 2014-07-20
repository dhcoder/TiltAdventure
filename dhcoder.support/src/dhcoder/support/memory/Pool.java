package dhcoder.support.memory;

import dhcoder.support.lambda.Action1;
import dhcoder.support.lambda.Func;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static dhcoder.support.utils.StringUtils.format;

/**
 * A class which manages a pool of pre-allocated objects so that you
 */
public class Pool<T> {

    public interface AllocateMethod<T> extends Func<T> {}

    public interface ResetMethod<T> extends Action1<T> {
        @Override
        void run(T item);
    }

    private static int DEFAULT_CAPACITY = 10;
    private final AllocateMethod<T> allocate;
    private final ResetMethod<T> reset;
    private final Stack<T> freeItems;
    private final List<T> usedItems;
    private final int capacity;

    public Pool(final AllocateMethod<T> allocate, final ResetMethod<T> reset) {
        this(allocate, reset, DEFAULT_CAPACITY);
    }

    public Pool(final AllocateMethod<T> allocate, final ResetMethod<T> reset, final int capacity) {
        this.allocate = allocate;
        this.reset = reset;
        this.capacity = capacity;
        freeItems = new Stack<T>();
        freeItems.ensureCapacity(capacity);
        usedItems = new ArrayList<T>(capacity);

    }

    public T grabNew() {
        T newItem;
        if (freeItems.size() > 0) {
            newItem = freeItems.pop();
        }
        else {
            newItem = allocate.run();
        }

        usedItems.add(newItem);

        if (usedItems.size() + freeItems.size() > capacity) {
            throw new IllegalStateException(
                format("Requested too many items from this pool (capacity: {0}) - are you forgetting to free some?",
                    capacity));
        }

        return newItem;
    }

    public void free(final T item) {
        int itemIndex = usedItems.indexOf(item);
        if (itemIndex < 0) {
            throw new IllegalArgumentException(format("Trying to remove item {0} that's not in the pool", item));
        }

        usedItems.remove(itemIndex);
        reset.run(item);
        freeItems.push(item);
    }

}
