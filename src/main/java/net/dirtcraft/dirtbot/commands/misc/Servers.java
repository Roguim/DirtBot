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
public class Servers implements ICommand {

    CommandsModule module;

    public Servers(CommandsModule module) { this.module = module;}

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        String names = "";
        String ips = "";
        for(int i = 0; i < DirtBot.getConfig().servers.size(); i++) {
            List<String> server = DirtBot.getConfig().servers.get(i);
            names += server.get(0);
            ips += "`" + server.get(1).toUpperCase() + ".DIRTCRAFT.GG" + "`";
            if(!(i + 1 == DirtBot.getConfig().servers.size())) {
                names += "\n";
                ips += "\n";
            }
        }
        MessageEmbed response = module.getEmbedUtils().getEmptyEmbed()
                .setImage("https://cdn.discordapp.com/attachments/470444805931925518/545741732541628416/Max-Resolution.gif")
                .addField("__Modpacks__", names, true)
                .addField("__IPs__", ips, true)
                .addField("__Server Hub__", "`DIRTCRAFT.GG`", false)
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
        return Arrays.asList("ip", "ips", "server", "servers", "join", "logon", "login", "play");
    }

    @Override
    public List<CommandArgument> args() {
        return null;
    }
}
