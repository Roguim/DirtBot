package net.dirtcraft.dirtbot.utils;

public class EmbedUtilsOld {

    /*

    public static EmbedBuilder getEmptyEmbed() {
        EmbedBuilder builder = new EmbedBuilder()
            .setTitle("<:redbulletpoint:539273059631104052> **DirtCraft's DirtBot** <:redbulletpoint:539273059631104052>")
            .setColor(DirtBot.getConfigurationManager().getConfig().embedColor)
            .setFooter(DirtBot.getConfigurationManager().getConfig().footer, null)
            .setTimestamp(Instant.now());
        return builder;
    }

    public static EmbedBuilder getExternalEmbed() {
        return getEmptyEmbed().setTitle("\u26A0 **DirtCraft's DirtBot** \u26A0");
    }

    public static EmbedBuilder getTicketLogEmbed(String eventName, String eventInfo, Ticket ticket, Member member) {
        EmbedBuilder builder = getEmptyEmbed()
            .addField("__Ticket Event | " + eventName + "__", eventInfo, false)
            .addField(getTicketLogInformationField(ticket, member));
        return builder;
    }

    /*
    public static EmbedBuilder getAppealLogEmbed(String eventName, String eventInfo, Appeal appeal, Member member) {

    }

    public static EmbedBuilder getErrorEmbed(String error) {
        EmbedBuilder builder = getEmptyEmbed()
                .addField("__Error!__", error, false);
        return builder;

    }

    public static EmbedBuilder invalidArgsEmbed(List<CommandArgument> args, String alias) {
        String commandSchema = DirtBot.getConfigurationManager().getConfig().prefix + alias;
        String argsInfo = "";
        for(CommandArgument arg : args) {
            commandSchema += " " + arg.getName().toLowerCase();
            argsInfo += "**" + arg.getName() + "**: " + arg.getDescription() + "\n";
        }
        EmbedBuilder builder = getEmptyEmbed()
                .addField("__Error | Invalid Arguments!__", "**" + commandSchema + "**\n" + argsInfo, false);
        return builder;
    }

    public static EmbedBuilder getReviewEmbed(boolean external) {
        EmbedBuilder reviewEmbed = null;
        if(external) reviewEmbed = getExternalEmbed();
        else reviewEmbed = getEmptyEmbed();
                reviewEmbed.addField(
                        "__Review__",
                        "Please consider leaving a review on your experience from our staff on DirtCraft." +
                                "\nWe appreciate the review and hope you enjoy your time on DirtCraft!" + "\n" +
                                "[Click me to leave a **review!**](https://ftbservers.com/server/Z0DoHV0S/dirtcraft-modded-servers)", false);
        return reviewEmbed;
    }

    public static MessageEmbed.Field getTicketLogInformationField(Ticket ticket, Member member) {
        return new MessageEmbed.Field("__Ticket Information__",
                "**Ticket ID:** " + ticket.getId() + "\n" +
                        "**Ticket Channel:** <#" + ticket.getChannel() + ">\n" +
                        "**Action Completed By:** <@" + member.getUser().getId() + ">", false);
    }


    public static MessageEmbed.Field getTicketLogInformationField(Ticket ticket) {
        return new MessageEmbed.Field("__Ticket Information__",
                "**Ticket ID:** " + ticket.getId() + "\n" +
                        "**Ticket Channel:** <#" + ticket.getChannel() + ">", false);
    }

    public static MessageEmbed.Field getTicketInformationField(Ticket ticket) {
        String membersFormatted = "";
        for(Member member : getMembers(DirtBot.getJda().getTextChannelById(ticket.getChannel()))) {
            membersFormatted += "<@" + member.getUser().getId() + ">, ";
        }
        membersFormatted = membersFormatted.substring(0, membersFormatted.length() - 2);
        return new MessageEmbed.Field("__Ticket Information__",
                "**Ticket ID:** " + ticket.getId() + "\n" +
                "**Member(s):** " + membersFormatted + "\n" +
                "**Username:** " + ticket.getUsername(false) + "\n" +
                "**Server:** " + ticket.getServer(false).toUpperCase() + "\n" +
                "**Level:** " + ticket.getLevel(), false);
    }

    public static String getAppealInfo(Appeal appeal) {
        String appealInfo = "";
        appealInfo += "**Username:** " + appeal.getUsername();
        appealInfo += "\n**Server:** " + appeal.getServer();
        appealInfo += "\n**Punishment Type:** " + appeal.getPunishmentType();
        appealInfo += "\n**Punisher:** <@" +appeal.getStaff() + ">";
        appealInfo += "\n**Channel:** <#" + appeal.getChannelID() + ">";
        appealInfo += "\n**Explanation:** " + appeal.getExplanation();
        return appealInfo;
    }

    public static void sendLog(LogType type, EmbedBuilder message) {
        String loggingChannelID = DirtBot.getConfigurationManager().getConfig().ticketLoggingChannelID;
        switch(type) {
            case TICKET:
                loggingChannelID = DirtBot.getConfigurationManager().getConfig().ticketLoggingChannelID;
                break;
            case APPEAL:
                loggingChannelID = DirtBot.getConfigurationManager().getConfig().appealLoggingChannelID;
                break;
        }
        DirtBot.getJda().getTextChannelById(loggingChannelID).sendMessage(message.build()).queue();
    }

    public static void sendLog(LogType type, String eventName, String eventInfo, Ticket ticket, Member member) {
        sendLog(type, getTicketLogEmbed(eventName, eventInfo, ticket, member));
    }

    public static List<Member> getMembers(Channel channel) {
        List<Member> members = new ArrayList<>();
        for(PermissionOverride po : channel.getPermissionOverrides()) {
            if(po.isMemberOverride() && !po.getMember().getUser().isBot()) members.add(po.getMember());
        }
        return members;
    }

    public enum LogType {
        TICKET,
        APPEAL
    }

    */

}
