package net.dirtcraft.dirtbot.utils.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dirtcraft.dirtbot.internal.embeds.EmbedUtils;
import net.dirtcraft.dirtbot.modules.MusicModule;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class MusicUtils {

    private static final AudioPlayerManager manager = new DefaultAudioPlayerManager();
    private static final HashMap<String, Map.Entry<AudioPlayer, TrackManager>> playerMap = new HashMap<>();

    private final MusicModule module;

    public MusicUtils(MusicModule module) {
        this.module = module;
    }

    public void loadTrack(MessageReceivedEvent event, String identifier) {
        Guild guild = event.getGuild();
        getOrCreatePlayer(guild);

        manager.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
            EmbedUtils embedUtils = module.getEmbedUtils();
            @Override
            public void trackLoaded(AudioTrack track) {
                String duration = DurationFormatUtils.formatDuration(track.getInfo().length, "mm:ss");
                MessageEmbed embed = embedUtils.getEmptyEmbed()
                        .setDescription("Now playing: **" + track.getInfo().title + "**")
                        .setFooter("Duration: " + duration, null)
                        .build();
                embedUtils.sendResponse(embed, event.getTextChannel());
                getOrCreateTrackManager(guild).queue(track, event.getMember());
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.getSelectedTrack() != null) trackLoaded(playlist.getSelectedTrack());
                else if (playlist.isSearchResult()) trackLoaded(playlist.getTracks().get(0));
                else {
                    for (AudioTrack track : playlist.getTracks()) {
                        getOrCreateTrackManager(guild).queue(track, event.getMember());
                    }
                    MessageEmbed embed = embedUtils.getEmptyEmbed()
                            .setDescription("Loaded Playlist: **" + playlist.getName() + "**")
                            .setFooter("Songs: " + playlist.getTracks().size(), null)
                            .build();
                    embedUtils.sendResponse(embed, event.getTextChannel());
                }
            }

            @Override
            public void noMatches() {
                embedUtils.sendError(event, "Could not find any tracks for: `" + identifier + "`");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                embedUtils.sendError(event, "Unable to load track: `" + identifier + "`!");
            }
        });
    }

    public AudioPlayer getOrCreatePlayer(Guild guild) {
        if (playerMap.containsKey(guild.getId())) return playerMap.get(guild.getId()).getKey();
        AudioPlayer newPlayer = manager.createPlayer();
        TrackManager manager = new TrackManager(newPlayer);
        newPlayer.addListener(manager);
        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(newPlayer));
        playerMap.put(guild.getId(), new AbstractMap.SimpleEntry<>(newPlayer, manager));
        return newPlayer;
    }

    public TrackManager getOrCreateTrackManager(Guild guild) {
        if (playerMap.containsKey(guild.getId())) return playerMap.get(guild.getId()).getValue();
        AudioPlayer newPlayer = manager.createPlayer();
        TrackManager manager = new TrackManager(newPlayer);
        newPlayer.addListener(manager);
        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(newPlayer));
        playerMap.put(guild.getId(), new AbstractMap.SimpleEntry<>(newPlayer, manager));
        return manager;
    }

}
