package com.srgeppi.main;

import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

public class DatabaseManager {
    private final Main plugin;
    private Connection connection;
    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final int port;

    public DatabaseManager(Main plugin) {
        this.plugin = plugin;
        
        // Get database config from environment or config
        this.host = System.getenv("POSTGRES_HOST") != null ? System.getenv("POSTGRES_HOST") : "postgres";
        this.port = 5432;
        this.database = System.getenv("POSTGRES_DB") != null ? System.getenv("POSTGRES_DB") : "minecraft_db";
        this.username = System.getenv("POSTGRES_USER") != null ? System.getenv("POSTGRES_USER") : "minecraft_user";
        this.password = System.getenv("POSTGRES_PASSWORD") != null ? System.getenv("POSTGRES_PASSWORD") : "change_me_in_production";
    }

    public boolean connect() {
        try {
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
            connection = DriverManager.getConnection(url, username, password);
            plugin.getLogger().info("Connected to PostgreSQL database!");
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to connect to database: " + e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("Disconnected from database!");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error disconnecting from database: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // Save or update player data
    public void savePlayer(Player player, boolean isLogin) {
        if (!isConnected()) {
            plugin.getLogger().warning("Database not connected, cannot save player data!");
            return;
        }

        try {
            UUID uuid = player.getUniqueId();
            String username = player.getName();
            String ipAddress = player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : null;

            // Check if player exists
            PreparedStatement checkStmt = connection.prepareStatement(
                "SELECT id, first_joined FROM players WHERE uuid = ?"
            );
            checkStmt.setString(1, uuid.toString());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Update existing player
                PreparedStatement updateStmt = connection.prepareStatement(
                    "UPDATE players SET username = ?, ip_address = ?, " +
                    (isLogin ? "last_login = NOW()" : "last_logout = NOW()") +
                    " WHERE uuid = ?"
                );
                updateStmt.setString(1, username);
                updateStmt.setString(2, ipAddress);
                updateStmt.setString(3, uuid.toString());
                updateStmt.executeUpdate();
                updateStmt.close();
            } else {
                // Insert new player
                PreparedStatement insertStmt = connection.prepareStatement(
                    "INSERT INTO players (uuid, username, ip_address, first_joined, last_login, last_logout) " +
                    "VALUES (?, ?, ?, NOW(), " + (isLogin ? "NOW()" : "NULL") + ", " + (isLogin ? "NULL" : "NOW()") + ")"
                );
                insertStmt.setString(1, uuid.toString());
                insertStmt.setString(2, username);
                insertStmt.setString(3, ipAddress);
                insertStmt.executeUpdate();
                insertStmt.close();
            }

            checkStmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error saving player data: " + e.getMessage());
        }
    }

    // Get player info
    public PlayerInfo getPlayerInfo(UUID uuid) {
        if (!isConnected()) {
            return null;
        }

        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT uuid, username, ip_address, first_joined, last_login, last_logout, user_id " +
                "FROM players WHERE uuid = ?"
            );
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                PlayerInfo info = new PlayerInfo(
                    rs.getString("uuid"),
                    rs.getString("username"),
                    rs.getString("ip_address"),
                    rs.getTimestamp("first_joined"),
                    rs.getTimestamp("last_login"),
                    rs.getTimestamp("last_logout"),
                    rs.getInt("user_id") != 0 ? rs.getInt("user_id") : null
                );
                stmt.close();
                return info;
            }
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error getting player info: " + e.getMessage());
        }
        return null;
    }

    // Link account with token
    public boolean linkAccount(UUID uuid, String token) {
        if (!isConnected()) {
            return false;
        }

        try {
            // Find token in linking_tokens table
            PreparedStatement tokenStmt = connection.prepareStatement(
                "SELECT user_id FROM linking_tokens WHERE token = ? AND expires_at > NOW()"
            );
            tokenStmt.setString(1, token);
            ResultSet tokenRs = tokenStmt.executeQuery();

            if (!tokenRs.next()) {
                tokenStmt.close();
                return false; // Invalid or expired token
            }

            int userId = tokenRs.getInt("user_id");
            tokenStmt.close();

            // Update or create player record with user_id
            // First check if player exists
            PreparedStatement checkStmt = connection.prepareStatement(
                "SELECT id FROM players WHERE uuid = ?"
            );
            checkStmt.setString(1, uuid.toString());
            ResultSet playerRs = checkStmt.executeQuery();

            if (playerRs.next()) {
                // Update existing player
                PreparedStatement updateStmt = connection.prepareStatement(
                    "UPDATE players SET user_id = ? WHERE uuid = ?"
                );
                updateStmt.setInt(1, userId);
                updateStmt.setString(2, uuid.toString());
                updateStmt.executeUpdate();
                updateStmt.close();
            } else {
                // Create new player (shouldn't happen, but handle it)
                // Player should exist from join event
                plugin.getLogger().warning("Player not found when linking account - this shouldn't happen!");
            }
            checkStmt.close();

            // Delete the used token
            PreparedStatement deleteTokenStmt = connection.prepareStatement(
                "DELETE FROM linking_tokens WHERE token = ?"
            );
            deleteTokenStmt.setString(1, token);
            deleteTokenStmt.executeUpdate();
            deleteTokenStmt.close();

            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Error linking account: " + e.getMessage());
            return false;
        }
    }
    
    // Set linking token (called when user generates token on website)
    public boolean setLinkingTokenForUser(int userId, String token) {
        if (!isConnected()) {
            return false;
        }

        try {
            // This will be called by API - we need to find which player to link
            // For now, we'll store it temporarily - the player will use /connect
            // Actually, the API should handle setting the token on a player record
            // This method is for when we know the UUID
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Error setting linking token: " + e.getMessage());
            return false;
        }
    }


    // Player info data class
    public static class PlayerInfo {
        public final String uuid;
        public final String username;
        public final String ipAddress;
        public final Timestamp firstJoined;
        public final Timestamp lastLogin;
        public final Timestamp lastLogout;
        public final Integer userId;

        public PlayerInfo(String uuid, String username, String ipAddress,
                         Timestamp firstJoined, Timestamp lastLogin, Timestamp lastLogout, Integer userId) {
            this.uuid = uuid;
            this.username = username;
            this.ipAddress = ipAddress;
            this.firstJoined = firstJoined;
            this.lastLogin = lastLogin;
            this.lastLogout = lastLogout;
            this.userId = userId;
        }
    }
}

