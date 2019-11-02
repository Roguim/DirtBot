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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CommandClass
public class Help implements ICommand {
	
	private CommandsModule module;
	
	public Help(CommandsModule module) {
		this.module = module;
	}

	@Override
	public boolean execute(MessageReceivedEvent event, List<String> args) {
		List<String> commands = DirtBot.getCoreModule().getCommandRegistry().getCommands()
				.stream()
				.filter(command -> command.hasPermission(event.getMember()))
				.map(command -> "**!" + command.aliases().get(0) + "**")
				.sorted(Comparator.comparing(command -> command))
				.collect(Collectors.toList());

		EmbedBuilder embed = module.getEmbedUtils().getEmptyEmbed();
		embed.setDescription(String.join("\n", commands));
		event.getChannel().sendMessage(embed.build()).queue();
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
		return Collections.singletonList("help");
	}

	@Override
	public List<CommandArgument> args() {
		return null;
	}

}
