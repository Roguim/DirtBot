package net.dirtcraft.dirtbot.commands.punishment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateUtils;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.ICommand;
import net.dirtcraft.dirtbot.modules.PunishmentModule;
import net.dirtcraft.dirtbot.modules.PunishmentModule.PunishmentLogType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandClass
public class Mute implements ICommand {
	
	private PunishmentModule module;
	
	public Mute(PunishmentModule module) {
		this.module = module;
	}

	@Override
	public boolean execute(MessageReceivedEvent event, List<String> args) {
		//Define necessary variables
		User punisher;
		User punished;
		String lengthRaw;
		String lengthReadable;
		String reason;
		
		//Set punisher
		punisher = event.getAuthor();
		
		//Try to get the punished user
		if(event.getMessage().getMentionedUsers().size() != 1) {
			module.getEmbedUtils().sendError("Please mention 1 user!", event.getTextChannel());
			event.getMessage().delete().queue();
			return false;
		} else {
			punished = event.getMessage().getMentionedUsers().get(0);
		}
		
		//Try to set the length
		lengthRaw = args.get(1);
		lengthReadable = getLengthReadable(lengthRaw);
		if(lengthReadable == null) {
			module.getEmbedUtils().sendError("Invalid time format!", event.getTextChannel());
			event.getMessage().delete().queue();
			return false;
		}
		
		
		//Set the reason
		reason = "";
		for(int i = 2; i < args.size(); i++) {
			reason += args.get(i) + " ";
		}
		reason = reason.stripTrailing();
		
		//Try to add the muted role
		if(event.getGuild().getMember(punished).getRoles().contains(event.getJDA().getRoleById(module.getConfig().mutedRoleID))) {
			module.getEmbedUtils().sendError("User is already muted!", event.getTextChannel());
			event.getMessage().delete().queue();
			return false;
		}
		event.getGuild().addRoleToMember(event.getGuild().getMember(punished), DirtBot.getJda().getRoleById(module.getConfig().mutedRoleID)).queue();
		
		//Send punishment log
		module.getEmbedUtils().sendPunishLog(punisher.getId(), punished.getId(), PunishmentLogType.MUTE, lengthReadable, reason);
		
		//Delete command from chat and send success message
		event.getMessage().delete().queue();
		event.getChannel().sendMessage(module.getEmbedUtils().getEmptyEmbed().addField("__Command Success__", "Command '!mute' Successfully run!", false).build()).queue((message) -> {
			message.delete().queueAfter(10, TimeUnit.SECONDS);
		});
		
		//Add mute to database
		try {
			Connection con = DriverManager.getConnection(module.getConfig().databaseUrl, module.getConfig().databaseUser, module.getConfig().databasePassword);
			
			PreparedStatement statement = con.prepareStatement("INSERT INTO mutes (discordid, unmutetime) VALUES (?, ?)");
			statement.setString(1, punished.getId());
			statement.setObject(2, getTimeFromLength(lengthRaw));
			statement.execute();

			con.close();
		} catch (SQLException e) {
			DirtBot.pokeDevs(e);
		}
		
		return false;
	}
	
	public String getLengthReadable(String lengthRaw) {
		String lengthMinusNums = lengthRaw.replaceAll("[0-9]", "");
		switch(lengthMinusNums) {
		case "s":
			return lengthRaw.replace("s", "") + " Seconds";
		case "m":
			return lengthRaw.replace("m", "") + " Minutes";
		case "h":
			return lengthRaw.replace("h", "") + " Hours";
		case "d":
			return lengthRaw.replace("d", "") + " Days";
		case "w":
			return lengthRaw.replace("w", "") + " Weeks";
		case "mo":
			return lengthRaw.replace("mo", "") + " Months";
		case "y":
			return lengthRaw.replace("y", "") + " Years";
		default:
			return null;
		}
	}
	
	public String getTimeFromLength(String lengthRaw) {
		String lengthInts = lengthRaw.replaceAll("[a-z]", "");
		String lengthMinusNums = lengthRaw.replaceAll("[0-9]", "");
		
		java.util.Date dt = new java.util.Date();
		
		switch(lengthMinusNums) {
		case "s":
			dt = DateUtils.addSeconds(dt, Integer.valueOf(lengthInts));
			break;
		case "m":
			dt = DateUtils.addMinutes(dt, Integer.valueOf(lengthInts));
			break;
		case "h":
			dt = DateUtils.addHours(dt, Integer.valueOf(lengthInts));
			break;
		case "d":
			dt = DateUtils.addDays(dt, Integer.valueOf(lengthInts));
			break;
		case "w":
			dt = DateUtils.addWeeks(dt, Integer.valueOf(lengthInts));
			break;
		case "mo":
			dt = DateUtils.addMonths(dt, Integer.valueOf(lengthInts));
			break;
		case "y":
			dt = DateUtils.addYears(dt, Integer.valueOf(lengthInts));
			break;
		}
		
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return sdf.format(dt);
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
		return List.of("mute");
	}

	@Override
	public List<CommandArgument> args() {
		return List.of(new CommandArgument("Mention User", "Mention the user you want to mute.", 1, 0), 
				       new CommandArgument("Length", "How long you want to mute for. Ex: 1s, 1m, 1d, 1w, 1mo, 1y.", 1, 0),
				       new CommandArgument("Reason", "The reason for muting the user.", 1, 0));
	}

}
