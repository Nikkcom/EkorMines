package me.ekorn.EkorMines.mine.persistence;

import me.ekorn.EkorMines.common.storage.Storage;
import me.ekorn.EkorMines.mine.domain.Mine;

import java.util.Collection;
import java.util.Optional;

public class YamlMinePersistence implements MinePersistence {
    private final Storage<String, Mine> storage;

    public YamlMinePersistence(Storage<String, Mine> storage) {
        this.storage = storage;
        this.storage.reload();
    }

    @Override
    public Optional<Mine> load(String id) {
        return storage.get(id);
    }

    @Override
    public Collection<Mine> loadAll() {
        return storage.getAll();
    }

    @Override
    public void save(Mine mine) {
        storage.save(mine);
    }

    @Override
    public void delete(String id) {
        storage.delete(id);
    }

    @Override
    public void reloadAll() {
        storage.reload();
    }
}
