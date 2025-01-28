package com.fancyfishing.gui;

import com.fancyfishing.FancyFishing;
import com.fancyfishing.managers.FishingItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PublicGUI {
    private final FancyFishing plugin;
    private int currentPage;
    private static final int ITEMS_PER_PAGE = 45;

    public PublicGUI(FancyFishing plugin) {
        this.plugin = plugin;
        this.currentPage = 0;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "FancyFishing - Items");

        // Add items for current page
        updateItems(gui);

        // Add button to add new items
        ItemStack addButton = new ItemStack(Material.EMERALD);
        ItemMeta addMeta = addButton.getItemMeta();
        addMeta.setDisplayName("§aAdd New Item");
        addMeta.setLore(Arrays.asList("§7Click to add a new item", "§7from your inventory"));
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

    public void openAddItemGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "FancyFishing - Add Item");
        
        // Add instructions
        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName("§eHow to Add Items");
        infoMeta.setLore(Arrays.asList(
            "§7Click any item in your inventory",
            "§7to add it to the fishing system",
            "",
            "§7The item will be added with:",
            "§7- Default chance: 100%",
            "§7- Default catcher level: 1"
        ));
        info.setItemMeta(infoMeta);
        gui.setItem(4, info);

        // Back button
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§cBack to Main Menu");
        back.setItemMeta(backMeta);
        gui.setItem(49, back);

        player.openInventory(gui);
    }

    private void updateItems(Inventory gui) {
        List<FishingItem> items = new ArrayList<>(plugin.getItemManager().getItems().values());
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, items.size());

        // Clear item slots
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            gui.setItem(i, null);
        }

        // Add items for current page
        for (int i = startIndex; i < endIndex; i++) {
            FishingItem fishingItem = items.get(i);
            ItemStack displayItem = fishingItem.getItem().clone();
            ItemMeta meta = displayItem.getItemMeta();
            
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.addAll(Arrays.asList(
                "",
                "§7Catch Chance: §e" + fishingItem.getChance() + "%",
                "§7Catcher Level: §e" + fishingItem.getCatcherLevel(),
                "",
                "§eLeft Click §7to edit",
                "§eRight Click §7to remove"
            ));
            meta.setLore(lore);
            displayItem.setItemMeta(meta);
            
            gui.setItem(i - startIndex, displayItem);
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
        int totalItems = plugin.getItemManager().getItems().size();
        return (currentPage + 1) * ITEMS_PER_PAGE < totalItems;
    }

    public UUID getItemIdFromSlot(int slot) {
        int index = slot + (currentPage * 45);
        List<UUID> items = new ArrayList<>(plugin.getItemManager().getItems().keySet());
        return index < items.size() ? items.get(index) : null;
    }
}