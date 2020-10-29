package net.dirtcraft.dirtbot.utils.verification;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.modules.VerificationModule;

import java.sql.*;
import java.util.Optional;

public class VerificationDatabaseHelper {

    private VerificationModule module;

    public VerificationDatabaseHelper(VerificationModule module) {
        this.module = module;
    }

    public Connection getDatabaseConnection() throws SQLException {
        return DriverManager.getConnection(module.getConfig().databaseUrl, module.getConfig().databaseUser, module.getConfig().databasePassword);
    }

    public void createRecord(String discordID, String code) {
        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("INSERT INTO verification (discordid, code) VALUES (?, ?)")) {

            ps.setString(1, discordID);
            ps.setString(2, code);

            ps.execute();

        } catch (SQLException exception) {
            exception.printStackTrace();
            DirtBot.pokeDevs(exception);
        }
    }

    public Optional<Boolean> hasRecord(String discordID) {
        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE discordid = ?")) {

            ps.setString(1, discordID);

            try (ResultSet rs = ps.executeQuery()) {
                return Optional.of(rs.next());
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            DirtBot.pokeDevs(exception);
        }
        return Optional.empty();
    }

    public Optional<String> getLastCode(String discordID) {
        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT code FROM verification WHERE discordid = ?")) {

            ps.setString(1, discordID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.ofNullable(rs.getString("code"));
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            DirtBot.pokeDevs(exception);
        }
        return Optional.empty();
    }

    public boolean isVerified(String discordID) {
        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE discordid = ?")) {

            ps.setString(1, discordID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("uuid") != null;
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            DirtBot.pokeDevs(exception);
        }
        return false;
    }

    public Optional<Boolean> codeExists(String code) {
        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE code = ?")) {
            ps.setString(1, code);

            try (ResultSet rs = ps.executeQuery()) {
                return Optional.of(rs.next());
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<String> getUUIDfromDiscordID(String discordID) {
        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM verification WHERE discordid = ?")) {
            ps.setString(1, discordID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.ofNullable(rs.getString("uuid"));
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<String> getUsernameFromUUID(String uuid) {
        if (uuid == null) return Optional.empty();

        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT Username FROM votedata WHERE UUID = ?")) {
            ps.setString(1, uuid);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.ofNullable(rs.getString("Username"));
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return Optional.empty();
    }
}
