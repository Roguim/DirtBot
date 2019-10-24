package net.dirtcraft.dirtbot.utils.music;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public class AudioPlayerSendHandler implements AudioSendHandler {

    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame = null;

    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    @Override
    public boolean canProvide() {
        if (lastFrame == null) lastFrame = audioPlayer.provide();


        return lastFrame != null;
    }

	@Override
	public ByteBuffer provide20MsAudio() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public boolean isOpus() {
        return true;
    }

}