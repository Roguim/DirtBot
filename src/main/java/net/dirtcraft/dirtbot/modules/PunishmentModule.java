package net.dirtcraft.dirtbot.modules;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.conversion.Path;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.commands.punishment.Mute;
import net.dirtcraft.dirtbot.commands.punishment.Unmute;
import net.dirtcraft.dirtbot.internal.configs.ConfigurationManager;
import net.dirtcraft.dirtbot.internal.configs.IConfigData;
import net.dirtcraft.dirtbot.internal.embeds.EmbedUtils;
import net.dirtcraft.dirtbot.internal.modules.Module;
import net.dirtcraft.dirtbot.internal.modules.ModuleClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

@ModuleClass
public class PunishmentModule extends Module<PunishmentModule.PunishmentConfigData, PunishmentModule.PunishmentEmbedUtils> {
	
	@Override
	public void initialize() {
		setEmbedUtils(new PunishmentModule.PunishmentEmbedUtils());
		
		initializeMutes();
		
		DirtBot.getCoreModule().registerCommands(
				new Mute(this),
				new Unmute(this)
		);
	}

	@Override
	public void initializeConfiguration() {
		ConfigSpec spec = new ConfigSpec();
		
        spec.define("database.url", "jdbc:mariadb://localhost:3306/support");
        spec.define("database.user", "");
        spec.define("database.password", "");
		
		spec.define("discord.channels.punishmentLogChannelID", "635917765160599585"); 
		
		spec.define("discord.roles.mutedRoleID", "589777192024670228"); 
		
        spec.define("discord.embeds.footer", "DirtCraft's DirtBOT | 2019");
        spec.define("discord.embeds.title", "<:redbulletpoint:539273059631104052> DirtCraft's DirtBOT <:redbulletpoint:539273059631104052>");
        spec.define("discord.embeds.color", 16711680);
		
		setConfig(new ConfigurationManager<>(PunishmentModule.PunishmentConfigData.class, spec, "Punishment"));
	}
	
	public static class PunishmentConfigData implements IConfigData {
        @Path("database.url")
        public String databaseUrl;
        @Path("database.user")
        public String databaseUser;
        @Path("database.password")
        public String databasePassword;
        
		@Path("discord.channels.punishmentLogChannelID")
		public String punishmentLogChannelID;
		
		@Path("discord.roles.mutedRoleID")
		public String mutedRoleID;
		
        @Path("discord.embeds.footer")
        public String embedFooter;
        @Path("discord.embeds.title")
        public String embedTitle;
        @Path("discord.embeds.color")
        public int embedColor;
	}
	
	public void initializeMutes() {
		//Retrieve a list of all mutes from the database
		Timer timer = new Timer();

		timer.schedule( new TimerTask() {
		    public void run() {
		    	try {
					Connection con = DriverManager.getConnection(getConfig().databaseUrl, getConfig().databaseUser, getConfig().databasePassword);
					
					PreparedStatement statement = con.prepareStatement("SELECT * FROM mutes");
					try(ResultSet results = statement.executeQuery()) {
						while(results.next()) {
							User user = DirtBot.getJda().getUserById(results.getString("discordid"));
							java.util.Date unmuteDate = results.getDate("unmutetime");
							java.util.Date currentDate = new java.util.Date();
							//Check if user is still in server
							if(!DirtBot.getJda().getGuildById("269639757351354368").isMember(user)) {
								PreparedStatement statementDelete = con.prepareStatement("DELETE FROM mutes WHERE discordid=?");
								statementDelete.setString(1, user.getId());
								statementDelete.execute();
								continue;
							}
							
							//Check if user is still muted
							Member member = DirtBot.getJda().getGuildById("269639757351354368").getMember(user); 
							if(!member.getRoles().contains(DirtBot.getJda().getRoleById(getConfig().mutedRoleID))) {
								PreparedStatement statementDelete = con.prepareStatement("DELETE FROM mutes WHERE discordid=?");
								statementDelete.setString(1, user.getId());
								statementDelete.execute();
								continue;
							}
							
							//Check if time is up on mute
							if(currentDate.compareTo(unmuteDate) > 0) {
								DirtBot.getJda().getGuildById("269639757351354368").removeRoleFromMember(member, DirtBot.getJda().getRoleById(getConfig().mutedRoleID)).queue();
								PreparedStatement statementDelete = con.prepareStatement("DELETE FROM mutes WHERE discordid=?");
								statementDelete.setString(1, user.getId());
								statementDelete.execute();
							}
						}
					}

					con.close();
				} catch (SQLException e) {
					DirtBot.pokeDevs(e);
				}
		    }
		 }, 60, 60*1000);
	}
	
