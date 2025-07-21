package me.ekorn.EkorMines.common;

import me.ekorn.EkorMines.common.api.PluginService;

import java.util.logging.Logger;

public class LoggingService implements PluginService {

    private final Logger logger;

    private boolean debugEnabled;


    public LoggingService(Logger logger) {
        this.logger = logger;
        debugEnabled = PluginContext.DEBUG_ENABLED;
    }

    // --- INFO ---
    public void info(String msg) {
        logger.info(msg);
    }
    public void info(String prefix, String msg) {
        logger.info("[" + prefix + "] " + msg);
    }

    // --- WARN ---
    public void warn(String msg) {
        logger.warning(msg);
    }
    public void warn(String prefix, String msg) {
        logger.warning("[" +prefix+ "] " + msg);
    }

    // --- SEVERE ---
    public void severe(String msg) {
        logger.severe(msg);
    }
    public void severe(String prefix, String msg) {
        logger.severe("[" +prefix+ "] " + msg);
    }

    // --- DEBUG ---
    public void debug(String msg) {
        if (debugEnabled) logger.info("[DEBUG] " + msg);
    }



    public boolean isDebugEnabled() {
        return debugEnabled;
    }
    public void setDebugEnabled(boolean enabled) {
        this.debugEnabled = enabled;
    }
    @Override
    public void init(PluginContext ctx) {
        this.debugEnabled = ctx.services().get(ConfigService.class).isDebugMode();
    }
}
