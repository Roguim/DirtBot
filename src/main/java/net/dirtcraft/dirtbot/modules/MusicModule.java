package net.dirtcraft.dirtbot.modules;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.conversion.Path;
import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.commands.music.Connect;
import net.dirtcraft.dirtbot.commands.music.Disconnect;
import net.dirtcraft.dirtbot.commands.music.Play;
import net.dirtcraft.dirtbot.commands.music.Volume;
import net.dirtcraft.dirtbot.internal.configs.ConfigurationManager;
import net.dirtcraft.dirtbot.internal.configs.IConfigData;
import net.dirtcraft.dirtbot.internal.embeds.EmbedUtils;
import net.dirtcraft.dirtbot.internal.modules.Module;
import net.dirtcraft.dirtbot.internal.modules.ModuleClass;
import net.dirtcraft.dirtbot.utils.music.MusicUtils;
import net.dv8tion.jda.core.EmbedBuilder;

import java.time.Instant;

@ModuleClass(classLiteral = MusicModule.class)
public class MusicModule extends Module<MusicModule.ConfigDataMusic, MusicModule.EmbedUtilsMusic> {

    private MusicUtils musicUtils;

    @Override
    public void initialize() {
        setEmbedUtils(new EmbedUtilsMusic());
        musicUtils = new MusicUtils(this);
        DirtBot.getCoreModule().registerCommands(
                new Connect(),
                new Disconnect(),
                new Play(),
                new Volume());
    }

    @Override
    public void initializeConfiguration() {
        ConfigSpec spec = new ConfigSpec();

        spec.define("discord.embeds.footer", "DirtCraft's DirtBOT | 2019");
        spec.define("discord.embeds.title", ":redbulletpoint: DirtCraft's DirtBOT :redbulletpoint:");
        spec.define("discord.embeds.color", 16711680);

        setConfig(new ConfigurationManager<>(ConfigDataMusic.class, spec, "Music"));
    }

    public static class ConfigDataMusic implements IConfigData {
        @Path("discord.embeds.footer")
        public String embedFooter;
        @Path("discord.embeds.title")
        public String embedTitle;
        @Path("discord.embeds.color")
        public int embedColor;
    }

    public class EmbedUtilsMusic extends EmbedUtils {
        @Override
        public EmbedBuilder getEmptyEmbed() {
            return new EmbedBuilder()
                    .setTitle(getConfig().embedTitle)
                    .setColor(getConfig().embedColor)
                    .setFooter(getConfig().embedFooter, null)
                    .setTimestamp(Instant.now());
        }
    }

    public MusicUtils getUtils() {
        return musicUtils;
    }
}
