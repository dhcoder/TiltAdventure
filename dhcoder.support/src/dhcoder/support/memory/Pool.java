package dhcoder.support.memory;

import dhcoder.support.opt.Opt;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static dhcoder.support.collection.ListUtils.swapToEndAndRemove;
import static dhcoder.support.text.StringUtils.format;

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

    public static interface AllocateMethod<T> {
        T run();
    }

    public static interface ResetMethod<T> {
        void run(T item);
    }

    private static final class ReflectionAllocator<T> {
        private static IllegalArgumentException newConstructionException(final Class<?> targetClass) {
            return new IllegalArgumentException(
                format("Class type {0} must have an empty constructor and be instantiable", targetClass));
        }

        private final Class<T> targetClass;
        private final Constructor<T> constructor;

        public ReflectionAllocator(final Class<T> targetClass) {
            this.targetClass = targetClass;
            try {
                constructor = targetClass.getDeclaredConstructor();
                constructor.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw newConstructionException(targetClass);
            }
        }

        public T allocate() {
            try {
                return constructor.newInstance();
            } catch (InstantiationException e) {
                throw newConstructionException(targetClass);
            } catch (IllegalAccessException e) {
                throw newConstructionException(targetClass);
            } catch (InvocationTargetException e) {
                throw newConstructionException(targetClass);
            }
        }
    }

    public static final int DEFAULT_CAPACITY = 10;

    public static <P extends Poolable> Pool<P> of(final Class<P> poolableClass) {
        return of(poolableClass, DEFAULT_CAPACITY);
    }

    public static <P extends Poolable> Pool<P> of(final Class<P> poolableClass, final int capacity) {
        return new Pool<P>(new AllocateMethod() {
            ReflectionAllocator<P> reflectionAllocator = new ReflectionAllocator<P>(poolableClass);

            public Object run() {
                return reflectionAllocator.allocate();
            }
        }, new ResetMethod() {
            @Override
            public void run(final Object item) {
                ((Poolable)item).reset();
            }
        }, capacity);
    }

    private final AllocateMethod<T> allocate;
    private final ResetMethod<T> reset;
    private final Stack<T> freeItems;
    private final ArrayList<T> itemsInUse;
    private final Opt<IntStack> markersOpt = Opt.withNoValue();
    private boolean resizable;
    private int capacity;

    public Pool(final AllocateMethod<T> allocate, final ResetMethod<T> reset) {
        this(allocate, reset, DEFAULT_CAPACITY);
    }

    public Pool(final AllocateMethod<T> allocate, final ResetMethod<T> reset, final int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(format("Invalid pool capacity: {0}", capacity));
        }

        this.allocate = allocate;
        this.reset = reset;
        this.capacity = capacity;

        freeItems = new Stack<T>();
        freeItems.ensureCapacity(capacity);
        itemsInUse = new ArrayList<T>(capacity);

        for (int i = 0; i < capacity; i++) {
            freeItems.push(allocate.run());
        }

        this.resizable = false; // TODO: This should default to true for production builds
    }

    public Pool setResizable(final boolean resizable) {
        this.resizable = resizable;
        return this;
    }

    public int getCapacity() { return capacity; }

    public List<T> getItemsInUse() {
        return itemsInUse;
    }

    public int getRemainingCount() { return freeItems.size(); }

    public T grabNew() {
        if (getRemainingCount() == 0) {

            if (!resizable) {
                throw new IllegalStateException(
                    format("Requested too many items from this pool (capacity: {0}) - are you forgetting to free some?",
                        capacity));
            }

            int oldCapacity = capacity;
            capacity *= 2;
            freeItems.ensureCapacity(capacity);
            itemsInUse.ensureCapacity(capacity);

            for (int i = oldCapacity; i < capacity; i++) {
                freeItems.push(allocate.run());
            }
        }

        T newItem = freeItems.pop();
        itemsInUse.add(newItem);

        return newItem;
    }

    public Pool<T> mark() {
        if (!markersOpt.hasValue()) {
            markersOpt.set(new IntStack(2));
        }

        markersOpt.getValue().push(itemsInUse.size());
        return this;
    }

    public void resetMark() {
        freeCount(itemsInUse.size() - markersOpt.getValue().pop());
    }

    public void freeCount(final int count) {
        int indexToFree = itemsInUse.size() - 1;
        for (int i = 0; i < count; ++i) {
            T item = itemsInUse.get(indexToFree);
            returnItemToPool(item);
            itemsInUse.remove(indexToFree);
            indexToFree--;
        }
    }

    public void freeAll() {
        freeCount(itemsInUse.size());
    }

    public void free(final T item) {
        swapToEndAndRemove(itemsInUse, item);
        returnItemToPool(item);
    }

    private void returnItemToPool(final T item) {
        reset.run(item);
        freeItems.push(item);
    }
}
