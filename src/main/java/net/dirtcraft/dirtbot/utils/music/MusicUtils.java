package net.dirtcraft.dirtbot.utils.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dirtcraft.dirtbot.internal.embeds.EmbedUtils;
import net.dirtcraft.dirtbot.modules.MusicModule;
import net.dv8tion.jda.core.EmbedBuilder;
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
        AudioSourceManagers.registerRemoteSources(manager);
    }

    public synchronized void loadTrack(MessageReceivedEvent event, String identifier) {
        Guild guild = event.getGuild();
        AudioPlayer player = getOrCreatePlayer(guild);

        manager.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
            EmbedUtils embedUtils = module.getEmbedUtils();
            @Override
            public void trackLoaded(AudioTrack track) {
                String duration = DurationFormatUtils.formatDuration(track.getInfo().length, "mm:ss");

                EmbedBuilder embed = embedUtils.getEmptyEmbed()
                        .setDescription("Now playing: **" + track.getInfo().title + "**")
                        .setFooter("Duration: " + duration, null);
                if (track.getInfo().uri.contains("youtube") || track.getInfo().uri.contains("youtu.be")) {
                    String[] split = track.getInfo().uri.replace("watch?v=", "").split("/");
                    String icon = "https://img.youtube.com/vi/" + split[split.length - 1] + "/default.jpg";
                    embed.setThumbnail(icon);
                }
                embedUtils.sendResponse(embed.build(), event.getTextChannel());
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
                embedUtils.sendError(event, "Could not find any tracks for: `" + identifier.replace("ytsearch:", "") + "`");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                embedUtils.sendError(event, "Unable to load track: `" + identifier.replace("ytsearch:", "") + "`!");
            }
        });
    }

    public AudioPlayer getOrCreatePlayer(Guild guild) {
        if (playerMap.containsKey(guild.getId())) return playerMap.get(guild.getId()).getKey();
        AudioPlayer player = manager.createPlayer();
        TrackManager manager = new TrackManager(player);
        player.addListener(manager);
        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
        playerMap.put(guild.getId(), new AbstractMap.SimpleEntry<>(player, manager));
        return player;
    }

    public TrackManager getOrCreateTrackManager(Guild guild) {
        if (playerMap.containsKey(guild.getId())) return playerMap.get(guild.getId()).getValue();
        AudioPlayer player = manager.createPlayer();
        TrackManager manager = new TrackManager(player);
        player.addListener(manager);
        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
        playerMap.put(guild.getId(), new AbstractMap.SimpleEntry<>(player, manager));
        return manager;
    }

}
