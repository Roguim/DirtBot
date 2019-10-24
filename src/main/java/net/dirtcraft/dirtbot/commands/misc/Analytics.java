package net.dirtcraft.dirtbot.commands.misc;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.ICommand;
import net.dirtcraft.dirtbot.modules.AnalyticsModule;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandClass(AnalyticsModule.class)
public class Analytics implements ICommand {

    private final AnalyticsModule analytics;

    public Analytics() {
        this.analytics = DirtBot.getModuleRegistry().getModule(AnalyticsModule.class);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {

        EmbedBuilder embed = analytics.getEmbedUtils().getEmptyEmbed();

        Map<String, Integer> playtimeMap;
        if (args.isEmpty()) {
            playtimeMap = analytics.getDatabase().getPlaytimeMap();
            embed.setTitle("<:redbulletpoint:539273059631104052> **Showing Past 24 Hours** <:redbulletpoint:539273059631104052>");
            embed.setFooter(playtimeMap.size() + " new " + (playtimeMap.size() != 1 ? "players" : "player") + " in the past 24 hours.", null);
        }
        else {
            int days;
            try {
                days = Integer.parseInt(args.get(0));
            } catch (NumberFormatException exception) {
                days = 1;
            }
            playtimeMap = analytics.getDatabase().getPlaytimeMap(days);
            embed.setTitle("<:redbulletpoint:539273059631104052> **Showing Past " + 24 * days + " Hours** <:redbulletpoint:539273059631104052>");
            embed.setFooter(playtimeMap.size() + " new " + (playtimeMap.size() != 1 ? "players" : "player") + " in the past " + days * 24 + " hours.", null);
        }

        Set<String> usernames = playtimeMap.keySet();
        List<String> playtimes = playtimeMap.values().stream().map(i -> i + (i != 1 ? " minutes" : " minute")).collect(Collectors.toList());

        embed.addField("__Username__", String.join("\n", usernames), true);
        embed.addField("__Playtime__", String.join("\n", playtimes), true);

        event.getChannel().sendMessage(embed.build()).queue();

        return true;
    }

    @Override
    public boolean hasPermission(Member member) {
        String id = member.getUser().getId();
        return id.equalsIgnoreCase("209865813849538560") || id.equalsIgnoreCase("155688380162637825");
    }

    @Override
    public boolean validChannel(TextChannel channel) {
        return true;
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("analytics");
    }

    @Override
    public List<CommandArgument> args() {
        return Collections.singletonList(new CommandArgument("Days", "Amount of days to show analytics for", 0, 2, true));
    }
}
