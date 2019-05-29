package net.dirtcraft.dirtbot.internal.configs;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;

import java.util.function.Supplier;

public class ConfigurationManager<T extends IConfigData> {

    private T config;

    private Class<T> clazz;

    private ConfigSpec configSpec;

    private String name;

    public ConfigurationManager(Class<T> clazz, ConfigSpec spec, String name) {
        this.clazz = clazz;
        this.configSpec = spec;
        this.name = name;
    }

    public ConfigSpec getConfigSpec() { return configSpec; }

    public T getConfig() { return config; }

    public String getFileName() { return name; }

    public void buildConfig() {
        FileConfig configFile = FileConfig.of("configs/" + getFileName() + ".toml");
        configFile.load();
        if(!getConfigSpec().isCorrect(configFile)) {
            getConfigSpec().correct(configFile);
            configFile.save();
        }

        config = new ObjectConverter().toObject(configFile, getSupplier());
        configFile.close();
    }

    private Supplier<T> getSupplier() {
        Supplier<T> supplier = () -> {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        };
        return supplier;
    }
}
