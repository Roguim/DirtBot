package net.dirtcraft.dirtbot.commands.misc;

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

@CommandClass(CommandsModule.class)
public class ShinyCSharp implements ICommand {

    private CommandsModule module;

    public ShinyCSharp(CommandsModule module) { this.module = module; }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        EmbedBuilder responseEmbed = module.getEmbedUtils().getEmptyEmbed()
                .addField("__C# vs Java__", "Some might say the Java is the superior language. However, C# has something very special that makes it undoubtedly better.\n\n***Structs***\n\nEvery morning when I wake up, I think of structs. Every night when I go to sleep, I think of structs. They are perfect. Classes purely for data. No matter that you can do the exact same thing in Java with a normal class, that's not important. Structs are love, structs are life. If you do not worship structs, I will kill you. This is why C# is superior to Java. If you say otherwise, you clearly do not know da wae of da struct. Have a nice day, as long as you love structs. If you don't love structs, then you don't deserve a nice day.\n\nP.S. Monodict with predicates will never be beaten.\n\n. P.S. Extension methods are love, extension methods are life.", false);
        event.getTextChannel().sendMessage(responseEmbed.build()).queue();
        return true;
    }

    @Override
    public boolean hasPermission(Member member) {
        if(member.getUser().getId().equals("248056002274918400")) return true;
        else return false;
    }

    @Override
    public boolean validChannel(TextChannel channel) {
        return true;
    }

    @Override
    public List<String> aliases() {
        return new ArrayList<>(Arrays.asList("c#", "shinyc#", "shiny", "csharp", "struct", "structs"));
    }

    @Override
    public List<CommandArgument> args() {
        return null;
    }
}
