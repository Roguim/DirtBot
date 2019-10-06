package net.dirtcraft.dirtbot.internal.modules;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.modules.CoreModule;
import net.dv8tion.jda.core.JDA;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ModuleRegistry {

    private List<Module> modules;

    public ModuleRegistry(CoreModule coreModule) {
        modules = new ArrayList<>();

        try {
            Reflections reflections = new Reflections("net.dirtcraft.dirtbot.modules");
            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(ModuleClass.class);

            for (Class<?> module : annotatedClasses) {
                if(!coreModule.getConfig().useDBModules && module.getAnnotation(ModuleClass.class).requiresDatabase()) continue;
                modules.add((Module) module.cast(module.getDeclaredConstructor().newInstance()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            DirtBot.pokeDevs(e);
        }
    }

    public void registerEventListeners(JDA jda) {
        for(Module module : modules) {
            if (module.getClass().getAnnotation(ModuleClass.class).eventSubscriber()) jda.addEventListener(module);
        }
    }

    public void initializeModules() {
        for(Module module : modules) {
            module.initialize();
        }
    }

    public void initializeModuleConfigurations() {
        for(Module module : modules) {
            module.initializeConfiguration();
        }
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        if(clazz.equals(CoreModule.class)) return clazz.cast(DirtBot.getCoreModule());
        for(Module module : modules) {
            if(clazz.isInstance(module)) return clazz.cast(module);
        }
        return null;
    }
}
