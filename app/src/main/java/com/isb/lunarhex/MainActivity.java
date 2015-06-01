package com.isb.lunarhex;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * The main activity.
 *
 * @author Ian Baker
 */
public class MainActivity extends Activity
{
    /**
     * Save state names
     */
    public static final String STATE_VIEW = "mainView";
    public static final String STATE_BOARD = "boardState";
    public static final String STATE_INITIAL_BOARD = "initialBoardState";
    public static final String STATE_SOLUTION = "solution";

    /**
     * The view of the game.
     */
    private MainView mainView;

    /**
     * Called when the activity is starting.
     *
     * @param	savedInstanceState - re-initialized activity data or null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startWithState(savedInstanceState);
    }

    /**
     * This method is called after onStart() when the activity is being re-initialized from a previously
     * saved state, given here in savedInstanceState.
     *
     * @param   savedInstanceState - The data most recently supplied in onSaveInstanceState(Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        startWithState(savedInstanceState);
    }

    /**
     * Starts the game with the given saved or new state.
     *
     * @param   savedInstanceState - The most recently saved state
     */
    private void startWithState(Bundle savedInstanceState)
    {
        mainView = (MainView) findViewById(R.id.view);
        mainView.initialize(savedInstanceState);
    }

    /**
     * Called to retrieve per-instance state from an activity before being killed so that the state
     * can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @param   outState - Bundle in which to place your saved state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        // Save the state of the application
        if(mainView.view instanceof Game)
        {
            outState.putString(STATE_VIEW, "game");
            outState.putString(STATE_BOARD, mainView.game.boardState);
            outState.putString(STATE_INITIAL_BOARD, mainView.game.initialBoardState);
            outState.putStringArrayList(STATE_SOLUTION, mainView.game.solution);
        }
        else
        {
            outState.putString(STATE_VIEW, "menu");
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Called after onRestoreInstanceState, onRestart, or onPause, for your activity
     * to start interacting with the user.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        mainView.resume();
    }

    /**
     * Called as part of the activity lifecycle when an activity is going into the
     * background, but has not (yet) been killed. The counterpart to onResume.
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        mainView.pause();
    }

    /**
     * Implement this method to handle touch screen motion events.
     *
     * @param	event - The motion event
     * @return	True if the event was handled, false otherwise.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        mainView.touchHandle(event);
        return true;
    }
}