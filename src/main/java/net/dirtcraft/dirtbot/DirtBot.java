package net.dirtcraft.dirtbot;

import net.dirtcraft.dirtbot.internal.modules.ModuleRegistry;
import net.dirtcraft.dirtbot.modules.CoreModule;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;

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
        jda = new JDABuilder(getConfig().botToken)
                .build()
                .awaitReady();
        jda.getPresence().setGame(Game.of(Game.GameType.STREAMING, "Booting Up...", "https://www.twitch.tv/dirtcraft/"));

        // Finish Primary Module Initialization
        coreModule.initialize();

        // Initialize Module Registry
        moduleRegistry = new ModuleRegistry();

        // Initialize Module Configurations
        moduleRegistry.initializeModuleConfigurations();

        // Initialize Modules
        moduleRegistry.initializeModules();

        // Register Module Listeners
        jda.addEventListener(coreModule);
        moduleRegistry.registerEventListeners(jda);

        System.out.println("DirtBot is now initialized");
        jda.getPresence().setGame(Game.of(Game.GameType.STREAMING, "on DirtCord", "https://www.twitch.tv/dirtcraft/"));

    }

    public static JDA getJda() {
        return jda;
    }

    public static CoreModule getCoreModule() { return coreModule; }

    public static CoreModule.ConfigDataCore getConfig() { return coreModule.getConfig(); }

    public static ModuleRegistry getModuleRegistry() { return moduleRegistry; }
}
