package com.fancyfishing.listeners;

import com.fancyfishing.FancyFishing;
import com.fancyfishing.gui.PublicGUI;
import com.fancyfishing.gui.PublicEditItemGUI;
import com.fancyfishing.managers.FishingItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PublicGUIListener implements Listener {
    private final FancyFishing plugin;
    private final PublicGUI publicGUI;
    private final PublicEditItemGUI editItemGUI;

    public PublicGUIListener(FancyFishing plugin) {
        this.plugin = plugin;
        this.publicGUI = new PublicGUI(plugin);
        this.editItemGUI = new PublicEditItemGUI(plugin);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().startsWith("FancyFishing")) {
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

        if (!title.startsWith("FancyFishing")) {
            return;
        }

        // Cancel all clicks in the top inventory
        if (event.getClickedInventory() == event.getView().getTopInventory()) {
            event.setCancelled(true);
        }

        // Handle different GUIs
        if (title.equals("FancyFishing - Items")) {
            handleMainGUI(event, player);
        } else if (title.equals("FancyFishing - Add Item")) {
            handleAddGUI(event, player);
        }
    }

    private void handleMainGUI(InventoryClickEvent event, Player player) {
        if (event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String name = clicked.getItemMeta().getDisplayName();

        if (name.equals("§ePrevious Page")) {
            publicGUI.previousPage(player);
        } else if (name.equals("§eNext Page")) {
            publicGUI.nextPage(player);
        } else if (name.equals("§aAdd New Item")) {
            publicGUI.openAddItemGUI(player);
        } else if (event.getSlot() < 45) { // Item slots
            if (event.isLeftClick()) {
                // Open edit GUI
                UUID itemId = publicGUI.getItemIdFromSlot(event.getSlot());
                if (itemId != null) {
                    plugin.getPublicEditItemGUIListener().setEditingItem(player, itemId);
                    editItemGUI.openGUI(player, itemId);
                }
            } else if (event.isRightClick()) {
                // Remove item
                UUID itemId = publicGUI.getItemIdFromSlot(event.getSlot());
                if (itemId != null) {
                    plugin.getItemManager().removeItem(itemId);
                    player.sendMessage(plugin.getConfigManager().getMessage("item_removed"));
                    publicGUI.openGUI(player);
                }
            }
        }
    }

    private void handleAddGUI(InventoryClickEvent event, Player player) {
        ItemStack clicked = event.getCurrentItem();
        
        // Handle clicking in the bottom inventory (player inventory)
        if (event.getClickedInventory() == event.getView().getBottomInventory()) {
            if (clicked != null) {
                event.setCancelled(true);
                FishingItem fishingItem = new FishingItem(
                    UUID.randomUUID(),
                    clicked.clone(),
                    100.0, // Default chance
                    1      // Default catcher level
                );
                plugin.getItemManager().addItem(fishingItem);
                player.sendMessage(plugin.getConfigManager().getMessage("item_added"));
                publicGUI.openGUI(player);
            }
            return;
        }

        // Handle clicking in the top inventory
        if (event.getClickedInventory() == event.getView().getTopInventory() && clicked != null && clicked.hasItemMeta()) {
            String name = clicked.getItemMeta().getDisplayName();
            if (name.equals("§cBack to Main Menu")) {
                publicGUI.openGUI(player);
            }
        }
    }
}