package com.fancyfishing;

import com.fancyfishing.commands.FancyFishingCommand;
import com.fancyfishing.commands.FancyFishingTabCompleter;
import com.fancyfishing.listeners.*;
import com.fancyfishing.managers.ConfigManager;
import com.fancyfishing.managers.ItemManager;
import com.fancyfishing.managers.FishingRodManager;
import com.fancyfishing.gui.*;
import org.bukkit.plugin.java.JavaPlugin;

public class FancyFishing extends JavaPlugin {
    private static FancyFishing instance;
    private ConfigManager configManager;
    private ItemManager itemManager;
    private FishingRodManager fishingRodManager;
    private FREditGUI frEditGUI;
    private ManageLoreGUI manageLoreGUI;

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.itemManager = new ItemManager(this);
        this.fishingRodManager = new FishingRodManager(this);
        
        // Initialize GUIs
        this.manageLoreGUI = new ManageLoreGUI(this);
        this.frEditGUI = new FREditGUI(this);
        
        // Register commands and tab completer
        getCommand("ff").setExecutor(new FancyFishingCommand(this));
        getCommand("ff").setTabCompleter(new FancyFishingTabCompleter());
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new FishingListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new FRGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new ManageLoreGUIListener(this, manageLoreGUI), this);
        
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

    public FREditGUI getFREditGUI() {
        return frEditGUI;
    }

    public ManageLoreGUI getManageLoreGUI() {
        return manageLoreGUI;
    }
}