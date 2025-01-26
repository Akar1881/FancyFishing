package com.fancyfishing.listeners;

import com.fancyfishing.FancyFishing;
import com.fancyfishing.gui.MainGUI;
import com.fancyfishing.gui.EditGUI;
import com.fancyfishing.managers.FishingItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class GUIListener implements Listener {
    private final FancyFishing plugin;
    private final MainGUI mainGUI;
    private final EditGUI editGUI;
    private final EditGUIListener editGUIListener;

    public GUIListener(FancyFishing plugin) {
        this.plugin = plugin;
        this.editGUIListener = new EditGUIListener(plugin);
        this.mainGUI = new MainGUI(plugin);
        this.editGUI = new EditGUI(plugin);
        
        // Register the EditGUIListener
        plugin.getServer().getPluginManager().registerEvents(editGUIListener, plugin);
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
            mainGUI.previousPage(player);
        } else if (name.equals("§eNext Page")) {
            mainGUI.nextPage(player);
        } else if (name.equals("§aAdd New Item")) {
            mainGUI.openAddItemGUI(player);
        } else if (event.getSlot() < 45) { // Item slots
            if (event.isLeftClick()) {
                // Open edit GUI
                UUID itemId = mainGUI.getItemIdFromSlot(event.getSlot());
                if (itemId != null) {
                    editGUI.openGUI(player, itemId);
                    editGUIListener.setEditingItem(player, itemId);
                }
            } else if (event.isRightClick()) {
                // Remove item
                UUID itemId = mainGUI.getItemIdFromSlot(event.getSlot());
                if (itemId != null) {
                    plugin.getItemManager().removeItem(itemId);
                    player.sendMessage(plugin.getConfigManager().getMessage("item_removed"));
                    mainGUI.openGUI(player);
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
                mainGUI.openGUI(player);
            }
            return;
        }

        // Handle clicking in the top inventory
        if (event.getClickedInventory() == event.getView().getTopInventory() && clicked != null && clicked.hasItemMeta()) {
            String name = clicked.getItemMeta().getDisplayName();
            if (name.equals("§cBack to Main Menu")) {
                mainGUI.openGUI(player);
            }
        }
    }
}