	public class PunishmentEmbedUtils extends EmbedUtils {
        @Override
        public EmbedBuilder getEmptyEmbed() {
            return new EmbedBuilder()
                    .setTitle(getConfig().embedTitle)
                    .setColor(getConfig().embedColor)
                    .setFooter(getConfig().embedFooter, null)
                    .setTimestamp(Instant.now());
        }
        
        public void sendError(String errorMessage, TextChannel channel) {
        	EmbedBuilder error = getEmptyEmbed().addField("__Error__", errorMessage, false);
        	channel.sendMessage(error.build()).queue((message) -> message.delete().queueAfter(10, TimeUnit.SECONDS));
        }
        
        public void sendPunishLog(String punisherID, String punishedID, PunishmentLogType logType, String length, String reason) {
        	TextChannel punishmentLogChannel = DirtBot.getJda().getTextChannelById(getConfig().punishmentLogChannelID);
        	
        	switch(logType) {
        	case BAN:
        		EmbedBuilder banLog = getEmptyEmbed().addField("__Punishment Event__", 
        		"**Punishment Type:** Ban\n" +
        		"**Punisher:** <@" + punisherID + ">\n" +
        		"**Punished Player:** <@" + punishedID + ">\n" +
        		"**Duration:** " + length + "\n" +
        		"**Reason:** " + reason, false);
        		punishmentLogChannel.sendMessage(banLog.build()).queue();
        		break;
        	case KICK:
        		EmbedBuilder kickLog = getEmptyEmbed().addField("__Punishment Event__", 
        		"**Punishment Type:** Kick\n" +
        		"**Punisher:** <@" + punisherID + ">\n" +
        		"**Punished Player:** <@" + punishedID + ">\n" +
        		"**Reason:** " + reason, false);
        		punishmentLogChannel.sendMessage(kickLog.build()).queue();
        		break;
        	case MUTE:
        		EmbedBuilder muteLog = getEmptyEmbed().addField("__Punishment Event__", 
        		"**Punishment Type:** Mute\n" +
        		"**Punisher:** <@" + punisherID + ">\n" +
        		"**Punished Player:** <@" + punishedID + ">\n" +
        		"**Duration:** " + length + "\n" +
        		"**Reason:** " + reason, false);
        		punishmentLogChannel.sendMessage(muteLog.build()).queue();
        		break;
        	case UNMUTE:
        		EmbedBuilder unmuteLog = getEmptyEmbed().addField("__Punishment Event__", 
        		"**Punishment Type:** Unmute\n" +
        		"**Punisher:** <@" + punisherID + ">\n" +
        		"**Punished Player:** <@" + punishedID + ">", false);
        		punishmentLogChannel.sendMessage(unmuteLog.build()).queue();
        		break;
        	case CLEARCHATALL:
        		EmbedBuilder clearChatAllLog = getEmptyEmbed().addField("__Punishment Event__",
        		"**Punishment Type:** Clear All Chat\n" +
        		"**Punisher:** <@" + punisherID + ">\n" +
        		"**Channel:** <#" + punishedID + ">\n" +
        		"**Messages Cleared:** " + length + "\n" +
        		"**Reason:** " + reason, false);
        		punishmentLogChannel.sendMessage(clearChatAllLog.build()).queue();
        		break;
        	case CLEARCHATUSER:
        		EmbedBuilder clearChatUserLog = getEmptyEmbed().addField("__Punishment Event__",
        		"**Punishment Type:** Clear User Chat\n" +
        		"**Punisher:** <@" + punisherID + ">\n" +
        		"**Punished Player:** <@" + punishedID + ">\n" +
        		"**Messages Cleared:** " + length + "\n" +
        		"**Reason:** " + reason, false);
        		punishmentLogChannel.sendMessage(clearChatUserLog.build()).queue();
        		break;
        	}
        }
		
	}
	
	public enum PunishmentLogType {
		BAN,
		KICK,
		MUTE,
		UNMUTE,
		CLEARCHATALL,
		CLEARCHATUSER
	}
	

}
