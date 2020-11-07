package net.dirtcraft.dirtbot.modules;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.conversion.Path;
import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.commands.misc.Analytics;
import net.dirtcraft.dirtbot.internal.configs.ConfigurationManager;
import net.dirtcraft.dirtbot.internal.configs.IConfigData;
import net.dirtcraft.dirtbot.internal.embeds.EmbedUtils;
import net.dirtcraft.dirtbot.internal.modules.Module;
import net.dirtcraft.dirtbot.internal.modules.ModuleClass;
import net.dirtcraft.dirtbot.utils.analytics.AnalyticsDatabaseHelper;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;

@ModuleClass(eventSubscriber = false, requiresDatabase = true)
public class AnalyticsModule extends Module<AnalyticsModule.ConfigDataAnalytics, AnalyticsModule.EmbedUtilsAnalytics> {

    private AnalyticsDatabaseHelper database;

    @Override
    public void initialize() {
        setEmbedUtils(new AnalyticsModule.EmbedUtilsAnalytics());
        database = new AnalyticsDatabaseHelper(this);
        DirtBot.getCoreModule().registerCommands(new Analytics());
    }

    @Override
    public void initializeConfiguration() {
        ConfigSpec spec = new ConfigSpec();

        spec.define("database.url", "jdbc:mariadb://localhost:3306/analytics");
        spec.define("database.user", "");
        spec.define("database.password", "");

        spec.define("discord.embeds.footer", "Created for DirtCraft");
        spec.define("discord.embeds.title", "<:redbulletpoint:539273059631104052> DirtCraft's DirtBOT <:redbulletpoint:539273059631104052>");
        spec.define("discord.embeds.color", 16711680);

        setConfig(new ConfigurationManager<>(AnalyticsModule.ConfigDataAnalytics.class, spec, "Analytics"));
    }

    public static class ConfigDataAnalytics implements IConfigData {
        @Path("database.url")
        public String databaseUrl;
        @Path("database.user")
        public String databaseUser;
        @Path("database.password")
        public String databasePassword;

        @Path("discord.embeds.footer")
        public String embedFooter;
        @Path("discord.embeds.title")
        public String embedTitle;
        @Path("discord.embeds.color")
        public int embedColor;
    }

    public class EmbedUtilsAnalytics extends EmbedUtils {
        @Override
        public EmbedBuilder getEmptyEmbed() {
            return new EmbedBuilder()
                    .setTitle(getConfig().embedTitle)
                    .setColor(getConfig().embedColor)
                    .setFooter(getConfig().embedFooter, null)
                    .setTimestamp(Instant.now());
        }
    }

    public AnalyticsDatabaseHelper getDatabase() {
        return database;
    }

}
