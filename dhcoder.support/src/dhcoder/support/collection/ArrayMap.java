package dhcoder.support.collection;

import dhcoder.support.memory.Pool;
import dhcoder.support.opt.OptInt;

import java.util.ArrayList;

import static dhcoder.support.math.MathUtils.log2;
import static dhcoder.support.text.StringUtils.format;

/**
 * A map implementation that uses {@link ArrayList}s under the hood, allowing it to preallocate all memory ahead of
 * time (unless the collection needs to grow.
 */
public final class ArrayMap<K, V> {

    private enum IndexMethod {
        GET,
        PUT,
    }

    // See: http://planetmath.org/goodhashtableprimes
    private static final int[] PRIME_TABLE_SIZES = new int[]{
        3, // 2^0 -- Ex: If requested size is 0-1, actually use 3.
        3, // 2^1 -- Ex: If requested size is 1-2, actually use 3.
        7, // 2^2 -- Ex: If requested size is 3-4, actually use 7.
        13, // 2^3 -- Ex: If requested size is 5-8, actually use 13. etc...
        23, // 2^4
        53, // 2^5
        97, // 2^6
        193, // 2^7
        389, // 2^8
        769, // 2^9
        1543, // 2^10
        3079, // 2^11
        6151, // 2^12
        12289, // 2^13
        24593, // 2^14
        49157, // 2^15
        98317, // 2^16
        196613, // 2^17
        393241, // 2^18
        786433, // 2^19
        1572869, // 2^20
        3145739, // 2^21
        6291469, // 2^22
        12582917, // 2^23
        25165843, // 2^24
        50331653, // 2^25
        100663319, // 2^26
        201326611, // 2^27
        402653189, // 2^28
        805306457, // 2^29
        1610612741, // 2^30
    };
    private static final int DEFAULT_EXPECTED_SIZE = 10;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private static int getNextPrimeSize(final int requestedSize) {
        int log2 = log2(requestedSize);
        if (log2 >= PRIME_TABLE_SIZES.length) {
            throw new IllegalStateException("Table can't grow big enough to accomodate requested size");
        }

        return PRIME_TABLE_SIZES[log2];
    }

    private final Pool<OptInt> indexPool = Pool.of(OptInt.class, 1);
    private final float loadFactor;
    private int size;
    private int resizeAtSize;
    private int capacity;
    // We use a probing algorithm to find a key's index, jumping over spots that are already taken to find a free
    // bucket. But if a key/value is removed, that leaves a hold that future searches for our key may fall in to. We
    // mark the spot so that, even if a key was removed, we know it used to be there.
    private boolean[] keyIsDead;
    private ArrayList<K> keys;
    private ArrayList<V> values;

    public ArrayMap() {
        this(DEFAULT_EXPECTED_SIZE, DEFAULT_LOAD_FACTOR);
    }

    public ArrayMap(final int expectedSize) {
        this(expectedSize, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Create a map with an expected size and load factor. The load factor dictates how full a hashtable should get
     * before it resizes. A load factor of 0.5 means the table should resize when it is 50% full.
     *
     * @throws IllegalArgumentException if the input load factor is not between 0 and 1.
     */
    public ArrayMap(final int expectedSize, final float loadFactor) {
        if (loadFactor <= 0.0f || loadFactor >= 1.0f) {
            throw new IllegalArgumentException(format("Load factor must be between 0 and 1. Got {0}", loadFactor));
        }

        this.loadFactor = loadFactor;

        capacity = getNextPrimeSize(expectedSize);

        // Ensure initial capacity to be at least large enough so we don't resize unless user puts more keys in than
        // they said they would. Note: adding 0.5f rounds up so we don't accidentally truncate our needed capacity by 1
        int enoughInitialCapacity = (int)(expectedSize / loadFactor + 0.5f);
        while (capacity < enoughInitialCapacity) {
            capacity = getNextPrimeSize(capacity + 1);
        }

        initializeStructures();
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(final K key) {
        OptInt indexOpt = indexPool.grabNew();
        getIndex(key, IndexMethod.GET, indexOpt);
        boolean containsKey = indexOpt.hasValue();
        indexPool.free(indexOpt);

        return containsKey;
    }

    public boolean containsValue(final V value) {
        return (values.contains(value));
    }

    public V get(final K key) {
        OptInt indexOpt = indexPool.grabNew();
        getIndex(key, IndexMethod.GET, indexOpt);
        if (!indexOpt.hasValue()) {
            indexPool.free(indexOpt);
            throw new IllegalArgumentException(format("No value associated with key {0}", key));
        }
        V value = values.get(indexOpt.getValue());
        indexPool.free(indexOpt);

        return value;
    }

    public void put(final K key, final V value) {
        OptInt indexOpt = indexPool.grabNew();
        getIndex(key, IndexMethod.PUT, indexOpt);
        int index = indexOpt.getValue();
        indexPool.free(indexOpt);

        keys.set(index, key);
        values.set(index, value);
        keyIsDead[index] = false;

        size++;

        if (size == resizeAtSize) {
            increaseCapacity();
        }
    }

    public V remove(final K key) {
        OptInt indexOpt = indexPool.grabNew();
        getIndex(key, IndexMethod.GET, indexOpt);
        if (!indexOpt.hasValue()) {
            indexPool.free(indexOpt);
            throw new IllegalArgumentException(format("No value associated with key {0}", key));
        }
        int index = indexOpt.getValue();
        indexPool.free(indexOpt);

        keyIsDead[index] = true;
        V value = values.get(index);
        values.set(index, null);
        keys.set(index, null);

        size--;

        return value;
    }

    private void increaseCapacity() {
        int oldCapacity = capacity;
        capacity = getNextPrimeSize(capacity + 1);

        ArrayList<K> keysCopy = keys;
        ArrayList<V> valuesCopy = values;

        initializeStructures();

        for (int i = 0; i < oldCapacity; ++i) {
            if (keysCopy.get(i) != null) {
                K key = keysCopy.get(i);
                V value = valuesCopy.get(i);

                put(key, value);
            }
        }
    }

    private void initializeStructures() {
        keyIsDead = new boolean[capacity];
        keys = new ArrayList<K>(capacity);
        values = new ArrayList<V>(capacity);

        size = 0;
        for (int i = 0; i < capacity; ++i) {
            keys.add(null);
            values.add(null);
        }

        resizeAtSize = (int)(capacity * loadFactor);
    }

    // Returns index of the key in this hashtable. If 'forGetMethods' is true, 'outIndex' won't have any value set if
    // the key couldn't be found.
    private void getIndex(final K key, final IndexMethod indexMethod, final OptInt outIndex) {
        outIndex.clear();

        int positiveHashCode = key.hashCode() & 0x7FFFFFFF;
        int initialIndex = positiveHashCode % capacity;
        int index = initialIndex;
        int quadraticOffset = 1;
        while (keys.get(index) != null || keyIsDead[index]) {
            if (indexMethod == IndexMethod.PUT && keyIsDead[index]) {
                outIndex.set(index); // This used to be a bucket for a key that got removed. Take it!
                return;
            }

            if (key.equals(keys.get(index))) {
                outIndex.set(index);
                return;
            }

            index = (initialIndex + quadraticOffset * quadraticOffset) % capacity;
            quadraticOffset++;
        }

        if (indexMethod == IndexMethod.PUT) {
            outIndex.set(index);
        }
    }
}
