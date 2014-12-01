package dhcoder.tool.collection;

import dhcoder.support.text.StringUtils;

import java.util.Hashtable;
import java.util.Map;

/**
 * Two way hashmap, courtesy of http://stackoverflow.com/a/3430209
 */
public final class BiMap<K, V> {
    private final Map<K,V> keyValue = new Hashtable<K, V>();
    private final Map<V,K> valueKey = new Hashtable<V, K>();

    public void put(final K key, final V value) {
        if (keyValue.containsKey(key)) {
            throw new IllegalArgumentException(StringUtils.format("Attempting to add duplicate key {0}", key));
        }
        if (valueKey.containsKey(value)) {
            throw new IllegalArgumentException(StringUtils.format("Attempting to add duplicate value {0}", value));
        }

        keyValue.put(key, value);
        valueKey.put(value, key);
    }

    public boolean containsKey(final K key) {
        return keyValue.containsKey(key);
    }

    public boolean containsValue(final V value) {
        return valueKey.containsKey(value);
    }

    public V getValue(final K key) {
        return keyValue.get(key);
    }

    public K getKey(final V key) {
        return valueKey.get(key);
    }
}
