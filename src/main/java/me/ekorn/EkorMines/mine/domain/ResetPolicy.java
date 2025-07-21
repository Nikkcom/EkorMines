package me.ekorn.EkorMines.mine.domain;

public class ResetPolicy {
    private int resetIntervalSecs;
    private boolean resetOnInterval;

    private double thresholdPct;
    private boolean resetOnThreshold;

    private boolean allowManualReset;
    private int manualResetCooldownSecs;

    private long lastResetTimestamp;

    private long lastManualResetTimestamp;

    public ResetPolicy() {
        this.resetIntervalSecs = 120;
        this.resetOnInterval = false;
        this.thresholdPct = 25.0;
        this.resetOnThreshold = false;
        this.allowManualReset = false;
        this.manualResetCooldownSecs = 0;
        this.lastResetTimestamp = System.currentTimeMillis();
        this.lastManualResetTimestamp = 0L;
    }

    public int getResetIntervalSecs() { return resetIntervalSecs; }
    public void setResetIntervalSecs(int s) { this.resetIntervalSecs = s; }

    public boolean isResetOnInterval() { return resetOnInterval; }
    public void setResetOnInterval(boolean f) { this.resetOnInterval = f; }

    public double getThresholdPct() { return thresholdPct; }
    public void setThresholdPct(double pct) { this.thresholdPct = pct; }

    public boolean isResetOnThreshold() { return resetOnThreshold; }
    public void setResetOnThreshold(boolean f) { this.resetOnThreshold = f; }

    public boolean isAllowManualReset() { return allowManualReset; }
    public void setAllowManualReset(boolean f) { this.allowManualReset = f; }

    public int getManualResetCooldownSecs() { return manualResetCooldownSecs; }
    public void setManualResetCooldownSecs(int s) { this.manualResetCooldownSecs = s; }

    public boolean shouldResetByInterval() {
        if (!resetOnInterval) return false;
        long now = System.currentTimeMillis();
        return now - lastResetTimestamp >= (long)resetIntervalSecs * 1_000L;
    }

    public boolean shouldResetByThreshold(int brokenBlocksCount, int totalBlocks) {
        if (!resetOnThreshold) return false;
        int remainCount = (int)Math.ceil(totalBlocks * thresholdPct);
        int triggerCount = totalBlocks - remainCount;
        return brokenBlocksCount >= triggerCount;
    }

    public boolean canManualReset() {
        if (!allowManualReset) return false;
        long now = System.currentTimeMillis();
        return now - lastManualResetTimestamp
                >= (long)manualResetCooldownSecs * 1_000L;
    }

    public void recordReset() {
        this.lastResetTimestamp = System.currentTimeMillis();
    }

    public void recordManualReset() {
        this.lastManualResetTimestamp = System.currentTimeMillis();
        recordReset();
    }

    @Override
    public String toString() {
        return "ResetPolicy[" +
                "interval=" + resetOnInterval + "@" + resetIntervalSecs + "s, " +
                "threshold=" + resetOnThreshold + "@" + thresholdPct + "%, " +
                "manual=" + allowManualReset + " (cd=" + manualResetCooldownSecs + "s)" +
                "]";
    }
}
