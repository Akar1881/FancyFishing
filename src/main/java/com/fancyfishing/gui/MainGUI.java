package com.fancyfishing.gui;

import com.fancyfishing.FancyFishing;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class MainGUI {
    private final FancyFishing plugin;
    private final PublicGUI publicGUI;
    private final PoolsGUI poolsGUI;

    public MainGUI(FancyFishing plugin) {
        this.plugin = plugin;
        this.publicGUI = new PublicGUI(plugin);
        this.poolsGUI = new PoolsGUI(plugin);
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "FancyFishing - Main Menu");

        // Public Fishing (Water)
        ItemStack publicFishing = new ItemStack(Material.WATER_BUCKET);
        ItemMeta publicMeta = publicFishing.getItemMeta();
        publicMeta.setDisplayName("§bPublicFishings");
        publicMeta.setLore(Arrays.asList(
            "§7Click to manage public fishing items",
            "§7These items can be caught anywhere in water"
        ));
        publicFishing.setItemMeta(publicMeta);
        gui.setItem(11, publicFishing);

        // Pool Fishing (Lava)
        ItemStack poolFishing = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta poolMeta = poolFishing.getItemMeta();
        poolMeta.setDisplayName("§6PoolsFishing");
        poolMeta.setLore(Arrays.asList(
            "§7Click to manage fishing pools",
            "§7Create and customize special fishing areas",
            "§7with unique items and requirements"
        ));
        poolFishing.setItemMeta(poolMeta);
        gui.setItem(15, poolFishing);

        player.openInventory(gui);
    }
}