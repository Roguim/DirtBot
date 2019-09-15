package net.dirtcraft.dirtbot.modules;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.conversion.Path;
import com.google.common.collect.Lists;
import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.commands.appeals.AcceptAppeal;
import net.dirtcraft.dirtbot.commands.appeals.RejectAppeal;
import net.dirtcraft.dirtbot.data.Appeal;
import net.dirtcraft.dirtbot.internal.configs.ConfigurationManager;
import net.dirtcraft.dirtbot.internal.configs.IConfigData;
import net.dirtcraft.dirtbot.internal.embeds.EmbedUtils;
import net.dirtcraft.dirtbot.internal.modules.Module;
import net.dirtcraft.dirtbot.internal.modules.ModuleClass;
import net.dirtcraft.dirtbot.utils.appeals.AppealUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@ModuleClass(classLiteral = AppealModule.class)
public class AppealModule extends Module<AppealModule.ConfigDataAppeals, AppealModule.EmbedUtilsAppeals> {

    private List<Appeal> incompleteAppeals;
    private AppealUtils appealUtils;

    @Override
    public void initialize() {
        // Initialize Archives Folder
        File archivesFolder = new File("archives" + File.separator + "appeals");
        if(!archivesFolder.exists()) archivesFolder.mkdirs();

        // Initialize Incomplete Appeals List
        incompleteAppeals = new ArrayList<>();

        // Initialize Embed Utils
        setEmbedUtils(new EmbedUtilsAppeals());

        // Initialize Appeal Utils
        appealUtils = new AppealUtils(this);

        // Purge Incomplete Appeals
        purgeIncompleteAppeals();

        // Ensure the appeals channel has the appropriate header
        printChannelHeader();

        // Register Commands
        DirtBot.getCoreModule().registerCommands(
                new AcceptAppeal(this),
                new RejectAppeal(this)
        );

        // Establish Tasks
        if(getConfig().autocloseInterval > 0) {
            TimerTask autoClose = new TimerTask() {
                @Override
                public void run() {
                    for(Iterator<Appeal> iterator = incompleteAppeals.iterator(); iterator.hasNext();) {
                        Appeal appeal = iterator.next();
                        LocalDateTime appealCreatedTime = LocalDateTime.parse(DirtBot.getJda().getTextChannelById(appeal.getChannelID()).getTopic().replace("Appeal Incomplete | ", ""));
                        LocalDateTime now = LocalDateTime.now();
                        if(ChronoUnit.HOURS.between(appealCreatedTime, now) >= 24) {
                            for(Member member : getAppealUtils().getAppealMembers(DirtBot.getJda().getTextChannelById(appeal.getChannelID()))) {
                                member.getUser().openPrivateChannel().queue((privateChannel) -> {
                                    EmbedBuilder dmEmbed = getEmbedUtils().getExternalEmbed()
                                            .addField("__Appeal Cancelled__", "Your appeal information was not filled out within 24 hours and was therefore deemed abandoned. Please submit a new appeal.", false);
                                    privateChannel.sendMessage(dmEmbed.build()).queue();
                                });
                            }
                            DirtBot.getJda().getTextChannelById(appeal.getChannelID()).delete().queue();
                            getEmbedUtils().sendLog("Cancelled (Abandoned)", "The appeal information was not filled out within 24 hours and the appeal was therefore deemed abandoned.", appeal, DirtBot.getJda().getGuildById(DirtBot.getConfig().serverID).getMemberById(DirtBot.getJda().getSelfUser().getId()));
                            iterator.remove();
                        }
                    }
                }
            };
            Timer autoCloseTimer = new Timer();
            autoCloseTimer.scheduleAtFixedRate(autoClose, 0, getConfig().autocloseInterval * 1000);
        } else {
            EmbedBuilder autoCloseNoInitEmbed = getEmbedUtils().getEmptyEmbed()
                    .addField("__Initialization Event | Abandoned Appeal Close__", "Abandoned Appeal Close has been **disabled** due to autoclose interval being set to -1.\n To enable Abandoned Appeal Close, configure autoclose interval to an integer greater than 0 and restart the bot.", false);
            getEmbedUtils().sendLog(autoCloseNoInitEmbed.build());
        }
    }

