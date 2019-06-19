package net.dirtcraft.dirtbot.internal.embeds;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class EmbedUtils {

    public abstract EmbedBuilder getEmptyEmbed();

    public EmbedBuilder getErrorEmbed(String error) {
        return getEmptyEmbed()
                .setColor(10223635)
                .addField("__Error!__", error, false);
    }

    public EmbedBuilder invalidArgsEmbed(List<CommandArgument> args, String alias) {
        String commandSchema = "**" + DirtBot.getConfig().botPrefix + alias + "**";
        String argsInfo = "";
        for(CommandArgument arg : args) {
            commandSchema += " " + arg.getName().toLowerCase();
            argsInfo += "**" + arg.getName() + "**: " + arg.getDescription() + "\n";
        }
        return getEmptyEmbed()
                .addField("__Error | Invalid Arguments!__", "*" + commandSchema + "*" + "\n" + argsInfo, false);
    }

    public void sendResponse(MessageEmbed embed, TextChannel channel) {
        channel.sendMessage(embed).queue((message) -> message.delete().queueAfter(10, TimeUnit.SECONDS));
    }

    public MessageEmbed getReviewEmbed() {
        return getEmptyEmbed()
                .addField("__Review__",
                        "Please consider leaving a review on your experiences on DirtCraft and the support you have received.\n" +
                                "We appreciate the review and hope you enjoy your time on DirtCraft!\n" +
                                "[Click me to leave a **review!**](https://ftbservers.com/server/Z0DoHV0S/dirtcraft-modded-servers)", false).build();
    }
}
