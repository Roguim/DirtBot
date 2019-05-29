package net.dirtcraft.dirtbot.commands.tickets;

import net.dirtcraft.dirtbot.data.Ticket;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.CommandTicketStaff;
import net.dirtcraft.dirtbot.modules.TicketModule;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandClass(TicketModule.class)
public class RemoveMember extends CommandTicketStaff {

    public RemoveMember(TicketModule module) {
        super(module);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        Ticket ticket = getTicket(event);
        String eventInfo = "";
        for(Member member : event.getMessage().getMentionedMembers()) {
            event.getTextChannel().getManager().removePermissionOverride(member).queue((po) -> { getModule().getEmbedUtils().updateTicketHeaderMessage(ticket); });
            eventInfo += "<@" + member.getUser().getId() + ">, ";
        }
        // Get rid of the extra ", " at the end
        eventInfo = eventInfo.substring(0, eventInfo.length() - 2);
        EmbedBuilder responseEmbed = getModule().getEmbedUtils().getEmptyEmbed()
                .addField("__Ticket Member(s) Removed__", eventInfo, false);
        getModule().getEmbedUtils().sendResponse(responseEmbed.build(), event.getTextChannel());
        getModule().getEmbedUtils().sendLog("Member(s) Removed", eventInfo, ticket, event.getMember());
        return true;
    }

    @Override
    public List<String> aliases() {
        return new ArrayList<>(Arrays.asList("remove"));
    }

    @Override
    public List<CommandArgument> args() {
        return new ArrayList<>(Arrays.asList(new CommandArgument("Discord Tag(s)", "Mention(s) for the user(s) you wish to remove from the ticket", 1, 0)));
    }

}
