package me.ekorn.EkorMines.common.storage;

import java.util.Collection;
import java.util.Optional;

public interface Storage<K, V> {
    Optional<V> get (K key);
    Collection<V> getAll();
    void save(V value);
    void delete(K key);
    void reload();
    void saveAll(Collection<V> values);
}
