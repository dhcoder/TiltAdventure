package dhcoder.support.collection;

/**
 * Like {@link ArrayMap} but where you only care whether a key is present or not and values don't matter.
 */
public final class ArraySet<E> {

    private ArrayMap<E, Object> internalMap;

    public ArraySet() { internalMap = new ArrayMap<E, Object>(); }

    public ArraySet(final int expectedSize) { internalMap = new ArrayMap<E, Object>(expectedSize); }

    /**
     * Create a set with an expected size and load factor. The load factor dictates how full a hashtable should get
     * before it resizes. A load factor of 0.5 means the table should resize when it is 50% full.
     *
     * @throws IllegalArgumentException if the input load factor is not between 0 and 1.
     */
    public ArraySet(final int expectedSize, final float loadFactor) {
        internalMap = new ArrayMap<E, Object>(expectedSize, loadFactor);
    }

    public int getSize() { return internalMap.getSize(); }

    public boolean isEmpty() { return internalMap.isEmpty(); }

    public boolean contains(final E element) { return internalMap.containsKey(element); }

    /**
     * Add the element, which must NOT exist in the set. Use {@link #putIf(Object)} if you don't need this
     * requirement.
     */
    public void put(final E element) { internalMap.put(element, null); }

    /**
     * Add the element, if it's not already in the set.
     */
    public void putIf(final E element) { internalMap.putOrReplace(element, null); }

    /**
     * Remove the element, which MUST exist in the set. Use {@link #removeIf(Object)} if you don't need this
     * requirement. This distinction can be useful to assert cases when you want to guarantee the element is in the set,
     * and it also better mimics the related {@link ArrayMap} class.
     */
    public void remove(final E element) { internalMap.remove(element); }

    /**
     * Remove the element, which may or may not exist in the set. Use {@link #remove(Object)} if you want to assert
     * existence of the element in the set.
     */
    public void removeIf(final E element) { internalMap.removeIf(element); }



    public void clear() { internalMap.clear(); }
}
