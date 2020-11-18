package net.dirtcraft.dirtbot.utils.tickets;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.data.Ticket;
import net.dirtcraft.dirtbot.modules.TicketModule;
import net.dirtcraft.dirtbot.modules.VerificationModule;
import net.dirtcraft.dirtbot.utils.verification.VerificationDatabaseHelper;

import java.sql.*;
import java.util.*;

public class TicketsDatabaseHelper {

    private TicketModule module;

    public TicketsDatabaseHelper(TicketModule module) {
        this.module = module;
    }

    public Connection getDatabaseConnection() throws SQLException {
        return DriverManager.getConnection(module.getConfig().databaseUrl, module.getConfig().databaseUser, module.getConfig().databasePassword);
    }

    /**
     * Inserts the given ticket into the database and returns the ticket populated with auto-fill fields.
     *
     * @param ticket A ticket populated with the desired data to be inserted into the database
     * @return The given ticket with the ID populated
     */
    public Ticket createTicket(Ticket ticket) {
        try (
            // Establish Database Connection
            Connection con = getDatabaseConnection();

            // Prepare Query
            PreparedStatement statement = con.prepareStatement("INSERT INTO tickets (open, message, discordid, username) VALUES (?, ?, ?, ?)")) {
        
            statement.setBoolean(1, ticket.getOpen());
            statement.setString(2, ticket.getMessage());
            statement.setString(3, ticket.getDiscordID(true));
            statement.setString(4, ticket.getUsername(true));

            // Create Ticket
            statement.executeUpdate();
            
            // Get latest ticket
            try (
                PreparedStatement statement2 = con.prepareStatement("SELECT LAST_INSERT_ID()");
                ResultSet results = statement2.executeQuery()) {
                if (results.next()) {
                    ticket.setId(results.getInt(1));
                    return ticket;
                }/* else {
                    // TODO Ticket Not Found After Insertion
                }*/
            }
        } catch (SQLException e) {
            e.printStackTrace();
            DirtBot.pokeDevs(e);
        }
        return null;
    }

    /**
     * Gets a ticket from the database based on row id.
     *
     * @param id Row id of the ticket in the database
     * @return The fetched ticket
     */
    public Optional<Ticket> getTicket(int id) {
        try (
            // Establish Database Connection
            Connection con = getDatabaseConnection();

            // Prepare Query
            PreparedStatement statement = con.prepareStatement("SELECT * FROM tickets WHERE id = ?")) {
            statement.setInt(1, id);

            // Execute Query
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    Ticket ticket = new Ticket(
                            results.getInt("id"),
                            true, results.getString("message"),
                            results.getString("username"),
                            results.getString("server"),
                            results.getString("channel"),
                            Ticket.Level.valueOf(results.getString("level").toUpperCase()),
                            results.getString("discordid"));
                    return Optional.of(ticket);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            DirtBot.pokeDevs(e);
        }
        return Optional.empty();
    }

    /**
     * Gets a ticket from the database based on discord channel id.
     *
     * @param channel The discord channel id
     * @return The fetched ticket
     */
    public Optional<Ticket> getTicket(String channel) {
        try (
            // Establish Database Connection
            Connection con = getDatabaseConnection();

            // Prepare Query
            PreparedStatement statement = con.prepareStatement("SELECT * FROM tickets WHERE channel = ?")) {
            statement.setString(1, channel);

            // Execute Query
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    Ticket ticket = new Ticket(
                            results.getInt("id"),
                            true,
                            results.getString("message"),
                            results.getString("username"),
                            results.getString("server"), results.getString("channel"),
                            Ticket.Level.valueOf(results.getString("level").toUpperCase()),
                            results.getString("discordid"));
                    return Optional.of(ticket);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            DirtBot.pokeDevs(e);
        }
        return Optional.empty();
    }

    /**
     * Gets a list of tickets that have not yet been assigned channels from the database.
     *
     * @return A list of unassigned tickets
     */

