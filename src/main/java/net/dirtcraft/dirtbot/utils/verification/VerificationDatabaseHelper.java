package net.dirtcraft.dirtbot.utils.verification;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.modules.VerificationModule;

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
             PreparedStatement ps = connection.prepareStatement("INSERT INTO verification (discordid, code) VALUES ('" + discordID + "', '" + code + "')")) {

            ps.execute();

        } catch (SQLException exception) {
            exception.printStackTrace();
            DirtBot.pokeTech(exception);
        }
    }

    public boolean hasRecord(String discordID) {
        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE discordid = '" + discordID + "'");
             ResultSet rs = ps.executeQuery()) {

            return rs.next();

        } catch (SQLException exception) {
            exception.printStackTrace();
            DirtBot.pokeTech(exception);
            return false;
        }
    }

    public String getLastCode(String discordID) {
        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT code FROM verification WHERE discordid = '" + discordID + "'");
             ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) return null;

            return rs.getString("code");

        } catch (SQLException exception) {
            exception.printStackTrace();
            DirtBot.pokeTech(exception);
            return null;
        }
    }

    public boolean isVerified(String discordID) {
        try (Connection connection = getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE discordid = '" + discordID + "'");
             ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) return false;

            return rs.getString("uuid") != null;

        } catch (SQLException exception) {
            exception.printStackTrace();
            DirtBot.pokeTech(exception);
            return false;
        }
    }
}
