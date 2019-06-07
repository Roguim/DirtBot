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

import java.time.Instant;

@ModuleClass(classLiteral = ServerRolesModule.class)
public class ServerRolesModule extends Module<ServerRolesModule.ConfigDataServerRoles, ServerRolesModule.EmbedUtilsServerRoles> {

    @Override
    public void initialize() {
        // Initialize Embed Utils
        setEmbedUtils(new EmbedUtilsServerRoles());
    }

    @Override
    public void initializeConfiguration() {
        ConfigSpec spec = new ConfigSpec();

        spec.define("discord.embeds.footer", "DirtCraft's DirtBot | 2019");
        spec.define("discord.embeds.title", ":redbulletpoint: DirtCraft's DirtBot :redbulletpoint:");
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

    // DO ALL OF YOUR EVENT LISTENERS DOWN HERE
    // THIS IS AN EXAMPLE LISTENER IN THIS FORMAT, YOU CAN DELETE THIS ONE IF YOU DON'T NEED IT
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getMessageId().equals("538574783319638026")) {
            switch (event.getReactionEmote().getName().toLowerCase()) {
                case "stoneblock":
                    giveRole(event);
                    break;
                case "projectozone":
                    giveRole(event);
                    break;
                case "continuum":
                    giveRole(event);
                    break;
                case "infinityevolved":
                    giveRole(event);
                    break;
                case "skyfactory":
                    giveRole(event);
                    break;
                case "revelation":
                    giveRole(event);
                    break;
                case "interactions":
                    giveRole(event);
                    break;
                case "skyadventures":
                    giveRole(event);
                    break;
                case "ultimatereloaded":
                    giveRole(event);
                    break;
                case "skyodyssey":
                    giveRole(event);
                    break;
                case "direwolf20":
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
                    removeRole(event);
                    break;
                case "projectozone":
                    removeRole(event);
                    break;
                case "continuum":
                    removeRole(event);
                    break;
                case "infinityevolved":
                    removeRole(event);
                    break;
                case "skyfactory":
                    removeRole(event);
                    break;
                case "revelation":
                    removeRole(event);
                    break;
                case "interactions":
                    removeRole(event);
                    break;
                case "skyadventures":
                    removeRole(event);
                    break;
                case "ultimatereloaded":
                    removeRole(event);
                    break;
                case "skyodyssey":
                    removeRole(event);
                    break;
                case "direwolf20":
                    removeRole(event);
                    break;
            }
        }
    }

    private void giveRole(MessageReactionAddEvent event) {
        event.getGuild().getRolesByName(event.getReactionEmote().getName().toLowerCase(), true).forEach(role ->
                event.getGuild().getController().addRolesToMember(event.getMember(), role).queue());
    }

    private void removeRole(MessageReactionRemoveEvent event) {
        event.getGuild().getRolesByName(event.getReactionEmote().getName().toLowerCase(), true).forEach(role ->
                event.getGuild().getController().removeRolesFromMember(event.getMember(), role).queue());
    }
}
