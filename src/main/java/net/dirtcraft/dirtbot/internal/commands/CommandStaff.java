package net.dirtcraft.dirtbot.internal.commands;

import net.dirtcraft.dirtbot.DirtBot;
import net.dv8tion.jda.core.entities.Member;

public abstract class CommandStaff implements ICommand {

    @Override
    public boolean hasPermission(Member member) {
        return member.getRoles().contains(DirtBot.getJda().getRoleById(DirtBot.getConfig().staffRoleID));
    }

}
