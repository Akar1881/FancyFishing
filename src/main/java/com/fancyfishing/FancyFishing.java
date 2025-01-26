package com.fancyfishing;

import com.fancyfishing.commands.FancyFishingCommand;
import com.fancyfishing.commands.FancyFishingTabCompleter;
import com.fancyfishing.listeners.FishingListener;
import com.fancyfishing.listeners.GUIListener;
import com.fancyfishing.listeners.EditGUIListener;
import com.fancyfishing.managers.ConfigManager;
import com.fancyfishing.managers.ItemManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FancyFishing extends JavaPlugin {
    private static FancyFishing instance;
    private ConfigManager configManager;
    private ItemManager itemManager;
    private EditGUIListener editGUIListener;

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.itemManager = new ItemManager(this);
        this.editGUIListener = new EditGUIListener(this);
        
        // Register commands and tab completer
        getCommand("ff").setExecutor(new FancyFishingCommand(this));
        getCommand("ff").setTabCompleter(new FancyFishingTabCompleter());
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new FishingListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(editGUIListener, this);
        
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

    public EditGUIListener getEditGUIListener() {
        return editGUIListener;
    }
}