package net.dirtcraft.dirtbot.commands.tickets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.CommandTicketStaff;
import net.dirtcraft.dirtbot.modules.TicketModule;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
        return new ArrayList<>(Collections.singletonList("info"));
    }

    @Override
    public List<CommandArgument> args() {
        return null;
    }
}
