package me.ekorn.EkorMines.mine.service;

import me.ekorn.EkorMines.mine.domain.Mine;

import java.util.function.Consumer;

public interface ResetScheduler {

    void scheduleInterval(Mine mine);
    void cancelInterval(String mineId);
    void performReset(Mine mine, Consumer<Mine> onComplete);
    boolean isResetting(String mineId);
}
