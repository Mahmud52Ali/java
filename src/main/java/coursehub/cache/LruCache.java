package coursehub.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/* Access order: успешный get и put делают ключ самым недавним.
   Ключи и значения null запрещены; ёмкость строго положительна. */
public final class LruCache<K, V> {
    private final int capacity;
    private final LinkedHashMap<K, V> entries = new LinkedHashMap<>(16, 0.75f, true);

    public LruCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Ёмкость должна быть положительной");
        }
        this.capacity = capacity;
    }

    public Optional<V> get(K key) {
        Objects.requireNonNull(key, "Ключ не должен быть null");
        return Optional.ofNullable(entries.get(key));
    }

    public void put(K key, V value) {
        Objects.requireNonNull(key, "Ключ не должен быть null");
        Objects.requireNonNull(value, "Значение не должно быть null");
        entries.put(key, value);
        if (entries.size() > capacity) {
            Iterator<K> iterator = entries.keySet().iterator();
            iterator.next();
            iterator.remove();
        }
    }

    public List<K> keysInLruOrder() {
        return List.copyOf(entries.keySet());
    }
}
