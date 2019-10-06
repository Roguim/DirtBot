package net.dirtcraft.dirtbot;

import net.dirtcraft.dirtbot.internal.modules.ModuleRegistry;
import net.dirtcraft.dirtbot.modules.CoreModule;
import net.dirtcraft.dirtbot.modules.MiscModule;
import net.dirtcraft.dirtbot.modules.VerificationModule;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

// You would rarely ever have to modify this class. New commands, features, etc. all belong in modules.
public class DirtBot {

    private static CoreModule coreModule;
    private static JDA jda;
    private static ModuleRegistry moduleRegistry;

    public static void main(String[] args) throws LoginException, InterruptedException {

        // Initialize Primary Module
        coreModule = new CoreModule();
        coreModule.initializeConfiguration();

        //Initialize Discord Bot
        jda = new JDABuilder(AccountType.BOT)
                .setToken(getConfig().botToken)
                .build()
                .awaitReady();
        jda.getPresence().setGame(Game.of(Game.GameType.STREAMING, "Booting Up...", "https://www.twitch.tv/dirtcraft/"));

        // Finish Primary Module Initialization
        coreModule.initialize();

        // Initialize Module Registry
        moduleRegistry = new ModuleRegistry(coreModule);

        // Initialize Module Configurations
        moduleRegistry.initializeModuleConfigurations();

        // Initialize Modules
        moduleRegistry.initializeModules();

        // Register Module Listeners
        jda.addEventListener(coreModule);
        moduleRegistry.registerEventListeners(jda);

        // Core Module Post-Init
        coreModule.postInitialize();

        // Update verification channel message
        moduleRegistry.getModule(VerificationModule.class).updateChannelMessage();
        // Initialize player count scheduler
        moduleRegistry.getModule(MiscModule.class).initPlayerCountScheduler();

        System.out.println("DirtBot is now initialized");
        jda.getPresence().setGame(Game.of(Game.GameType.STREAMING, "on DIRTCRAFT.GG", "https://www.twitch.tv/dirtcraft/"));

    }

    public static JDA getJda() {
        return jda;
    }

    public static CoreModule getCoreModule() { return coreModule; }

    public static CoreModule.ConfigDataCore getConfig() { return coreModule.getConfig(); }

    public static ModuleRegistry getModuleRegistry() { return moduleRegistry; }

    private static ArrayList<String> exceptionNotifications = new ArrayList<String>() {{
        add("177618988761743360");
        add("209865813849538560");
        add("248056002274918400");
        add("217768355833053185");
    }};

    public static void pokeDevs(Exception e) {
        for (String notification : exceptionNotifications) {
            DirtBot.getJda().getUserById(notification).openPrivateChannel().queue((privateChannel) -> {
                String[] exception = ExceptionUtils.getStackTrace(e).split("\\r?\\n");
                List<String> messageExceptions = new ArrayList<>();
                String workingException = "";
                for(String string : exception) {
                    if(workingException.length() + string.length() <= 1900) workingException += "\n" + string;
                    else {
                        messageExceptions.add(workingException);
                        workingException = string;
                    }
                }
                if(!workingException.equals("")) messageExceptions.add(workingException);
                privateChannel.sendMessage("**NEW ERROR**").queue((message) -> {
                    for(String string : messageExceptions) {
                        privateChannel.sendMessage("```" + string + "```").queue();
                    }
                });
            });
        }
    }
}
