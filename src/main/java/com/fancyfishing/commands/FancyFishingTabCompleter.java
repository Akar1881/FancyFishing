package com.fancyfishing.commands;

import com.fancyfishing.FancyFishing;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FancyFishingTabCompleter implements TabCompleter {
    private static final List<String> COMMANDS = Arrays.asList("gui", "catcher", "ecatcher", "reload", "help");
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], COMMANDS, completions);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("catcher")) {
                if (args[1].isEmpty()) {
                    completions.add("1");
                    completions.add("50");
                    completions.add("100");
                }
            } else if (args[0].equalsIgnoreCase("ecatcher")) {
                StringUtil.copyPartialMatches(args[1], FancyFishing.getInstance().getFishingRodManager().getFishingRodNames(), completions);
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("ecatcher")) {
            if (args[2].isEmpty()) {
                completions.add("1");
                completions.add("50");
                completions.add("100");
            }
        }
        
        Collections.sort(completions);
        return completions;
    }
}