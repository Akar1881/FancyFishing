package com.fancyfishing;

import com.fancyfishing.commands.FancyFishingCommand;
import com.fancyfishing.commands.FancyFishingTabCompleter;
import com.fancyfishing.listeners.FishingListener;
import com.fancyfishing.listeners.GUIListener;
import com.fancyfishing.listeners.FRGUIListener;
import com.fancyfishing.managers.ConfigManager;
import com.fancyfishing.managers.ItemManager;
import com.fancyfishing.managers.FishingRodManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FancyFishing extends JavaPlugin {
    private static FancyFishing instance;
    private ConfigManager configManager;
    private ItemManager itemManager;
    private FishingRodManager fishingRodManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.itemManager = new ItemManager(this);
        this.fishingRodManager = new FishingRodManager(this);
        
        // Register commands and tab completer
        getCommand("ff").setExecutor(new FancyFishingCommand(this));
        getCommand("ff").setTabCompleter(new FancyFishingTabCompleter());
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new FishingListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new FRGUIListener(this), this);
        
        // Load configuration and data
        configManager.loadConfig();
        itemManager.loadItems();
        fishingRodManager.loadRods();
        
        getLogger().info("FancyFishing has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save all data
        itemManager.saveItems();
        configManager.saveConfig();
        
        getLogger().info("FancyFishing has been disabled!");
    }

    public static FancyFishing getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public FishingRodManager getFishingRodManager() {
        return fishingRodManager;
    }
}