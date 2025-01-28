package com.fancyfishing.managers;

import com.fancyfishing.FancyFishing;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FishingRodManager {
    private final FancyFishing plugin;
    private final File fishingRodsFolder;

    public FishingRodManager(FancyFishing plugin) {
        this.plugin = plugin;
        this.fishingRodsFolder = new File(plugin.getDataFolder(), "fishing_rods");
        if (!fishingRodsFolder.exists()) {
            fishingRodsFolder.mkdirs();
        }
    }

    public void saveFishingRod(ItemStack rod, int level) {
        if (rod == null || rod.getType() != Material.FISHING_ROD) {
            return;
        }

        String displayName = getFishingRodDisplayName(rod);
        File file = new File(fishingRodsFolder, displayName + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        // Save the original item
        ItemStack clonedRod = rod.clone();
        ItemMeta meta = clonedRod.getItemMeta();
        if (meta.hasLore()) {
            List<String> lore = meta.getLore();
            // Remove existing catcher level lore if exists
            lore.removeIf(line -> line.startsWith("§7Catcher Level: §e") || line.isEmpty());
            meta.setLore(lore);
            clonedRod.setItemMeta(meta);
        }

        config.set("display_name", displayName);
        config.set("original_item", clonedRod);
        config.set("catcher_level", level);

        try {
            config.save(file);
            plugin.getLogger().info("Saved fishing rod: " + displayName);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save fishing rod " + displayName + ": " + e.getMessage());
        }
    }

    public int getCatcherLevel(ItemStack rod) {
        if (rod == null || rod.getType() != Material.FISHING_ROD) {
            return 1;
        }

        String displayName = getFishingRodDisplayName(rod);
        File file = new File(fishingRodsFolder, displayName + ".yml");
        
        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            return config.getInt("catcher_level", 1);
        }

        return 1;
    }

    public void updateRodLore(ItemStack rod) {
        if (rod == null || rod.getType() != Material.FISHING_ROD) {
            return;
        }

        int level = getCatcherLevel(rod);
        ItemMeta meta = rod.getItemMeta();
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

        // Remove existing catcher level lore and empty lines at the end
        while (!lore.isEmpty() && (lore.get(lore.size() - 1).isEmpty() || 
               lore.get(lore.size() - 1).startsWith("§7Catcher Level: §e"))) {
            lore.remove(lore.size() - 1);
        }

        // Add empty line if there are other lores
        if (!lore.isEmpty()) {
            lore.add("");
        }

        // Add catcher level
        lore.add("§7Catcher Level: §e" + level);
        
        meta.setLore(lore);
        rod.setItemMeta(meta);
    }

    private String getFishingRodDisplayName(ItemStack rod) {
        if (rod.hasItemMeta() && rod.getItemMeta().hasDisplayName()) {
            return rod.getItemMeta().getDisplayName()
                .replaceAll("§[0-9a-fk-or]", "") // Remove color codes
                .replaceAll("[^a-zA-Z0-9]", "_") // Replace special chars with underscore
                .toLowerCase();
        }
        return "fishing_rod";
    }

    public void deleteFishingRod(String name) {
        File file = new File(fishingRodsFolder, name + ".yml");
        if (file.exists()) {
            file.delete();
            plugin.getLogger().info("Deleted fishing rod: " + name);
        }
    }

    public List<String> getFishingRodNames() {
        List<String> names = new ArrayList<>();
        File[] files = fishingRodsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String name = file.getName().replace(".yml", "");
                names.add(name);
            }
        }
        return names;
    }

    public ItemStack getOriginalRod(String name) {
        File file = new File(fishingRodsFolder, name + ".yml");
        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            return config.getItemStack("original_item");
        }
        return null;
    }
}