    @Override
    public void initializeConfiguration() {
        ConfigSpec spec = new ConfigSpec();

        spec.define("discord.embeds.footer", "DirtCraft Appeals System | 2019");
        spec.define("discord.embeds.title", ":redbulletpoint: DirtCraft Appeals :redbulletpoint:");
        spec.define("discord.embeds.color", 16711680);

        spec.define("discord.channels.appealChannelID", "");
        spec.define("discord.channels.loggingChannelID", "");

        spec.define("discord.categories.appealCategoryID", "");

        spec.define("intervals.autoclose", -1);

        setConfig(new ConfigurationManager<>(ConfigDataAppeals.class, spec, "Appeals"));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot() || event.getAuthor().isFake()) return;

        // Appeal Setup Message?
        if (event.getTextChannel() == null) return;
        if (event.getTextChannel().getParent() == null) return;
        if (event.getTextChannel().getParent().getId().equals(getConfig().appealCategoryID)) {
            ticketInformationFilled(event, null, event.getMessage());
        }
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if(event.getUser().isBot()) return;

        event.getChannel().getMessageById(event.getMessageId()).queue((message) -> {
            if(!message.getAuthor().isBot()) return;

            // Appeal Creation?
            if(message.getTextChannel().getId().equals(getConfig().appealChannelID)) {
                appealCreated(event);
            // Punishment Type?
            } else if(event.getChannel().getParent().getId().equals(getConfig().appealCategoryID)) {
                ticketInformationFilled(null, event, message);
            }
        });
    }

    public static class ConfigDataAppeals implements IConfigData {
        @Path("discord.embeds.footer")
        public String embedFooter;
        @Path("discord.embeds.title")
        public String embedTitle;
        @Path("discord.embeds.color")
        public int embedColor;

        @Path("discord.channels.appealChannelID")
        public String appealChannelID;
        @Path("discord.channels.loggingChannelID")
        public String loggingChannelID;

        @Path("discord.categories.appealCategoryID")
        public String appealCategoryID;

        @Path("intervals.autoclose")
        public int autocloseInterval;
    }

    public class EmbedUtilsAppeals extends EmbedUtils {

        @Override
        public EmbedBuilder getEmptyEmbed() {
            return new EmbedBuilder()
                    .setTitle(getConfig().embedTitle)
                    .setColor(getConfig().embedColor)
                    .setFooter(getConfig().embedFooter, null)
                    .setTimestamp(Instant.now());
        }

        public EmbedBuilder getExternalEmbed() {
            return getEmptyEmbed().setTitle(getConfig().embedTitle.replace(":redbulletpoint:", "\ud83e\udd16"));
        }

        public void sendLog(String eventName, String eventInfo, Appeal appeal, Member member) {
            sendLog(eventName, eventInfo, DirtBot.getJda().getTextChannelById(appeal.getChannelID()), member);
        }

        public void sendLog(String eventName, String eventInfo, TextChannel appeal, Member member) {
            sendLog(getEmptyEmbed()
                    .addField("__Appeal Event | " + eventName + "__", eventInfo, false)
                    .addField("__Appeal Information__",
                            "**Appealer:** <@" + getAppealUtils().getAppealMembers(appeal).get(0).getUser().getId() + ">\n" +
                                    "**Appeal:** <#" + appeal.getId() + ">\n" +
                                    "**Action Completed By:** <@" + member.getUser().getId() + ">", false)
                    .build());
        }

        public void sendLog(MessageEmbed message) {
            DirtBot.getJda().getTextChannelById(getConfig().loggingChannelID).sendMessage(message).queue();
        }
    }

    public AppealUtils getAppealUtils() { return appealUtils; }

    public void archiveAppeal(List<Message> history, String username) {
        List<String> lines = new ArrayList<>();
            new Thread(() -> {
                try {
                    for(Message message : Lists.reverse(history)) {
                        String line = "";
                        if (message.getMember() != null) line += message.getMember().getEffectiveName();
                        else line += "N/A (User has left the discord server)";
                        line += " : ";
                        line += message.getCreationTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
                        line += "> ";
                        line += message.getContentDisplay();
                        for(MessageEmbed embed : message.getEmbeds()) {
                            for(MessageEmbed.Field field : embed.getFields()) {
                                line += " [" + field.getName() + "] ";
                                line += field.getValue();
                            }
                        }
                        line += "\n";
                        lines.add(line);
                    }
                    java.nio.file.Path file = Paths.get("archives" + File.separator + "appeals" + File.separator + username.replaceAll("[^a-zA-Z0-9]", "") + ".txt");
                    Files.write(file, lines, Charset.forName("UTF-8"));
                    new ZipFile("archives" + File.separator + "appeals" + File.separator + LocalDate.now().format(DateTimeFormatter.ofPattern("W-LLLL-yyyy")) + ".zip").addFile(file.toFile(), new ZipParameters());
                    file.toFile().delete();
                } catch (Exception e) {
                    e.printStackTrace();
                    DirtBot.pokeDevs(e);
                }
            }).start();
    }

    private void printChannelHeader() {
        EmbedBuilder instructions = getEmbedUtils().getEmptyEmbed()
                .addField("__Appeal System Instructions__",
                        "**1.** Click the \uD83D\uDCDD below to create an appeal.\n" +
                                "**2.** Click the channel link in the response to navigate to your appeal.\n " +
                                "**3.** Follow the prompts listed in your appeal.\n" +
                                "**4.** Please wait patiently. You're more likely to have your appeal accepted if you're kind and respectful towards staff.", false);
        TextChannel appealChannel = DirtBot.getJda().getTextChannelById(getConfig().appealChannelID);
        boolean messageFound = false;
        List<Message> pinnedMessagesAppeal = appealChannel.getPinnedMessages().complete();
        for (Message message : pinnedMessagesAppeal) {
            if (message.getEmbeds().size() > 0 && message.getAuthor().isBot() && message.getEmbeds().get(0).getFields().get(0).getName().contains(instructions.getFields().get(0).getName())) {
                message.editMessage(instructions.build()).complete();
                message.clearReactions().complete();
                message.addReaction("\uD83D\uDCDD").queue();
                messageFound = true;
            }
        }
        if (!messageFound) {
            appealChannel.sendMessage(instructions.build()).queue((message) -> {
                message.pin().queue();
                message.addReaction("\uD83D\uDCDD").queue();
            });
        }
    }

    private void purgeIncompleteAppeals() {
        for(TextChannel channel : DirtBot.getJda().getCategoryById(getConfig().appealCategoryID).getTextChannels()) {
            if(channel.getTopic().toLowerCase().contains("incomplete")) {

                for(Member member : getAppealUtils().getAppealMembers(channel)) {
                    member.getUser().openPrivateChannel().queue((privateChannel) -> {
                        EmbedBuilder dmEmbed = getEmbedUtils().getExternalEmbed()
                                .addField("__Appeal Cancelled__", "Your appeal information was not filled out and the bot has since restarted. Please submit a new appeal.", false);
                        privateChannel.sendMessage(dmEmbed.build()).queue();
                    });
                    getEmbedUtils().sendLog(getEmbedUtils().getEmptyEmbed().addField("Appeal Event | Deleted (Abandoned)", "The appeal information was not filled out before the bot restarted.", false).build());
                }
                channel.delete().queue();
            }
        }
    }

    private void appealCreated(GuildMessageReactionAddEvent event) {
        Guild server = DirtBot.getJda().getGuildById(DirtBot.getConfig().serverID);
        for(TextChannel channel : server.getCategoryById(getConfig().appealCategoryID).getTextChannels()) {
            if(channel.getName().toLowerCase().equals(event.getMember().getEffectiveName().toLowerCase())) {
                getEmbedUtils().sendResponse(getEmbedUtils().getErrorEmbed("An appeal already exists for this user!\n\n Please click: <#" + channel.getId() + "> to view this appeal.").build(), DirtBot.getJda().getTextChannelById(getConfig().appealChannelID));
                event.getReaction().removeReaction(event.getUser()).queue();
                return;
            }
        }
        server.getController().createTextChannel(event.getMember().getEffectiveName())
                .setParent(server.getCategoryById(getConfig().appealCategoryID))
                .addPermissionOverride(event.getMember(), EnumSet.of(Permission.MESSAGE_READ), null)
                .addPermissionOverride(server.getRoleById(DirtBot.getConfig().staffRoleID), EnumSet.of(Permission.MESSAGE_READ), null)
                .addPermissionOverride(server.getRoleById(server.getId()), null, EnumSet.of(Permission.MESSAGE_READ))
                .setTopic("Appeal Incomplete | " + LocalDateTime.now())
                .queue((appealChannel)-> {
                    EmbedBuilder response = getEmbedUtils().getEmptyEmbed()
                            .addField("__**Appeal Created**__", "Hello <@" + event.getMember().getUser().getId() + ">, \n I have created the channel <#" + appealChannel.getId() + "> for your appeal. Please follow the prompts in the aforementioned channel to submit your appeal.", false);
                    event.getChannel().sendMessage(response.build()).queue((responseMessage) -> {
                        responseMessage.delete().queueAfter(10, TimeUnit.SECONDS);
                    });
                    Appeal appeal = new Appeal(appealChannel.getId());
                    incompleteAppeals.add(appeal);
                    EmbedBuilder appealStartEmbed = getEmbedUtils().getEmptyEmbed()
                            .addField("__Player Username__", "Please send your Minecraft username below.", false);
                    ((TextChannel) appealChannel).sendMessage(appealStartEmbed.build()).queue((appealHeader) -> {
                        appealHeader.pin().queue();
                    });
                    event.getReaction().removeReaction(event.getUser()).queue();
                    getEmbedUtils().sendLog("Created", "A new appeal has been submitted.", appeal, event.getMember());
                });
        return;
    }

    private void ticketInformationFilled(MessageReceivedEvent me, GuildMessageReactionAddEvent re, Message message) {
        for(Iterator<Appeal> iterator = incompleteAppeals.iterator(); iterator.hasNext();) {
            Appeal appeal = iterator.next();
            if (appeal.getChannelID().equals(message.getChannel().getId())) {
                // Username Prompt Answered
                if (appeal.getUsername() == null) {
                    appeal.setUsername(message.getContentDisplay());
                    message.getChannel().getPinnedMessages().queue((pinnedMessages) -> {
                        EmbedBuilder appealServerEmbed = getEmbedUtils().getEmptyEmbed()
                                .addField("__Server__", "Please send the server code below. This can be found by looking at the server's gamechat. Send what you see following the hyphen (-).", false);
                        pinnedMessages.get(0).editMessage(appealServerEmbed.build()).queue();
                    });
                    message.delete().queue();
                // Server Prompt Answered
                } else if(appeal.getServer() == null) {
                    boolean validServer = false;
                    for(List<String> serverInfo : DirtBot.getConfig().servers) {
                        if(serverInfo.get(0).toLowerCase().equals(message.getContentDisplay().toLowerCase()) || serverInfo.get(1).toLowerCase().equals(message.getContentDisplay().toLowerCase())) validServer = true;
                    }
                    if(validServer) {
                        appeal.setServer(message.getContentDisplay().toUpperCase());
                        message.getChannel().getPinnedMessages().queue((pinnedMessages) -> {
                            EmbedBuilder appealPunishmentTypeEmbed = getEmbedUtils().getEmptyEmbed()
                                    .addField("__Punishment Type__", "Please select your punishment type:\n" +
                                            "Mute [\uD83D\uDD07]\n" +
                                            "Ban [\uD83D\uDEAB]", false);
                            pinnedMessages.get(0).editMessage(appealPunishmentTypeEmbed.build()).queue((updatedMessage) -> {
                                updatedMessage.addReaction("\uD83D\uDD07").queue();
                                updatedMessage.addReaction("\uD83D\uDEAB").queue();
                            });
                        });
                    } else {
                        getEmbedUtils().sendResponse(getEmbedUtils().getErrorEmbed("The given server is not valid! Please use the server code found in the server's gamechat channel name!").build(), me.getTextChannel());
                    }
                    message.delete().queue();
                // Punishment Type Answered
                } else if(appeal.getPunishmentType() == null) {
                    if(re.getReactionEmote().getName().equals("\uD83D\uDD07") || re.getReactionEmote().getName().equals("\uD83D\uDEAB")) {
                        switch(re.getReactionEmote().getName()) {
                            case "\uD83D\uDD07":
                                appeal.setPunishmentType(Appeal.PunishmentType.MUTE);
                                break;
                            case "\uD83D\uDEAB":
                                appeal.setPunishmentType(Appeal.PunishmentType.BAN);
                                break;
                        }
                        message.getChannel().getPinnedMessages().queue((pinnedMessages) -> {
                            EmbedBuilder appealStaffEmbed = getEmbedUtils().getEmptyEmbed()
                                    .addField("__Staff Member__", "Please mention (@) the staff member who punished you.", false);
                            pinnedMessages.get(0).editMessage(appealStaffEmbed.build()).queue();
                            pinnedMessages.get(0).clearReactions().queue();
                        });
                    }
                // Staff Member Answered
                } else if(appeal.getStaff() ==  null) {
                    boolean found = false;
                    for (Member member : message.getMentionedMembers()) {
                        if(member.getRoles().contains(DirtBot.getJda().getRoleById(DirtBot.getConfig().staffRoleID))) {
                            found = true;
                            appeal.setStaff(member.getUser().getId());
                            message.getChannel().getPinnedMessages().queue((pinnedMessages) -> {
                                EmbedBuilder appealExplanationEmbed = getEmbedUtils().getEmptyEmbed()
                                        .addField("__Explanation__", "Please explain why your punishment should be changed or removed.", false);
                                pinnedMessages.get(0).editMessage(appealExplanationEmbed.build()).queue();
                            });
                            message.delete().queue();
                        }
                    }
                    if(!found) {
                        EmbedBuilder appealInvalidStaffEmbed = getEmbedUtils().getEmptyEmbed()
                                .addField("__Invalid Staff Member!__", "Invalid staff member! Please mention (@) the staff member who punished you", false);
                        message.getTextChannel().sendMessage(appealInvalidStaffEmbed.build()).queue((response) -> {
                            response.delete().queueAfter(10, TimeUnit.SECONDS);
                            message.delete().queue();
                        });
                    }
                // Explanation Answered
                } else if(appeal.getExplanation() == null) {
                    appeal.setExplanation(message.getContentRaw());
                    message.getChannel().getPinnedMessages().queue((pinnedMessages) -> {
                        EmbedBuilder appealHeaderEmbed = getEmbedUtils().getEmptyEmbed()
                                .addField("__New Appeal__", getAppealUtils().getAppealInfo(appeal), false);
                        pinnedMessages.get(0).editMessage(appealHeaderEmbed.build()).queue();
                    });
                    iterator.remove();
                    message.getTextChannel().getManager().setTopic("Pending Appeal").queue();
                    message.getTextChannel().sendMessage("<@" + appeal.getStaff() + ">").queue();
                    message.delete().queue();
                }
            }
        }
    }
}
