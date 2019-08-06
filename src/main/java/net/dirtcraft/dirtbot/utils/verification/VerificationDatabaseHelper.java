package net.dirtcraft.dirtbot.utils.verification;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.modules.VerificationModule;

import javax.annotation.Nullable;
import java.sql.*;

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

    public boolean hasRecord(String discordID) {
        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE discordid = ?")) {

            ps.setString(1, discordID);
            ResultSet rs = ps.executeQuery();

            boolean result = rs.next();

            rs.close();

            return result;

        } catch (SQLException exception) {
            exception.printStackTrace();
            DirtBot.pokeDevs(exception);
            return false;
        }
    }

    public String getLastCode(String discordID) {
        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT code FROM verification WHERE discordid = ?")) {

            ps.setString(1, discordID);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close();
                return null;
            } else {
                String code = rs.getString("code");
                rs.close();
                return code;
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            DirtBot.pokeDevs(exception);
            return null;
        }
    }

    public boolean isVerified(String discordID) {
        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE discordid = ?")) {

            ps.setString(1, discordID);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close();
                return false;
            } else {
                boolean result = rs.getString("uuid") != null;
                rs.close();
                return result;
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            DirtBot.pokeDevs(exception);
            return false;
        }
    }

    public boolean codeExists(String code) {
        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE code = ?")) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();

            boolean result = rs.next();

            rs.close();

            return result;

        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Nullable
    public String getUUIDfromDiscordID(String discordID) {
        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM verification WHERE discordid = ?")) {
            ps.setString(1, discordID);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close();
                return null;
            } else {
                String uuid = rs.getString("uuid");
                rs.close();
                return uuid;
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Nullable
    public String getUsernamefromUUID(String uuid) {
        if (uuid == null) return null;

        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT Username FROM votedata WHERE UUID = '" + uuid + "'");
             ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) {
                rs.close();
                return null;
            } else {
                String username = rs.getString("Username");
                rs.close();
                return username;
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
