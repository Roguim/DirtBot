package net.dirtcraft.dirtbot.commands.misc;

import net.dirtcraft.dirtbot.DirtBot;
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

@CommandClass(CommandsModule.class)
public class Versions implements ICommand {

    CommandsModule module;

    public Versions(CommandsModule module) { this.module = module;}

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        String names = "";
        String versions = "";
        for(int i = 0; i < DirtBot.getConfig().servers.size(); i++) {
            List<String> server = DirtBot.getConfig().servers.get(i);
            names += server.get(0);
            versions += "`" + server.get(4) + "`";
            if(!(i + 1 == DirtBot.getConfig().servers.size())) {
                names += "\n";
                versions += "\n";
            }
        }
        MessageEmbed response = module.getEmbedUtils().getEmptyEmbed()
                .addField("__Modpacks__", names, true)
                .addField("__Versions__", versions, true)
                .build();
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
        return Arrays.asList("version", "versions", "modpackversions", "packversions");
    }

    @Override
    public List<CommandArgument> args() {
        return null;
    }
}
