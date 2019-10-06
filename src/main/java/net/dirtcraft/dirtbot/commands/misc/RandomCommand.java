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
public class RandomCommand implements ICommand {

    private final CommandsModule module;

    public RandomCommand(CommandsModule module) {
        this.module = module;
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        int guildSize = event.getGuild().getMembers().size();
        java.util.Random random = new java.util.Random();
        int randMember = random.nextInt(guildSize);

        EmbedBuilder responseEmbed = module.getEmbedUtils().getEmptyEmbed()
                .addField("__The Chosen One__",
                        "**" + event.getGuild().getMembers().get(randMember).getUser().getName() + "**#`" + event.getGuild().getMembers().get(randMember).getUser().getDiscriminator() + "` has been selected as the chosen one!",
                        false);

        module.getEmbedUtils().sendResponse(responseEmbed.build(), event.getTextChannel());
        return true;
    }

    @Override
    public List<String> aliases() {
        return new ArrayList<>(Arrays.asList("random"));
    }

    @Override
    public List<CommandArgument> args() {
        return null;
    }

    @Override
    public boolean hasPermission(Member member) {
        return member.getRoles().contains(DirtBot.getJda().getRoleById(DirtBot.getConfig().staffRoleID));
    }

    @Override
    public boolean validChannel(TextChannel channel) {
        return true;
    }
}