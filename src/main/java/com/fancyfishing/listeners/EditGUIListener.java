package com.fancyfishing.listeners;

import com.fancyfishing.FancyFishing;
import com.fancyfishing.gui.MainGUI;
import com.fancyfishing.gui.EditGUI;
import com.fancyfishing.managers.FishingItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditGUIListener implements Listener {
    private final FancyFishing plugin;
    private final Map<UUID, UUID> editingItems;
    private final Map<UUID, String> awaitingInput;
    private final MainGUI mainGUI;
    private final EditGUI editGUI;

    public EditGUIListener(FancyFishing plugin) {
        this.plugin = plugin;
        this.editingItems = new HashMap<>();
        this.awaitingInput = new HashMap<>();
        this.mainGUI = new MainGUI(plugin);
        this.editGUI = new EditGUI(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (!title.equals("FancyFishing - Edit Item")) {
            return;
        }

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) {
            return;
        }

        UUID itemId = editingItems.get(player.getUniqueId());
        if (itemId == null) {
            return;
        }

        FishingItem item = plugin.getItemManager().getItem(itemId);
        if (item == null) {
            return;
        }

        int slot = event.getSlot();

        switch (slot) {
            case 11: // Chance modifier
                awaitingInput.put(player.getUniqueId(), "chance");
                player.closeInventory();
                player.sendMessage("§e=== Catch Chance Settings ===");
                player.sendMessage("§7Please type a number between §e0.1% §7and §e100%");
                player.sendMessage("§7Current chance: §e" + item.getChance() + "%");
                player.sendMessage("§7Example: §e50 §7for 50% chance, §e0.5 §7for 0.5% chance");
                break;

            case 15: // Level modifier
                awaitingInput.put(player.getUniqueId(), "level");
                player.closeInventory();
                player.sendMessage("§e=== Catcher Level Settings ===");
                player.sendMessage("§7Please type a number between §e1 §7and §e100");
                player.sendMessage("§7Current level: §e" + item.getCatcherLevel());
                player.sendMessage("§7This is the minimum fishing rod level required to catch this item");
                break;

            case 26: // Back button
                editingItems.remove(player.getUniqueId());
                player.closeInventory();
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    mainGUI.openGUI(player);
                }, 1L);
                break;
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String inputType = awaitingInput.get(player.getUniqueId());
        
        if (inputType == null) {
            return;
        }
        
        event.setCancelled(true);
        UUID itemId = editingItems.get(player.getUniqueId());
        if (itemId == null) {
            awaitingInput.remove(player.getUniqueId());
            return;
        }

        FishingItem item = plugin.getItemManager().getItem(itemId);
        if (item == null) {
            player.sendMessage("§cError: Item not found!");
            awaitingInput.remove(player.getUniqueId());
            editingItems.remove(player.getUniqueId());
            return;
        }

        String input = event.getMessage().trim();

        try {
            switch (inputType) {
                case "chance":
                    double chance = Double.parseDouble(input);
                    if (chance < 0.1 || chance > 100) {
                        player.sendMessage("§cChance must be between 0.1 and 100!");
                        player.sendMessage("§7Example: §e50 §7for 50% chance, §e0.5 §7for 0.5% chance");
                        return;
                    }
                    item.setChance(chance);
                    plugin.getItemManager().updateItem(item);
                    player.sendMessage("§aSuccessfully set catch chance to §e" + chance + "%");
                    break;

                case "level":
                    int level = Integer.parseInt(input);
                    if (level < 1 || level > 100) {
                        player.sendMessage("§cLevel must be between 1 and 100!");
                        return;
                    }
                    item.setCatcherLevel(level);
                    plugin.getItemManager().updateItem(item);
                    player.sendMessage("§aSuccessfully set minimum catcher level to §e" + level);
                    break;
            }

            awaitingInput.remove(player.getUniqueId());
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                editGUI.openGUI(player, itemId);
            }, 1L);

        } catch (NumberFormatException e) {
            player.sendMessage("§cPlease enter a valid number!");
            if (inputType.equals("chance")) {
                player.sendMessage("§7Example: §e50 §7for 50% chance, §e0.5 §7for 0.5% chance");
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            if (!awaitingInput.containsKey(player.getUniqueId())) {
                editingItems.remove(player.getUniqueId());
            }
        }
    }

    public void setEditingItem(Player player, UUID itemId) {
        editingItems.put(player.getUniqueId(), itemId);
    }
}