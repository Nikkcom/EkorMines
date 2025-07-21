package me.ekorn.EkorMines.mine.persistence.serializer;

import me.ekorn.EkorMines.common.storage.SectionSerializer;
import me.ekorn.EkorMines.mine.domain.Mine;
import me.ekorn.EkorMines.mine.domain.WeightedPicker;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class WeightedPickerSerializer implements SectionSerializer<Mine> {

    @Override
    public String getSectionName() {
        return "picker";
    }

    @Override
    public void write(ConfigurationSection section, Mine value) {
        WeightedPicker<Material> picker = value.getPicker();
        ConfigurationSection weightsSec = section.createSection("weights");
        for (var entry : picker.getWeights().entrySet()) {
            String matName = entry.getKey().name();
            double weight = entry.getValue();
            weightsSec.set(matName, weight);
        }
    }

    @Override
    public void read(ConfigurationSection section, Mine value) {
        ConfigurationSection weightsSec = section.getConfigurationSection("weights");
        if (weightsSec == null) {
            throw new IllegalArgumentException("picker.weights section is missing");
        }

        WeightedPicker<Material> picker = new WeightedPicker<>();
        for (String matName : weightsSec.getKeys(false)) {
            String rawWeight = weightsSec.getString(matName);
            Material mat;
            try {
                mat = Material.valueOf(matName);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid material in picker: " +matName, ex);
            }
            double weight;
            try {
                weight = weightsSec.getDouble(matName);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Invalid weight for " + matName, ex);
            }
            picker.setWeight(mat, weight);
        }
        value.setPicker(picker);
    }
}
