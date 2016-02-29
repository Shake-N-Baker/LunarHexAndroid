package com.isb.lunarhex;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * The main view surface.
 *
 * @author Ian Baker
 */
public class MainView extends SurfaceView implements Runnable
{
    /**
     * Constants
     */
    private static final int FRAME_RATE = 60;
    public static final int TRANSITION_TOTAL_FRAMES = 10;
    public static int FONT_SIZE_15_SP;
    public static int FONT_SIZE_30_SP;
    public static int FONT_SIZE_60_SP;
    private static int MS_PER_CYCLE;
    private static int SCREEN_WIDTH;
    private static int SCREEN_HEIGHT;
    public static Typeface RALEWAY_BOLD_FONT;

    /**
     * The start time in milliseconds of this cycle
     */
    private volatile long startTime;

    /**
     * Whether the game is running or not
     */
    public volatile boolean running = false;

    /**
     * The surface holder which contains the canvas
     */
    public SurfaceHolder holder;

    /**
     * The thread to run rendering in
     */
    public Thread renderThread = null;

    /**
     * The current view
     */
    public InteractiveView view;

    /**
     * The game instance
     */
    public Game game;

    /**
     * The menu instance
     */
    public Menu menu;

    /**
     * The foreground used in transitions
     */
    private static Bitmap transitionMask;

    /**
     * Flag whether a transition animation is being played between game states
     */
    private boolean transitioning;

    /**
     * Flag whether the transition is a fade out
     */
    private boolean transitioningOut;

    /**
     * The number of frame updates since transition started
     */
    private int transitionFramesPassed;

    /**
     * The event that triggered the transition
     */
    private CustomEvent transitionEvent;

    /// TODO: Remove debug FPS tracking variable
    /**
     * Float measurement of the frames per second
     */
    private float framesPerSecond = 0f;
    private List<Integer> totalTimes = new ArrayList<Integer>();

    /**
     * Reference to the list of boards to be used in the main set of boards.
     */
    private List<String> mainBoardSet;

    /**
     * Reference to the list of lists of boards. Each list represents boards of
     * index + 1 length minimum number of moves to solve. i.e. boardSet[0] is a
     * list of boards solved in 1 move. boardSet[1] = 2 move solves. etc.
     */
    private List<List<String>> boardSet;

    /**
     * Constructor for the game canvas.
     *
     * @param context - The context
     */
    public MainView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        MS_PER_CYCLE = 1000 / FRAME_RATE;
        holder = getHolder();

        // Set up fonts
        FONT_SIZE_15_SP = getContext().getResources().getDimensionPixelSize(R.dimen.font_size_15);
        FONT_SIZE_30_SP = getContext().getResources().getDimensionPixelSize(R.dimen.font_size_30);
        FONT_SIZE_60_SP = getContext().getResources().getDimensionPixelSize(R.dimen.font_size_60);
        RALEWAY_BOLD_FONT = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Bold.ttf");

