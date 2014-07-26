package dhcoder.support.memory;

import dhcoder.support.lambda.Action1;
import dhcoder.support.lambda.Func;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static dhcoder.support.utils.ListUtils.swapToEndAndRemove;
import static dhcoder.support.utils.StringUtils.format;

/**
 * A class which manages a pool of pre-allocated objects so you can avoid thrashing Android's garbage collector when you
 * want to make lots of small, temporary allocations.
 * <p/>
 * Pools are constructed with two callbacks, one which allocates a new instance of a class, and one which clears an
 * instance of a class for re-use later. After that, just call {@link #grabNew()} and {@link #free(Object)}, and this
 * class will take care of the rest!
 * <p/>
 * Be careful using pools. After you grab something from a pool, you have to remember to release it - and if anyone is
 * still holding on to that reference after you release it, that's an error - they will soon find the reference reset
 * underneath them.
 */
public final class Pool<T> {

    public interface AllocateMethod<T> extends Func<T> {}

    public interface ResetMethod<T> extends Action1<T> {
        @Override
        void run(T item);
    }

    private static int DEFAULT_CAPACITY = 10;
    private final ResetMethod<T> reset;
    private final Stack<T> freeItems;
    private final List<T> usedItems;
    private final int capacity;

    public Pool(final AllocateMethod<T> allocate, final ResetMethod<T> reset) {
        this(allocate, reset, DEFAULT_CAPACITY);
    }

    public Pool(final AllocateMethod<T> allocate, final ResetMethod<T> reset, final int capacity) {

        if (capacity <= 0) {
            throw new IllegalArgumentException(format("Invalid pool capacity: {0}", capacity));
        }

        this.reset = reset;
        this.capacity = capacity;

        freeItems = new Stack<T>();
        freeItems.ensureCapacity(capacity);
        usedItems = new ArrayList<T>(capacity);

        for (int i = 0; i < capacity; i++) {
            freeItems.push(allocate.run());
        }
    }

    public int getCapacity() { return capacity; }

    public int getUsedCount() { return usedItems.size(); }

    public int getRemainingCount() { return freeItems.size(); }

    public T grabNew() {
        if (getRemainingCount() == 0) {
            throw new IllegalStateException(
                format("Requested too many items from this pool (capacity: {0}) - are you forgetting to free some?",
                    capacity));
        }

        T newItem = freeItems.pop();
        usedItems.add(newItem);

        return newItem;
    }

    public void free(final T item) {
        swapToEndAndRemove(usedItems, item);
        reset.run(item);
        freeItems.push(item);
    }
}
