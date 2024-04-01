package com.massivecraft.factions.data;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for managing the data of a faction.
 * It stores the data in a YAML file on the disk.
 */
public class FactionData {

    private static final String FACTION_DATA_PATH = "/faction-data/";

    private final String factionID;
    private final String factionTag;
    private Map<String, Object> map;
    private boolean saving;

    /**
     * Creates a new instance of the FactionData class.
     *
     * @param faction The faction for which the data is being managed.
     */
    public FactionData(Faction faction) {
        this.factionTag = faction.getTag();
        this.factionID = faction.getId();
        this.map = new HashMap<>();
        this.saving = false;
        this.load();
    }

    /**
     * Generates the path for the faction file.
     *
     * @return The path as a string.
     */
    private String getFactionFilePath() {
        return FactionsPlugin.getInstance().getDataFolder() + FACTION_DATA_PATH + factionID + ".yml";
    }

    /**
     * Adds a new key-value pair to the stored data.
     *
     * @param key   The key of the data to be stored.
     * @param value The value of the data to be stored.
     */
    public void addStoredValue(String key, Object value) {
        if (key!= null && value!= null) {
            if (!this.map.containsKey(key)) {
                this.map.put(key, value);
            }
        } else {
            // Handle null key or value, you may choose to log a warning or throw an exception
            System.out.println("Warning: Attempted to add null key or value.");
        }
    }

    /**
     * Checks if the data is being saved.
     *
     * @return true if the data is being saved, false otherwise.
     */
    public boolean isSaving() {
        return this.saving;
    }

    /**
     * Gets the YAML configuration for the faction data.
     *
     * @return The YAML configuration.
     */
    public YamlConfiguration getConfiguration() {
        return YamlConfiguration.loadConfiguration(new File(getFactionFilePath()));
    }

    /**
     * Gets the value of a stored data with a specific key.
     *
     * @param key The key of the data to be retrieved.
     * @return The value of the data, or null if the data does not exist.
     */
    public Object getStoredValue(String key) {
        return this.map.get(key);
    }

    /**
     * Checks if a specific key exists in the stored data.
     *
     * @param key The key to be checked.
     * @return true if the key exists, false otherwise.
     */
    public boolean hasStoredValue(String key) {
        return this.map.containsKey(key);
    }

    /**
     * Checks if a specific key exists in the YAML configuration.
     *
     * @param key The key to be checked.
     * @return true if the key exists, false otherwise.
     */
    public boolean hasConfigValue(String key) {
        return this.getConfiguration().contains(key);
    }

    /**
     * Checks if a specific key exists in either the stored data or the YAML configuration.
     *
     * @param key The key to be checked.
     * @return true if the key exists, false otherwise.
     */
    public boolean hasValue(String key) {
        boolean value = hasConfigValue(key) || hasStoredValue(key);
        if (!value) {
            setDefaultPath(key, null);
        }
        return value;
    }

    /**
     * Sets a default value for a specific key, if it does not exist in either the stored data or the YAML configuration.
     *
     * @param path  The key of the data to be set.
     * @param value The default value to be set.
     */
    public void setDefaultPath(String path, Object value) {
        this.map.put(path, value);
    }

    /**
     * Gets all the key-value pairs in the stored data.
     *
     * @return A map of all the key-value pairs in the stored data.
     */
    public Map<String, Object> getStoredValues() {
        return this.map;
    }

    /**
     * Sets all the key-value pairs in the stored data.
     *
     * @param map A map of the key-value pairs to be set.
     */
    public void setStoredValues(Map<String, Object> map) {
        this.map = map;
    }

    /**
     * Gets the value of a data with a specific key, falling back to a default value if the data does not exist.
     *
     * @param key         The key of the data to be retrieved.
     * @param defaultValue The default value to be returned if the data does not exist.
     * @return The value of the data, or the default value if the data does not exist.
     */
    public Object getValue(String key, Object defaultValue) {
        return this.map.computeIfAbsent(key, k -> {
            Object value = getConfiguration().get(k);
            return (value!= null)? value : defaultValue;
        });
    }

    /**
     * Deletes the faction data file for a specific faction.
     *
     * @param faction The faction for which the data file is to be deleted.
     */
    public void deleteFactionData(Faction faction) {
        File file = new File(getFactionFilePath());

        if (file.delete()) {
            Logger.print("Deleting faction-data for faction " + faction.getTag(), Logger.PrefixType.DEFAULT);
        } else {
            Logger.print("Failed to delete faction-data for faction " + faction.getTag(), Logger.PrefixType.WARNING);
        }
    }

    /**
     * Saves the data to the disk.
     */
    public void save() {
        if (this.isSaving()) {
            return;
        }
        this.saving = true;
        File file = new File(getFactionFilePath());
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        Bukkit.getLogger().info("[FactionData] Saving " + this.factionTag + "'s Data to the disk");

        map.forEach(configuration::set);

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.saving = false;
        }
    }

    /**
     * Loads the data from the disk.
     */
    public void load() {
        File file = new File(getFactionFilePath());
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        // Load values from the configuration into the map
        for (String key : configuration.getKeys(false)) {
            map.put(key, configuration.get(key));
        }
    }

    /**
     * Gets the ID of the faction.
     *
     * @return The ID of the faction.
     */
    public String getFactionID() {
        return factionID;
    }

    /**
     * Gets the tag of the faction.
     *
     * @return The tag of the faction.
     */
    public String getFactionTag() {
        return factionTag;
    }

    /**
     * Removes the data from memory and the disk.
     */
    public void removeSafely() {
        this.save();
    }

    @Override
    public String toString() {
        return "FactionData{" +
                "factionID='" + factionID + '\'' +
                ", factionTag='" + factionTag + '\'' +
                ", map=" + map +
                ", saving=" + saving +
                '}';
    }
}