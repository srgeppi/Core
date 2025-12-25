package com.srgeppi.main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventListener implements Listener {
    private final DatabaseManager dbManager;

    public PlayerEventListener(Main plugin, DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Save player data on join
        dbManager.savePlayer(event.getPlayer(), true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Save player data on quit
        dbManager.savePlayer(event.getPlayer(), false);
    }
}

