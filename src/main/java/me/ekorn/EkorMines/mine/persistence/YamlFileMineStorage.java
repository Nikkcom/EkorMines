package me.ekorn.EkorMines.mine.persistence;

import me.ekorn.EkorMines.common.storage.SectionSerializer;
import me.ekorn.EkorMines.mine.domain.Mine;
import me.ekorn.EkorMines.common.storage.AbstractStorage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YamlFileMineStorage extends AbstractStorage<String, Mine> {
    private final File dir;
    private final List<SectionSerializer<Mine>> serializers;


    public YamlFileMineStorage(File dir, List<SectionSerializer<Mine>> serializers) {
        this.dir = dir;
        this.serializers = serializers;
        if (!dir.exists()) dir.mkdirs();
        reload();
    }

    @Override
    protected String extractKey(Mine mine) {
        return mine.getId().toLowerCase();
    }

    @Override
    public void reload() {
        cache.clear();

        File[] files = dir.listFiles(f -> f.isFile() && f.getName().endsWith(".yml"));
        if (files == null) return;


        for (File f : files) {
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            String id = cfg.getString("id");
            if (id == null) {
                Bukkit.getLogger().warning("Skipping "+f.getName()+": missing 'id' field");
                continue;
            }

            try {
                Mine mine = new Mine(id);

                for (SectionSerializer<Mine> ser : serializers) {
                    ConfigurationSection sec = cfg.getConfigurationSection(ser.getSectionName());
                    if (sec == null) {
                        throw new IllegalArgumentException(
                                "Missing section '" +ser.getSectionName()+ "' in " +f.getName());
                    }
                    ser.read(sec, mine);
                }

                cache.put(id, mine);
            } catch (Exception ex) {
                Bukkit.getLogger().severe("Failed loading mine '"+id+"' from "+f.getName()+": " + ex.getMessage());
            }
        }
    }

    @Override
    protected void doSave(Mine mine) {
        File f = new File(dir, extractKey(mine) + ".yml");
        YamlConfiguration yamlConfig = new YamlConfiguration();

        yamlConfig.set("id", mine.getId());

        for (SectionSerializer<Mine> ser : serializers) {
            ConfigurationSection sect = yamlConfig.createSection(ser.getSectionName());
            ser.write(sect, mine);
        }
        try {
            yamlConfig.save(f);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed saving mine " + mine.getId());
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(String key) {
        cache.remove(key);
        new File(dir, key + ".yml").delete();
    }

    @Override
    protected Collection<Mine> doLoadAll() {
        reload();
        return getAll();
    }
}
