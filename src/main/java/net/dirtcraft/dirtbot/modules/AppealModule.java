package net.dirtcraft.dirtbot.modules;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.conversion.Path;
import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.data.Appeal;
import net.dirtcraft.dirtbot.internal.configs.ConfigurationManager;
import net.dirtcraft.dirtbot.internal.configs.IConfigData;
import net.dirtcraft.dirtbot.internal.embeds.EmbedUtils;
import net.dirtcraft.dirtbot.internal.modules.Module;
import net.dirtcraft.dirtbot.internal.modules.ModuleClass;
import net.dirtcraft.dirtbot.utils.appeals.AppealUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ModuleClass(classLiteral = AppealModule.class)
public class AppealModule extends Module<AppealModule.ConfigDataAppeals, AppealModule.EmbedUtilsAppeals> {

    private List<Appeal> incompleteAppeals;
    private AppealUtils appealUtils;

    @Override
    public void initialize() {
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

        setConfig(new ConfigurationManager<>(ConfigDataAppeals.class, spec, "Appeals"));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;

        // Appeal Setup Message?
        if(event.getTextChannel().getParent().getId().equals(getConfig().appealCategoryID)) {
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
    }

    public AppealUtils getAppealUtils() { return appealUtils; }

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
                        EmbedBuilder dmEmbed = getEmbedUtils().getEmptyEmbed()
                                .addField("__Appeal Cancelled__", "Your appeal information was not filled out and the bot has since restarted. Please submit a new appeal.", false);
                        privateChannel.sendMessage(dmEmbed.build()).queue();
                    });
                }
                channel.delete().queue();
            }
        }
    }

    private void appealCreated(GuildMessageReactionAddEvent event) {
        Guild server = DirtBot.getJda().getGuildById(DirtBot.getConfig().serverID);
        TextChannel appealChannel = (TextChannel) server.getController().createTextChannel(event.getMember().getEffectiveName())
                .setParent(server.getCategoryById(getConfig().appealCategoryID))
                .addPermissionOverride(event.getMember(), EnumSet.of(Permission.MESSAGE_READ), null)
                .addPermissionOverride(server.getRoleById(DirtBot.getConfig().staffRoleID), EnumSet.of(Permission.MESSAGE_READ), null)
                .addPermissionOverride(server.getRoleById(server.getId()), null, EnumSet.of(Permission.MESSAGE_READ))
                .setTopic("Appeal Incomplete | " + LocalDateTime.now())
                .complete();
        EmbedBuilder response = getEmbedUtils().getEmptyEmbed()
                .addField("__**Appeal Created**__", "Hello <@" + event.getMember().getUser().getId() + ">, \n I have created the channel <#" + appealChannel.getId() + "> for your appeal. Please follow the prompts in the aforementioned channel to submit your appeal.", false);
        event.getChannel().sendMessage(response.build()).queue((responseMessage) -> {
            responseMessage.delete().queueAfter(10, TimeUnit.SECONDS);
        });
        Appeal appeal = new Appeal(appealChannel.getId());
        incompleteAppeals.add(appeal);
        EmbedBuilder appealStartEmbed = getEmbedUtils().getEmptyEmbed()
                .addField("__Player Username__", "Please send your Minecraft username below.", false);
        appealChannel.sendMessage(appealStartEmbed.build()).queue((appealHeader) -> {
            appealHeader.pin().queue();
        });
        event.getReaction().removeReaction(event.getUser()).queue();
        return;
    }

    private void ticketInformationFilled(MessageReceivedEvent me, GuildMessageReactionAddEvent re, Message message) {
        for(Appeal appeal : incompleteAppeals) {
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
                    message.delete().queue();
                // Punishment Type Answered
                } else if(appeal.getPunishmentType() == null) {
                    if(re.getReactionEmote().equals("\uD83D\uDD07") || re.getReactionEmote().equals("\uD83D\uDEAB")) {
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
                                .addField("__New Ban Appeal__", getAppealUtils().getAppealInfo(appeal), false);
                        pinnedMessages.get(0).editMessage(appealHeaderEmbed.build()).queue();
                    });
                    message.getTextChannel().getManager().setTopic("Pending Appeal").queue();
                    message.getTextChannel().sendMessage("<@" + appeal.getStaff() + ">").queue();
                    message.delete().queue();
                }
            }
        }
    }
}
