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
        if (level > levelsCleared)
        {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putInt(PlayerData.activity.getString(R.string.save_levels_cleared), level);
            editor.putInt(PlayerData.activity.getString(R.string.save_best_clear_level) + String.valueOf(level), moves);
            editor.commit();
        }
        else if (moves < movesCleared)
        {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putInt(PlayerData.activity.getString(R.string.save_best_clear_level) + String.valueOf(level), moves);
            editor.commit();
        }
    }
}