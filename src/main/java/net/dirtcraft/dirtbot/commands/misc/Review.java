package net.dirtcraft.dirtbot.commands.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.ICommand;
import net.dirtcraft.dirtbot.modules.CommandsModule;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandClass()
public class Review implements ICommand {

    private final CommandsModule module;

    public Review(CommandsModule module) {
        this.module = module;
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        event.getTextChannel().sendMessage(module.getEmbedUtils().getReviewEmbed()).queue();
        return true;
    }

    @Override
    public boolean hasPermission(Member member) {
        return true;
    }

    @Override
    public boolean validChannel(TextChannel channel) {
        return true;
    }

    @Override
    public List<String> aliases() {
        return new ArrayList<>(Arrays.asList("review"));
    }

    @Override
    public List<CommandArgument> args() {
        return null;
    }
}
