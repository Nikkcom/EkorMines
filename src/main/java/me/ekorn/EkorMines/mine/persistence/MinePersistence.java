package me.ekorn.EkorMines.mine.persistence;

import me.ekorn.EkorMines.mine.domain.Mine;

import java.util.Collection;
import java.util.Optional;

public interface MinePersistence {
    Optional<Mine> load(String id);
    Collection<Mine> loadAll();
    void save(Mine mine);
    void delete(String id);
    void reloadAll();
}
