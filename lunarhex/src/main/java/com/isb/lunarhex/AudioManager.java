package com.isb.lunarhex;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * The audio manager handles sound and music for the game.
 *
 * @author Ian Baker
 */
public class AudioManager
{
    /**
     * Reference to the singleton media player
     */
    private static MediaPlayer mediaPlayer;

    /**
     * Reference to the context to be used
     */
    private static Context context;

    /**
     * The volume of all sounds to be played between 1.0 and 0.0
     */
    private static float soundVolume;

    /**
     * The volume of music to be played between 1.0 and 0.0
     */
    private static float musicVolume;

    /**
     * Initialization of the audio manager class.
     */
    public static void initialize(Context context)
    {
        AudioManager.context = context;
        soundVolume = PlayerData.getSoundVolume() / 100f;
        musicVolume = PlayerData.getMusicVolume() / 100f;
    }

    /**
     * Plays the audio file passed in.
     *
     * @param   audioID - The resource id for the audio to be played
     */
    public static void play(int audioID)
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = MediaPlayer.create(context, audioID);
        if (audioID == R.raw.hit || audioID == R.raw.slide || audioID == R.raw.tap)
        {
            mediaPlayer.setVolume(soundVolume, soundVolume);
        }
        else
        {
            mediaPlayer.setVolume(musicVolume, musicVolume);
        }
        mediaPlayer.start();
    }

    /**
     * Sets the volume of sounds to be played.
     *
     * @param   volume - Volume between 1.0 and 0.0
     */
    public static void setSoundVolume(float volume)
    {
        AudioManager.soundVolume = volume;
    }

    /**
     * Sets the volume of music to be played.
     *
     * @param   volume - Volume between 1.0 and 0.0
     */
    public static void setMusicVolume(float volume)
    {
        AudioManager.musicVolume = volume;
    }
}