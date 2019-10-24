package net.dirtcraft.dirtbot.commands.tickets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.data.Ticket;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.CommandTicketStaff;
import net.dirtcraft.dirtbot.modules.TicketModule;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandClass(TicketModule.class)
public class SetLevel extends CommandTicketStaff {

    Ticket.Level level;

    public SetLevel(TicketModule module, Ticket.Level level) {
        super(module);
        this.level = level;
    }

    // TODO REWRITE THIS COMMAND
    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        Ticket ticket = getTicket(event);
        // Don't allow the user to set the ticket to the level it's already at
        if(ticket.getLevel() == level) {
            EmbedBuilder errorEmbed = getModule().getEmbedUtils().getErrorEmbed("Already at level " + level.toString().toUpperCase() + "!");
            event.getChannel().sendMessage(errorEmbed.build()).queue();
            return true;
        }
        // Populate embeds and complete execution
        String eventInfo = "**Old Level:** " + ticket.getLevel().toString().toUpperCase()+ "\n" +
                "**New Level:**  " + level.toString().toUpperCase();
        EmbedBuilder responseEmbed = getModule().getEmbedUtils().getEmptyEmbed()
                .addField("__Ticket Level Set__", eventInfo, false);
        ticket = getModule().getTicketUtils().setTicketLevel(ticket, level);
        getModule().getEmbedUtils().sendResponse(responseEmbed.build(), event.getTextChannel());
        getModule().getEmbedUtils().sendLog("Level Set", eventInfo, ticket, event.getMember());
        getModule().getEmbedUtils().updateTicketHeaderMessage(ticket);
        if(level == Ticket.Level.ADMIN && ticket.getServer(true) != null) {
            // Ping Subscribers
            DirtBot.getJda().getTextChannelById(getModule().getConfig().notificationChannelID).retrieveMessageById(getModule().getTicketNotificationEmbeds().get(ticket.getServer(false))).queue((message) -> {
                for(MessageReaction reaction : message.getReactions()) {
                    if(reaction.getReactionEmote().getName().equals("\uD83D\uDCEC")) {
                        reaction.retrieveUsers().queue((users) -> {
                            String pingMessage = "";
                            boolean hasUser = false;
                            for(User user : users) {
                                Member member = DirtBot.getJda().getGuildById(DirtBot.getConfig().serverID).getMember(user);
                                if (member == null) continue;
                                if (!member.getRoles().contains(DirtBot.getJda().getRoleById(DirtBot.getConfig().staffRoleID))) continue;
                                if(!user.isBot() && member.getRoles().contains(DirtBot.getJda().getRoleById(DirtBot.getConfig().adminRoleID))) {
                                    pingMessage += "<@" + user.getId() + ">, ";
                                    hasUser = true;
                                }
                            }
                            if(hasUser) {
                                event.getTextChannel().sendMessage(pingMessage.substring(0, pingMessage.length() - 2)).queue();
                            }
                        });
                    }
                }
            });
        } else if(level == Ticket.Level.URGENT && ticket.getServer(true) != null) {
        	for(Member user : DirtBot.getJda().getTextChannelById(ticket.getChannel()).getMembers()) {
        		if(user.getRoles().contains(DirtBot.getJda().getRoleById(DirtBot.getConfig().networkRoleID)) && !user.getUser().isBot()) {
        			event.getTextChannel().sendMessage(user.getUser().getAsMention()).queue();
        		}
        	}
        }
        //Add server code to Owner channels	
        if(level == Ticket.Level.OWNER && ticket.getServer(true) != null) {	
        	String ticketName = event.getChannel().getName();	
        	String server = ticket.getServer(true);	
        	String ticketId = String.valueOf(ticket.getId());
    		if(ticketName.contains(server)) {
    			ticketName = ticketName.replaceAll("-" + server, "");
    			ticketName = ticketName.replaceAll(ticketId, "");
    			ticketName += server + "-" + ticketId;
    		} else {
    			ticketName = ticketName.replaceAll(ticketId, "");
    			ticketName += server + "-" + ticketId;
    		}
    		event.getTextChannel().getManager().setName(ticketName).queue();
        }
        //Add server code to Owner channels	
        if(level == Ticket.Level.OWNER && ticket.getServer(true) != null) {	
        	String ticketName = event.getChannel().getName();	
        	String server = ticket.getServer(true);	
        	String ticketId = String.valueOf(ticket.getId());
    		if(ticketName.contains(server)) {
    			ticketName = ticketName.replaceAll("-" + server, "");
    			ticketName = ticketName.replaceAll(ticketId, "");
    			ticketName += server + "-" + ticketId;
    		} else {
    			ticketName = ticketName.replaceAll(ticketId, "");
    			ticketName += server + "-" + ticketId;
    		}
    		event.getTextChannel().getManager().setName(ticketName).queue();
        }
        return true;
    }
    
    @Override
    public List<String> aliases() {
        switch(level) {
            case NORMAL:
                return new ArrayList<>(Arrays.asList("normal", "staff", "mod", "moderator", "helper", "default"));
            case ADMIN:
                return new ArrayList<>(Arrays.asList("admin", "administrator", "admins"));
            case URGENT:
            	return new ArrayList<>(Arrays.asList("urgent", "network", "plshelpme"));
            case OWNER:
                return new ArrayList<>(Arrays.asList("owner", "julian", "ten", "owners"));
        }
        return null;
    }

    @Override
    public List<CommandArgument> args() {
        return null;
    }
}
