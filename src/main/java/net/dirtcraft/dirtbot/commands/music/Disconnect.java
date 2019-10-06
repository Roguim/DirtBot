package net.dirtcraft.dirtbot.commands.music;

import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.MusicCommand;
import net.dirtcraft.dirtbot.modules.MusicModule;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@CommandClass(MusicModule.class)
public class Disconnect extends MusicCommand {

    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        AudioManager audioManager = event.getGuild().getAudioManager();
        if (!audioManager.isConnected()) return sendError(event, "I am not connected to a voice channel!");
        VoiceChannel voiceChannel = audioManager.getConnectedChannel();
        @Nullable
        GuildVoiceState voiceState = event.getMember().getVoiceState();
        if (voiceState == null || !voiceState.inVoiceChannel()) return sendError(event, "You must be connected to a voice channel!");
        if (!voiceChannel.getId().equalsIgnoreCase(voiceState.getChannel().getId())) return sendError(event, "You are not connected to the same channel as me!");
        audioManager.closeAudioConnection();
        EmbedBuilder embed = getModule().getEmbedUtils().getEmptyEmbed().setDescription("**Disconnected** from <#" + voiceChannel.getId() + ">");
        getModule().getEmbedUtils().sendResponse(embed.build(), event.getTextChannel());
        return true;
    }

    @Override
    public boolean validChannel(TextChannel channel) {
        return true;
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("disconnect", "leave", "quit");
    }

    @Override
    public List<CommandArgument> args() {
        return Collections.emptyList();
    }
}
