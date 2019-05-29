package net.dirtcraft.dirtbot.commands.tickets;

import net.dirtcraft.dirtbot.data.Ticket;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.CommandTicketStaff;
import net.dirtcraft.dirtbot.modules.TicketModule;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandClass(TicketModule.class)
public class SetTicketName extends CommandTicketStaff {
    public SetTicketName(TicketModule module) {
        super(module);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        Ticket ticket = getTicket(event);
        String oldName = event.getChannel().getName();
        String newName = String.join("-", args) + "-" + ticket.getId();
        String eventInfo = "**Old Name:** " + oldName + "\n" +
                "**New Name:** " + newName;
        EmbedBuilder responseEmbed = getModule().getEmbedUtils().getEmptyEmbed()
                .addField("__Ticket Name Set__", eventInfo, false);
        event.getTextChannel().getManager().setName(newName).complete();
        getModule().getEmbedUtils().sendResponse(responseEmbed.build(), event.getTextChannel());
        getModule().getEmbedUtils().sendLog("Name Set", eventInfo, getTicket(event), event.getMember());
        getModule().getEmbedUtils().updateTicketHeaderMessage(ticket);
        return true;
    }

    @Override
    public List<String> aliases() {
        return new ArrayList<>(Arrays.asList("rename"));
    }

    @Override
    public List<CommandArgument> args() {
        return new ArrayList<>(Arrays.asList(new CommandArgument("Name", "Desired name for the ticket. Spaces will be hyphenated", 1, 0)));
    }
}
