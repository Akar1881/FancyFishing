package com.fancyfishing.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FancyFishingTabCompleter implements TabCompleter {
    private static final List<String> COMMANDS = Arrays.asList("gui", "catcher", "reload", "help");
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], COMMANDS, completions);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("catcher")) {
            if (args[1].isEmpty()) {
                completions.add("1");
                completions.add("50");
                completions.add("100");
            } else {
                try {
                    int level = Integer.parseInt(args[1]);
                    if (level >= 1 && level <= 100) {
                        completions.add(String.valueOf(level));
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        
        Collections.sort(completions);
        return completions;
    }
}