package com.fancyfishing.gui;

import com.fancyfishing.FancyFishing;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class FREditGUI {
    private final FancyFishing plugin;
    private final ManageLoreGUI manageLoreGUI;

    public FREditGUI(FancyFishing plugin) {
        this.plugin = plugin;
        this.manageLoreGUI = new ManageLoreGUI(plugin);
    }

    public void openGUI(Player player, String rodName) {
        Inventory gui = Bukkit.createInventory(null, 36, "Editing " + rodName);

        ItemStack rod = plugin.getFishingRodManager().getRod(rodName);
        if (rod == null) {
            player.closeInventory();
            return;
        }

        // Display current fishing rod
        gui.setItem(4, rod);

        // Display Name modifier
        ItemStack nameItem = new ItemStack(Material.PAPER);
        ItemMeta nameMeta = nameItem.getItemMeta();
        nameMeta.setDisplayName("§eDisplay Name");
        nameMeta.setLore(Arrays.asList(
            "§7Current name: " + rodName,
            "§7This is the fishing rod's display name",
            "",
            "§eClick §7to change"
        ));
        nameItem.setItemMeta(nameMeta);
        gui.setItem(11, nameItem);

        // Catcher Level modifier
        ItemStack levelItem = new ItemStack(Material.FLINT);
        ItemMeta levelMeta = levelItem.getItemMeta();
        levelMeta.setDisplayName("§eCatcher Level");
        int currentLevel = plugin.getFishingRodManager().getRodLevel(rod);
        levelMeta.setLore(Arrays.asList(
            "§7Current level: §f" + currentLevel,
            "§7This is the fishing rod's catcher level",
            "",
            "§eClick §7to change"
        ));
        levelItem.setItemMeta(levelMeta);
        gui.setItem(13, levelItem);

        // Enchantments manager
        ItemStack enchantItem = new ItemStack(Material.ENCHANTING_TABLE);
        ItemMeta enchantMeta = enchantItem.getItemMeta();
        enchantMeta.setDisplayName("§eEnchantments");
        
        if (plugin.getFishingRodManager().isCustomEnchanted(rod)) {
            enchantMeta.setLore(Arrays.asList(
                "§cThis rod has custom enchantments",
                "§cYou can only modify the name",
                "§cand catcher level"
            ));
        } else {
            enchantMeta.setLore(Arrays.asList(
                "§7Manage fishing rod enchantments",
                "",
                "§eClick §7to open enchantment manager"
            ));
        }
        
        enchantItem.setItemMeta(enchantMeta);
        gui.setItem(15, enchantItem);

        // Manage Lores button
        ItemStack loreItem = new ItemStack(Material.PAPER);
        ItemMeta loreMeta = loreItem.getItemMeta();
        loreMeta.setDisplayName("§eManage Lores");
        loreMeta.setLore(Arrays.asList(
            "§7Click to open lores GUI",
            "§7to manage lores"
        ));
        loreItem.setItemMeta(loreMeta);
        gui.setItem(32, loreItem);

        // Get a copy button
        ItemStack copyButton = new ItemStack(Material.EMERALD);
        ItemMeta copyMeta = copyButton.getItemMeta();
        copyMeta.setDisplayName("§aGet a Copy");
        copyMeta.setLore(Arrays.asList(
            "§7Click to get a copy of",
            "§7this fishing rod"
        ));
        copyButton.setItemMeta(copyMeta);
        gui.setItem(30, copyButton);

        // Back button
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("§cBack to Catchers Menu");
        backButton.setItemMeta(backMeta);
        gui.setItem(31, backButton);

        player.openInventory(gui);
    }
}