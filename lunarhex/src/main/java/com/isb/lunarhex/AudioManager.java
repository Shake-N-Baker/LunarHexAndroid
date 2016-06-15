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
     * Initialization of the audio manager class.
     */
    public static void initialize(Context context)
    {
        AudioManager.context = context;
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
        mediaPlayer.start();
    }
}