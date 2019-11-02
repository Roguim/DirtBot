package net.dirtcraft.dirtbot.commands.misc;

import java.util.List;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.ICommand;
import net.dirtcraft.dirtbot.modules.CommandsModule;
import net.dirtcraft.dirtbot.modules.StaffCordModule;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandClass
public class Help implements ICommand {
	
	private CommandsModule module;
	
	public Help(CommandsModule module) {
		this.module = module;
	}

	@Override
	public boolean execute(MessageReceivedEvent event, List<String> args) {
		for(ICommand command : DirtBot.getCoreModule().getCommandRegistry().getCommands()) {
			EmbedBuilder embed = module.getEmbedUtils().getEmptyEmbed();
			String argsInfo = "None";
			if(command.args() != null) {
				argsInfo = "";
		        for(CommandArgument arg : command.args()) {
		            argsInfo += "**" + arg.getName() + "**: " + arg.getDescription() + "\n";
		        }
			}
			embed.addField("__!" + command.aliases().get(0) + "__", 
					"**Aliases:** " + String.join(", ", command.aliases() + "\n" +
			        "**Permission:** " + String.valueOf(command.hasPermission(event.getMember()))) + "\n" +
					"**Arguments:** " + argsInfo, false);
			event.getChannel().sendMessage(embed.build()).queue();
		}
		return true;
	}

	@Override
	public boolean hasPermission(Member member) {
		return true;
	}

	@Override
	public boolean validChannel(TextChannel channel) {
		if(channel.getGuild().getId().equals("568930779191574568")) return true;
		return false;
	}

	@Override
	public List<String> aliases() {
		return List.of("help");
	}

	@Override
	public List<CommandArgument> args() {
		return null;
	}

}
