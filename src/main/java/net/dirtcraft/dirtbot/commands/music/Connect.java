package net.dirtcraft.dirtbot.commands.music;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.MusicCommand;
import net.dirtcraft.dirtbot.modules.MusicModule;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandClass(MusicModule.class)
public class Connect extends MusicCommand {


    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        if (event.getGuild().getAudioManager().isConnected())  return sendError(event, "I am already in a voice channel!");

        GuildVoiceState voiceState = event.getMember().getVoiceState();
        if (voiceState == null || !voiceState.inVoiceChannel()) return sendError(event, "You must be in a voice channel!");
        VoiceChannel voiceChannel = voiceState.getChannel();
        event.getGuild().getAudioManager().openAudioConnection(voiceChannel);

        EmbedBuilder embed = getModule().getEmbedUtils().getEmptyEmbed().setDescription("**Connected** to <#" + voiceChannel.getId() + ">");
        getModule().getEmbedUtils().sendResponse(embed.build(), event.getTextChannel());

        return true;
    }

    @Override
    public boolean validChannel(TextChannel channel) {
        return true;
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("connect", "join");
    }

    @Override
    public List<CommandArgument> args() {
        return Collections.emptyList();
    }

}
