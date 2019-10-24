package net.dirtcraft.dirtbot.commands.tickets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.data.Ticket;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.CommandTicketStaff;
import net.dirtcraft.dirtbot.modules.TicketModule;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandClass(TicketModule.class)
public class AddMember extends CommandTicketStaff {

    public AddMember(TicketModule module) {
        super(module);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        Ticket ticket = getTicket(event);
        String eventInfo = "";
        for(Member member : event.getMessage().getMentionedMembers()) {
            event.getTextChannel().getManager().putPermissionOverride(member, EnumSet.of(Permission.MESSAGE_READ), null).queue((po) -> { getModule().getEmbedUtils().updateTicketHeaderMessage(ticket); });
            eventInfo += "<@" + member.getUser().getId() + ">, ";
        }
        // Get rid of the extra ", " at the end
        eventInfo = eventInfo.substring(0, eventInfo.length() - 2);
        EmbedBuilder responseEmbed = DirtBot.getModuleRegistry().getModule(TicketModule.class).getEmbedUtils().getEmptyEmbed()
                .addField("__Ticket Member(s) Added__", eventInfo, false);
        getModule().getEmbedUtils().sendResponse(responseEmbed.build(), event.getTextChannel());
        getModule().getEmbedUtils().sendLog("Member(s) Added", eventInfo, ticket, event.getMember());
        return true;
    }

    @Override
    public List<String> aliases() {
        return new ArrayList<>(Collections.singletonList("add"));
    }

    @Override
    public List<CommandArgument> args() {
        return new ArrayList<>(Collections.singletonList(new CommandArgument("Discord Tag(s)", "Mention(s) for the user(s) you wish to add to the ticket", 1, 0)));
    }

}
