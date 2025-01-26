package com.fancyfishing.managers;

import com.fancyfishing.FancyFishing;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FishingRodManager {
    private final FancyFishing plugin;
    private final File rodsFolder;
    private final Map<String, ItemStack> fishingRods;

    public FishingRodManager(FancyFishing plugin) {
        this.plugin = plugin;
        this.rodsFolder = new File(plugin.getDataFolder(), "fishing_rods");
        this.fishingRods = new HashMap<>();
        
        if (!rodsFolder.exists()) {
            rodsFolder.mkdirs();
        }
    }

    public void loadRods() {
        fishingRods.clear();
        File[] files = rodsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            ItemStack rod = config.getItemStack("rod");
            if (rod != null) {
                // Load and set the catcher level from config
                int catcherLevel = config.getInt("catcher-level", 1);
                setRodLevel(rod, catcherLevel);
                
                fishingRods.put(rod.getItemMeta().getDisplayName(), rod);
                plugin.getLogger().info("Loaded fishing rod: " + rod.getItemMeta().getDisplayName());
            }
        }
    }

    public void saveRod(ItemStack rod) {
        if (rod == null || !rod.hasItemMeta()) return;
        
        String displayName = rod.getItemMeta().getDisplayName();
        File file = new File(rodsFolder, sanitizeFileName(displayName) + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        
        // Save the rod and its catcher level
        config.set("rod", rod);
        config.set("catcher-level", getRodLevel(rod));
        
        try {
            config.save(file);
            fishingRods.put(displayName, rod.clone()); // Add to memory cache
            plugin.getLogger().info("Saved fishing rod: " + displayName);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save fishing rod: " + displayName);
        }
    }

    public void removeRod(String displayName) {
        fishingRods.remove(displayName);
        File file = new File(rodsFolder, sanitizeFileName(displayName) + ".yml");
        if (file.exists()) {
            file.delete();
        }
    }

    private String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
    }

    public Map<String, ItemStack> getFishingRods() {
        return new HashMap<>(fishingRods);
    }

    public ItemStack getRod(String displayName) {
        return fishingRods.get(displayName);
    }

    public int getRodLevel(ItemStack rod) {
        if (rod == null || !rod.hasItemMeta()) {
            return 1;
        }

        ItemMeta meta = rod.getItemMeta();
        if (!meta.hasLore()) {
            return 1;
        }

        List<String> lore = meta.getLore();
        for (String line : lore) {
            if (line.startsWith("§8Catcher Level:")) {
                try {
                    return Integer.parseInt(line.substring(16).trim());
                } catch (NumberFormatException e) {
                    return 1;
                }
            }
        }
        
        return 1;
    }

    public void setRodLevel(ItemStack rod, int level) {
        if (rod == null || !rod.hasItemMeta()) return;
        
        ItemMeta meta = rod.getItemMeta();
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        List<String> newLore = new ArrayList<>();
        
        // Remove old catcher level if it exists
        lore.removeIf(line -> line.startsWith("§8Catcher Level:"));
        
        // Add catcher level at the beginning
        newLore.add("§8Catcher Level: " + level);
        
        // Add enchantment descriptions if they exist
        List<String> enchantLore = new ArrayList<>();
        for (String line : lore) {
            if (line.startsWith("§7") && (
                line.contains("Luck of the Sea") ||
                line.contains("Lure") ||
                line.contains("Unbreaking")
            )) {
                enchantLore.add(line);
            }
        }
        newLore.addAll(enchantLore);
        
        // Add remaining lore lines that aren't enchantment descriptions
        for (String line : lore) {
            if (!enchantLore.contains(line)) {
                newLore.add(line);
            }
        }
        
        meta.setLore(newLore);
        rod.setItemMeta(meta);
    }

    public boolean isCustomEnchanted(ItemStack rod) {
        if (rod == null || !rod.hasItemMeta()) return false;
        
        // Check if the rod has enchantments but none of them are our standard fishing enchantments
        if (!rod.getEnchantments().isEmpty()) {
            boolean hasOnlyStandardEnchants = rod.getEnchantments().keySet().stream()
                .allMatch(enchant -> 
                    enchant.equals(Enchantment.LUCK) ||
                    enchant.equals(Enchantment.LURE) ||
                    enchant.equals(Enchantment.DURABILITY)
                );
            return !hasOnlyStandardEnchants;
        }
        return false;
    }
}