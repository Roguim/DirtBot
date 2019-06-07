package net.dirtcraft.dirtbot.modules;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.conversion.Path;
import net.dirtcraft.dirtbot.internal.configs.ConfigurationManager;
import net.dirtcraft.dirtbot.internal.configs.IConfigData;
import net.dirtcraft.dirtbot.internal.embeds.EmbedUtils;
import net.dirtcraft.dirtbot.internal.modules.Module;
import net.dirtcraft.dirtbot.internal.modules.ModuleClass;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.time.Instant;

import static net.dirtcraft.dirtbot.DirtBot.getConfig;

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
    public void onMessageReceived(MessageReceivedEvent event) {

    }
}
