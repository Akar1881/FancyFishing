package com.fancyfishing.managers;

import com.fancyfishing.FancyFishing;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemManager {
    private final FancyFishing plugin;
    private final Map<UUID, FishingItem> items;
    private final File itemsFolder;

    public ItemManager(FancyFishing plugin) {
        this.plugin = plugin;
        this.items = new HashMap<>();
        this.itemsFolder = new File(plugin.getDataFolder(), "items");
        if (!itemsFolder.exists()) {
            itemsFolder.mkdirs();
        }
    }

    public void loadItems() {
        items.clear();
        File[] files = itemsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            UUID id = UUID.fromString(config.getString("id"));
            ItemStack item = config.getItemStack("item");
            double chance = config.getDouble("chance", 100.0);
            int catcherLevel = config.getInt("catcherLevel", 1);
            
            FishingItem fishingItem = new FishingItem(id, item, chance, catcherLevel);
            fishingItem.setCatchMessage(config.getString("catchMessage", null));
            items.put(id, fishingItem);
        }
    }

    public void saveItems() {
        // Delete all existing files first to clean up removed items
        File[] files = itemsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }

        // Save all current items
        for (FishingItem item : items.values()) {
            saveItem(item);
        }
    }

    public void addItem(FishingItem item) {
        items.put(item.getId(), item);
        saveItem(item); // Save immediately when item is added
    }

    public void removeItem(UUID id) {
        FishingItem item = items.get(id);
        if (item != null) {
            String fileName = getFileName(item);
            items.remove(id);
            File file = new File(itemsFolder, fileName);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public void updateItem(FishingItem item) {
        items.put(item.getId(), item);
        saveItem(item); // Save immediately when item is updated
    }

    private void saveItem(FishingItem item) {
        File file = new File(itemsFolder, getFileName(item));
        YamlConfiguration config = new YamlConfiguration();
        
        config.set("id", item.getId().toString());
        config.set("item", item.getItem());
        config.set("chance", item.getChance());
        config.set("catcherLevel", item.getCatcherLevel());
        config.set("catchMessage", item.getCatchMessage());
        
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save item " + item.getId() + ": " + e.getMessage());
        }
    }

    private String getFileName(FishingItem item) {
        ItemStack itemStack = item.getItem();
        String displayName = "unknown";
        
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            displayName = itemStack.getItemMeta().getDisplayName()
                .replaceAll("§[0-9a-fk-or]", "") // Remove color codes
                .replaceAll("[^a-zA-Z0-9]", "_") // Replace special chars with underscore
                .toLowerCase();
        } else {
            displayName = itemStack.getType().name().toLowerCase();
        }
        
        return displayName + "_" + item.getId().toString().substring(0, 8) + ".yml";
    }

    public FishingItem getItem(UUID id) {
        return items.get(id);
    }

    public Map<UUID, FishingItem> getItems() {
        return new HashMap<>(items);
    }
}