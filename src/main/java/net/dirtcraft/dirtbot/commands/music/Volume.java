package net.dirtcraft.dirtbot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dirtcraft.dirtbot.internal.commands.CommandArgument;
import net.dirtcraft.dirtbot.internal.commands.CommandClass;
import net.dirtcraft.dirtbot.internal.commands.MusicCommand;
import net.dirtcraft.dirtbot.internal.embeds.EmbedUtils;
import net.dirtcraft.dirtbot.modules.MusicModule;
import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@CommandClass(MusicModule.class)
public class Volume extends MusicCommand {
    @Override
    public boolean execute(MessageReceivedEvent event, List<String> args) {
        AudioManager audioManager = event.getGuild().getAudioManager();
        if (!audioManager.isConnected()) return sendError(event, "I am not connected to a voice channel!");
        VoiceChannel voiceChannel = audioManager.getConnectedChannel();
        @Nullable
        GuildVoiceState voiceState = event.getMember().getVoiceState();
        if (voiceState == null || !voiceState.inVoiceChannel()) return sendError(event, "You must be connected to a voice channel!");
        if (!voiceChannel.getId().equalsIgnoreCase(voiceState.getChannel().getId())) return sendError(event, "You are not connected to the same channel as me!");

        AudioPlayer player = getUtils().getOrCreatePlayer(event.getGuild());
        EmbedUtils embedUtils = getModule().getEmbedUtils();
        if (args.isEmpty()) {
            MessageEmbed embed = embedUtils.getEmptyEmbed().setDescription("The volume is currently **" + player.getVolume() + "**").build();
            embedUtils.sendResponse(embed, event.getTextChannel());
            return true;
        }
        int volume;
        try {
            volume = Integer.parseInt(args.get(0));
        } catch (NumberFormatException exception) {
            return sendError(event, "The value **" + args.get(0) + "** is not a valid volume amount!");
        }

        player.setVolume(volume);
        MessageEmbed embed = embedUtils.getEmptyEmbed().setDescription("Volume has been set to **" + volume + "**").build();
        embedUtils.sendResponse(embed, event.getTextChannel());

        return true;
    }

    @Override
    public boolean validChannel(TextChannel channel) {
        return true;
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("volume");
    }

    @Override
    public List<CommandArgument> args() {
        return Collections.singletonList(new CommandArgument("Volume", "Sets the volume for the music currently playing", 1, 1, true));
    }
}
