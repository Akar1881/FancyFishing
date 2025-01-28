package com.fancyfishing.gui;

import com.fancyfishing.FancyFishing;
import com.fancyfishing.managers.Pool;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PoolsGUI {
    private final FancyFishing plugin;
    private int currentPage;
    private static final int ITEMS_PER_PAGE = 45;

    public PoolsGUI(FancyFishing plugin) {
        this.plugin = plugin;
        this.currentPage = 0;
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "FancyFishing - Pools");

        updatePools(gui);

        // Navigation items
        if (currentPage > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName("§ePrevious Page");
            prevPage.setItemMeta(prevMeta);
            gui.setItem(45, prevPage);
        }

        // Page indicator
        ItemStack pageIndicator = new ItemStack(Material.PAPER);
        ItemMeta pageMeta = pageIndicator.getItemMeta();
        pageMeta.setDisplayName("§fPage: " + (currentPage + 1));
        pageIndicator.setItemMeta(pageMeta);
        gui.setItem(49, pageIndicator);

        if (hasNextPage()) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName("§eNext Page");
            nextPage.setItemMeta(nextMeta);
            gui.setItem(53, nextPage);
        }

        player.openInventory(gui);
    }

    private void updatePools(Inventory gui) {
        List<Pool> pools = new ArrayList<>(plugin.getPoolManager().getPools());
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, pools.size());

        // Clear item slots
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            gui.setItem(i, null);
        }

        // Add pools for current page
        for (int i = startIndex; i < endIndex; i++) {
            Pool pool = pools.get(i);
            ItemStack poolItem = new ItemStack(Material.PAPER);
            ItemMeta meta = poolItem.getItemMeta();
            meta.setDisplayName("§e" + pool.getName());
            meta.setLore(Arrays.asList(
                "§7Catcher Level Required: §e" + pool.getCatcherLevel(),
                "",
                "§eLeft Click §7to manage items",
                "§cRight Click §7to delete pool"
            ));
            poolItem.setItemMeta(meta);
            gui.setItem(i - startIndex, poolItem);
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
        int totalPools = plugin.getPoolManager().getPools().size();
        return (currentPage + 1) * ITEMS_PER_PAGE < totalPools;
    }

    public String getPoolNameFromSlot(int slot) {
        int index = slot + (currentPage * ITEMS_PER_PAGE);
        List<Pool> pools = new ArrayList<>(plugin.getPoolManager().getPools());
        return index < pools.size() ? pools.get(index).getName() : null;
    }
}