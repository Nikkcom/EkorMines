package me.ekorn.EkorMines.mine.service;

import org.bukkit.Location;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CreationService {
    public static class Session {
        private final String mineId;
        private Location loc1, loc2;

        public Session(String mineId) {
            this.mineId = mineId;
        }

        public String getMineId() {
            return mineId;
        }
        public Optional<Location> getFirst() {
            return Optional.ofNullable(loc1);
        }
        public Optional<Location> getSecond() {
            return Optional.ofNullable(loc2);
        }
        private void setFirst(Location loc) {
            this.loc1 = loc;
        }
        private void setSecond(Location loc) {
            this.loc2 = loc;
        }
    }

    private final Map<UUID, Session> sessions = new ConcurrentHashMap<>();

    public boolean startSession(UUID player, String mineId) {
        return sessions.putIfAbsent(player, new Session(mineId)) == null;
    }

    public boolean setFirstCorner(UUID player, Location loc) {
        Session s = sessions.get(player);
        if (s == null) return false;
        s.setFirst(loc);
        return true;
    }

    public boolean setSecondCorner(UUID player, Location loc) {
        Session s = sessions.get(player);
        if (s == null) return false;
        s.setSecond(loc);
        return true;
    }

    public Optional<Session> completeSession(UUID player) {
        Session s = sessions.get(player);
        if (s == null) return Optional.empty();
        if (s.getFirst().isEmpty() || s.getSecond().isEmpty()) {
            return Optional.empty();
        }
        sessions.remove(player);
        return Optional.of(s);
    }
    public boolean hasSession(UUID player) {
        return sessions.containsKey(player);
    }

    public void cancelSession(UUID player) {
        sessions.remove(player);
    }
}
