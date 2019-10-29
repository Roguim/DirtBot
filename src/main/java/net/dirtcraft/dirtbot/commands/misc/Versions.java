package net.dirtcraft.dirtbot.commands.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.ICommand;
import net.dirtcraft.dirtbot.modules.CommandsModule;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandClass
public class Versions implements ICommand {

    private final CommandsModule module;

    public Versions(CommandsModule module) {
        this.module = module;
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> versions = new ArrayList<>();

        for (int i = 0; i < DirtBot.getConfig().servers.size(); i++) {
            List<String> server = DirtBot.getConfig().servers.get(i);
            names.add(server.get(0));
            versions.add(server.get(4));
        }
        
        ArrayList<String> nameVersions = new ArrayList<String>();
        for(int i = 0; i < names.size(); i++) {
        	String current;
        	current = names.get(i);
        	final int spaces = 20;
        	current = StringUtils.rightPad(current, (spaces-names.get(i).length())+names.get(i).length());
        	current += versions.get(i);
        	nameVersions.add(current);
        }
        
        MessageEmbed versionsEmbed = module.getEmbedUtils().getEmptyEmbed()
        		.addField("__ModPacks__                                    __Versions__", 
        				"```" + String.join("\n", nameVersions) + "```", false)
        		.build();
        event.getTextChannel().sendMessage(versionsEmbed).queue();
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
