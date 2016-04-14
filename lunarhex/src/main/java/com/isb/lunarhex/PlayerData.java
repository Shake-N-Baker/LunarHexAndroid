package com.isb.lunarhex;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * The player data class holds the players progress within the game.
 *
 * @author Ian Baker
 */
public class PlayerData
{
    /**
     * Reference to the activity
     */
    private static Activity activity;

    /**
     * Initialization of the player data class.
     *
     * @param   activity - The reference to the activity
     */
    public static void initialize(Activity activity)
    {
        PlayerData.activity = activity;
        /// TODO: Remove this when finished testing
        PlayerData.activity.getPreferences(Context.MODE_PRIVATE).edit().clear().commit();
    }

    /**
     * Returns the number of levels cleared.
     *
     * @return  The number of levels cleared
     */
    public static int getLevelsCleared()
    {
        SharedPreferences sharedPrefs = PlayerData.activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPrefs.getInt(PlayerData.activity.getString(R.string.save_levels_cleared), 0);
    }

    /**
     * Updates the player data to reflect the fact that the level was passed in
     * the specified number of moves.
     *
     * @param   level - The level that was passed
     * @param   moves - The number of moves taken to pass the level
     */
    public static void setSolveMoves(int level, int moves)
    {
        SharedPreferences sharedPrefs = PlayerData.activity.getPreferences(Context.MODE_PRIVATE);
        int levelsCleared = sharedPrefs.getInt(PlayerData.activity.getString(R.string.save_levels_cleared), 0);
        int movesCleared = sharedPrefs.getInt(PlayerData.activity.getString(R.string.save_best_clear_level) + String.valueOf(level), 99);
        if (level >= levelsCleared)
        {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putInt(PlayerData.activity.getString(R.string.save_levels_cleared), level + 1);
            editor.putInt(PlayerData.activity.getString(R.string.save_best_clear_level) + String.valueOf(level), moves);
            editor.apply();
        }
        else if (moves < movesCleared)
        {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putInt(PlayerData.activity.getString(R.string.save_best_clear_level) + String.valueOf(level), moves);
            editor.apply();
        }
    }

    /**
     * Returns the sound volume level 0 to 100.
     *
     * @return  The sound volume level
     */
    public static int getSoundVolume()
    {
        SharedPreferences sharedPrefs = PlayerData.activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPrefs.getInt(PlayerData.activity.getString(R.string.save_sound_volume), 100);
    }

    /**
     * Updates the player data with the new sound volume level.
     *
     * @param   volume - The sound volume level 0 to 100
     */
    public static void setSoundVolume(int volume)
    {
        SharedPreferences sharedPrefs = PlayerData.activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(PlayerData.activity.getString(R.string.save_sound_volume), volume);
        editor.apply();
    }

    /**
     * Returns the music volume level 0 to 100.
     *
     * @return  The music volume level
     */
    public static int getMusicVolume()
    {
        SharedPreferences sharedPrefs = PlayerData.activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPrefs.getInt(PlayerData.activity.getString(R.string.save_music_volume), 100);
    }

    /**
     * Updates the player data with the new music volume level.
     *
     * @param   volume - The music volume level 0 to 100
     */
    public static void setMusicVolume(int volume)
    {
        SharedPreferences sharedPrefs = PlayerData.activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(PlayerData.activity.getString(R.string.save_music_volume), volume);
        editor.apply();
    }
}