package me.ekorn.EkorMines.common.message.service;

import me.ekorn.EkorMines.common.ConfigService;

public class LocaleManager {
    private final ConfigService configService;

    public LocaleManager(ConfigService configService) {
        this.configService = configService;
    }

    public String getCurrentLocale() {
        return configService.getLocale();
    }
}
