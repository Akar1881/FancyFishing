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

        // Add Lore button
        ItemStack addButton = new ItemStack(Material.EMERALD);
        ItemMeta addMeta = addButton.getItemMeta();
        addMeta.setDisplayName("§aAdd New Lore");
        addMeta.setLore(Arrays.asList(
            "§7Click to add a new lore line",
            "§7Color codes are supported (e.g. &e)"
        ));
        addButton.setItemMeta(addMeta);
        gui.setItem(11, addButton);

        // Remove Last Lore button
        ItemStack removeButton = new ItemStack(Material.REDSTONE);
        ItemMeta removeMeta = removeButton.getItemMeta();
        removeMeta.setDisplayName("§cRemove Last Lore");
        removeMeta.setLore(Arrays.asList(
            "§7Click to remove the last",
            "§7lore line"
        ));
        removeButton.setItemMeta(removeMeta);
        gui.setItem(15, removeButton);

        // Back button
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("§cBack to Edit Menu");
        backButton.setItemMeta(backMeta);
        gui.setItem(22, backButton);

        player.openInventory(gui);
    }

    public void addLore(ItemStack rod, String lore) {
        ItemMeta meta = rod.getItemMeta();
        List<String> lores = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        lores.add(lore.replace('&', '§'));
        meta.setLore(lores);
        rod.setItemMeta(meta);
    }

    public void removeLastLore(ItemStack rod) {
        ItemMeta meta = rod.getItemMeta();
        if (!meta.hasLore()) return;
        
        List<String> lores = meta.getLore();
        if (lores.isEmpty()) return;
        
        lores.remove(lores.size() - 1);
        meta.setLore(lores);
        rod.setItemMeta(meta);
    }
}