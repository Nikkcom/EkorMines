package me.ekorn.EkorMines.common;

import java.util.*;

public class ServiceRegistry {

    private final Map<Class<?>, Object> services = new HashMap<>();

    public <T> void register(Class<T> type, T instance) {
        services.put(type, instance);
    }

    public <T> T get(Class<T> type) {
        return type.cast(services.get(type));
    }

    public <T> boolean has(Class<T> type) {
        return services.containsKey(type);
    }

    public void clear() {
        services.clear();
    }

    public Collection<Object> all() {
        return services.values();
    }
}
