package net.dirtcraft.dirtbot.commands.tickets;

import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.CommandTicketStaff;
import net.dirtcraft.dirtbot.modules.TicketModule;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandClass(TicketModule.class)
public class GetInfo extends CommandTicketStaff {


    public GetInfo(TicketModule module) {
        super(module);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        event.getTextChannel().sendMessage(getModule().getEmbedUtils().getTicketHeader(getTicket(event))).queue((message) -> message.pin().queue());
        return true;
    }

    @Override
    public List<String> aliases() {
        return new ArrayList<>(Arrays.asList("info"));
    }

    @Override
    public List<CommandArgument> args() {
        return null;
    }
}
