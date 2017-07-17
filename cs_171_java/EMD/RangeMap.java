import java.util.List;

// This is the interface that EMD implements.
// Note that this interface can be used for
// a variety of key-value store implementations
// that support ranged queries.

interface RangeMap<K extends Comparable<K>, V>{
    public static class KVPair<K, V> {
        public K key;
        public V value;

        public KVPair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public String toString() {
            return this.key + ": " + this.value;
        }
    }

    public void add(K key, V value);
    public V get(K key);
    public K next(K key);

    // [start, end]
    public List<KVPair<K, V>> range(K start, K end);

    public void remove(K key);
}
