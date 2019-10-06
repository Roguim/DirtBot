package net.dirtcraft.dirtbot.modules;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.conversion.Path;
import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.internal.commands.CommandRegistry;
import net.dirtcraft.dirtbot.internal.commands.ICommand;
import net.dirtcraft.dirtbot.internal.configs.ConfigurationManager;
import net.dirtcraft.dirtbot.internal.configs.IConfigData;
import net.dirtcraft.dirtbot.internal.embeds.EmbedUtils;
import net.dirtcraft.dirtbot.internal.modules.Module;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// This is a special module that is loaded independently before the ModuleRegistry is initialized.
// This is where things that are required for other modules should go, such as the information required to initialize the JDA.
// Only use this when absolutely necessary!
// DO NOT ANNOTATE THIS. This module exists outside of the Module registry, and as such should NOT be annotated.
public class CoreModule extends Module<CoreModule.ConfigDataCore, CoreModule.EmbedUtilsCore> {

    private CommandRegistry commandRegistry;

    @Override
    public void initialize() {
        commandRegistry = new CommandRegistry();
    }

    @Override
    public void initializeConfiguration() {
        File configFolder = new File("configs");
        if(!configFolder.exists()) configFolder.mkdir();

        ConfigSpec spec = new ConfigSpec();

        spec.define("bot.prefix", "!");
        spec.define("bot.token", "");

        spec.define("discord.roles.donatorRoleID", "591145069810155530");
        spec.define("discord.roles.boosterRoleID", "581195961813172225");
        spec.define("discord.roles.staffRoleID", "");
        spec.define("discord.roles.adminRoleID", "");
        spec.define("discord.roles.networkRoleID", "591732856443895808");
        spec.define("discord.roles.ownerRoleID", "");

        spec.define("discord.serverID", "");

        spec.define("discord.channels.infoChannelID", "");
        spec.define("discord.channels.botspamChannelID", "");

        List<List<String>> serverListExample = new ArrayList<>();
        serverListExample.add(Arrays.asList("Server 1 Human Name", "Server 1 Code (int, sf3, etc.)", "Server 1 Support Category ID", "Server 1 Admin Support Channel ID", "Server 1 Version"));
        serverListExample.add(Arrays.asList("Server 2 Human Name", "Server 2 Code (int, sf3, etc.)", "Server 2 Support Category ID", "Server 2 Admin Support Channel ID", "Server 2 Version"));
        spec.defineList("servers", serverListExample, p -> p instanceof ArrayList && ((ArrayList) p).size() == 5);

        setConfig(new ConfigurationManager<>(ConfigDataCore.class, spec, "DirtBot"));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getMessage().getType() == MessageType.CHANNEL_PINNED_ADD && event.getAuthor().getId().equals(DirtBot.getJda().getSelfUser().getId())) {
            event.getMessage().delete().queue();
            return;
        }
        commandRegistry.executeCommand(event);
    }

    public static class ConfigDataCore implements IConfigData {
        @Path("bot.prefix")
        public String botPrefix;
        @Path("bot.token")
        public String botToken;

        @Path("discord.roles.donatorRoleID")
        public String donatorRoleID;
        @Path("discord.roles.boosterRoleID")
        public String boosterRoleID;
        @Path("discord.roles.staffRoleID")
        public String staffRoleID;
        @Path("discord.roles.adminRoleID")
        public String adminRoleID;
        @Path("discord.roles.networkRoleID")
        public String networkRoleID;
        @Path("discord.roles.ownerRoleID")
        public String ownerRoleID;

        @Path("discord.serverID")
        public String serverID;

        @Path("discord.channels.infoChannelID")
        public String infoChannelID;
        @Path("discord.channels.botspamChannelID")
        public String botspamChannelID;

        @Path("servers")
        public List<List<String>> servers;
    }

    public class EmbedUtilsCore extends EmbedUtils {
        @Override
        public EmbedBuilder getEmptyEmbed() {
            return null;
        }
    }

    public void registerCommands(ICommand... commands) {
        for(ICommand command : commands) {
            commandRegistry.registerCommand(command);
        }
    }

    public void postInitialize() {
        // Info Channel Header
        TextChannel infoChannel = DirtBot.getJda().getTextChannelById(getConfig().infoChannelID);
        infoChannel.getIterableHistory().queue((messageHistory) -> {
            for (Message message : messageHistory) message.delete().queue();
            infoChannel.sendMessage(getConfig().botPrefix + "servers").queue((serversCall) ->
                    infoChannel.sendMessage(getConfig().botPrefix + "shop").queue((shopCall) ->
                            infoChannel.sendMessage(getConfig().botPrefix + "vote").queue(/*(voteCall) -> {
                                MessageEmbed nitroMessage = new EmbedBuilder()
                                        .setColor(16728319)
                                        .setTitle("<:nitro:582673760298074120> **Nitro Boosts** <:nitro:582673760298074120>")
                                        .addField("__Discord Rewards__",
                                                "**•** More emote slots :heartbeat:\n" +
                                                "**•** Animated server icon :100:\n" +
                                                "**•** Better VC bitrate :musical_note:\n" +
                                                "**•** Up to 100 MB uploads for non-nitro users :sparkles:\n" +
                                                "**•** And much more :sparkling_heart:", false)
                                        .addField("__In-Game Rewards__", "Additionally, you will receive **$10,000** in-game coins for Nitro Boosting on a server of your choice.\n Please create a new ticket in <#576254302490722306> to claim your reward.\n\n[**Click me to learn more!**](https://support.discordapp.com/hc/en-us/articles/360028038352-Server-Boosting-)", false)
                                        .build();
                                infoChannel.sendMessage(nitroMessage).queueAfter(15, TimeUnit.SECONDS);
                            }))*/)));
        });
    }

}
