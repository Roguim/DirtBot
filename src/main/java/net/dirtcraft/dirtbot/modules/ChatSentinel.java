package net.dirtcraft.dirtbot.modules;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.conversion.Path;
import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.internal.configs.ConfigurationManager;
import net.dirtcraft.dirtbot.internal.configs.IConfigData;
import net.dirtcraft.dirtbot.internal.embeds.EmbedUtils;
import net.dirtcraft.dirtbot.internal.modules.Module;
import net.dirtcraft.dirtbot.internal.modules.ModuleClass;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@ModuleClass(classLiteral = ChatSentinel.class)
public class ChatSentinel extends Module<ChatSentinel.ConfigDataChatSentinel, ChatSentinel.EmbedUtilsChatSentinel> {

    @Override
    public void initialize() {
        // Initialize Embed utils
        setEmbedUtils(new EmbedUtilsChatSentinel());
    }

    @Override
    public void initializeConfiguration() {
        ConfigSpec spec = new ConfigSpec();

        spec.define("discord.embeds.footer", "DirtCraft's DirtBot | 2019");
        spec.define("discord.embeds.title", ":redbulletpoint: DirtCraft's DirtBot :redbulletpoint:");
        spec.define("discord.embeds.color", 16711680);

        setConfig(new ConfigurationManager<>(ConfigDataChatSentinel.class, spec, "ChatSentinel"));
    }

    public static class ConfigDataChatSentinel implements IConfigData {
        @Path("discord.embeds.footer")
        public String embedFooter;
        @Path("discord.embeds.title")
        public String embedTitle;
        @Path("discord.embeds.color")
        public int embedColor;
    }

    public class EmbedUtilsChatSentinel extends EmbedUtils {
        @Override
        public EmbedBuilder getEmptyEmbed() {
            return new EmbedBuilder()
                    .setTitle(getConfig().embedTitle)
                    .setColor(getConfig().embedColor)
                    .setFooter(getConfig().embedFooter, null)
                    .setTimestamp(Instant.now());
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Guild server = DirtBot.getJda().getGuildById(DirtBot.getConfig().serverID);
        Member ten = server.getMemberById("155688380162637825");
        Member julian = server.getMemberById("209865813849538560");

        if (event.getMember().getUser().isFake() || event.getMember().getUser().isBot()) return;
        if (DirtBot.getJda().getRoleById(DirtBot.getConfig().staffRoleID) == null) return;
        if (event.getMember() == null) return;
        if (event.getMember().getRoles() == null) return;
        if (!event.getMessage().getAuthor().isBot() && !event.getMember().getRoles().contains(DirtBot.getJda().getRoleById(DirtBot.getConfig().staffRoleID))) {
            if (event.getMessage().getMentionedMembers().contains(ten)) {
                event.getTextChannel().sendMessage(getEmbedUtils().getErrorEmbed("Hey! <@155688380162637825> doesn't like to be pinged.\nWhy don't you make a ticket instead?\nUse <#" + DirtBot.getModuleRegistry().getModule(TicketModule.class).getConfig().supportChannelID + ">.").build()).queue(m -> m.delete().queueAfter(10, TimeUnit.SECONDS));
                //event.getMessage().delete().queue();
            } else if (event.getMessage().getMentionedMembers().contains(julian)) {
                event.getTextChannel().sendMessage(getEmbedUtils().getErrorEmbed("Hey! <@209865813849538560> doesn't like to be pinged.\nWhy don't you make a ticket instead?\nUse <#" + DirtBot.getModuleRegistry().getModule(TicketModule.class).getConfig().supportChannelID + ">.").build()).queue(m -> m.delete().queueAfter(10, TimeUnit.SECONDS));
                //event.getMessage().delete().queue();
            } else if (event.getMessage().getMentionedRoles().contains(DirtBot.getJda().getRoleById(DirtBot.getConfig().staffRoleID))) {
                event.getTextChannel().sendMessage(getEmbedUtils().getErrorEmbed("Hey! Please don't ping our staff team.\nWhy don't you make a ticket instead?\nUse <#" + DirtBot.getModuleRegistry().getModule(TicketModule.class).getConfig().supportChannelID + ">.").build()).queue(m -> m.delete().queueAfter(10, TimeUnit.SECONDS));
                //event.getMessage().delete().queue();
            }
        }
    }

    @Override
    public void onGuildMemberNickChange(GuildMemberNickChangeEvent event) {
        ArrayList<String> names = new ArrayList<>();
        Role staffRole = DirtBot.getJda().getGuildById(DirtBot.getConfig().serverID).getRoleById(DirtBot.getConfig().staffRoleID);
        if (event.getMember().getRoles().contains(staffRole)) return;

        event.getGuild().getMembersWithRoles(staffRole).forEach(staffMember -> {
            if (staffMember.getNickname() == null) {
                names.add(staffMember.getEffectiveName());
            } else {
                names.add(staffMember.getUser().getName());
                names.add(staffMember.getEffectiveName());
            }
        });

        if (names.contains(event.getNewNick())) {
            event.getGuild().getController().setNickname(event.getMember(), event.getPrevNick()).queue();
        }
    }
}
