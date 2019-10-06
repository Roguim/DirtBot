package net.dirtcraft.dirtbot.internal.commands;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.modules.MusicModule;
import net.dirtcraft.dirtbot.utils.music.MusicUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class MusicCommand implements ICommand {

    @Override
    public boolean hasPermission(Member member) {
        Role donator = DirtBot.getJda().getRoleById(DirtBot.getConfig().donatorRoleID);
        Role nitroBooster = DirtBot.getJda().getRoleById(DirtBot.getConfig().boosterRoleID);
        Role owner = DirtBot.getJda().getRoleById(DirtBot.getConfig().ownerRoleID);
        return member.getRoles().contains(donator) || member.getRoles().contains(nitroBooster) || member.getRoles().contains(owner);
    }

    public MusicModule getModule() {
        return DirtBot.getModuleRegistry().getModule(MusicModule.class);
    }

    public MusicUtils getUtils() {
        return getModule().getUtils();
    }

    public boolean sendError(MessageReceivedEvent event, String error) {
        getModule().getEmbedUtils().sendError(event, error);
        return false;
    }

}
