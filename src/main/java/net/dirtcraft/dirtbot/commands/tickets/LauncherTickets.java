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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandClass(TicketModule.class)
public class LauncherTickets extends CommandTicketStaff {


    public LauncherTickets(TicketModule module) {
		super(module);
	}

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        Ticket ticket = getTicket(event);
        String eventInfo = "**Launcher Ticket Created**";
        getModule().getEmbedUtils().sendLog("Ticket Modified", eventInfo, ticket, event.getMember());
        String pingMessage = "";
        Message message = DirtBot.getJda().getTextChannelById(getModule().getConfig().notificationChannelID).retrieveMessageById(getModule().getTicketNotificationEmbeds().get("launcher")).complete();
        for(MessageReaction reaction : message.getReactions()) {
            if(reaction.getReactionEmote().getName().equals("\uD83D\uDCEC")) {
                for(User user : reaction.retrieveUsers().complete()) {
                    if(!user.isBot()) pingMessage += "<@" + user.getId() + "> ";
                }
            }
        }
        TextChannel channel = DirtBot.getJda().getTextChannelById(ticket.getChannel());
        channel.getManager().setParent(DirtBot.getJda().getCategoryById(getModule().getConfig().launcherSupportCategoryID)).queue();
        event.getTextChannel().sendMessage(pingMessage).queue();
        return true;
    }

    @Override
    public List<String> aliases() {
        return new ArrayList<>(Arrays.asList("launcherticket", "lt", "dev"));
    }

    @Override
    public List<CommandArgument> args() {
        return null;
    }
}
