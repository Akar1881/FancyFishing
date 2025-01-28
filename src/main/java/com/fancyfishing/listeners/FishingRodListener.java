package com.fancyfishing.listeners;

import com.fancyfishing.FancyFishing;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class FishingRodListener implements Listener {
    private final FancyFishing plugin;

    public FishingRodListener(FancyFishing plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updatePlayerInventory(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        // Schedule a delayed check to ensure item is properly placed in inventory
        new BukkitRunnable() {
            @Override
            public void run() {
                updatePlayerInventory((Player) event.getWhoClicked());
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        if (item.getType() == Material.FISHING_ROD) {
            // Schedule a delayed check to ensure item is properly placed in inventory
            new BukkitRunnable() {
                @Override
                public void run() {
                    updatePlayerInventory(event.getPlayer());
                }
            }.runTaskLater(plugin, 1L);
        }
    }

    private void updatePlayerInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.FISHING_ROD) {
                plugin.getFishingRodManager().updateRodLore(item);
            }
        }
    }
}