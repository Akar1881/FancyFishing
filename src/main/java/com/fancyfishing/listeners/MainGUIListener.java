package com.fancyfishing.listeners;

import com.fancyfishing.FancyFishing;
import com.fancyfishing.gui.MainGUI;
import com.fancyfishing.gui.PublicGUI;
import com.fancyfishing.gui.PoolsGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class MainGUIListener implements Listener {
    private final FancyFishing plugin;
    private final MainGUI mainGUI;
    private final PublicGUI publicGUI;
    private final PoolsGUI poolsGUI;

    public MainGUIListener(FancyFishing plugin) {
        this.plugin = plugin;
        this.mainGUI = new MainGUI(plugin);
        this.publicGUI = new PublicGUI(plugin);
        this.poolsGUI = new PoolsGUI(plugin);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals("FancyFishing - Main Menu")) {
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

        if (!title.equals("FancyFishing - Main Menu")) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;

        switch (event.getSlot()) {
            case 11: // Public Fishing
                publicGUI.openGUI(player);
                break;
            case 15: // Pool Fishing
                poolsGUI.openGUI(player);
                break;
        }
    }
}