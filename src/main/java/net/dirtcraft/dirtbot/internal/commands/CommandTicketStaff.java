package net.dirtcraft.dirtbot.internal.commands;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.modules.TicketModule;
import net.dv8tion.jda.core.entities.Member;

public abstract class CommandTicketStaff extends CommandTicket {

    public CommandTicketStaff(TicketModule module) {
        super(module);
    }

    @Override
    public boolean hasPermission(Member member) {
        if(member.getRoles().contains(DirtBot.getJda().getRoleById(DirtBot.getConfig().staffRoleID))) return true;
        else return false;
    }
}
