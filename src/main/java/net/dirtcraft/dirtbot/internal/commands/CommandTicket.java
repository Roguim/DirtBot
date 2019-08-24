package net.dirtcraft.dirtbot.internal.commands;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.data.Ticket;
import net.dirtcraft.dirtbot.modules.TicketModule;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class CommandTicket implements ICommand {

    private TicketModule module;

    public CommandTicket(TicketModule module) {
        this.module = module;
    }

    @Override
    public boolean validChannel(TextChannel channel) {
        return module.getTicketUtils().isTicketChannel(channel);
    }

    @Nullable
    public Ticket getTicket(MessageReceivedEvent event) {
        Optional<Ticket> optionalTicket = module.getDatabaseHelper().getTicket(event.getChannel().getId());
        if (optionalTicket.isPresent()) return optionalTicket.get();
        else DirtBot.pokeDevs(new NullPointerException("The Ticket channel ID \"" + event.getChannel().getId() + "\" is null!"));
        return null;
    }

    protected TicketModule getModule() { return module; }

}
