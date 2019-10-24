package net.dirtcraft.dirtbot.commands.misc;

import java.util.Arrays;
import java.util.List;

import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.ICommand;
import net.dirtcraft.dirtbot.modules.CommandsModule;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandClass()
public class Unstuck implements ICommand {

    private final CommandsModule module;

    public Unstuck(CommandsModule module) {
        this.module = module;
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        MessageEmbed response = module.getEmbedUtils().getEmptyEmbed()
                .addField("__Unstuck__", "[**Click me to purchase a free unstuck!**](https://store.dirtcraft.net/category/1280580)\n" +
                       "It may take up to **15 minutes** for your unstuck to apply." , false).build();
        event.getTextChannel().sendMessage(response).queue();
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
        return Arrays.asList("unstuck");
    }

    @Override
    public List<CommandArgument> args() {
        return null;
    }
}
