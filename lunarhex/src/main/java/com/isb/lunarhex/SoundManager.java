package com.isb.lunarhex;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * The sound manager handles sound and music for the game.
 *
 * @author Ian Baker
 */
public class SoundManager
{
    /**
     * Reference to the media player used for sound
     */
    private static MediaPlayer soundMediaPlayer;

    /**
     * Reference to the media player used for music
     */
    private static MediaPlayer musicMediaPlayer;

    /**
     * The last position of the music before pausing
     */
    private static int lastMusicPosition;

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
        SoundManager.context = context;
        soundVolume = PlayerData.getSoundVolume() / 100f;
        musicVolume = PlayerData.getMusicVolume() / 100f;
    }

    /**
     * Plays the sound file passed in.
     *
     * @param   audioID - The resource id for the audio to be played
     */
    public static void play(int audioID)
    {
        if (soundVolume > 0)
        {
            if (soundMediaPlayer != null)
            {
                soundMediaPlayer.release();
                soundMediaPlayer = null;
            }
            soundMediaPlayer = MediaPlayer.create(context, audioID);
            if (soundMediaPlayer != null)
            {
                soundMediaPlayer.setVolume(soundVolume, soundVolume);
                soundMediaPlayer.start();
            }
        }
    }

    /**
     * Plays the music file passed in.
     *
     * @param   audioID - The resource id for the audio to be played
     */
    public static void playMusic(int audioID)
    {
        if (musicVolume > 0)
        {
            if (musicMediaPlayer != null)
            {
                musicMediaPlayer.release();
                musicMediaPlayer = null;
            }
            musicMediaPlayer = MediaPlayer.create(context, audioID);
            if (musicMediaPlayer != null)
            {
                musicMediaPlayer.setVolume(musicVolume, musicVolume);
                musicMediaPlayer.setLooping(true);
                musicMediaPlayer.seekTo(lastMusicPosition);
                musicMediaPlayer.start();
            }
        }
    }

    /**
     * Stop the music from playing recording its current position.
     */
    public static void stopMusic()
    {
        if (musicMediaPlayer != null)
        {
            if (musicMediaPlayer.isPlaying())
            {
                musicMediaPlayer.pause();
                lastMusicPosition = musicMediaPlayer.getCurrentPosition();
            }
        }
    }

    /**
     * Checks whether the music is currently playing.
     *
     * @return  Boolean whether the music is currently playing
     */
    public static boolean musicPlaying()
    {
        if (musicMediaPlayer != null)
        {
            return musicMediaPlayer.isPlaying();
        }
        return false;
    }

    /**
     * Sets the volume of sounds to be played.
     *
     * @param   volume - Volume between 1.0 and 0.0
     */
    public static void setSoundVolume(float volume)
    {
        SoundManager.soundVolume = volume;
        if (soundMediaPlayer != null)
        {
            soundMediaPlayer.setVolume(soundVolume, soundVolume);
        }
    }

    /**
     * Sets the volume of music to be played.
     *
     * @param   volume - Volume between 1.0 and 0.0
     */
    public static void setMusicVolume(float volume)
    {
        SoundManager.musicVolume = volume;
        if (musicMediaPlayer != null)
        {
            musicMediaPlayer.setVolume(musicVolume, musicVolume);
        }
    }
}