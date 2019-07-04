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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandClass(CommandsModule.class)
public class Versions implements ICommand {

    CommandsModule module;

    public Versions(CommandsModule module) { this.module = module;}

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> versions = new ArrayList<>();

        for(int i = 0; i < DirtBot.getConfig().servers.size(); i++) {
            List<String> server = DirtBot.getConfig().servers.get(i);
            names.add(server.get(0));
            versions.add(server.get(4));
        }

        MessageEmbed response = module.getEmbedUtils().getEmptyEmbed()
                .addField("__ModPacks__", String.join("\n", names), true)
                .addField("__Versions__", String.join("\n", versions), true)
                .build();
        event.getTextChannel().sendMessage(response).queue();
        return true;
    }

    private int sortType(String server) {
        return server.toLowerCase().contains("pixel") ? 0 : 1;
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
