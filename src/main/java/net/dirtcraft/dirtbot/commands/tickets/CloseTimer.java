package net.dirtcraft.dirtbot.commands.tickets;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.text.WordUtils;

import com.vdurmont.emoji.EmojiParser;

import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.CommandTicketStaff;
import net.dirtcraft.dirtbot.modules.TicketModule;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandClass(TicketModule.class)
public class CloseTimer extends CommandTicketStaff {

    public CloseTimer(TicketModule module) {
        super(module);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        String reason = EmojiParser.parseToAliases(String.join(" ", args), EmojiParser.FitzpatrickAction.REMOVE);

        EmbedBuilder responseEmbed = getModule().getEmbedUtils().getEmptyEmbed()
                .addField("__Ticket Close Timer Set__", "This ticket will be automatically closed in **24 hours**\n" +
                        "To **close now**, please select \u2705" + "\n" +
                        "To **cancel closure**, please select \u274C", false)
                .addField("__Reason__", "```" + WordUtils.capitalizeFully(reason) + "```", false);

        MessageEmbed reviewEmbed = getModule().getEmbedUtils().getReviewEmbed();

        event.getTextChannel().sendMessage(responseEmbed.build()).queue(message ->
                event.getTextChannel().sendMessage(reviewEmbed).queue((msg) -> {
                    msg.addReaction("\u2705").queue();
                    msg.addReaction("\u274C").queue();
                    getModule().getDatabaseHelper().addConfirmationMessage(getTicket(event).getId(), msg.getId(), reason, LocalDateTime.now().plusDays(1).toString());
                })
        );

        event.getTextChannel().getManager().setName("pending-review-" + getTicket(event).getId()).queueAfter(1, TimeUnit.SECONDS);
        event.getTextChannel().getManager().setTopic("This ticket has now pending a review").queueAfter(1, TimeUnit.SECONDS);

        return true;
    }

    @Override
    public List<String> aliases() {
        return new ArrayList<>(Arrays.asList("timerclose", "timedclose", "timeclose", "autoclose", "abandoned", "abandonedclose", "closetime", "closetimer"));
    }

    @Override
    public List<CommandArgument> args() {
        return new ArrayList<>(Collections.singletonList(new CommandArgument("Reason", "Reason for closing the ticket", 1, 1024)));
    }
}
