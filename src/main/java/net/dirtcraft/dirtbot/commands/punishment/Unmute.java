package net.dirtcraft.dirtbot.commands.punishment;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.ICommand;
import net.dirtcraft.dirtbot.modules.PunishmentModule;
import net.dirtcraft.dirtbot.modules.PunishmentModule.PunishmentLogType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandClass
public class Unmute implements ICommand {
	
	private PunishmentModule module;
	
	public Unmute(PunishmentModule module) {
		this.module = module;
	}

	@Override
	public boolean execute(MessageReceivedEvent event, List<String> args) {
		Member punisher = event.getMember();
		Member punished;
		
		if(event.getMessage().getMentionedUsers().size() != 1) {
			module.getEmbedUtils().sendError("Please mention 1 user!", event.getTextChannel());
			event.getMessage().delete().queue();
			return false;
		} else {
			punished = event.getGuild().getMember(event.getMessage().getMentionedUsers().get(0));
		}
		
		if(!punished.getRoles().contains(event.getJDA().getRoleById(module.getConfig().mutedRoleID))) {
			module.getEmbedUtils().sendError("User is not muted!", event.getTextChannel());
			event.getMessage().delete().queue();
			return false;
		}
		 
		event.getGuild().removeRoleFromMember(punished, DirtBot.getJda().getRoleById(module.getConfig().mutedRoleID)).queue();
		
		module.getEmbedUtils().sendPunishLog(punisher.getId(), punished.getId(), PunishmentLogType.UNMUTE, null, null);
		
		event.getMessage().delete().queue();
		event.getChannel().sendMessage(module.getEmbedUtils().getEmptyEmbed().addField("__Command Success__", "Command '!unmute' Successfully run!", false).build()).queue((message) -> {
			message.delete().queueAfter(10, TimeUnit.SECONDS);
		});
		
		return false;
	}

	@Override
	public boolean hasPermission(Member member) {
		if(member.getRoles().contains(DirtBot.getJda().getRoleById(DirtBot.getConfig().adminRoleID))) return true;
		return false;
	}

	@Override
	public boolean validChannel(TextChannel channel) {
		return true;
	}

	@Override
	public List<String> aliases() {
		return Collections.singletonList("unmute");
	}

	@Override
	public List<CommandArgument> args() {
		return Collections.singletonList(new CommandArgument("Mention User", "Mention the user to unmute", 1, 0));
	}

}
