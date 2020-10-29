package net.dirtcraft.dirtbot.modules;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.conversion.Path;
import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.internal.configs.ConfigurationManager;
import net.dirtcraft.dirtbot.internal.configs.IConfigData;
import net.dirtcraft.dirtbot.internal.embeds.EmbedUtils;
import net.dirtcraft.dirtbot.internal.modules.Module;
import net.dirtcraft.dirtbot.internal.modules.ModuleClass;
import net.dirtcraft.dirtbot.utils.verification.VerificationDatabaseHelper;
import net.dirtcraft.dirtbot.utils.verification.VerificationUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.time.Instant;
import java.util.Optional;

@ModuleClass(requiresDatabase = true)
public class VerificationModule extends Module<VerificationModule.ConfigDataVerification, VerificationModule.EmbedUtilsVerification> {

    private VerificationDatabaseHelper database;

    @Override
    public void initialize() {
        // Initialize Embed Utils
        setEmbedUtils(new VerificationModule.EmbedUtilsVerification());

        database = new VerificationDatabaseHelper(this);
    }

    @Override
    public void initializeConfiguration() {
        ConfigSpec spec = new ConfigSpec();

        spec.define("database.url", "jdbc:mariadb://localhost:3306/verification");
        spec.define("database.user", "");
        spec.define("database.password", "");

        spec.define("discord.embeds.footer", "DirtCraft's DirtBOT | 2019");
        spec.define("discord.embeds.title", ":redbulletpoint: DirtCraft's DirtBOT :redbulletpoint:");
        spec.define("discord.embeds.color", 16711680);

        spec.define("discord.channels.verificationChannelID", "");

        setConfig(new ConfigurationManager<>(ConfigDataVerification.class, spec, "Verification"));
    }

    public class EmbedUtilsVerification extends EmbedUtils {
        @Override
        public EmbedBuilder getEmptyEmbed() {
            return new EmbedBuilder()
                    .setTitle(getConfig().embedTitle)
                    .setColor(getConfig().embedColor)
                    .setFooter(getConfig().embedFooter, null)
                    .setTimestamp(Instant.now());
        }
    }

    public static class ConfigDataVerification implements IConfigData {
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

        @Path("discord.channels.verificationChannelID")
        public String verificationChannelID;
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().isBot() || event.getUser().isFake()) return;
        if (!event.getChannel().getId().equals(getConfig().verificationChannelID)) return;

        event.getReaction().removeReaction(event.getUser()).queue();

        String discordID = event.getUser().getId();

        String verificationCode;

        if (database.hasRecord(discordID).orElse(true)) {
            verificationCode = !database.isVerified(discordID) ? database.getLastCode(discordID).orElse(null) : null;
        } else {
            verificationCode = VerificationUtils.getSaltString();
            while (database.codeExists(verificationCode).orElse(false)) {
                verificationCode = VerificationUtils.getSaltString();
            }
        }

        if (!database.hasRecord(discordID).orElse(true)) database.createRecord(discordID, verificationCode);

        Optional<String> optionalUUID = database.getUUIDfromDiscordID(discordID);
        Optional<String> username;
        if (optionalUUID.isPresent()) username = database.getUsernameFromUUID(optionalUUID.get());
        else username = Optional.empty();

        EmbedBuilder verify = verificationCode != null ?
                getEmbedUtils().getEmptyEmbed().setDescription("Please enter **/verify " + verificationCode + "** in-game to verify your account") :
                getEmbedUtils().getErrorEmbed("Your account is already verified" + (username.map(s -> " with **" + s + "**!").orElse("!")));

        event.getUser().openPrivateChannel().queue(dm -> dm.sendMessage(verify.build()).queue());

    }

    public void updateChannelMessage() {

        EmbedBuilder verificationMessage = getEmbedUtils().getEmptyEmbed()
                .setTimestamp(null)
                .setDescription("Select :white_check_mark: to link your Discord & Minecraft account");

        TextChannel verificationChannel = DirtBot.getJda()
                .getGuildById(DirtBot.getConfig().serverID)
                .getTextChannelById(getConfig().verificationChannelID);

        if (verificationChannel.hasLatestMessage()) {
            verificationChannel
                    .getIterableHistory()
                    .forEach(messages -> messages.delete().complete());
        }

        DirtBot.getJda()
                .getGuildById(DirtBot.getConfig().serverID)
                .getTextChannelById(getConfig().verificationChannelID)
                .sendMessage(verificationMessage.build())
                .queue(message -> {
                    message.addReaction("\u2705").queue();
                    message.pin().queue();
                });
    }

    public VerificationDatabaseHelper getVerificationDatabase() {
        return database;
    }
}
