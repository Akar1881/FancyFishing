package com.fancyfishing.managers;

import com.fancyfishing.FancyFishing;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PoolManager {
    private final FancyFishing plugin;
    private final Map<String, Pool> pools;
    private final File poolsFolder;

    public PoolManager(FancyFishing plugin) {
        this.plugin = plugin;
        this.pools = new HashMap<>();
        this.poolsFolder = new File(plugin.getDataFolder(), "pools");
        if (!poolsFolder.exists()) {
            poolsFolder.mkdirs();
        }
        loadPools();
    }

    public void loadPools() {
        pools.clear();
        File[] files = poolsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            String name = config.getString("name");
            Location pos1 = (Location) config.get("pos1");
            Location pos2 = (Location) config.get("pos2");
            int catcherLevel = config.getInt("catcherLevel", 1);

            Pool pool = new Pool(name, pos1, pos2, catcherLevel);
            
            // Load pool items
            ConfigurationSection itemsSection = config.getConfigurationSection("items");
            if (itemsSection != null) {
                for (String key : itemsSection.getKeys(false)) {
                    ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                    if (itemSection != null) {
                        FishingItem item = loadItem(itemSection);
                        if (item != null) {
                            pool.addItem(item);
                        }
                    }
                }
            }
            
            pools.put(name, pool);
        }
    }

    private FishingItem loadItem(ConfigurationSection section) {
        try {
            UUID id = UUID.fromString(section.getString("id"));
            ItemStack item = section.getItemStack("item");
            double chance = section.getDouble("chance", 100.0);
            int catcherLevel = section.getInt("catcherLevel", 1);
            
            FishingItem fishingItem = new FishingItem(id, item, chance, catcherLevel);
            fishingItem.setCatchMessage(section.getString("catchMessage"));
            return fishingItem;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load pool item: " + e.getMessage());
            return null;
        }
    }

    public void savePool(Pool pool) {
        File file = new File(poolsFolder, pool.getName() + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        
        config.set("name", pool.getName());
        config.set("pos1", pool.getPos1());
        config.set("pos2", pool.getPos2());
        config.set("catcherLevel", pool.getCatcherLevel());

        // Save pool items
        ConfigurationSection itemsSection = config.createSection("items");
        for (Map.Entry<UUID, FishingItem> entry : pool.getItems().entrySet()) {
            FishingItem item = entry.getValue();
            ConfigurationSection itemSection = itemsSection.createSection(entry.getKey().toString());
            itemSection.set("id", item.getId().toString());
            itemSection.set("item", item.getItem());
            itemSection.set("chance", item.getChance());
            itemSection.set("catcherLevel", item.getCatcherLevel());
            itemSection.set("catchMessage", item.getCatchMessage());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save pool " + pool.getName() + ": " + e.getMessage());
        }
    }

    public void deletePool(String name) {
        Pool pool = pools.remove(name);
        if (pool != null) {
            File file = new File(poolsFolder, name + ".yml");
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public Pool getPool(String name) {
        return pools.get(name);
    }

    public List<Pool> getPools() {
        return new ArrayList<>(pools.values());
    }

    public Pool createPool(String name, Location pos1, Location pos2, int catcherLevel) {
        // Check if pool name already exists
        if (pools.containsKey(name)) {
            return null;
        }

        // Check if area contains water or lava
        boolean hasWaterOrLava = false;
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Material type = new Location(pos1.getWorld(), x, y, z).getBlock().getType();
                    if (type == Material.WATER || type == Material.LAVA) {
                        hasWaterOrLava = true;
                        break;
                    }
                }
                if (hasWaterOrLava) break;
            }
            if (hasWaterOrLava) break;
        }

        if (!hasWaterOrLava) {
            return null;
        }

        Pool pool = new Pool(name, pos1, pos2, catcherLevel);
        pools.put(name, pool);
        savePool(pool);
        return pool;
    }

    public Pool getPoolAtLocation(Location location) {
        for (Pool pool : pools.values()) {
            if (pool.isInPool(location)) {
                return pool;
            }
        }
        return null;
    }
}