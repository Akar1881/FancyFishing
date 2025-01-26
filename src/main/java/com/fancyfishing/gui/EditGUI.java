package com.fancyfishing.gui;

import com.fancyfishing.FancyFishing;
import com.fancyfishing.managers.FishingItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.UUID;

public class EditGUI {
    private final FancyFishing plugin;

    public EditGUI(FancyFishing plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player, UUID itemId) {
        FishingItem fishingItem = plugin.getItemManager().getItem(itemId);
        if (fishingItem == null) {
            player.closeInventory();
            return;
        }

        Inventory gui = Bukkit.createInventory(null, 27, "FancyFishing - Edit Item");

        // Display current item
        gui.setItem(4, fishingItem.getItem());

        // Chance modifier (XP Bottle)
        ItemStack chanceItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta chanceMeta = chanceItem.getItemMeta();
        chanceMeta.setDisplayName("§eCatch Chance");
        chanceMeta.setLore(Arrays.asList(
            "§7Current chance: §f" + fishingItem.getChance() + "%",
            "",
            "§7This chance represents the rarity",
            "§7of catching this item. Higher chance",
            "§7means the item is more common to catch.",
            "",
            "§7Example:",
            "§7- 100% = Very common",
            "§7- 50% = Uncommon",
            "§7- 10% = Rare",
            "§7- 1% = Very rare",
            "",
            "§eClick §7to change chance"
        ));
        chanceItem.setItemMeta(chanceMeta);
        gui.setItem(11, chanceItem);

        // Level modifier (Flint)
        ItemStack levelItem = new ItemStack(Material.FLINT);
        ItemMeta levelMeta = levelItem.getItemMeta();
        levelMeta.setDisplayName("§eCatcher Level");
        levelMeta.setLore(Arrays.asList(
            "§7Current level: §f" + fishingItem.getCatcherLevel(),
            "",
            "§7This level determines the minimum",
            "§7fishing rod catcher level required",
            "§7to catch this item.",
            "",
            "§7Players can only catch this item",
            "§7if their fishing rod has a catcher",
            "§7level equal to or higher than this.",
            "",
            "§eClick §7to change level"
        ));
        levelItem.setItemMeta(levelMeta);
        gui.setItem(15, levelItem);

        // Back button
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§cBack to Main Menu");
        back.setItemMeta(backMeta);
        gui.setItem(26, back);

        player.openInventory(gui);
    }
}