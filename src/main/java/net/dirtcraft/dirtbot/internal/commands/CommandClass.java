package net.dirtcraft.dirtbot.internal.commands;

import net.dirtcraft.dirtbot.internal.modules.Module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandClass {
    Class<? extends Module> value();
}
