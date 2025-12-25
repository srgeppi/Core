package com.srgeppi.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    
    @Override
    public void onEnable() {
        getLogger().info("HelloPlugin enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("HelloPlugin disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ping")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                long ping = player.getPing();
                player.sendMessage("§aPong! Your ping: §e" + ping + "ms");
            } else {
                sender.sendMessage("§aPong! (Console)");
            }
            return true;
        }
        return false;
    }
}
