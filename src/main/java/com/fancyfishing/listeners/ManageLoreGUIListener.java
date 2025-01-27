package com.fancyfishing.listeners;

import com.fancyfishing.FancyFishing;
import com.fancyfishing.gui.ManageLoreGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ManageLoreGUIListener implements Listener {
    private final FancyFishing plugin;
    private final ManageLoreGUI manageLoreGUI;
    private final Map<UUID, String> editingLore;
    private final Map<UUID, Integer> editingSlot;

    public ManageLoreGUIListener(FancyFishing plugin, ManageLoreGUI manageLoreGUI) {
        this.plugin = plugin;
        this.manageLoreGUI = manageLoreGUI;
        this.editingLore = new HashMap<>();
        this.editingSlot = new HashMap<>();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        if (!title.startsWith("Manage Lores - ")) return;
        
        event.setCancelled(true);
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;

        String rodName = title.substring("Manage Lores - ".length());
        ItemStack rod = plugin.getFishingRodManager().getRod(rodName);
        if (rod == null) return;

        int slot = event.getSlot();

        if (clicked.getType() == Material.BARRIER) {
            plugin.getFishingRodManager().saveRod(rod);
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getFREditGUI().openGUI(player, rodName);
            });
            return;
        }

        if (clicked.getType() == Material.EMERALD) {
            // Add new lore button
            ItemMeta meta = rod.getItemMeta();
            List<String> lores = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            
            if (lores.size() >= 15) {
                player.sendMessage("§cMaximum number of lores reached (15)!");
                return;
            }

            // Find next available slot (excluding slots 1 and 14)
            int nextSlot = 0;
            for (int i = 2; i <= 13; i++) {
                if (!hasLoreAtSlot(lores, i)) {
                    nextSlot = i;
                    break;
                }
            }

            if (nextSlot == 0) {
                player.sendMessage("§cNo available slots for new lores!");
                return;
            }

            lores.add("§7New lore <" + nextSlot + ">");
            meta.setLore(lores);
            rod.setItemMeta(meta);
            manageLoreGUI.openGUI(player, rodName);
            return;
        }

        if (slot >= 0 && slot <= 14 && clicked.getType() == Material.PAPER) {
            if (slot == 1) {
                // Catcher level lore - cannot be edited or removed
                player.sendMessage("§cCatcher level lore cannot be modified!");
                return;
            }

            if (slot == 14) {
                // Rarity lore - can be edited but not removed
                if (event.isLeftClick()) {
                    editingLore.put(player.getUniqueId(), rodName);
                    editingSlot.put(player.getUniqueId(), slot);
                    player.closeInventory();
                    player.sendMessage("§eEnter new rarity (COMMON, RARE, EPIC, LEGENDARY, MYTHIC):");
                } else {
                    player.sendMessage("§cRarity lore cannot be removed!");
                }
                return;
            }

            // Regular lore slots (2-13)
            if (event.isLeftClick()) {
                editingLore.put(player.getUniqueId(), rodName);
                editingSlot.put(player.getUniqueId(), slot);
                player.closeInventory();
                player.sendMessage("§eEnter new lore (color codes supported, e.g., &8 for dark gray):");
            } else if (event.isRightClick()) {
                ItemMeta meta = rod.getItemMeta();
                List<String> lores = meta.getLore();
                removeLoreAtSlot(lores, slot);
                meta.setLore(lores);
                rod.setItemMeta(meta);
                manageLoreGUI.openGUI(player, rodName);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String rodName = editingLore.get(player.getUniqueId());
        if (rodName == null) return;

        event.setCancelled(true);
        Integer slot = editingSlot.get(player.getUniqueId());
        if (slot == null) return;

        ItemStack rod = plugin.getFishingRodManager().getRod(rodName);
        if (rod == null) return;

        String input = event.getMessage();
        ItemMeta meta = rod.getItemMeta();
        List<String> lores = meta.hasLore() ? meta.getLore() : new ArrayList<>();

        if (slot == 14) {
            // Handle rarity input
            String rarity = input.toUpperCase();
            if (!isValidRarity(rarity)) {
                player.sendMessage("§cInvalid rarity! Use: COMMON, RARE, EPIC, LEGENDARY, or MYTHIC");
                return;
            }
            setLoreAtSlot(lores, slot, "§7Rarity: " + getRarityColor(rarity) + rarity);
        } else {
            // Handle regular lore input
            setLoreAtSlot(lores, slot, input.replace('&', '§'));
        }

        meta.setLore(lores);
        rod.setItemMeta(meta);

        editingLore.remove(player.getUniqueId());
        editingSlot.remove(player.getUniqueId());

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            manageLoreGUI.openGUI(player, rodName);
        });
    }

    private boolean isValidRarity(String rarity) {
        return rarity.matches("^(COMMON|RARE|EPIC|LEGENDARY|MYTHIC)$");
    }

    private String getRarityColor(String rarity) {
        switch (rarity) {
            case "COMMON": return "§7";
            case "RARE": return "§9";
            case "EPIC": return "§5";
            case "LEGENDARY": return "§6";
            case "MYTHIC": return "§d";
            default: return "§7";
        }
    }

    private boolean hasLoreAtSlot(List<String> lores, int slot) {
        for (String lore : lores) {
            if (lore.contains("<" + slot + ">")) {
                return true;
            }
        }
        return false;
    }

    private void setLoreAtSlot(List<String> lores, int slot, String newLore) {
        for (int i = 0; i < lores.size(); i++) {
            if (lores.get(i).contains("<" + slot + ">")) {
                lores.set(i, newLore);
                return;
            }
        }
        lores.add(newLore);
    }

    private void removeLoreAtSlot(List<String> lores, int slot) {
        lores.removeIf(lore -> lore.contains("<" + slot + ">"));
    }
}