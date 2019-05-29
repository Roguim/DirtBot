package net.dirtcraft.dirtbot.internal.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public interface ICommand {

    boolean execute(MessageReceivedEvent event, List<String> args);

    boolean hasPermission(Member member);

    boolean validChannel(TextChannel channel);

    List<String> aliases();

    List<CommandArgument> args();

}
