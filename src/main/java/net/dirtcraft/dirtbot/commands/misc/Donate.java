package net.dirtcraft.dirtbot.commands.misc;

import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.ICommand;
import net.dirtcraft.dirtbot.modules.CommandsModule;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

@CommandClass()
public class Donate implements ICommand {

    private final CommandsModule module;

    public Donate(CommandsModule module) {
        this.module = module;
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        MessageEmbed response = module.getEmbedUtils().getEmptyEmbed()
                .setTimestamp(null)
                .setFooter(null, null)
                .addField("__DirtCraft Support Shop__", "[Click me to open the **Pixelmon DirtStore**](https://store.pixelmon.gg/)\n" +
                                "[Click me to open the **Feed The Beast DirtStore**](https://store.dirtcraft.net/)",
                        false).build();
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
        return Arrays.asList("shop", "donate", "support", "pay");
    }

    @Override
    public List<CommandArgument> args() {
        return null;
    }
}
