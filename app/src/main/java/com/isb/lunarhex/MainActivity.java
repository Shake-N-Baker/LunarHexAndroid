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
     * The canvas.
     */
    private GameView view;

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
        view = (GameView) findViewById(R.id.view);
        view.initialize(savedInstanceState);
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
        outState.putString("boardState", view.boardState);
        outState.putString("initialBoardState", view.initialBoardState);
        outState.putStringArrayList("solution", view.solution);
        /// TODO: Save more

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
        view.resume();
    }

    /**
     * Called as part of the activity lifecycle when an activity is going into the
     * background, but has not (yet) been killed. The counterpart to onResume.
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        view.pause();
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
        view.touchHandle(event);
        return true;
    }
}