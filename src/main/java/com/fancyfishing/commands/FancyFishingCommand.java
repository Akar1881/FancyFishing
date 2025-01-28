package com.fancyfishing.commands;

import com.fancyfishing.FancyFishing;
import com.fancyfishing.gui.MainGUI;
import com.fancyfishing.managers.Pool;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

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
            case "pcreate":
                handlePoolCreate(player, args);
                break;
            case "catcher":
                handleCatcherCommand(player, args);
                break;
            case "ecatcher":
                handleEditCatcherCommand(player, args);
                break;
            case "reload":
                plugin.getConfigManager().loadConfig();
                plugin.getItemManager().loadItems();
                plugin.getPoolManager().loadPools();
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

    private void handlePoolCreate(Player player, String[] args) {
        if (args.length != 3) {
            player.sendMessage("§cUsage: /ff pcreate <name> <catcher_level>");
            return;
        }

        String name = args[1];
        int catcherLevel;

        try {
            catcherLevel = Integer.parseInt(args[2]);
            if (catcherLevel < 1 || catcherLevel > 100) {
                player.sendMessage("§cCatcher level must be between 1 and 100!");
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid catcher level number!");
            return;
        }

        WorldEditPlugin worldEdit = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEdit == null) {
            player.sendMessage("§cWorldEdit is not installed!");
            return;
        }

        try {
            Region region = worldEdit.getSession(player).getSelection(BukkitAdapter.adapt(player.getWorld()));
            if (region == null) {
                player.sendMessage("§cPlease make a selection with WorldEdit first!");
                return;
            }

            Pool pool = plugin.getPoolManager().createPool(
                name,
                BukkitAdapter.adapt(player.getWorld(), region.getMinimumPoint()),
                BukkitAdapter.adapt(player.getWorld(), region.getMaximumPoint()),
                catcherLevel
            );

            if (pool == null) {
                player.sendMessage("§cError: No water or lava found in the selected area or pool name already exists!");
                return;
            }

            player.sendMessage("§aSuccessfully created pool '" + name + "' with catcher level requirement: " + catcherLevel);

        } catch (IncompleteRegionException e) {
            player.sendMessage("§cPlease make a complete selection with WorldEdit first!");
        }
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

            plugin.getFishingRodManager().saveFishingRod(mainHand, level);
            plugin.getFishingRodManager().updateRodLore(mainHand);
            player.sendMessage("§aCatcher level set to " + level);

        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid level number!");
        }
    }

    private void handleEditCatcherCommand(Player player, String[] args) {
        if (args.length != 3) {
            player.sendMessage("§cUsage: /ff ecatcher <catchername> <newlevel>");
            return;
        }

        String rodName = args[1].toLowerCase();
        try {
            int newLevel = Integer.parseInt(args[2]);
            if (newLevel < 1 || newLevel > 100) {
                player.sendMessage("§cLevel must be between 1 and 100!");
                return;
            }

            List<String> rodNames = plugin.getFishingRodManager().getFishingRodNames();
            if (!rodNames.contains(rodName)) {
                player.sendMessage("§cFishing rod '" + rodName + "' not found!");
                return;
            }

            ItemStack originalRod = plugin.getFishingRodManager().getOriginalRod(rodName);
            if (originalRod != null) {
                plugin.getFishingRodManager().saveFishingRod(originalRod, newLevel);
                player.sendMessage("§aUpdated catcher level for '" + rodName + "' to " + newLevel);
            } else {
                player.sendMessage("§cError: Could not load original fishing rod data!");
            }

        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid level number!");
        }
    }

    private void sendHelp(Player player) {
        List<String> help = Arrays.asList(
            "§6=== FancyFishing Help ===",
            "§e/ff gui §7- Open the main menu",
            "§e/ff pcreate <name> <level> §7- Create a new fishing pool",
            "§e/ff catcher <level> §7- Set fishing rod catcher level",
            "§e/ff ecatcher <name> <level> §7- Edit existing catcher level",
            "§e/ff reload §7- Reload the plugin configuration",
            "§e/ff help §7- Show this help message"
        );
        help.forEach(player::sendMessage);
    }
}