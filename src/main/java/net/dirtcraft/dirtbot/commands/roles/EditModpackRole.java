package net.dirtcraft.dirtbot.commands.roles;

import net.dirtcraft.dirtbot.DirtBot;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.ICommand;
import net.dirtcraft.dirtbot.modules.ServerRolesModule;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.security.auth.login.LoginException;
import java.util.*;

@CommandClass(ServerRolesModule.class)
public class EditModpackRole implements ICommand {

    private ServerRolesModule module;

    public EditModpackRole(ServerRolesModule module) {
        this.module = module;
    }

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        String name = args.get(0);
        name = name.replace("_", " ");
        if (event.getMessage().getEmotes().isEmpty()) return false;
        Emote emote = event.getMessage().getEmotes().get(0);

        try {
            JDA jda = JDABuilder.createLight(module.getConfig().serverRolesToken)
                    .build()
                    .awaitReady();
            Message message = jda.getTextChannelById("538491797152989184").retrieveMessageById("538574783319638026").complete();
            MessageEmbed embed = message.getEmbeds().get(0);
            EmbedBuilder builder = new EmbedBuilder(embed);
            String description = embed.getDescription();
            List<String> lines = Arrays.asList(description.split(System.getProperty("line.separator")));
            Optional<String> optionalLine = lines.stream().filter(string -> string.contains(emote.getId())).findFirst();
            if (optionalLine.isPresent()) {
                String line = optionalLine.get();
                int index = lines.indexOf(line);
                ArrayList<String> newLines = new ArrayList<>(lines);
                newLines.remove(index);
                String newLine = "<:" + emote.getName() + ":" + emote.getId() + "> — **" + name + "**";
                newLines.add(index, newLine);
                builder.setDescription(String.join("\n", newLines));
                message.editMessage(builder.build()).complete();
            }

            jda.shutdown();
            if (jda != null && jda.getStatus() != JDA.Status.SHUTDOWN && jda.getStatus() != JDA.Status.SHUTTING_DOWN) jda.shutdownNow();
            if (!optionalLine.isPresent()) return false;
        } catch (LoginException | InterruptedException exception) {
            exception.printStackTrace();
            DirtBot.pokeDevs(exception);
            return false;
        }

        Role role = DirtBot.getJda().getRolesByName(name.replaceAll("\\s+", ""), true).get(0);

        EmbedBuilder embed = module.getEmbedUtils().getEmptyEmbed();
        embed.setTitle("New Modpack Role Edited!");
        embed.setDescription("**<@&" + role.getId() + ">** role has been edited with the emoji <:" + emote.getName() + ":" + emote.getId() + ">\nUse <#538491797152989184> to add the new role!");
        event.getTextChannel().sendMessage(embed.build()).queue();
        return true;
    }

    @Override
    public boolean hasPermission(Member member) {
        return member.getRoles().contains(DirtBot.getJda().getRoleById(DirtBot.getConfig().ownerRoleID));
    }

    @Override
    public boolean validChannel(TextChannel channel) {
        return true;
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("editmodpackrole");
    }

    @Override
    public List<CommandArgument> args() {
        return Arrays.asList(
                new CommandArgument("name", "Name of new modpack role you would like to create", 1, 0),
                new CommandArgument("emoji", "Emoji for the new modpack role that will be created", 1, 0));
    }

}
