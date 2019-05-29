package net.dirtcraft.dirtbot.modules;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.conversion.Path;
import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.commands.misc.RandomCommand;
import net.dirtcraft.dirtbot.commands.misc.Review;
import net.dirtcraft.dirtbot.commands.misc.ShinyCSharp;
import net.dirtcraft.dirtbot.internal.configs.ConfigurationManager;
import net.dirtcraft.dirtbot.internal.configs.IConfigData;
import net.dirtcraft.dirtbot.internal.embeds.EmbedUtils;
import net.dirtcraft.dirtbot.internal.modules.Module;
import net.dirtcraft.dirtbot.internal.modules.ModuleClass;
import net.dv8tion.jda.core.EmbedBuilder;

import java.time.Instant;

@ModuleClass(classLiteral = CommandsModule.class, eventSubscriber = false)
public class CommandsModule extends Module<CommandsModule.ConfigDataCommands, CommandsModule.EmbedUtilsCommands> {

    @Override
    public void initialize() {
        // Initialize Embed Utils
        setEmbedUtils(new EmbedUtilsCommands());

        // Register Commands
        DirtBot.getCoreModule().registerCommands(
                //new NotMyDepartment(this), Vetoed by Julian ):
                new RandomCommand(this),
                new Review(this),
                new ShinyCSharp(this)
        );
    }

    @Override
    public void initializeConfiguration() {
        ConfigSpec spec = new ConfigSpec();

        spec.define("discord.embeds.footer", "DirtCraft's DirtBot | 2019");
        spec.define("discord.embeds.title", ":redbulletpoint: DirtCraft's DirtBot :redbulletpoint:");
        spec.define("discord.embeds.color", 16711680);

        setConfig(new ConfigurationManager<>(ConfigDataCommands.class, spec, "Commands"));
    }

    public static class ConfigDataCommands implements IConfigData {
        @Path("discord.embeds.footer")
        public String embedFooter;
        @Path("discord.embeds.title")
        public String embedTitle;
        @Path("discord.embeds.color")
        public int embedColor;
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
