package com.fancyfishing.listeners;

import com.fancyfishing.FancyFishing;
import com.fancyfishing.gui.CatchersGUI;
import com.fancyfishing.gui.FREditGUI;
import com.fancyfishing.gui.FREnchantmentGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FRGUIListener implements Listener {
    private final FancyFishing plugin;
    private final Map<UUID, String> editingRods;
    private final Map<UUID, String> awaitingInput;
    private final CatchersGUI catchersGUI;
    private final FREditGUI editGUI;
    private final FREnchantmentGUI enchantmentGUI;
    
    public FRGUIListener(FancyFishing plugin) {
        this.plugin = plugin;
        this.editingRods = new HashMap<>();
        this.awaitingInput = new HashMap<>();
        this.catchersGUI = new CatchersGUI(plugin);
        this.editGUI = new FREditGUI(plugin);
        this.enchantmentGUI = new FREnchantmentGUI(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        if (title.equals("FancyFishing - Fishing Rods")) {
            event.setCancelled(true);
            handleMainGUI(event, player);
        } else if (title.equals("FancyFishing - Add Rod")) {
            event.setCancelled(true);
            handleAddRodGUI(event, player);
        } else if (title.startsWith("Editing ")) {
            event.setCancelled(true);
            handleEditGUI(event, player);
        } else if (title.startsWith("Enchantments - ")) {
            event.setCancelled(true);
            handleEnchantmentGUI(event, player);
        }
    }

    private void handleMainGUI(InventoryClickEvent event, Player player) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;

        if (clicked.getType() == Material.EMERALD) {
            openAddRodGUI(player);
            return;
        }

        if (clicked.getType() == Material.ARROW) {
            if (clicked.getItemMeta().getDisplayName().equals("§ePrevious Page")) {
                catchersGUI.previousPage(player);
            } else if (clicked.getItemMeta().getDisplayName().equals("§eNext Page")) {
                catchersGUI.nextPage(player);
            }
            return;
        }

        if (event.getSlot() < 45 && clicked.getType() == Material.FISHING_ROD) {
            String rodName = catchersGUI.getRodNameFromSlot(event.getSlot());
            if (rodName == null) return;

            if (event.isLeftClick()) {
                editingRods.put(player.getUniqueId(), rodName);
                editGUI.openGUI(player, rodName);
            } else if (event.isRightClick()) {
                plugin.getFishingRodManager().removeRod(rodName);
                player.sendMessage("§aFishing rod removed successfully!");
                catchersGUI.openGUI(player);
            }
        }
    }

    private void openAddRodGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "FancyFishing - Add Rod");
        
        // Instructions paper
        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName("§eHow to Add Fishing Rods");
        infoMeta.setLore(Arrays.asList(
            "§7Click any fishing rod in your inventory",
            "§7to add it to the system",
            "",
            "§7The rod will be added with:",
            "§7- Default catcher level: 1",
            "§7- No enchantments",
            "",
            "§cNote: Only fishing rods can be added!"
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

    private void handleAddRodGUI(InventoryClickEvent event, Player player) {
        if (event.getClickedInventory() == event.getView().getTopInventory()) {
            ItemStack clicked = event.getCurrentItem();
            if (clicked != null && clicked.getType() == Material.BARRIER) {
                catchersGUI.openGUI(player);
            }
            return;
        }

        // Handle clicking in player inventory
        ItemStack clicked = event.getCurrentItem();
        if (clicked != null) {
            if (clicked.getType() != Material.FISHING_ROD) {
                player.sendMessage("§cYou can only add fishing rods!");
                return;
            }

            ItemStack rod = clicked.clone();
            ItemMeta meta = rod.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§8Catcher Level: 1");
            meta.setLore(lore);
            rod.setItemMeta(meta);
            
            plugin.getFishingRodManager().saveRod(rod);
            player.sendMessage("§aFishing rod added successfully!");
            catchersGUI.openGUI(player);
        }
    }

    private void handleEditGUI(InventoryClickEvent event, Player player) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;
    
        String rodName = editingRods.get(player.getUniqueId());
        if (rodName == null) return;
    
        ItemStack rod = plugin.getFishingRodManager().getRod(rodName);
        if (rod == null) return;
    
        switch (event.getSlot()) {
            case 11: // Display Name
                awaitingInput.put(player.getUniqueId(), "name:" + rodName);
                player.closeInventory();
                player.sendMessage("§eEnter new name for the fishing rod (color codes allowed):");
                break;
    
            case 13: // Catcher Level
                awaitingInput.put(player.getUniqueId(), "level:" + rodName);
                player.closeInventory();
                player.sendMessage("§eEnter new catcher level (1-100):");
                break;
    
            case 15: // Enchantments
                if (!plugin.getFishingRodManager().isCustomEnchanted(rod)) {
                    enchantmentGUI.openGUI(player, rodName);
                } else {
                    player.sendMessage("§cThis rod has custom enchantments. You can only modify the name and catcher level.");
                }
                break;
    
            case 31: // Back button
                editingRods.remove(player.getUniqueId());
                catchersGUI.openGUI(player);
                break;
        }
    }

    private void handleEnchantmentGUI(InventoryClickEvent event, Player player) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;

        String rodName = editingRods.get(player.getUniqueId());
        if (rodName == null) return;

        ItemStack rod = plugin.getFishingRodManager().getRod(rodName);
        if (rod == null) return;

        if (clicked.getType() == Material.BARRIER) {
            editGUI.openGUI(player, rodName);
            return;
        }

        if (clicked.getType() == Material.ENCHANTED_BOOK) {
            String enchantName = clicked.getItemMeta().getDisplayName().substring(2);
            Enchantment enchant = enchantmentGUI.getEnchantmentFromName(enchantName);
            if (enchant == null) return;

            int currentLevel = rod.getEnchantmentLevel(enchant);
            int maxLevel = enchantmentGUI.getMaxLevel(enchant);

            if (event.isLeftClick() && currentLevel > 0) {
                enchantmentGUI.updateEnchantmentLevel(rod, enchant, currentLevel - 1, maxLevel);
                plugin.getFishingRodManager().saveRod(rod);
                enchantmentGUI.openGUI(player, rodName);
            } else if (event.isRightClick() && currentLevel < maxLevel) {
                enchantmentGUI.updateEnchantmentLevel(rod, enchant, currentLevel + 1, maxLevel);
                plugin.getFishingRodManager().saveRod(rod);
                enchantmentGUI.openGUI(player, rodName);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String input = awaitingInput.get(player.getUniqueId());
        if (input == null) return;

        event.setCancelled(true);
        String[] parts = input.split(":", 2);
        String type = parts[0];
        String rodName = parts[1];

        ItemStack rod = plugin.getFishingRodManager().getRod(rodName);
        if (rod == null) {
            player.sendMessage("§cError: Fishing rod not found!");
            awaitingInput.remove(player.getUniqueId());
            return;
        }

        String message = event.getMessage();
        ItemMeta meta = rod.getItemMeta();

        switch (type) {
            case "name":
                String newName = message.replace('&', '§');
                plugin.getFishingRodManager().removeRod(rodName);
                meta.setDisplayName(newName);
                rod.setItemMeta(meta);
                plugin.getFishingRodManager().saveRod(rod);
                editingRods.put(player.getUniqueId(), newName);
                player.sendMessage("§aFishing rod name updated!");
                break;

            case "level":
                try {
                    int level = Integer.parseInt(message);
                    if (level < 1 || level > 100) {
                        player.sendMessage("§cLevel must be between 1 and 100!");
                        return;
                    }
                    
                    plugin.getFishingRodManager().setRodLevel(rod, level);
                    plugin.getFishingRodManager().saveRod(rod);
                    player.sendMessage("§aCatcher level updated!");
                } catch (NumberFormatException e) {
                    player.sendMessage("§cPlease enter a valid number!");
                    return;
                }
                break;
        }

        awaitingInput.remove(player.getUniqueId());
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            editGUI.openGUI(player, rod.getItemMeta().getDisplayName());
        });
    }
}