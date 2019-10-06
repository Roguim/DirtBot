package net.dirtcraft.dirtbot.modules;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.conversion.Path;
import net.dirtcraft.dirtbot.internal.configs.ConfigurationManager;
import net.dirtcraft.dirtbot.internal.configs.IConfigData;
import net.dirtcraft.dirtbot.internal.embeds.EmbedUtils;
import net.dirtcraft.dirtbot.internal.modules.Module;
import net.dirtcraft.dirtbot.internal.modules.ModuleClass;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.time.Instant;

@ModuleClass
public class ServerRolesModule extends Module<ServerRolesModule.ConfigDataServerRoles, ServerRolesModule.EmbedUtilsServerRoles> {

    @Override
    public void initialize() {
        // Initialize Embed Utils
        setEmbedUtils(new EmbedUtilsServerRoles());
    }

    @Override
    public void initializeConfiguration() {
        ConfigSpec spec = new ConfigSpec();

        spec.define("discord.embeds.footer", "DirtCraft's DirtBOT | 2019");
        spec.define("discord.embeds.title", ":redbulletpoint: DirtCraft's DirtBOT :redbulletpoint:");
        spec.define("discord.embeds.color", 16711680);

        setConfig(new ConfigurationManager<>(ConfigDataServerRoles.class, spec, "ServerRoles"));
    }

    public static class ConfigDataServerRoles implements IConfigData {
        @Path("discord.embeds.footer")
        public String embedFooter;
        @Path("discord.embeds.title")
        public String embedTitle;
        @Path("discord.embeds.color")
        public int embedColor;
    }

    public class EmbedUtilsServerRoles extends EmbedUtils {
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
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getMessageId().equals("538574783319638026")) {
            switch (event.getReactionEmote().getName().toLowerCase()) {
                case "stoneblock":
                case "projectozone":
                case "continuum":
                case "glacialawakening":
                case "infinityevolved":
                case "skyfactory":
                case "revelation":
                case "rlcraft":
                case "interactions":
                case "skyadventures":
                case "ultimatereloaded":
                case "skyodyssey":
                case "direwolf20":
                case "omnifactory":
                case "pixelmon":
                    giveRole(event);
                    break;
            }
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (event.getMessageId().equals("538574783319638026")) {
            switch (event.getReactionEmote().getName().toLowerCase()) {
                case "stoneblock":
                case "projectozone":
                case "continuum":
                case "glacialawakening":
                case "infinityevolved":
                case "skyfactory":
                case "revelation":
                case "rlcraft":
                case "interactions":
                case "skyadventures":
                case "ultimatereloaded":
                case "skyodyssey":
                case "direwolf20":
                case "omnifactory":
                case "pixelmon":
                    removeRole(event);
                    break;
            }
        }
    }

    private void giveRole(MessageReactionAddEvent event) {
        event.getGuild().getRolesByName(event.getReactionEmote().getName().toLowerCase(), true).forEach(role ->
                event.getGuild().getController().addRolesToMember(event.getMember(), role).queue());

        EmbedBuilder embed = getEmbedUtils().getEmptyEmbed()
                .setColor(Color.GREEN)
                .setTitle("<:redbulletpoint:539273059631104052>**DirtCraft Role Assignments**<:redbulletpoint:539273059631104052>")
                .setTimestamp(Instant.now());
        if (!event.getReactionEmote().getName().equalsIgnoreCase("pixelmon")) {
            embed.setDescription("You are now subscribed to updates regarding the ModPack **" + String.join(" ", StringUtils.splitByCharacterTypeCamelCase(event.getReactionEmote().getName())) + "**");
        } else {
            embed.setDescription("You are now subscribed to updates regarding **Pixelmon Reforged**");
        }
        event.getMember().getUser().openPrivateChannel().queue(dm -> dm.sendMessage(embed.build()).queue());
    }

    private void removeRole(MessageReactionRemoveEvent event) {
        event.getGuild().getRolesByName(event.getReactionEmote().getName().toLowerCase(), true).forEach(role ->
                event.getGuild().getController().removeRolesFromMember(event.getMember(), role).queue());

        EmbedBuilder embed = getEmbedUtils().getEmptyEmbed()
                .setColor(Color.RED)
                .setTitle("<:redbulletpoint:539273059631104052>**DirtCraft Role Assignments**<:redbulletpoint:539273059631104052>")
                .setTimestamp(Instant.now());
        if (!event.getReactionEmote().getName().toLowerCase().equalsIgnoreCase("pixelmon")) {
                embed.setDescription("You are no longer subscribed to updates regarding the ModPack **" + String.join(" ", StringUtils.splitByCharacterTypeCamelCase(event.getReactionEmote().getName())) + "**");
        } else {
            embed.setDescription("You are no longer subscribed to updates regarding **Pixelmon Reforged**");
        }
        event.getMember().getUser().openPrivateChannel().queue(dm -> dm.sendMessage(embed.build()).queue());
    }
}
