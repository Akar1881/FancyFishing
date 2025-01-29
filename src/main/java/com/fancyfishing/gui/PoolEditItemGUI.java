package com.fancyfishing.gui;

import com.fancyfishing.FancyFishing;
import com.fancyfishing.managers.FishingItem;
import com.fancyfishing.managers.Pool;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.UUID;

public class PoolEditItemGUI {
    private final FancyFishing plugin;
    public static final String GUI_TITLE = "Pool Edit Item"; // Added constant for title

    public PoolEditItemGUI(FancyFishing plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player, UUID itemId, String poolName) {
        Pool pool = plugin.getPoolManager().getPool(poolName);
        if (pool == null) {
            player.closeInventory();
            return;
        }

        FishingItem fishingItem = pool.getItem(itemId);
        if (fishingItem == null) {
            player.closeInventory();
            return;
        }

        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE);

        // Display current item
        gui.setItem(4, fishingItem.getItem());

        // Chance modifier (XP Bottle)
        ItemStack chanceItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta chanceMeta = chanceItem.getItemMeta();
        chanceMeta.setDisplayName("§eChange Catch Chance");
        chanceMeta.setLore(Arrays.asList(
            "§7Current chance: §f" + fishingItem.getChance() + "%",
            "",
            "§7Click to modify the chance",
            "§7of catching this item"
        ));
        chanceItem.setItemMeta(chanceMeta);
        gui.setItem(11, chanceItem);

        // Level modifier (Flint)
        ItemStack levelItem = new ItemStack(Material.FLINT);
        ItemMeta levelMeta = levelItem.getItemMeta();
        levelMeta.setDisplayName("§eChange Catcher Level");
        levelMeta.setLore(Arrays.asList(
            "§7Current level: §f" + fishingItem.getCatcherLevel(),
            "",
            "§7Click to modify the minimum",
            "§7catcher level required"
        ));
        levelItem.setItemMeta(levelMeta);
        gui.setItem(13, levelItem);

        // Message modifier (Paper)
        ItemStack messageItem = new ItemStack(Material.PAPER);
        ItemMeta messageMeta = messageItem.getItemMeta();
        messageMeta.setDisplayName("§eChange Catch Message");
        String currentMessage = fishingItem.getCatchMessage() != null ? fishingItem.getCatchMessage() : "None";
        messageMeta.setLore(Arrays.asList(
            "§7Current message:",
            "§f" + currentMessage,
            "",
            "§7Click to modify the message",
            "§7shown when caught"
        ));
        messageItem.setItemMeta(messageMeta);
        gui.setItem(15, messageItem);

        // Back button
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§cBack");
        backMeta.setLore(Arrays.asList("§7Click to return to", "§7the previous menu"));
        back.setItemMeta(backMeta);
        gui.setItem(26, back);

        player.openInventory(gui);
    }
}