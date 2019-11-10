package net.dirtcraft.dirtbot.modules;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.conversion.Path;
import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.commands.misc.*;
import net.dirtcraft.dirtbot.internal.configs.ConfigurationManager;
import net.dirtcraft.dirtbot.internal.configs.IConfigData;
import net.dirtcraft.dirtbot.internal.embeds.EmbedUtils;
import net.dirtcraft.dirtbot.internal.modules.Module;
import net.dirtcraft.dirtbot.internal.modules.ModuleClass;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ModuleClass(eventSubscriber = false)
public class CommandsModule extends Module<CommandsModule.ConfigDataCommands, CommandsModule.EmbedUtilsCommands> {

    @Override
    public void initialize() {
        // Initialize Embed Utils
        setEmbedUtils(new EmbedUtilsCommands());

        // Register Commands
        DirtBot.getCoreModule().registerCommands(
                new Donate(this),
                new Help(this),
                new Launcher(this),
                new Maps(this),
                //new NotMyDepartment(this), Vetoed by Julian ):
                new Ping(this),
                new Purge(this),
                new RandomCommand(this),
                new Review(this),
                new Servers(this),
                new Site(this),
                new Stop(this),
                new Update(this),
                new Unstuck(this),
                new Versions(this),
                new Vote(this)
        );
    }

    @Override
    public void initializeConfiguration() {
        ConfigSpec spec = new ConfigSpec();

        spec.define("discord.embeds.footer", "DirtCraft's DirtBOT | 2019");
        spec.define("discord.embeds.title", ":redbulletpoint: DirtCraft's DirtBOT :redbulletpoint:");
        spec.define("discord.embeds.color", 16711680);

        List<List<String>> mapDownloadsListExample = new ArrayList<>();
        mapDownloadsListExample.add(Arrays.asList("Server 1 Name", "Server 1 Download Link", "Server 1 Dates"));
        mapDownloadsListExample.add(Arrays.asList("Server 2 Name", "Server 2 Download Link", "Server 2 Dates"));
        spec.defineList("servers.old.maps", mapDownloadsListExample, p -> p instanceof ArrayList && ((ArrayList) p).size() == 3);

        setConfig(new ConfigurationManager<>(ConfigDataCommands.class, spec, "Commands"));
    }

    public static class ConfigDataCommands implements IConfigData {
        @Path("discord.embeds.footer")
        public String embedFooter;
        @Path("discord.embeds.title")
        public String embedTitle;
        @Path("discord.embeds.color")
        public int embedColor;

        @Path("servers.old.maps")
        public List<List<String>> oldMaps;
    }

    public class EmbedUtilsCommands extends EmbedUtils {

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
