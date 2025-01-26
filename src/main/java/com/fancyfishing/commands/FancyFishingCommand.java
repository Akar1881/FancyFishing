package com.fancyfishing.commands;

import com.fancyfishing.FancyFishing;
import com.fancyfishing.gui.MainGUI;
import com.fancyfishing.gui.CatchersGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FancyFishingCommand implements CommandExecutor {
    private final FancyFishing plugin;
    private final MainGUI mainGUI;
    private final CatchersGUI catchersGUI;

    public FancyFishingCommand(FancyFishing plugin) {
        this.plugin = plugin;
        this.mainGUI = new MainGUI(plugin);
        this.catchersGUI = new CatchersGUI(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("fancyfishing.admin")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "gui":
                mainGUI.openGUI(player);
                break;
            case "catcher":
                catchersGUI.openGUI(player);
                break;
            case "reload":
                plugin.getConfigManager().loadConfig();
                plugin.getItemManager().loadItems();
                player.sendMessage("§aPlugin reloaded successfully!");
                break;
            case "help":
                sendHelp(player);
                break;
            default:
                player.sendMessage("§cUnknown command. Use /ff help for help.");
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(new String[]{
            "§6=== FancyFishing Help ===",
            "§e/ff gui §7- Open the item management GUI",
            "§e/ff catcher §7- Open the fishing rod management GUI",
            "§e/ff reload §7- Reload the plugin configuration",
            "§e/ff help §7- Show this help message"
        });
    }
}