package net.dirtcraft.dirtbot.commands.appeals;

import net.dirtcraft.dirtbot.internal.commands.CommandAppealStaff;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.modules.AppealModule;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandClass(AppealModule.class)
public class AcceptAppeal extends CommandAppealStaff {

    public AcceptAppeal(AppealModule module) {
        super(module);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        String message = String.join(" ", args);
        MessageEmbed responseEmbed = getModule().getEmbedUtils().getExternalEmbed()
                .addField("__Appeal Accepted__", "Your appeal has been accepted by <@" + event.getMember().getUser().getId() + "> with the following message:\n + '''" + message + "'''", false)
                .build();
        event.getTextChannel().getIterableHistory().queue((iterableHistory) -> {
            String appealerName = "";
            for(Member member : getModule().getAppealUtils().getAppealMembers(event.getTextChannel())) {
                member.getUser().openPrivateChannel().queue((privateChannel -> privateChannel.sendMessage(responseEmbed).queue()));
                appealerName = member.getEffectiveName();
            }
            getModule().archiveAppeal(iterableHistory, appealerName);
            getModule().getEmbedUtils().sendLog("Accepted", "An appeal has been accepted with the following message:\n```" + message + "```", event.getTextChannel(), event.getMember());
            event.getTextChannel().delete().queue();
        });
        return true;
    }

    @Override
    public List<String> aliases() {
        return new ArrayList<>(Collections.singletonList("accept"));
    }

    @Override
    public List<CommandArgument> args() {
        return new ArrayList<>(Collections.singletonList(new CommandArgument("Message", "The message to be sent to the appealer.", 1, 1024)));
    }
}
