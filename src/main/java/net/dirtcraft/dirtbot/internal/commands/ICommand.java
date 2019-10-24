package net.dirtcraft.dirtbot.internal.commands;

import java.util.List;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface ICommand {

    boolean execute(MessageReceivedEvent event, List<String> args);

    boolean hasPermission(Member member);

    boolean validChannel(TextChannel channel);

    List<String> aliases();

    List<CommandArgument> args();

}
