package com.fancyfishing.commands;

import com.fancyfishing.FancyFishing;
import com.fancyfishing.gui.MainGUI;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class FancyFishingCommand implements CommandExecutor {
    private final FancyFishing plugin;
    private final MainGUI mainGUI;

    public FancyFishingCommand(FancyFishing plugin) {
        this.plugin = plugin;
        this.mainGUI = new MainGUI(plugin);
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
                handleCatcherCommand(player, args);
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

    private void handleCatcherCommand(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage("§cUsage: /ff catcher <level>");
            return;
        }

        try {
            int level = Integer.parseInt(args[1]);
            if (level < 1 || level > 100) {
                player.sendMessage("§cLevel must be between 1 and 100!");
                return;
            }

            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if (mainHand == null || mainHand.getType() == Material.AIR) {
                player.sendMessage("§cYou must hold a fishing rod!");
                return;
            }

            if (mainHand.getType() != Material.FISHING_ROD) {
                player.sendMessage("§cYou must hold a fishing rod!");
                return;
            }

            ItemMeta meta = mainHand.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§8Catcher Level: " + level);
            meta.setLore(lore);
            mainHand.setItemMeta(meta);
            player.sendMessage("§aCatcher level set to " + level);

        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid level number!");
        }
    }

    private void sendHelp(Player player) {
        List<String> help = Arrays.asList(
            "§6=== FancyFishing Help ===",
            "§e/ff gui §7- Open the item management GUI",
            "§e/ff catcher <level> §7- Set fishing rod catcher level",
            "§e/ff reload §7- Reload the plugin configuration",
            "§e/ff help §7- Show this help message"
        );
        help.forEach(player::sendMessage);
    }
}