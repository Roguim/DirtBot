package net.dirtcraft.dirtbot.commands.tickets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.dirtcraft.dirtbot.data.Ticket;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.CommandTicketStaff;
import net.dirtcraft.dirtbot.modules.TicketModule;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandClass(TicketModule.class)
public class SetUsername extends CommandTicketStaff {

    public SetUsername(TicketModule module) {
        super(module);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        // Populate embeds and complete execution
        Ticket ticket = getTicket(event);
        String eventInfo = "**Old Username:** " + ticket.getUsername(false) + "\n" +
                "**New Username:** " + args.get(0);
        EmbedBuilder responseEmbed = getModule().getEmbedUtils().getEmptyEmbed()
                .addField("__Ticket Username Set__", eventInfo, false);
        ticket.setUsername(args.get(0));
        getModule().getDatabaseHelper().modifyTicket(ticket);
        getModule().getEmbedUtils().sendResponse(responseEmbed.build(), event.getTextChannel());
        getModule().getEmbedUtils().sendLog("Username Set", eventInfo, ticket, event.getMember());
        getModule().getEmbedUtils().updateTicketHeaderMessage(ticket);
        return true;
    }

    @Override
    public List<String> aliases() {
        return new ArrayList<>(Arrays.asList("username"));
    }

    @Override
    public List<CommandArgument> args() {
        return new ArrayList<>(Collections.singletonList(new CommandArgument("Username", "Minecraft Username (\"reset\" to clear)", 0, 16)));
    }

}
