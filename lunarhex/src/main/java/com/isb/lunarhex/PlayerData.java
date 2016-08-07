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
    }

    /**
     * Returns the level clear states, 0 = not cleared, 1 = cleared, 2 = optimal clear
     *
     * @return  The level clear states
     */
    public static String getLevelClearStates()
    {
        SharedPreferences sharedPrefs = PlayerData.activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPrefs.getString(PlayerData.activity.getString(R.string.save_level_clear_states), "000000000000000000000000000000");
    }

    /**
     * Returns whether the player is considered a new player or not based on level clear states.
     *
     * @return  Whether the player is new
     */
    public static boolean getNewPlayerStatus()
    {
        SharedPreferences sharedPrefs = PlayerData.activity.getPreferences(Context.MODE_PRIVATE);
        String clearStates = sharedPrefs.getString(PlayerData.activity.getString(R.string.save_level_clear_states), "000000000000000000000000000000");
        return clearStates.equals("000000000000000000000000000000");
    }

    /**
     * Updates the player data if the levels newest state is the best clear achieved.
     *
     * @param   level - The level to be updated zero based
     * @param   state - 0 for not cleared, 1 for cleared, 2 for optimally cleared
     */
    public static void updateLevelClearStates(int level, int state)
    {
        SharedPreferences sharedPrefs = PlayerData.activity.getPreferences(Context.MODE_PRIVATE);
        String currentStates = sharedPrefs.getString(PlayerData.activity.getString(R.string.save_level_clear_states), "000000000000000000000000000000");
        if (0 <= level && level <= 29)
        {
            int currentLevelState = Integer.valueOf(currentStates.substring(level, level + 1));
            if (state > currentLevelState && state <= 2)
            {
                String newStates = currentStates.substring(0, level) + String.valueOf(state) + currentStates.substring(level + 1, currentStates.length());
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PlayerData.activity.getString(R.string.save_level_clear_states), newStates);
                editor.apply();
            }
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
        SoundManager.setSoundVolume(volume / 100f);
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
        SoundManager.setMusicVolume(volume / 100f);
    }
}