package net.dirtcraft.dirtbot.commands.misc;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.ICommand;
import net.dirtcraft.dirtbot.modules.CommandsModule;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandClass()
public class NotMyDepartment implements ICommand {

    private final CommandsModule module;

    public NotMyDepartment(CommandsModule module) {
        this.module = module;
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        EmbedBuilder embedResponse = module.getEmbedUtils().getEmptyEmbed()
                .addField("__Not My Department__", "This is not my department. Please hold while some unpaid intern is forced to deal with you.", false);
        event.getTextChannel().sendMessage(embedResponse.build()).queue();
        return true;
    }

    @Override
    public boolean hasPermission(Member member) {
        if (member.getRoles().contains(DirtBot.getJda().getRoleById(DirtBot.getConfig().staffRoleID))) return true;
        else return false;
    }

    @Override
    public boolean validChannel(TextChannel channel) {
        return true;
    }

    @Override
    public List<String> aliases() {
        return new ArrayList<>(Arrays.asList("notmydepartment", "i'monbreak", "imonbreak", "i'monlunch", "imonlunch"));
    }

    @Override
    public List<CommandArgument> args() {
        return null;
    }
}
