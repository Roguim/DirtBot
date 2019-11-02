package net.dirtcraft.dirtbot.commands.staff;

import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.ICommand;
import net.dirtcraft.dirtbot.modules.StaffCordModule;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

@CommandClass
public class UpdateTicketInfo implements ICommand {
	
	private StaffCordModule module;
	public UpdateTicketInfo(StaffCordModule module) {
		this.module = module;
	}

	@Override
	public boolean execute(MessageReceivedEvent event, List<String> args) {
		module.initializeTicketInfo();
		return true;
	}

	@Override
	public boolean hasPermission(Member member) {
		return true;
	}

	@Override
	public boolean validChannel(TextChannel channel) {
		if(!channel.getGuild().getId().equals(module.getConfig().staffCordGuildID)) return false;
		if(!channel.getId().equals(module.getConfig().ticketInfoChannelID)) return false;
		return true;
	}

	@Override
	public List<String> aliases() {
		return Arrays.asList("update", "updoot", "reload", "updateyoudumbbot");
	}

	@Override
	public List<CommandArgument> args() {
		return null;
	}

}
