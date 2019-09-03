package net.dirtcraft.dirtbot.commands.misc;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.ICommand;
import net.dirtcraft.dirtbot.modules.CommandsModule;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandClass(CommandsModule.class)
public class Purge implements ICommand {

    private final CommandsModule module;

    public Purge(CommandsModule module) {
        this.module = module;
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        for (Message message : event.getTextChannel().getHistoryBefore(event.getMessageId(), Integer.parseInt(args.get(0))).complete().getRetrievedHistory()) {
            message.delete().queue();
        }
        return true;
    }

    @Override
    public boolean hasPermission(Member member) {
        return member.getRoles().contains(DirtBot.getJda().getRoleById(DirtBot.getConfig().ownerRoleID));
    }

    @Override
    public boolean validChannel(TextChannel channel) {
        return true;
    }

    @Override
    public List<String> aliases() {
        return new ArrayList<>(Arrays.asList("purge", "hidetheevidence", "begone"));
    }

    @Override
    public List<CommandArgument> args() {
        return new ArrayList<>(Arrays.asList(new CommandArgument("Messages", "The number of messages to be purged.", 1, 2)));
    }
}