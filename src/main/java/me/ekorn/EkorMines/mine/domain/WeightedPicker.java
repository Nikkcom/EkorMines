package me.ekorn.EkorMines.mine.domain;

import java.util.*;

public class WeightedPicker<T> {
    private final Map<T, Double> weights = new LinkedHashMap<>();
    private final NavigableMap<Double, T> prefixSums = new TreeMap<>();
    private double totalWeight = 0.0;
    private boolean dirty = true;
    private final Random rng;

    public WeightedPicker() {
        this(new Random());
    }
    public WeightedPicker(Random rng) {
        this.rng = Objects.requireNonNull(rng, "rng must not be null");
    }

    public void setWeight(T item, double weight) {
        if (weight < 0) throw new IllegalArgumentException("Weight cannot be negative");
        weights.put(Objects.requireNonNull(item, "item must not be null"), weight);
        dirty = true;
    }

    public boolean remove(T item) {
        if (weights.remove(item) != null) {
            dirty = true;
            return true;
        }
        return false;
    }

    public Map<T, Double> getWeights() {
        return Collections.unmodifiableMap(weights);
    }

    public double getTotalWeight() {
        ensureDistribution();
        return totalWeight;
    }

    public T nextRandom() {
        ensureDistribution();
        if (prefixSums.isEmpty()) return null;
        double r = rng.nextDouble() * totalWeight;
        return prefixSums.higherEntry(r).getValue();
    }

    private void ensureDistribution() {
        if (!dirty) return;

        prefixSums.clear();
        totalWeight = 0.0;

        for (Map.Entry<T, Double> entry : weights.entrySet()) {
            double w = entry.getValue();
            if (w <= 0.0) continue;
            totalWeight += w;
            prefixSums.put(totalWeight, entry.getKey());
        }

        dirty = false;
    }
}
