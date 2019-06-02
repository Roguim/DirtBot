package net.dirtcraft.dirtbot.utils.appeals;

import net.dirtcraft.dirtbot.data.Appeal;
import net.dirtcraft.dirtbot.modules.AppealModule;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PermissionOverride;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class AppealUtils {

    private AppealModule module;

    public AppealUtils(AppealModule module) { this.module = module; }

    public List<Member> getAppealMembers(TextChannel channel) {
        ArrayList<Member> members = new ArrayList<>();
        for(PermissionOverride po : channel.getMemberPermissionOverrides()) {
            if(!po.getMember().getUser().isBot()) members.add(po.getMember());
        }
        return members;
    }

    public String getAppealInfo(Appeal appeal) {
        String appealInfo = "";
        appealInfo += "**Username:** " + appeal.getUsername();
        appealInfo += "\n**Server:** " + appeal.getServer();
        appealInfo += "\n**Punishment Type:** " + appeal.getPunishmentType();
        appealInfo += "\n**Punisher:** <@" +appeal.getStaff() + ">";
        appealInfo += "\n**Channel:** <#" + appeal.getChannelID() + ">";
        appealInfo += "\n**Explanation:** " + appeal.getExplanation();
        return appealInfo;
    }

    public boolean isAppeal(TextChannel channel) {
        if(channel.getParent().getId().equals(module.getConfig().appealCategoryID)) return true;
        return false;
    }

}
