package com.fancyfishing;

import com.fancyfishing.commands.FancyFishingCommand;
import com.fancyfishing.commands.FancyFishingTabCompleter;
import com.fancyfishing.listeners.*;
import com.fancyfishing.managers.ConfigManager;
import com.fancyfishing.managers.ItemManager;
import com.fancyfishing.managers.FishingRodManager;
import com.fancyfishing.managers.PoolManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FancyFishing extends JavaPlugin {
    private static FancyFishing instance;
    private ConfigManager configManager;
    private ItemManager itemManager;
    private FishingRodManager fishingRodManager;
    private PoolManager poolManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.itemManager = new ItemManager(this);
        this.fishingRodManager = new FishingRodManager(this);
        this.poolManager = new PoolManager(this);
        
        // Register commands and tab completer
        getCommand("ff").setExecutor(new FancyFishingCommand(this));
        getCommand("ff").setTabCompleter(new FancyFishingTabCompleter());
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new FishingListener(this), this);
        getServer().getPluginManager().registerEvents(new PublicGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new FishingRodListener(this), this);
        getServer().getPluginManager().registerEvents(new MainGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new PoolsGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new PoolsItemsGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new EditItemGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new PoolEditItemGUIListener(this), this);
        
        // Load configuration
        configManager.loadConfig();
        itemManager.loadItems();
        
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

    public PoolManager getPoolManager() {
        return poolManager;
    }
}