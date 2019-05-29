package net.dirtcraft.dirtbot.commands.tickets;

import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.CommandTicketStaff;
import net.dirtcraft.dirtbot.modules.TicketModule;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandClass(TicketModule.class)
public class CloseTimer extends CommandTicketStaff {

    public CloseTimer(TicketModule module) {
        super(module);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        String reason = String.join(" ", args);

        EmbedBuilder responseEmbed = getModule().getEmbedUtils().getEmptyEmbed()
                .addField("__Ticket Close Timer Set__", "This ticket will be automatically closed in **24 hours.**\n" +
                        "To **close now**, please select \u2705" + "\n" +
                        "To **cancel closure**, please select \u274C", false)
                .addField("__Reason__", reason, false);

        MessageEmbed reviewEmbed = getModule().getEmbedUtils().getReviewEmbed();

        event.getTextChannel().sendMessage(responseEmbed.build()).queue(message ->
                event.getTextChannel().sendMessage(reviewEmbed).queue((msg) -> {
                    msg.addReaction("\u2705").queue();
                    msg.addReaction("\u274C").queue();
                    getModule().getDatabaseHelper().addConfirmationMessage(getTicket(event).getId(), msg.getId(), reason, LocalDateTime.now().plusDays(1).toString());
                })
        );

        event.getTextChannel().getManager().setName("pending-review-" + getTicket(event).getId()).queue();
        event.getTextChannel().getManager().setTopic("This ticket has now pending a review").queue();

        return true;
    }

    @Override
    public List<String> aliases() {
        return new ArrayList<>(Arrays.asList("timerclose", "timedclose", "timeclose", "autoclose", "abandoned", "abandonedclose", "closetime", "closetimer"));
    }

    @Override
    public List<CommandArgument> args() {
        return new ArrayList<>(Arrays.asList(new CommandArgument("Reason", "Reason for closing the ticket", 1, 1024)));
    }
}
