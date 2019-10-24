package net.dirtcraft.dirtbot.internal.commands;

import net.dirtcraft.dirtbot.modules.AppealModule;
import net.dv8tion.jda.api.entities.TextChannel;

public abstract class CommandAppealStaff extends CommandStaff {

    AppealModule module;

    public CommandAppealStaff(AppealModule module) {
        this.module = module;
    }

    @Override
    public boolean validChannel(TextChannel channel) {
        return module.getAppealUtils().isAppeal(channel);
    }

    public AppealModule getModule() { return module; }

}
