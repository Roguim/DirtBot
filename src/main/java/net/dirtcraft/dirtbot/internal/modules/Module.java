package net.dirtcraft.dirtbot.internal.modules;

import net.dirtcraft.dirtbot.internal.configs.ConfigurationManager;
import net.dirtcraft.dirtbot.internal.configs.IConfigData;
import net.dirtcraft.dirtbot.internal.embeds.EmbedUtils;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public abstract class Module<T extends IConfigData, U extends EmbedUtils> extends ListenerAdapter {

    ConfigurationManager<T> config;

    U embedUtil;

    public abstract void initialize();

    public abstract void initializeConfiguration();

    public ConfigurationManager<T> getConfigManager() {return config;}

    public T getConfig() { return getConfigManager().getConfig(); }

    protected void setConfig(ConfigurationManager<T> config) {
        this.config = config;
        config.buildConfig();
    }

    public U getEmbedUtils() { return embedUtil; }

    protected void setEmbedUtils(U embedUtil) { this.embedUtil = embedUtil; }


}
