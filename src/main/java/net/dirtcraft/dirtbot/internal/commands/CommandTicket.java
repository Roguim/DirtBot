package net.dirtcraft.dirtbot.internal.commands;

import net.dirtcraft.dirtbot.data.Ticket;
import net.dirtcraft.dirtbot.modules.TicketModule;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class CommandTicket implements ICommand {

    private TicketModule module;

    public CommandTicket(TicketModule module) {
        this.module = module;
    }

    @Override
    public boolean validChannel(TextChannel channel) {
        return module.getTicketUtils().isTicketChannel(channel);
    }

    public Ticket getTicket(MessageReceivedEvent event) {
        return module.getDatabaseHelper().getTicket(event.getChannel().getId());
    }

    protected TicketModule getModule() { return module; }

}
