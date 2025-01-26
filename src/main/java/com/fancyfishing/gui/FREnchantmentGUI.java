package com.fancyfishing.gui;

import com.fancyfishing.FancyFishing;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FREnchantmentGUI {
    private final FancyFishing plugin;

    public FREnchantmentGUI(FancyFishing plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player, String rodName) {
        ItemStack rod = plugin.getFishingRodManager().getRod(rodName);
        if (rod == null) {
            player.sendMessage("§cError: Fishing rod not found!");
            return;
        }

        Inventory gui = Bukkit.createInventory(null, 27, "Enchantments - " + rodName);

        // Luck of the Sea
        ItemStack luckBook = createEnchantmentBook(
            "Luck of the Sea",
            "Increases your chances of catching valuable items.",
            3,
            rod.getEnchantmentLevel(Enchantment.LUCK)
        );
        gui.setItem(11, luckBook);

        // Lure
        ItemStack lureBook = createEnchantmentBook(
            "Lure",
            "Decreases the time for fish to take the bait.",
            3,
            rod.getEnchantmentLevel(Enchantment.LURE)
        );
        gui.setItem(13, lureBook);

        // Unbreaking
        ItemStack unbreakingBook = createEnchantmentBook(
            "Unbreaking",
            "Increases the durability of your fishing rod.",
            3,
            rod.getEnchantmentLevel(Enchantment.DURABILITY)
        );
        gui.setItem(15, unbreakingBook);

        // Back button
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("§cBack to Edit Menu");
        backButton.setItemMeta(backMeta);
        gui.setItem(26, backButton);

        // Current rod display
        ItemStack displayRod = rod.clone();
        ItemMeta displayMeta = displayRod.getItemMeta();
        List<String> displayLore = new ArrayList<>();
        displayLore.add("§7Current Enchantments:");
        
        if (rod.getEnchantmentLevel(Enchantment.LUCK) > 0) {
            displayLore.add("§7- Luck of the Sea " + rod.getEnchantmentLevel(Enchantment.LUCK));
        }
        if (rod.getEnchantmentLevel(Enchantment.LURE) > 0) {
            displayLore.add("§7- Lure " + rod.getEnchantmentLevel(Enchantment.LURE));
        }
        if (rod.getEnchantmentLevel(Enchantment.DURABILITY) > 0) {
            displayLore.add("§7- Unbreaking " + rod.getEnchantmentLevel(Enchantment.DURABILITY));
        }
        
        if (displayLore.size() == 1) {
            displayLore.add("§7None");
        }
        
        displayMeta.setLore(displayLore);
        displayRod.setItemMeta(displayMeta);
        gui.setItem(4, displayRod);

        player.openInventory(gui);
    }

    private ItemStack createEnchantmentBook(String name, String description, int maxLevel, int currentLevel) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.setDisplayName("§e" + name);

        List<String> lore = new ArrayList<>();
        lore.add("§7" + description);
        lore.add("");
        lore.add("§7Current Level: §e" + currentLevel);
        lore.add("");
        lore.add("§eRight Click §7to increase level");
        lore.add("§eLeft Click §7to decrease level");
        lore.add("");
        lore.add("§7Maximum Level: §e" + maxLevel);
        
        if (currentLevel == 0) {
            lore.add("§cEnchantment not applied");
        } else if (currentLevel == maxLevel) {
            lore.add("§6Maximum level reached!");
        }
        
        meta.setLore(lore);

        // Add enchantment glow if the enchantment is active
        if (currentLevel > 0) {
            switch (name) {
                case "Luck of the Sea":
                    meta.addStoredEnchant(Enchantment.LUCK, currentLevel, true);
                    break;
                case "Lure":
                    meta.addStoredEnchant(Enchantment.LURE, currentLevel, true);
                    break;
                case "Unbreaking":
                    meta.addStoredEnchant(Enchantment.DURABILITY, currentLevel, true);
                    break;
            }
        }

        book.setItemMeta(meta);
        return book;
    }

    public void updateEnchantmentLevel(ItemStack rod, Enchantment enchant, int newLevel, int maxLevel) {
        if (newLevel < 0) {
            newLevel = 0;
        } else if (newLevel > maxLevel) {
            newLevel = maxLevel;
        }

        if (newLevel == 0) {
            rod.removeEnchantment(enchant);
        } else {
            rod.addUnsafeEnchantment(enchant, newLevel);
        }
    }

    public Enchantment getEnchantmentFromName(String name) {
        switch (name) {
            case "Luck of the Sea":
                return Enchantment.LUCK;
            case "Lure":
                return Enchantment.LURE;
            case "Unbreaking":
                return Enchantment.DURABILITY;
            default:
                return null;
        }
    }

    public int getMaxLevel(Enchantment enchant) {
        return 3; // All enchantments now have max level 3
    }
}