        loadBoardSets(context);
    }

    /**
     * Initializes or re-initializes the current view with the given state.
     * Called on start up and when brought from background.
     *
     * @param   state - The state of the view
     */
    public void initialize(Bundle state)
    {
        // Set the screen resolution
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        SCREEN_HEIGHT = dm.heightPixels;
        SCREEN_WIDTH = dm.widthPixels;

        // Setup transition variables
        if (transitionMask == null)
        {
            transitionMask = Bitmap.createBitmap(SCREEN_WIDTH, SCREEN_HEIGHT, Bitmap.Config.ARGB_8888);
            transitionMask.eraseColor(Color.rgb(0, 0, 0));
        }
        transitioning = false;
        transitioningOut = false;
        transitionFramesPassed = 0;
        transitionEvent = null;

        // Setup Game and Menu
        game = new Game(this, SCREEN_WIDTH, SCREEN_HEIGHT, mainBoardSet, boardSet);
        menu = new Menu(this, SCREEN_WIDTH, SCREEN_HEIGHT);

        String savedView = "menu";
        if(state != null)
        {
            savedView = state.getString(MainActivity.STATE_VIEW);
        }
        if (savedView.equals("menu"))
        {
            view = menu;
        }
        else
        {
            view = game;
        }
        view.initialize(state);
    }

    /**
     * Load and setup the sets of boards.
     *
     * @param context - The context
     */
    private void loadBoardSets(Context context)
    {
        mainBoardSet = new ArrayList<String>();
        boardSet = new ArrayList<List<String>>();
        for (int i = 0; i < 20; i++)
        {
            List<String> moveSet = new ArrayList<String>();
            boardSet.add(moveSet);
        }

        // Fetch the boards from the raw resources and store them
        try
        {
            BufferedReader inputMainBoards = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.boards_main)));
            BufferedReader inputSmallSetBoards = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.boards_small)));
            try
            {
                String mainBoardString = inputMainBoards.readLine();
                String[] mainBoards = mainBoardString.split(",");
                for (int i = 0; i < mainBoards.length; i++)
                {
                    mainBoardSet.add(mainBoards[i]);
                }
                String smallBoardString = inputSmallSetBoards.readLine();
                String[] smallBoards = smallBoardString.split(",");
                for (int i = 0; i < smallBoards.length; i++)
                {
                    if (mainBoardSet.indexOf(smallBoards[i]) == -1)
                    {
                        int solveMoves = Integer.parseInt(String.valueOf(smallBoards[i].charAt(0)), 36);
                        boardSet.get(solveMoves - 1).add(smallBoards[i]);
                    }
                }
            }
            finally
            {
                inputMainBoards.close();
            }
        }
        catch (FileNotFoundException e)
        {
            Log.e("LunarHex", "Error loading file: " + e);
        }
        catch (IOException e)
        {
            Log.e("LunarHex", "Error reading file: " + e);
        }
    }

    /**
     * Called to resume the game.
     */
    public void resume()
    {
        running = true;
        startTime = System.currentTimeMillis();
        renderThread = new Thread(this);
        renderThread.start();
    }

    /**
     * Starts executing the active part of the classes' code. This method is called when
     * a thread is started that has been created with a class which implements Runnable.
     */
    @Override
    public void run()
    {
        while(running)
        {
            if(!holder.getSurface().isValid())
            {
                // Skip this while loop iteration and wait until it becomes valid
                continue;
            }

            // Sleep extra time to produce the desired frame rate
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            if (totalTime < MS_PER_CYCLE)
            {
                try
                {
                    Thread.sleep(MS_PER_CYCLE - totalTime);
                }
                catch(InterruptedException e)
                {
                    Log.e("LunarHex", "Thread sleep exception: " + e);
                }
            }
            startTime = System.currentTimeMillis();

            /// TODO: Remove debug set frames per second
            if (totalTime > 0)
            {
                framesPerSecond = 1000 / totalTime;
                totalTimes.add((int) totalTime);
                if (totalTimes.size() > 60)
                {
                    totalTimes.remove(0);
                    int sum = 0;
                    for (int i = 0; i < totalTimes.size(); i++)
                    {
                        sum += totalTimes.get(i);
                    }
                    framesPerSecond = (60 * 1000) / sum;
                }
            }

            cycle();
        }
    }

    /**
     * Called to pause the game.
     */
    public void pause()
    {
        running = false;
        boolean retry = true;
        while(retry)
        {
            try
            {
                renderThread.join();
                retry = false;
            }
            catch (InterruptedException e)
            {
                // Retry
            }
        }
    }

    /**
     * Handles the frame based logic.
     */
    private void cycle()
    {
        Canvas canvas = holder.lockCanvas();
        view.update(canvas, framesPerSecond);
        if (transitioning)
        {
            handleTransition(canvas);
        }
        holder.unlockCanvasAndPost(canvas);
    }

    /**
     * Handles the transition between game states, fade in and out.
     *
     * @param   canvas - The canvas to draw on
     */
    private void handleTransition(Canvas canvas)
    {
        int opacity = (int) (255 * ((double) transitionFramesPassed / (double) TRANSITION_TOTAL_FRAMES));
        if (opacity < 0)
        {
            opacity = 0;
        }
        else if (opacity > 255)
        {
            opacity = 255;
        }
        transitionMask.eraseColor(Color.argb(opacity, 0, 0, 0));
        canvas.drawBitmap(transitionMask, 0, 0, null);
        if (transitioningOut)
        {
            if (transitionFramesPassed > MainView.TRANSITION_TOTAL_FRAMES)
            {
                transitioning = false;
                handleEvent(transitionEvent);
            }
            transitionFramesPassed++;
        }
        else
        {
            if (transitionFramesPassed < 0)
            {
                transitioning = false;
            }
            transitionFramesPassed--;
        }
    }

    /**
     * Handles the view logic for touch events.
     *
     * @param   motionEvent - The touch motion event
     */
    public void handleTouch(MotionEvent motionEvent)
    {
        Touch.x = (int) motionEvent.getX();
        Touch.y = (int) motionEvent.getY();
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            Touch.downX = (int) motionEvent.getX();
            Touch.downY = (int) motionEvent.getY();
        }
        if (!transitioning)
        {
            view.handleTouch(motionEvent);
        }
    }

    /**
     * Handles custom events after transition.
     *
     * @param   event - The custom event
     */
    private void handleEvent(CustomEvent event)
    {
        if (event.isType(CustomEvent.NEW_CUSTOM_GAME))
        {
            game.currentLevel = -1;
            game.newBoardState();
            view = game;
        }
        else if (event.isType(CustomEvent.START_LEVEL))
        {
            game.currentLevel = Integer.parseInt(event.data);
            game.newBoardState();
            view = game;
        }
        else if (event.isType(CustomEvent.EXIT_GAME))
        {
            view = menu;
        }
        view.generateBackground();
        initializeFadeIn();
    }

    /**
     * Triggers the transition for the custom event.
     *
     * @param   event - The custom event
     */
    public void triggerEvent(CustomEvent event)
    {
        // Start fading out of the current screen
        transitioning = true;
        transitioningOut = true;
        transitionFramesPassed = 0;
        transitionEvent = event;
    }

    /**
     * Initializes a fade in transition.
     */
    private void initializeFadeIn()
    {
        // Start fading into the current screen
        transitioning = true;
        transitioningOut = false;
        transitionFramesPassed = MainView.TRANSITION_TOTAL_FRAMES;
    }
}