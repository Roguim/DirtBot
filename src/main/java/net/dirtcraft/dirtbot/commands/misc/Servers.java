package net.dirtcraft.dirtbot.commands.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.ICommand;
import net.dirtcraft.dirtbot.modules.CommandsModule;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandClass()
public class Servers implements ICommand {

    private final CommandsModule module;

    public Servers(CommandsModule module) {
        this.module = module;
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {

        //DirtBot.getConfig().servers.get(0).sort(String.CASE_INSENSITIVE_ORDER.thenComparing(Comparator.naturalOrder()));

        final ArrayList<String> pixelNames = new ArrayList<String>() {{
            add("Pixelmon RedStone");
            add("Pixelmon GlowStone");
            add("Pixelmon Lapis");
        }};

        final ArrayList<String> pixelIPs = new ArrayList<String>() {{
            add("**RED**.PIXELMON.GG");
            add("Coming Soon");
            add("Coming Soon");
        }};

        ArrayList<String> names = new ArrayList<>();

        ArrayList<String> ips = new ArrayList<>();

        for (int i = 0; i < DirtBot.getConfig().servers.size(); i++) {
            List<String> server = DirtBot.getConfig().servers.get(i);
            if (server.get(0).toLowerCase().contains("pixel")) continue;
            names.add(server.get(0));
            ips.add("**" + server.get(1).toUpperCase() + "**.DIRTCRAFT.GG");
        }

        MessageEmbed pixelmon = module.getEmbedUtils().getEmptyEmbed()
                .setTimestamp(null)
                .addField("__Pixelmon Servers__", String.join("\n", pixelNames), true)
                .addField("__Pixelmon IP's__", String.join("\n", pixelIPs), true)
                .setImage("https://cdn.discordapp.com/attachments/470444805931925518/594983906986426379/ezgif-2-0a96d0e47fd2.gif")
                .setFooter("You can connect to any server through HUB", null)
                .addField("__Network Hub__", "`PIXELMON.GG`", false)
                .build();

        MessageEmbed response = module.getEmbedUtils().getEmptyEmbed()
                .setTimestamp(null)
                .setFooter("You can connect to any server through HUB", null)
                .setImage("https://cdn.discordapp.com/attachments/470444805931925518/545741732541628416/Max-Resolution.gif")
                .addField("__ModPacks__", String.join("\n", names), true)
                .addField("__ModPack IP's__", String.join("\n", ips), true)
                .addField("__Network Hub__", "`DIRTCRAFT.GG`", false)
                .build();

        event.getTextChannel().sendMessage(pixelmon).complete(/*message -> message.getTextChannel().sendMessage(response).queue()*/);
        event.getTextChannel().sendMessage(response).complete();
        return true;
    }

    @Override
    public boolean hasPermission(Member member) {
        return true;
    }

    @Override
    public boolean validChannel(TextChannel channel) {
        return true;
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("ip", "ips", "servers", "join", "logon", "login");
    }

    @Override
    public List<CommandArgument> args() {
        return null;
    }
}
