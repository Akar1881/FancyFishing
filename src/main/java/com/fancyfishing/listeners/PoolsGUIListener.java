package com.fancyfishing.listeners;

import com.fancyfishing.FancyFishing;
import com.fancyfishing.gui.PoolsGUI;
import com.fancyfishing.gui.PoolsItemsGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class PoolsGUIListener implements Listener {
    private final FancyFishing plugin;
    private final PoolsGUI poolsGUI;

    public PoolsGUIListener(FancyFishing plugin) {
        this.plugin = plugin;
        this.poolsGUI = new PoolsGUI(plugin);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals("FancyFishing - Pools")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (!title.equals("FancyFishing - Pools")) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;

        int slot = event.getSlot();

        if (slot < 45) { // Pool slots
            String poolName = poolsGUI.getPoolNameFromSlot(slot);
            if (poolName != null) {
                if (event.isLeftClick()) {
                    // Open pool items GUI
                    new PoolsItemsGUI(plugin, poolName).openGUI(player);
                } else if (event.isRightClick()) {
                    // Delete pool
                    plugin.getPoolManager().deletePool(poolName);
                    player.sendMessage("§cPool '" + poolName + "' has been deleted.");
                    poolsGUI.openGUI(player);
                }
            }
        } else {
            // Navigation
            switch (slot) {
                case 45: // Previous page
                    poolsGUI.previousPage(player);
                    break;
                case 53: // Next page
                    poolsGUI.nextPage(player);
                    break;
            }
        }
    }
}