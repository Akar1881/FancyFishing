package com.fancyfishing.listeners;

import com.fancyfishing.FancyFishing;
import com.fancyfishing.managers.FishingItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FishingListener implements Listener {
    private final FancyFishing plugin;
    private final Random random;

    public FishingListener(FancyFishing plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        event.setCancelled(true);
        
        ItemStack rod = event.getPlayer().getInventory().getItemInMainHand();
        int catcherLevel = plugin.getFishingRodManager().getCatcherLevel(rod);
        
        List<FishingItem> possibleItems = new ArrayList<>();
        for (FishingItem item : plugin.getItemManager().getItems().values()) {
            if (item.getCatcherLevel() <= catcherLevel) {
                possibleItems.add(item);
            }
        }

        if (possibleItems.isEmpty()) {
            return;
        }

        // Calculate total weight
        double totalWeight = possibleItems.stream()
                .mapToDouble(FishingItem::getChance)
                .sum();

        // Select random item based on weight
        double randomValue = random.nextDouble() * totalWeight;
        double currentWeight = 0;
        FishingItem selectedItem = null;

        for (FishingItem item : possibleItems) {
            currentWeight += item.getChance();
            if (randomValue <= currentWeight) {
                selectedItem = item;
                break;
            }
        }

        if (selectedItem != null) {
            event.getPlayer().getWorld().dropItemNaturally(
                event.getPlayer().getLocation(),
                selectedItem.getItem()
            );
            
            // Send catch message if set
            String catchMessage = selectedItem.getCatchMessage();
            if (catchMessage != null) {
                event.getPlayer().sendMessage(catchMessage);
            }
        }
    }
}