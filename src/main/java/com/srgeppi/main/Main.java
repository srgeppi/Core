package com.srgeppi.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;

public class Main extends JavaPlugin {
    private DatabaseManager dbManager;
    private PlayerEventListener playerListener;

    @Override
    public void onEnable() {
        getLogger().info("HelloPlugin enabled!");
        
        // Initialize database manager
        dbManager = new DatabaseManager(this);
        if (dbManager.connect()) {
            getLogger().info("Database connection established!");
        } else {
            getLogger().severe("Failed to connect to database! Some features may not work.");
        }
        
        // Register event listener
        playerListener = new PlayerEventListener(this, dbManager);
        getServer().getPluginManager().registerEvents(playerListener, this);
    }

    @Override
    public void onDisable() {
        if (dbManager != null) {
            dbManager.disconnect();
        }
        getLogger().info("HelloPlugin disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();
        
        if (cmd.equals("ping")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                long ping = player.getPing();
                player.sendMessage("§aPong! Your ping: §e" + ping + "ms");
            } else {
                sender.sendMessage("§aPong! (Console)");
            }
            return true;
        }
        
        if (cmd.equals("info")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cOnly players can use this command!");
                return true;
            }
            
            Player player = (Player) sender;
            DatabaseManager.PlayerInfo info = dbManager.getPlayerInfo(player.getUniqueId());
            
            if (info == null) {
                player.sendMessage("§cNo player data found!");
                return true;
            }
            
            // Format dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            player.sendMessage("§6§l━━━━━━━ §e§lYOUR INFO §6§l━━━━━━━");
            player.sendMessage("§eUUID: §f" + info.uuid);
            player.sendMessage("§eUsername: §f" + info.username);
            if (info.ipAddress != null) {
                player.sendMessage("§eIP Address: §f" + info.ipAddress);
            }
            if (info.firstJoined != null) {
                player.sendMessage("§eFirst Joined: §f" + dateFormat.format(info.firstJoined));
            }
            if (info.lastLogin != null) {
                player.sendMessage("§eLast Login: §f" + dateFormat.format(info.lastLogin));
            }
            if (info.lastLogout != null) {
                player.sendMessage("§eLast Logout: §f" + dateFormat.format(info.lastLogout));
            }
            if (info.userId != null) {
                player.sendMessage("§a✓ Linked to website account (ID: " + info.userId + ")");
            } else {
                player.sendMessage("§7Not linked to website account");
            }
            
            return true;
        }
        
        if (cmd.equals("connect")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cOnly players can use this command!");
                return true;
            }
            
            Player player = (Player) sender;
            
            if (args.length < 1) {
                player.sendMessage("§cUsage: /connect <token>");
                player.sendMessage("§7Get your linking token from the website dashboard.");
                return true;
            }
            
            String token = args[0];
            
            if (dbManager.linkAccount(player.getUniqueId(), token)) {
                player.sendMessage("§a✓ Successfully linked your Minecraft account to your website account!");
            } else {
                player.sendMessage("§c✗ Invalid or expired token. Please generate a new one on the website.");
            }
            
            return true;
        }
        
        return false;
    }
}
