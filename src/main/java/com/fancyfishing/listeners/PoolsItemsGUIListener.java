package com.fancyfishing.listeners;

import com.fancyfishing.FancyFishing;
import com.fancyfishing.gui.EditItemGUI;
import com.fancyfishing.gui.PoolsGUI;
import com.fancyfishing.gui.PoolsItemsGUI;
import com.fancyfishing.managers.FishingItem;
import com.fancyfishing.managers.Pool;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PoolsItemsGUIListener implements Listener {
    private final FancyFishing plugin;
    private final EditItemGUI editItemGUI;
    private final EditItemGUIListener editItemGUIListener;
    private final Map<UUID, String> addingItemToPool; // Store pool name for players adding items

    public PoolsItemsGUIListener(FancyFishing plugin) {
        this.plugin = plugin;
        this.editItemGUI = new EditItemGUI(plugin);
        this.editItemGUIListener = new EditItemGUIListener(plugin);
        this.addingItemToPool = new HashMap<>();
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        if (title.startsWith("Pool Items - ")) {
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

        if (!title.startsWith("Pool Items - ")) {
            return;
        }

        // Handle add item GUI
        if (title.equals("Pool Items - Add Item")) {
            handleAddItemGUI(event, player);
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        String poolName = title.substring("Pool Items - ".length());
        Pool pool = plugin.getPoolManager().getPool(poolName);
        if (pool == null) {
            player.closeInventory();
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;

        PoolsItemsGUI gui = new PoolsItemsGUI(plugin, poolName);
        int slot = event.getSlot();

        if (slot < 45) { // Item slots
            UUID itemId = gui.getItemIdFromSlot(slot);
            if (itemId != null) {
                if (event.isLeftClick()) {
                    // Open edit GUI
                    editItemGUI.openGUI(player, itemId, poolName);
                    editItemGUIListener.setEditingItem(player, itemId, poolName);
                } else if (event.isRightClick()) {
                    // Remove item
                    pool.removeItem(itemId);
                    plugin.getPoolManager().savePool(pool);
                    player.sendMessage("§cItem removed from pool '" + poolName + "'");
                    gui.openGUI(player);
                }
            }
        } else {
            switch (slot) {
                case 45: // Previous page
                    gui.previousPage(player);
                    break;
                case 49: // Add new item
                    addingItemToPool.put(player.getUniqueId(), poolName);
                    PoolsItemsGUI.openAddItemGUI(player, poolName);
                    break;
                case 53: // Next page
                    gui.nextPage(player);
                    break;
            }
        }
    }

    private void handleAddItemGUI(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        String poolName = addingItemToPool.get(player.getUniqueId());
        if (poolName == null) {
            player.closeInventory();
            return;
        }

        Pool pool = plugin.getPoolManager().getPool(poolName);
        if (pool == null) {
            player.closeInventory();
            addingItemToPool.remove(player.getUniqueId());
            return;
        }
        
        // Handle clicking in the bottom inventory (player inventory)
        if (event.getClickedInventory() == event.getView().getBottomInventory()) {
            ItemStack clicked = event.getCurrentItem();
            if (clicked != null) {
                FishingItem fishingItem = new FishingItem(
                    UUID.randomUUID(),
                    clicked.clone(),
                    100.0, // Default chance
                    1      // Default catcher level
                );
                pool.addItem(fishingItem);
                plugin.getPoolManager().savePool(pool);
                player.sendMessage(plugin.getConfigManager().getMessage("item_added"));
                addingItemToPool.remove(player.getUniqueId());
                new PoolsItemsGUI(plugin, poolName).openGUI(player);
            }
            return;
        }

        // Handle clicking in the top inventory
        if (event.getClickedInventory() == event.getView().getTopInventory()) {
            ItemStack clicked = event.getCurrentItem();
            if (clicked != null && clicked.hasItemMeta()) {
                String name = clicked.getItemMeta().getDisplayName();
                if (name.equals("§cBack")) {
                    addingItemToPool.remove(player.getUniqueId());
                    new PoolsItemsGUI(plugin, poolName).openGUI(player);
                }
            }
        }
    }
}