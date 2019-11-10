package net.dirtcraft.dirtbot.commands.misc;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.ICommand;
import net.dirtcraft.dirtbot.modules.CommandsModule;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@CommandClass
public class Stop implements ICommand {

    private final CommandsModule module;

    public Stop(CommandsModule module) {
        this.module = module;
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        EmbedBuilder embed = module.getEmbedUtils().getEmptyEmbed();
        embed.setColor(10223635);
        embed.setDescription("**Shutting Down...**");
        event.getChannel().sendMessage(embed.build()).queue();
        System.exit(0);
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
        return Arrays.asList("stop", "restart");
    }

    @Override
    public List<CommandArgument> args() {
        return Collections.emptyList();
    }
}
