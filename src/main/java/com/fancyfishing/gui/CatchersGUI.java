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
import java.util.Map;

public class CatchersGUI {
    private final FancyFishing plugin;
    private int currentPage;
    private static final int ITEMS_PER_PAGE = 45;

    public CatchersGUI(FancyFishing plugin) {
        this.plugin = plugin;
        this.currentPage = 0;
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "FancyFishing - Fishing Rods");

        // Add fishing rods for current page
        updateItems(gui);

        // Add new rod button
        ItemStack addButton = new ItemStack(Material.EMERALD);
        ItemMeta addMeta = addButton.getItemMeta();
        addMeta.setDisplayName("§aAdd New Fishing Rod");
        addMeta.setLore(Arrays.asList(
            "§7Click to add a new fishing rod",
            "§7from your inventory"
        ));
        addButton.setItemMeta(addMeta);
        gui.setItem(49, addButton);

        // Navigation items
        if (currentPage > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName("§ePrevious Page");
            prevPage.setItemMeta(prevMeta);
            gui.setItem(45, prevPage);
        }

        ItemStack pageIndicator = new ItemStack(Material.PAPER);
        ItemMeta pageMeta = pageIndicator.getItemMeta();
        pageMeta.setDisplayName("§fPage: " + (currentPage + 1));
        pageIndicator.setItemMeta(pageMeta);
        gui.setItem(50, pageIndicator);

        if (hasNextPage()) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName("§eNext Page");
            nextPage.setItemMeta(nextMeta);
            gui.setItem(53, nextPage);
        }

        player.openInventory(gui);
    }

    private void updateItems(Inventory gui) {
        Map<String, ItemStack> rods = plugin.getFishingRodManager().getFishingRods();
        List<ItemStack> rodList = new ArrayList<>(rods.values());
        
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, rodList.size());

        // Clear item slots
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            gui.setItem(i, null);
        }

        // Add items for current page
        for (int i = startIndex; i < endIndex; i++) {
            ItemStack rod = rodList.get(i).clone();
            ItemMeta meta = rod.getItemMeta();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            
            // Add edit/remove instructions
            lore.addAll(Arrays.asList(
                "",
                "§eLeft Click §7to edit",
                "§eRight Click §7to remove"
            ));
            meta.setLore(lore);
            rod.setItemMeta(meta);
            
            gui.setItem(i - startIndex, rod);
            plugin.getLogger().info("Added rod to GUI: " + meta.getDisplayName() + " at slot " + (i - startIndex));
        }
    }

    public void nextPage(Player player) {
        if (hasNextPage()) {
            currentPage++;
            openGUI(player);
        }
    }

    public void previousPage(Player player) {
        if (currentPage > 0) {
            currentPage--;
            openGUI(player);
        }
    }

    private boolean hasNextPage() {
        int totalItems = plugin.getFishingRodManager().getFishingRods().size();
        return (currentPage + 1) * ITEMS_PER_PAGE < totalItems;
    }

    public String getRodNameFromSlot(int slot) {
        int index = slot + (currentPage * ITEMS_PER_PAGE);
        List<String> rodNames = new ArrayList<>(plugin.getFishingRodManager().getFishingRods().keySet());
        return index < rodNames.size() ? rodNames.get(index) : null;
    }
}