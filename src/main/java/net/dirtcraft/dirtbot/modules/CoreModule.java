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
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.File;

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

        spec.define("discord.roles.staffRoleID", "");
        spec.define("discord.roles.adminRoleID", "");

        spec.define("discord.serverID", "");

        setConfig(new ConfigurationManager<>(ConfigDataCore.class, spec, "DirtBot"));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        commandRegistry.executeCommand(event);
    }

    public static class ConfigDataCore implements IConfigData {
        @Path("bot.prefix")
        public String botPrefix;
        @Path("bot.token")
        public String botToken;

        @Path("discord.roles.staffRoleID")
        public String staffRoleID;
        @Path("discord.roles.adminRoleID")
        public String adminRoleID;

        @Path("discord.serverID")
        public String serverID;
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

}
