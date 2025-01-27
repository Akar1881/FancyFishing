package com.fancyfishing.gui;

import com.fancyfishing.FancyFishing;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManageLoreGUI {
    private final FancyFishing plugin;

    public ManageLoreGUI(FancyFishing plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player, String rodName) {
        Inventory gui = Bukkit.createInventory(null, 27, "Manage Lores - " + rodName);

        ItemStack rod = plugin.getFishingRodManager().getRod(rodName);
        if (rod == null) {
            player.closeInventory();
            return;
        }

        // Display current rod with lores
        gui.setItem(4, rod);

        ItemMeta rodMeta = rod.getItemMeta();
        List<String> currentLores = rodMeta.hasLore() ? rodMeta.getLore() : new ArrayList<>();

        // Add lore slots (0-14)
        for (int i = 0; i <= 14; i++) {
            ItemStack loreItem = new ItemStack(Material.PAPER);
            ItemMeta loreMeta = loreItem.getItemMeta();
            
            if (i == 1) {
                // Catcher level lore - cannot be edited or removed
                loreMeta.setDisplayName("§eLore: Catcher Level");
                loreMeta.setLore(Arrays.asList(
                    "§7This lore shows the catcher level",
                    "§cCannot be edited or removed"
                ));
            } else if (i == 14) {
                // Rarity lore - can be edited but not removed
                loreMeta.setDisplayName("§eLore: Rarity");
                List<String> rarityLore = new ArrayList<>();
                rarityLore.add("§7Current: " + getCurrentRarity(currentLores));
                rarityLore.add("");
                rarityLore.add("§eLeft Click §7to edit");
                rarityLore.add("§cCannot be removed");
                loreMeta.setLore(rarityLore);
            } else {
                // Regular lore slots
                String currentLore = getLoreAtSlot(currentLores, i);
                loreMeta.setDisplayName("§eLore: " + i);
                List<String> lore = new ArrayList<>();
                lore.add("§7Current: " + (currentLore != null ? currentLore : "Empty"));
                lore.add("");
                lore.add("§eLeft Click §7to edit");
                lore.add("§eRight Click §7to remove");
                loreMeta.setLore(lore);
            }
            
            loreItem.setItemMeta(loreMeta);
            gui.setItem(i, loreItem);
        }

        // Add new lore button
        ItemStack addButton = new ItemStack(Material.EMERALD);
        ItemMeta addMeta = addButton.getItemMeta();
        addMeta.setDisplayName("§aAdd New Lore");
        addMeta.setLore(Arrays.asList(
            "§7Click to add a new lore line",
            "§7Color codes are supported (e.g. &e)"
        ));
        addButton.setItemMeta(addMeta);
        gui.setItem(22, addButton);

        // Back button
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("§cBack to Edit Menu");
        backButton.setItemMeta(backMeta);
        gui.setItem(26, backButton);

        player.openInventory(gui);
    }

    private String getCurrentRarity(List<String> lores) {
        for (String lore : lores) {
            if (lore.contains("Rarity:")) {
                return lore;
            }
        }
        return "§7Rarity: §7COMMON";
    }

    private String getLoreAtSlot(List<String> lores, int slot) {
        for (String lore : lores) {
            if (lore.contains("<" + slot + ">")) {
                return lore;
            }
        }
        return null;
    }
}