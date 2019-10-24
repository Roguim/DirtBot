package net.dirtcraft.dirtbot.commands.tickets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dirtcraft.dirtbot.data.Ticket;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.CommandTicketStaff;
import net.dirtcraft.dirtbot.modules.TicketModule;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandClass(TicketModule.class)
public class SilentClose extends CommandTicketStaff {
    public SilentClose(TicketModule module) {
        super(module);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        Ticket ticket = getTicket(event);
        getModule().getEmbedUtils().sendLog("Silently Closed", "This ticket has been closed without sending a review message or reason to the user.", ticket, event.getMember());
        getModule().getTicketUtils().closeTicket(ticket, "", false);
        return true;
    }

    @Override
    public List<String> aliases() {
        return new ArrayList<>(Arrays.asList("sclose", "silentclose"));
    }

    @Override
    public List<CommandArgument> args() {
        return null;
    }
}
