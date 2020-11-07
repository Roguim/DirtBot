package net.dirtcraft.dirtbot.modules;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.conversion.Path;
import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.commands.staff.UpdateTicketInfo;
import net.dirtcraft.dirtbot.data.OrganizedMessage;
import net.dirtcraft.dirtbot.data.Ticket;
import net.dirtcraft.dirtbot.data.Ticket.Level;
import net.dirtcraft.dirtbot.internal.configs.ConfigurationManager;
import net.dirtcraft.dirtbot.internal.configs.IConfigData;
import net.dirtcraft.dirtbot.internal.embeds.EmbedUtils;
import net.dirtcraft.dirtbot.internal.modules.Module;
import net.dirtcraft.dirtbot.internal.modules.ModuleClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ModuleClass (requiresDatabase = true, experimental = true)
public class StaffCordModule extends Module<StaffCordModule.StaffCordConfigData, StaffCordModule.StaffCordEmbedUtils> {
	
	@Override
	public void initialize() {
		setEmbedUtils(new StaffCordModule.StaffCordEmbedUtils());
		
		initializeTicketInfo();
		
		DirtBot.getCoreModule().registerCommands(
				new UpdateTicketInfo(this));
	}
	
	public void initializeTicketInfo() {
		TextChannel ticketInfoChannel = null;
		for(GuildChannel channel : DirtBot.getJda().getGuildById(getConfig().staffCordGuildID).getChannels(false)) {
			if(channel.getId().equals(getConfig().ticketInfoChannelID)) ticketInfoChannel = (TextChannel) channel;
		}
		if(ticketInfoChannel == null) return;
		try {
			ticketInfoChannel.retrievePinnedMessages().complete().get(0).delete().complete();
		} catch (Exception ignored) {}
		
		initialTicketInfoMessage();
		
		
	}
	
	public void initialTicketInfoMessage() {
		TextChannel ticketInfoChannel = null;
		for(GuildChannel channel : DirtBot.getJda().getGuildById(getConfig().staffCordGuildID).getChannels(false)) {
			if(channel.getId().equals(getConfig().ticketInfoChannelID)) ticketInfoChannel = (TextChannel) channel;
		}
		
        ArrayList<String> serverNames = new ArrayList<>();
        ArrayList<String> serverCodes = new ArrayList<>();
        ArrayList<String> supportCategories = new ArrayList<>();
        ArrayList<String> openTickets = new ArrayList<>();
        ArrayList<String> adminTickets = new ArrayList<>();
        ArrayList<String> normalTickets = new ArrayList<>();

        for(int i = 0; i < DirtBot.getConfig().servers.size(); i++) {
            List<String> server = DirtBot.getConfig().servers.get(i);
            serverNames.add(server.get(0));
            serverCodes.add(server.get(1));
            supportCategories.add(server.get(2));
        }
        
        Iterator<String> categories = supportCategories.iterator();
        Iterator<String> codes = serverCodes.iterator();
        while(categories.hasNext() && codes.hasNext()) {
        	
        	int open = 0;
        	int admin = 0;
        	int normal = 0;
        	
        	for(GuildChannel channel : DirtBot.getJda().getCategoryById(categories.next()).getChannels()) {
            	if(channel.getType() != ChannelType.TEXT) continue;
            	TextChannel tchannel = (TextChannel) channel;
            	try {
					PreparedStatement statement = DriverManager.getConnection(getConfig().databaseUrl, getConfig().databaseUser, getConfig().databasePassword)
					.prepareStatement("SELECT * FROM tickets WHERE channel=?");
					statement.setString(1, tchannel.getId());
					ResultSet results = statement.executeQuery();
					if (results.next()) {
	                    Ticket ticket = new Ticket(
	                            results.getInt("id"),
	                            true, results.getString("message"),
	                            results.getString("username"),
	                            results.getString("server"),
	                            results.getString("channel"),
	                            Ticket.Level.valueOf(results.getString("level").toUpperCase()),
	                            results.getString("discordid"));
            			open++;
            			if(ticket.getLevel() == Level.ADMIN) {
            				admin++;
            			} else if(ticket.getLevel() == Level.NORMAL) {
            				normal++;
            			}
	                }
				} catch (SQLException e) {
					e.printStackTrace();
				}
        	}
        	openTickets.add(String.valueOf(open));
            adminTickets.add(String.valueOf(admin));
            normalTickets.add(String.valueOf(normal));
        }

        OrganizedMessage initialMessage = new OrganizedMessage();
        initialMessage.addColumn("Servers", serverNames);
        initialMessage.addColumn("Codes", serverCodes);
		initialMessage.addColumn("Open Tickets", openTickets);
		initialMessage.addColumn("Normal Tickets", normalTickets);
		initialMessage.addColumn("Admin Tickets", adminTickets);
        
		ticketInfoChannel.sendMessage(initialMessage.getMessage()).queue(msg -> msg.pin().queue());
	}

	@Override
	public void initializeConfiguration() {
		ConfigSpec spec = new ConfigSpec();
		
        spec.define("discord.embeds.footer", "Created for DirtCraft");
        spec.define("discord.embeds.title", "<:redbulletpoint:539273059631104052> DirtCraft's DirtBOT <:redbulletpoint:539273059631104052>");
        spec.define("discord.embeds.color", 16711680);
        
        spec.define("database.url", "jdbc:mariadb://localhost:3306/support");
        spec.define("database.user", "");
        spec.define("database.password", "");
        
        spec.define("discord.guild.staff", "568930779191574568");
        spec.define("discord.channels.ticketinfo", "639975024320839722");
        
        setConfig(new ConfigurationManager<>(StaffCordModule.StaffCordConfigData.class, spec, "StaffCord"));
	}
	
	public static class StaffCordConfigData implements IConfigData {	
        @Path("discord.embeds.footer")
        public String embedFooter;
        @Path("discord.embeds.title")
        public String embedTitle;
        @Path("discord.embeds.color")
        public int embedColor;
        
        @Path("database.url")
        public String databaseUrl;
        @Path("database.user")
        public String databaseUser;
        @Path("database.password")
        public String databasePassword;
        
        @Path("discord.guild.staff")
        public String staffCordGuildID;
        @Path("discord.channels.ticketinfo")
        public String ticketInfoChannelID;
	}
	
	public class StaffCordEmbedUtils extends EmbedUtils {
        @Override
        public EmbedBuilder getEmptyEmbed() {
            return new EmbedBuilder()
                    .setTitle(getConfig().embedTitle)
                    .setColor(getConfig().embedColor)
                    .setFooter(getConfig().embedFooter, null)
                    .setTimestamp(Instant.now());
        }
		
	}

}
