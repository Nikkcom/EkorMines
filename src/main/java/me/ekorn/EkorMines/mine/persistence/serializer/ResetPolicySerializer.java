package me.ekorn.EkorMines.mine.persistence.serializer;

import me.ekorn.EkorMines.common.storage.SectionSerializer;
import me.ekorn.EkorMines.mine.domain.Mine;
import me.ekorn.EkorMines.mine.domain.ResetPolicy;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class ResetPolicySerializer implements SectionSerializer<Mine> {
    @Override
    public String getSectionName() {
        return "reset-policy";
    }

    @Override
    public void write(ConfigurationSection section, Mine value) {
        ResetPolicy p = value.getPolicy();
        section.set("intervalEnabled", p.isResetOnInterval());
        section.set("intervalSeconds", p.getResetIntervalSecs());
        section.set("thresholdEnabled", p.isResetOnThreshold());
        section.set("thresholdPercent", p.getThresholdPct());
        section.set("manualEnabled", p.isAllowManualReset());
        section.set("manualCooldownSecs", p.getManualResetCooldownSecs());
    }

    @Override
    public void read(ConfigurationSection section, Mine value) {
        for (String key : List.of(
                "intervalEnabled",
                "intervalSeconds",
                "thresholdEnabled",
                "thresholdPercent",
                "manualEnabled",
                "manualCooldownSecs"
        )) {
            if (!section.contains(key)) {
                throw new IllegalArgumentException("reset-policy." +key+ " is missing");
            }
        }

        boolean ie = section.getBoolean("intervalEnabled");
        int secs = section.getInt("intervalSeconds");
        boolean te = section.getBoolean("thresholdEnabled");
        double pct = section.getDouble("thresholdPercent");
        boolean me = section.getBoolean("manualEnabled");
        int mcd = section.getInt("manualCooldownSecs");

        ResetPolicy policy = new ResetPolicy();
        policy.setResetOnInterval(ie);
        policy.setResetIntervalSecs(secs);
        policy.setResetOnThreshold(te);
        policy.setThresholdPct(pct);
        policy.setAllowManualReset(me);
        policy.setManualResetCooldownSecs(mcd);

        value.setPolicy(policy);
    }
}
