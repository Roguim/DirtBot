package net.dirtcraft.dirtbot.commands.music;

import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.MusicCommand;
import net.dirtcraft.dirtbot.modules.MusicModule;
import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@CommandClass(MusicModule.class)
public class Play extends MusicCommand {
    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {

        String url = args.get(0);

        if (!isUrl(url)) url = "ytsearch:" + url;

        AudioManager audioManager = event.getGuild().getAudioManager();
        if (!audioManager.isConnected()) return sendError(event, "Please connect me to a voice channel using **!connect**");

        GuildVoiceState voiceState = event.getMember().getVoiceState();
        if (voiceState == null || !voiceState.inVoiceChannel()) return sendError(event, "You must be in a voice channel!");
        VoiceChannel voiceChannel = voiceState.getChannel();
        if (!audioManager.getConnectedChannel().getId().equalsIgnoreCase(voiceChannel.getId())) return sendError(event, "We are not in the same voice channel!");
        //audioManager.setSendingHandler(new AudioPlayerHandler());
        //audioManager.openAudioConnection(voiceChannel);

        getUtils().loadTrack(event, url);

        return true;
    }

    private boolean isUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException exception) {
            return false;
        }
    }

    @Override
    public boolean validChannel(TextChannel channel) {
        return true;
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("play", "music");
    }

    @Override
    public List<CommandArgument> args() {
        return Collections.singletonList(new CommandArgument("URL", "URL of the song you want to play", 1, 0));
    }
}
