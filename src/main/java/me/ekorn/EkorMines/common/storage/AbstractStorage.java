package me.ekorn.EkorMines.common.storage;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractStorage<K, V> implements Storage<K, V> {
    protected final Map<K, V> cache = new ConcurrentHashMap<>();

    @Override
    public Optional<V> get(K key) {
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public Collection<V> getAll() {
        return Collections.unmodifiableCollection(cache.values());
    }

    @Override
    public void save(V value) {
        K key = extractKey(value);
        cache.put(key, value);
        doSave(value);
    }

    @Override
    public void saveAll(Collection<V> values) {
        for (V value : values) {
            save(value);
        }
    }

    @Override
    public void delete(K key) {
        cache.remove(key);
        doDelete(key);
    }

    @Override
    public void reload() {
        cache.clear();
        Collection<V> loaded = doLoadAll();
        for (V value : loaded) {
            cache.put(extractKey(value), value);
        }
    }

    protected abstract K extractKey(V value);

    protected abstract void doSave(V value);

    protected abstract void doDelete(K key);

    protected abstract Collection<V> doLoadAll();
}
