package net.dirtcraft.dirtbot.utils.analytics;

import net.dirtcraft.dirtbot.data.Player;
import net.dirtcraft.dirtbot.modules.AnalyticsModule;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class AnalyticsDatabaseHelper {

    private AnalyticsModule module;

    public AnalyticsDatabaseHelper(AnalyticsModule module) {
        this.module = module;
    }

    private Connection getDatabaseConnection() throws SQLException {
        return DriverManager.getConnection(module.getConfig().databaseUrl, module.getConfig().databaseUser, module.getConfig().databasePassword);
    }

    public List<Player> getNewcomersInPast24Hours() {
        List<String> newcomerUUIDs = new ArrayList<>();
        List<String> newcomerUsernames = new ArrayList<>();
        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM analytics WHERE time >= NOW() - INTERVAL 1 DAY");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                newcomerUUIDs.add("'" + rs.getString("uuid") + "'");
            }

            try (PreparedStatement usernamePS = connection.prepareStatement("SELECT Username FROM votedata WHERE UUID IN (" + String.join(",", newcomerUUIDs) + ")");
                 ResultSet usernameRS = usernamePS.executeQuery()) {

                while (usernameRS.next()) {
                    newcomerUsernames.add(usernameRS.getString("Username"));
                }
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < newcomerUUIDs.size(); i++) {
            players.add(new Player(newcomerUUIDs.get(i), newcomerUsernames.get(i)));
        }
        return players;
    }

    public List<Player> getNewcomers(int days) {
        List<String> newcomerUUIDs = new ArrayList<>();
        List<String> newcomerUsernames = new ArrayList<>();
        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM analytics WHERE time >= NOW() - INTERVAL " + days + " DAY");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                newcomerUUIDs.add("'" + rs.getString("uuid") + "'");
            }

            try (PreparedStatement usernamePS = connection.prepareStatement("SELECT Username FROM votedata WHERE UUID IN (" + String.join(",", newcomerUUIDs) + ")");
                 ResultSet usernameRS = usernamePS.executeQuery()) {

                while (usernameRS.next()) {
                    newcomerUsernames.add(usernameRS.getString("Username"));
                }
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < newcomerUUIDs.size(); i++) {
            players.add(new Player(newcomerUUIDs.get(i), newcomerUsernames.get(i)));
        }
        return players;
    }

    public Map<String, Integer> getPlaytimeMap() {
        Map<String, Integer> playtimeMap = new HashMap<>();
        List<Player> newcomers = getNewcomersInPast24Hours();
        if (newcomers.isEmpty()) return playtimeMap;
        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM pixel_playtime WHERE uuid IN (" + newcomers.stream().map(Player::getUniqueId).collect(Collectors.joining(",")) + ")")) {

            try (ResultSet rs = ps.executeQuery()) {
               while (rs.next()) {
                   playtimeMap.put(getUsernameByUUID(newcomers, rs.getString("uuid")), rs.getInt("playtime"));
               }
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return playtimeMap;
    }

    public Map<String, Integer> getPlaytimeMap(int days) {
        Map<String, Integer> playtimeMap = new HashMap<>();
        List<Player> newcomers = getNewcomers(days);
        if (newcomers.isEmpty()) return playtimeMap;
        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM pixel_playtime WHERE uuid IN (" + newcomers.stream().map(Player::getUniqueId).collect(Collectors.joining(",")) + ")")) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    playtimeMap.put(getUsernameByUUID(newcomers, rs.getString("uuid")), rs.getInt("playtime"));
                }
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return playtimeMap;
    }

    private String getUsernameByUUID(List<Player> players, String uuid) {
        for (Player player : players) {
            if (player.getUniqueId().replace("'", "").equalsIgnoreCase(uuid)) return player.getUsername();
        }
        return "N/A";
    }

}