    public List<Ticket> getGameTickets() {
        List<Ticket> tickets = new ArrayList<>();
        try (
            // Establish Database Connection
            Connection con = getDatabaseConnection();

            // Prepare Query
            PreparedStatement statement = con.prepareStatement("SELECT * FROM tickets WHERE open = ? AND channel IS NULL")) {

            statement.setBoolean(1, true);

            // Fetch Results
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                tickets.add(new Ticket(
                        results.getInt("id"),
                        true,
                        results.getString("message"),
                        results.getString("username"),
                        results.getString("server"),
                        results.getString("channel"),
                        Ticket.Level.valueOf(results.getString("level")),
                        results.getString("discordid")));
            }

            // Clean Up
            results.close();

        } catch (SQLException e) {
            e.printStackTrace();
            DirtBot.pokeDevs(e);
        }
        return tickets;
    }

    /**
     * Checks if the user has an open ticket
     *
     * @return A boolean if the user has a ticket
     */

    public boolean hasOpenTicket(String discordID) {
        try (
            Connection connection = getDatabaseConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT open FROM tickets WHERE discordid = ? AND open = 1")) {
            ps.setString(1, discordID);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            DirtBot.pokeDevs(exception);
        }
        return false;
    }

    /**
     * Updates a ticket in the database with information to delete the ticket
     *
     * @param ticket The ticket that needs to be deleted
     */

    public void closeTicket(Ticket ticket) {
        try (Connection con = getDatabaseConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE tickets SET open = ?, channel = ? WHERE id = ?")) {
            statement.setBoolean(1, ticket.getOpen());
            statement.setString(2, null);
            statement.setInt(3, ticket.getId());

            statement.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
            DirtBot.pokeDevs(exception);
        }
    }

    /**
     * Updates a ticket in the database.
     *
     * @param ticket The ticket with updated data
     */
    public void modifyTicket(Ticket ticket) {
        try (
            // Establish Database Connection
            Connection con = getDatabaseConnection();
            // Prepare Query
            PreparedStatement statement = con.prepareStatement(
                    "UPDATE tickets SET open = ?, username = ?, server = ?, channel = ?, level = ?, discordid = ? WHERE id = ?")) {
            statement.setBoolean(1, ticket.getOpen());
            statement.setString(2, ticket.getUsername(true));
            statement.setString(3, ticket.getServer(true));
            statement.setString(4, ticket.getChannel());
            statement.setString(5, ticket.getLevel().toString().toLowerCase());
            statement.setString(6, ticket.getDiscordID(true));
            statement.setInt(7, ticket.getId());

            statement.executeUpdate();

            /*
            // Push the Changes & Clean Up
            if (!statement.execute()) {
                // TODO Something went wrong
            }
            */

        } catch (SQLException e) {
            e.printStackTrace();
            DirtBot.pokeDevs(e);
        }
    }

    public void setTicketLevel(Ticket ticket) {
        try (Connection con = getDatabaseConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE tickets SET level = ? WHERE id = ?")) {
            statement.setString(1, ticket.getLevel().toString().toLowerCase());
            statement.setInt(2, ticket.getId());

            statement.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
            DirtBot.pokeDevs(exception);
        }
    }

    public void setTicketChannel(Ticket ticket) {
        try (Connection con = getDatabaseConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE tickets SET channel = ? WHERE id = ?")) {
            statement.setString(1, ticket.getChannel());
            statement.setInt(2, ticket.getId());

            statement.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
            DirtBot.pokeDevs(exception);
        }
    }

    public void setTicketUsername(Ticket ticket) {
        try (Connection connection = getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE tickets set username = ? WHERE id = ?")) {
            statement.setString(1, ticket.getUsername(true));
            statement.setInt(2, ticket.getId());

            statement.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
            DirtBot.pokeDevs(exception);
        }
    }

    public void setTicketServer(Ticket ticket) {
        try (Connection connection = getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE tickets set server = ? WHERE id = ?")) {
            statement.setString(1, ticket.getServer(true));
            statement.setInt(2, ticket.getId());

            statement.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
            DirtBot.pokeDevs(exception);
        }
    }

    public Optional<String> getLastTicketChannelID(String discordID) {
        try (
            Connection connection = getDatabaseConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT channel FROM tickets WHERE discordid = ? ORDER BY id DESC")) {
            ps.setString(1, discordID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.ofNullable(rs.getString("channel"));
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            DirtBot.pokeDevs(exception);
        }

        return Optional.empty();
    }

    public void addConfirmationMessage(int ticketId, String confirmationMessageId, String reason) {
        try (
            // Establish Database Connection
            Connection con = getDatabaseConnection();
            // Prepare Query
            PreparedStatement statement = con.prepareStatement("INSERT INTO closes (ticketid, confirmationid, reason) VALUES (?, ?, ?)")) {
            statement.setInt(1, ticketId);
            statement.setString(2, confirmationMessageId);
            statement.setString(3, reason);

            statement.executeUpdate();
            // Execute Query & Clean Up
            /*if (!statement.execute()) {
                // TODO Something went wrong
            }*/

        } catch (SQLException e) {
            e.printStackTrace();
            DirtBot.pokeDevs(e);
        }
    }

    public void addConfirmationMessage(int ticketId, String confirmationMessageId, String reason, String closeTime) {
        try (
            // Establish Database Connection
            Connection con = getDatabaseConnection();
            // Prepare Query
            PreparedStatement statement = con.prepareStatement("INSERT INTO closes (ticketid, confirmationid, reason, closetime) VALUES (?, ?, ?, ?)")) {

            statement.setInt(1, ticketId);
            statement.setString(2, confirmationMessageId);
            statement.setString(3, reason);
            statement.setString(4, closeTime);

            statement.executeUpdate();
            /*
            // Execute Query & Clean Up
            if (!statement.execute()) {
                // TODO Something went wrong
            }*/
        } catch (SQLException e) {
            e.printStackTrace();
            DirtBot.pokeDevs(e);
        }
    }

    public HashMap<Integer, String> getAllTimedCloses() {
        HashMap<Integer, String> timedCloses = new HashMap<>();
        try (
            //Establish Database Connection
            Connection con = getDatabaseConnection();
            // Prepare Query
            PreparedStatement statement = con.prepareStatement("SELECT ticketid, closetime FROM closes WHERE closetime IS NOT NULL")) {

            // Fetch Results
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    timedCloses.put(results.getInt("ticketid"), results.getString("closetime"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            DirtBot.pokeDevs(e);
        }

        return timedCloses;
    }

    public void removeConfirmationMessage(String confirmationMessageId) {
        try (
            // Establish Database Connection
            Connection con = getDatabaseConnection();
            // Prepare Query
            PreparedStatement statement = con.prepareStatement("DELETE FROM closes WHERE confirmationid = ?")) {
            statement.setString(1, confirmationMessageId);

            statement.executeUpdate();
            /*
            // Execute Query & Clean Up
            if (!statement.execute()) {
                // TODO Something went wrong
            }*/
        } catch (SQLException e) {
            e.printStackTrace();
            DirtBot.pokeDevs(e);
        }
    }

    public void removeAllConfirmationMessages(int ticketId) {
        try (
            // Establish Database Connection
            Connection con = getDatabaseConnection();

            // Prepare Query
            PreparedStatement statement = con.prepareStatement("DELETE FROM closes WHERE ticketid = ?")) {

            statement.setInt(1, ticketId);

            statement.executeUpdate();
            /*
            // Execute Query & Clean Up
            if (!statement.execute()) {
                // TODO Something went wrong
            }*/

        } catch (SQLException e) {
            e.printStackTrace();
            DirtBot.pokeDevs(e);
        }
    }

    /*public void removeAllConfirmationMessages(String confirmationMessageId) {
        try {
            // Establish Database Connection
            Connection con = getDatabaseConnection();

            // Prepare Query
            PreparedStatement statement = con.prepareStatement("SELECT * FROM closes WHERE confirmationid=? LIMIT 1");
            statement.setString(1, confirmationMessageId);

            // Execute Query
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                removeAllConfirmationMessages(results.getInt("ticketid"));
            }

            // Clean Up
            results.close();
            statement.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            DirtBot.pokeDevs(e);
        }
    }*/

    public boolean isConfirmationMessage(String confirmationMessageId) {
        try (
            // Establish Database Connection
            Connection con = getDatabaseConnection();

            // Prepare Query
            PreparedStatement statement = con.prepareStatement("SELECT * FROM closes WHERE confirmationid = ?")) {
            statement.setString(1, confirmationMessageId);

            // Execute Query
            try (ResultSet results = statement.executeQuery()) {
                return results.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            DirtBot.pokeDevs(e);
        }
        return false;
    }

    public String getClosureReason(String confirmationMessageId) {
        try (
            // Establish Database Connection
            Connection con = getDatabaseConnection();

            // Prepare Query
            PreparedStatement statement = con.prepareStatement("SELECT * FROM closes WHERE confirmationid=  ?")) {

            statement.setString(1, confirmationMessageId);

            // Execute Query
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) return results.getString("reason");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            DirtBot.pokeDevs(e);
        }
        return "No reason found.";
    }

    public Optional<String> getUsernameFromDiscordId(String discordId) {
        VerificationDatabaseHelper verificationDatabase = DirtBot.getModuleRegistry().getModule(VerificationModule.class).getVerificationDatabase();
        Optional<String> optionalUUID = verificationDatabase.getUUIDfromDiscordID(discordId);
        if (!optionalUUID.isPresent()) return Optional.empty();
        return verificationDatabase.getUsernameFromUUID(optionalUUID.get());
    }

    /*
    public int getLastAutoIncrementID() {
        try {
            // Establish Database Connection
            Connection con = getDatabaseConnection();

            // Prepare Query
            PreparedStatement statement = con.prepareStatement("SELECT AUTO_INCREMENT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?");
            statement.setString(1, module.getConfig().databaseUrl.substring(module.getConfig().databaseUrl.lastIndexOf("/") + 1));
            statement.setString(2, "tickets");

            // Execute Query
            ResultSet results = statement.executeQuery();

            // Clean Up & Return Data
            if(results.next()) {
                int result = results.getInt(1);
                results.close();
                statement.close();
                con.close();
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            DirtBot.pokeDevs(e);
        }
        return 0;
    }*/
}
