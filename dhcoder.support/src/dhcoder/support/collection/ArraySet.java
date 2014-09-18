package dhcoder.support.collection;

/**
 * Like {@link ArrayMap} but where you only care whether a key is present or not and values don't matter.
 */
public final class ArraySet<K> {

    private ArrayMap<K, Object> internalMap;

    public ArraySet() { internalMap = new ArrayMap<K, Object>(); }

    public ArraySet(final int expectedSize) { internalMap = new ArrayMap<K, Object>(expectedSize); }

    /**
     * Create a set with an expected size and load factor. The load factor dictates how full a hashtable should get
     * before it resizes. A load factor of 0.5 means the table should resize when it is 50% full.
     *
     * @throws IllegalArgumentException if the input load factor is not between 0 and 1.
     */
    public ArraySet(final int expectedSize, final float loadFactor) {
        internalMap = new ArrayMap<K, Object>(expectedSize, loadFactor);
    }

    public int getSize() { return internalMap.getSize(); }

    public boolean isEmpty() { return internalMap.isEmpty(); }

    public boolean containsKey(final K key) { return internalMap.containsKey(key); }

    public void put(final K key) { internalMap.put(key, null); }

    public void remove(final K key) { internalMap.remove(key); }
}
