package com.fancyfishing.managers;

import com.fancyfishing.FancyFishing;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final FancyFishing plugin;
    private FileConfiguration config;
    private final Map<String, String> messages;

    public ConfigManager(FancyFishing plugin) {
        this.plugin = plugin;
        this.messages = new HashMap<>();
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        
        // Load messages
        messages.put("item_added", config.getString("messages.item_added", "Item successfully added to the system!"));
        messages.put("item_removed", config.getString("messages.item_removed", "Item successfully removed from the system!"));
        messages.put("chance_updated", config.getString("messages.chance_updated", "Catch chance updated!"));
        messages.put("catcher_level_updated", config.getString("messages.catcher_level_updated", "Catcher level updated!"));
        messages.put("gui_opened", config.getString("messages.gui_opened", "Opening item management GUI..."));
    }

    public void saveConfig() {
        plugin.saveConfig();
    }

    public int getPages() {
        return config.getInt("pages", 5);
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key, "Message not found: " + key);
    }
}