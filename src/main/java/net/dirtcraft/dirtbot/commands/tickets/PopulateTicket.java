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
public class PopulateTicket extends CommandTicketStaff {

    public PopulateTicket(TicketModule module) {
        super(module);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        boolean serverValid = false;
        for(List<String> serverInfo : getModule().getConfig().servers) {
            if(serverInfo.get(1).toLowerCase().equals(args.get(1).toLowerCase())) {
                serverValid = true;
            }
        }
        if(serverValid) {
            Ticket ticket = getTicket(event);
            String newName = String.join("-", args.subList(2, args.size()));
            event.getTextChannel().getManager().setName(newName).queue();
            ticket.setUsername(args.get(0));
            getModule().getTicketUtils().setTicketServer(ticket, args.get(1).toLowerCase());
            getModule().getDatabaseHelper().modifyTicket(ticket);
            getModule().getEmbedUtils().updateTicketHeaderMessage(ticket);

            String eventInfo = "**Username:** " + ticket.getUsername(false) + "\n" +
                    "**Server::** " + ticket.getServer(false).toUpperCase() + "\n" +
                    "**Ticket Name:** " + newName;

            getModule().getEmbedUtils().sendResponse(getModule().getEmbedUtils().getEmptyEmbed().addField("__Ticket Populated__", eventInfo, false).build(), event.getTextChannel());
            getModule().getEmbedUtils().sendLog("Ticket Populated", eventInfo, ticket, event.getMember());

            String pingMessage = "";
            for(User user : getModule().getTicketUtils().getNotificationSubscribers(ticket, getModule())) {
                pingMessage += "<@" + user.getId() + "> ";
            }
            event.getTextChannel().sendMessage(pingMessage).queue();
        }
        return true;
    }

    @Override
    public List<String> aliases() {
        return new ArrayList<>(Arrays.asList("populate"));
    }

    @Override
    public List<CommandArgument> args() {
        return new ArrayList<>(Arrays.asList(
           new CommandArgument("Username", "Minecraft Username", 0, 16),
                new CommandArgument("Server", "Server code for the server (See the server's gamechat channel name for code", 1, 0),
                new CommandArgument("Name", "Desired name for the ticket. Spaces will be hyphenated", 1, 0)
        ));
    }
}
