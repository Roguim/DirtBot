package net.dirtcraft.dirtbot.internal.modules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleClass {
    Class<? extends Module> classLiteral();
    boolean eventSubscriber() default true;
}
