package net.dirtcraft.dirtbot.utils.tickets;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.data.Ticket;
import net.dirtcraft.dirtbot.modules.TicketModule;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class TicketUtils {

    private TicketModule module;

    public TicketUtils(TicketModule module) {
        this.module = module;
    }

    public void updateCategory(Ticket ticket, String oldServer) {
        if (ticket.getChannel() == null) return;
        TextChannel ticketChannel = DirtBot.getJda().getTextChannelById(ticket.getChannel());
        if (ticket.getLevel() == Ticket.Level.OWNER) {
            ticketChannel.getManager().setParent(DirtBot.getJda().getCategoryById(module.getConfig().ownerSupportCategoryID)).queue();
            module.getEmbedUtils().deleteTicketAdminMessage(ticket, oldServer);
            return;
        }
        if(ticket.getServer(true) == null) {
            ticketChannel.getManager().setParent(DirtBot.getJda().getCategoryById(module.getConfig().supportCategoryID)).queue();
        } else {
            for (List<String> serverInfo : DirtBot.getConfig().servers) {
                if(serverInfo.get(1).toLowerCase().equals(ticket.getServer(true).toLowerCase())) {
                    ticketChannel.getManager().setParent(DirtBot.getJda().getCategoryById(serverInfo.get(2))).queue();
                }
            }
        }
        module.getEmbedUtils().deleteTicketAdminMessage(ticket, oldServer);
        if(ticket.getLevel() == Ticket.Level.ADMIN) module.getEmbedUtils().postTicketAdminMessage(ticket);
    }

    public void updateCategory(Ticket ticket) {
        updateCategory(ticket, ticket.getServer(true));
    }

    public TextChannel createTicket(String message, Member member) {
        return createTicket(module.getDatabaseHelper().createTicket(new Ticket(message)), member);
    }

    public TextChannel createTicket(Ticket ticket, Member member) {
        Guild server = DirtBot.getJda().getGuildById(DirtBot.getConfig().serverID);
        TextChannel ticketChannel = (TextChannel) server.getController().createTextChannel(Integer.toString(ticket.getId()))
                .setParent(server.getCategoryById(module.getConfig().supportCategoryID))
                .addPermissionOverride(member, EnumSet.of(Permission.MESSAGE_READ), null)
                .addPermissionOverride(server.getRoleById(DirtBot.getConfig().staffRoleID), EnumSet.of(Permission.MESSAGE_READ), null)
                .addPermissionOverride(server.getRoleById(server.getId()), null, EnumSet.of(Permission.MESSAGE_READ))
                .setTopic("**Awaiting <@&" + DirtBot.getConfig().staffRoleID + ">'s Response...**")
                .complete();
        ticket.setChannel(ticketChannel.getId());
        module.getDatabaseHelper().modifyTicket(ticket);
        ticketChannel.sendMessage(module.getEmbedUtils().getTicketHeader(ticket)).queue((message) -> message.pin().queue());
        module.getEmbedUtils().sendLog("Created", ticket.getMessage(), ticket, member);
        return ticketChannel;
    }

    public Ticket setTicketLevel(Ticket ticket, Ticket.Level level) {
        // Make sure the ticket has a channel in the first place
        if (ticket.getChannel() == null) return ticket;
        // Make sure the ticket isn't being set to the same level it already is
        if (ticket.getLevel() == level) return ticket;

        ticket.setLevel(level);
        module.getDatabaseHelper().modifyTicket(ticket);
        updateCategory(ticket);
        return ticket;
    }

    public Ticket setTicketServer(Ticket ticket, String server) {
        // Set the server in the ticket & DB. If it's clear or reset, set it to null
        String oldServer = ticket.getServer(true);
        if(server.toLowerCase().equals("reset") || server.toLowerCase().equals("clear")) ticket.setServer(null);
        else ticket.setServer(server.toLowerCase());
        updateCategory(ticket, oldServer);
        return ticket;
    }

    public void closeTicket(Ticket ticket, String message, boolean review) {
        module.archiveTicket(ticket);
        TextChannel ticketChannel = DirtBot.getJda().getTextChannelById(ticket.getChannel());
        module.getDatabaseHelper().removeAllConfirmationMessages(ticket.getId());
        module.getEmbedUtils().deleteTicketAdminMessage(ticket, ticket.getServer(true));
        if(review) {
            EmbedBuilder reviewDM = module.getEmbedUtils().getEmptyEmbed()
                    .addField("__Ticket Closed__", "Your ticket (#" + ticket.getId() + " - " + ticketChannel.getName() + ") has been closed for the following reason:\n" +
                            "```" + message + "```", false);
            for(Member member : getTicketMembers(ticket)) {
                member.getUser().openPrivateChannel().queue((dmChannel) ->  dmChannel.sendMessage(reviewDM.build()).queue());
            }
        }
        ticket.setOpen(false);
        ticket.setChannel(null);
        module.getDatabaseHelper().modifyTicket(ticket);
        ticketChannel.delete().queue();
    }

    public List<Member> getTicketMembers(Ticket ticket) {
        ArrayList<Member> members = new ArrayList<>();
        for(PermissionOverride po : DirtBot.getJda().getTextChannelById(ticket.getChannel()).getMemberPermissionOverrides()) {
            if(!po.getMember().getUser().isBot()) members.add(po.getMember());
        }
        return members;
    }

    public boolean isTicketChannel(TextChannel channel) {
        if(channel.getParent().getId().equals(module.getConfig().supportCategoryID)) return true;
        if(channel.getParent().getId().equals(module.getConfig().ownerSupportCategoryID)) return true;
        for(List<String> serverInfo : DirtBot.getConfig().servers) {
            if(serverInfo.get(2).equals(channel.getParent().getId())) return true;
        }
        return false;
    }

    public List<User> getNotificationSubscribers(Ticket ticket, TicketModule module) {
        List<User> results = new ArrayList<>();

        if(ticket.getServer(true) != null) {
            Message message = DirtBot.getJda().getTextChannelById(module.getConfig().notificationChannelID).getMessageById(module.getTicketNotificationEmbeds().get(ticket.getServer(false).toLowerCase())).complete();
            for(MessageReaction reaction : message.getReactions()) {
                if(reaction.getReactionEmote().getName().equals("\uD83D\uDCEC")) {
                    for(User user : reaction.getUsers().complete()) {
                        if(!user.isBot()) results.add(user);
                    }
                }
            }
        }

        return results;
    }

}
