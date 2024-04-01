package com.massivecraft.factions.data.helpers;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.data.FactionData;
import com.massivecraft.factions.data.listener.FactionDataListener;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * This class provides helper methods for managing Faction data.
 *
 * @author Driftay
 */
public class FactionDataHelper {

    /**
     * The path where faction data is stored.
     */
    private static final String FACTION_DATA_PATH = "/faction-data/";

    /**
     * The list of all faction data.
     */
    private static final List<FactionData> data = new ArrayList<>();

    /**
     * Gets the list of all faction data.
     *
     * @return the list of all faction data
     */
    public static List<FactionData> getData() {
        return data;
    }

    /**
     * Initializes the faction data helper.
     */
    public static void init() {
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            if (faction.isSystemFaction()) continue;
            FactionData data = new FactionData(faction);
            FactionDataHelper.addFactionData(data);
        }

        FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new FactionDataListener(), FactionsPlugin.getInstance());

        File directory = getFactionDirectory();
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    /**
     * Cleans up any resources used by the faction data helper.
     */
    public static void onDisable() {
        for (FactionData dataItem : data) {
            dataItem.removeSafely();
        }
    }

    /**
     * Gets the file for a specific faction.
     *
     * @param faction the faction
     * @return the file for the faction
     */
    public static File getFactionFile(Faction faction) {
        return new File(FactionsPlugin.getInstance().getDataFolder(), FACTION_DATA_PATH + faction.getId() + ".yml");
    }

    /**
     * Gets the directory for faction data.
     *
     * @return the directory for faction data
     */
    public static File getFactionDirectory() {
        return new File(FactionsPlugin.getInstance().getDataFolder() + FACTION_DATA_PATH);
    }

    /**
     * Creates the configuration file for a specific faction if it does not already exist.
     *
     * @param faction the faction
     */
    public static void createConfiguration(Faction faction) {
        File file = getFactionFile(faction);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a faction data object to the list of all faction data.
     *
     * @param factionData the faction data object
     */
    public static synchronized void addFactionData(FactionData factionData) {
        data.add(factionData);
    }

    /**
     * Removes a faction data object from the list of all faction data.
     *
     * @param factionData the faction data object
     */
    public static synchronized void removeFactionData(FactionData factionData) {
        data.remove(factionData);
    }

    /**
     * Sets a value in the configuration file for a specific faction.
     *
     * @param faction the faction
     * @param key the key of the value to set
     * @param value the value to set
     */
    public static void setConfigValue(Faction faction, String key, Object value) {
        File file = getFactionFile(faction);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(key, value);
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets a default value in the configuration file for a specific faction if it does not already exist.
     *
     * @param faction the faction
     * @param key the key of the value to set
     * @param value the value to set
     */
    public static void setDefaultConfigValue(Faction faction, String key, Object value) {
        FactionData factionData = findFactionData(faction);
        if (factionData!= null) {
            factionData.setDefaultPath(key, value);
            factionData.save(); // Assuming you have an async save method in FactionData
        }
    }

    /**
     * Gets the configuration for a specific faction.
     *
     * @param faction the faction
     * @return the configuration for the faction, or null if the file does not exist
     */
    public static YamlConfiguration getConfiguration(Faction faction) {
        File file = getFactionFile(faction);
        if (!file.exists()) {
            return null;
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Gets a list of all the configuration files for factions.
     *
     * @return a list of all the configuration files for factions
     */
    public static List<File> getAllFactionFiles() {
        File directory = getFactionDirectory();
        File[] files = Objects.requireNonNull(directory.listFiles());
        return new ArrayList<>(Arrays.asList(files));
    }

    /**
     * Removes a specific path from all the configuration files for factions.
     *
     * @param path the path to remove
     * @return the number of files that were modified
     */
    public static int removeDataFromFiles(String path) {
        int count = 0;
        File directory = getFactionDirectory();
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                config.set(path, null);
                config.save(file);
                count++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * Checks if a configuration file exists for a specific faction.
     *
     * @param faction the faction
     * @return true if the configuration file exists, false otherwise
     */
    public static boolean doesConfigurationExist(Faction faction) {
        return getFactionFile(faction).exists();
    }

    /**
     * Finds a faction data object by its faction ID.
     *
     * @param factionID the faction ID
     * @return the faction data object, or null if no match is found
     */
    public static FactionData findFactionData(String factionID) {
        return data.stream()
                .filter(d -> d.getFactionID().equals(factionID))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds a faction data object by its faction.
     *
     * @param faction the faction
     * @return the faction data object, or null if no match is found
     */
    public static FactionData findFactionData(Faction faction) {
        return findFactionData(faction.getId());
    }

    /**
     * Gets the faction ID from a configuration file.
     *
     * @param file the configuration file
     * @return the faction ID
     */
    public static String getFactionIDFromFile(File file) {
        return Factions.getInstance().getFactionById(file.getName().replace(".yml", "")).getId();
    }
}