package net.dirtcraft.dirtbot.commands.misc;

import com.google.gson.JsonObject;
import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.ICommand;
import net.dirtcraft.dirtbot.modules.CommandsModule;
import net.dirtcraft.dirtbot.utils.update.JsonUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@CommandClass
public class Update implements ICommand {

    private final CommandsModule module;

    public Update(CommandsModule module) {
        this.module = module;
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        JsonObject json = JsonUtils.getJsonFromURL("http://api.feed-the-beast.com/ss/api/JSON/pack");
        EmbedBuilder embed = module.getEmbedUtils().getEmptyEmbed();
        HashMap<String, String> versionMap = new HashMap<>();

        List<List<String>> servers = DirtBot.getConfig().servers.stream().sorted(Comparator.comparing(server -> server.get(0))).collect(Collectors.toList());

        for (List<String> server : servers) {
            String name = server.get(0).replaceAll("\\s", "");
            switch (name) {
                case "DireWolf20":
                    name = "FTBPresentsDirewolf20112";
                    break;
                case "FTBInfinityEvolved":
                    name = "FTBInfinity";
                    break;
                case "SkyAdventures":
                    name = "FTBSkyAdventures";
                    break;
                case "SkyFactory3":
                    name = "FTBPresentsSkyfactory3";
                    break;
                case "SkyOdyssey":
                    name = "FTBSkyOdyssey";
                    break;
                case "StoneBlock2":
                    name = "FTBPresentsStoneblock2";
                    break;
            }
            if (json.get(name) == null) continue;

            String currentVersion = server.get(4);
            String updatedVersion = json.get(name).getAsJsonObject()
                    .get("versions")
                    .getAsJsonArray()
                    .get(0)
                    .getAsJsonObject()
                    .get("version").getAsString();
            if (!currentVersion.equalsIgnoreCase(updatedVersion)) versionMap.put(server.get(0), currentVersion + " → " + updatedVersion);
        }

        if (!versionMap.isEmpty()) {
            embed.addField("__ModPack__", String.join("\n", versionMap.keySet()), true);
            embed.addField("__Version__", String.join("\n", versionMap.values()), true);
        } else embed.setDescription("**All ModPacks are up to date!**");

        event.getChannel().sendMessage(embed.build()).queue();
        return true;
    }

    @Override
    public boolean hasPermission(Member member) {
        return true;
    }

    @Override
    public boolean validChannel(TextChannel channel) {
        return channel.getGuild().getId().equalsIgnoreCase("568930779191574568");
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("update");
    }

    @Override
    public List<CommandArgument> args() {
        return null;
    }
}
