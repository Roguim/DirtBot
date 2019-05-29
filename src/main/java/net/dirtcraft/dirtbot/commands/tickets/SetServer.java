package net.dirtcraft.dirtbot.commands.tickets;

import net.dirtcraft.dirtbot.data.Ticket;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.CommandTicketStaff;
import net.dirtcraft.dirtbot.modules.TicketModule;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandClass(TicketModule.class)
public class SetServer extends CommandTicketStaff {

    public SetServer(TicketModule module) {
        super(module);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        boolean serverValid = false;
        for(List<String> serverInfo : getModule().getConfig().servers) {
            if(serverInfo.get(1).toLowerCase().equals(args.get(0).toLowerCase())) {
                serverValid = true;
            }
        }
        if(serverValid) {
            Ticket ticket = getModule().getDatabaseHelper().getTicket(event.getTextChannel().getId());
            String eventInfo = "**Old Server:** " + ticket.getServer(false).toUpperCase() + "\n" + "**New Server:** " + args.get(0).toUpperCase();
            getModule().getEmbedUtils().sendResponse(getModule().getEmbedUtils().getEmptyEmbed().addField("__Ticket Server Set__", eventInfo, false).build(), event.getTextChannel());
            getModule().getEmbedUtils().sendLog("Server Set", eventInfo, ticket, event.getMember());
            getModule().getTicketUtils().setTicketServer(ticket, args.get(0).toLowerCase());
            getModule().getDatabaseHelper().modifyTicket(ticket);
            getModule().getEmbedUtils().updateTicketHeaderMessage(ticket);
            String pingMessage = "";
            for(User user : getModule().getTicketUtils().getNotificationSubscribers(ticket, getModule())) {
                pingMessage += "<@" + user.getId() + "> ";
            }
            event.getTextChannel().sendMessage(pingMessage).queue();
        } else {
            getModule().getEmbedUtils().sendResponse(getModule().getEmbedUtils().getErrorEmbed("Invalid Server Code!").build(), event.getTextChannel());
        }
        return true;
    }

    @Override
    public List<String> aliases() {
        return new ArrayList<>(Arrays.asList("server"));
    }

    @Override
    public List<CommandArgument> args() {
        return new ArrayList<>(Arrays.asList(new CommandArgument("Server", "Server code for the server (See the server's gamechat channel name for code, use \"reset\" to clear)", 1, 0)));
    }
}
