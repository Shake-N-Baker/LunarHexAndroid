package com.isb.lunarhex;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * The menu class.
 *
 * @author Ian Baker
 */
public class Menu implements InteractiveView
{
    /**
     * Constants
     */
    private static final float TITLE_X_PERCENT = 66f / 100f;
    private static final float TITLE_Y_PERCENT = 20f / 100f;
    private static final float LEVELS_TOP_LEFT_X_PERCENT = 50f / 100f;
    private static final float LEVELS_TOP_LEFT_Y_PERCENT = 74f / 100f;
    private static final float LEVELS_SPACING_X_PERCENT = 43f / 100f;
    private static final float TRANSITION_DISTANCE_X_PERCENT = 2f / 100f;
    private static int TITLE_X;
    private static int TITLE_Y;
    private static int LEVELS_TOP_LEFT_X;
    private static int LEVELS_TOP_LEFT_Y;
    private static int LEVELS_SPACING_X;
    private static int TRANSITION_DISTANCE_X;

    /**
     * Reference to the main view.
     */
    MainView mainView;

    /**
     * The screen width
     */
    private int screenWidth;

    /**
     * The screen height
     */
    private int screenHeight;

    /**
     * The background image
     */
    private static Bitmap background;

    /**
     * The paint used for the title
     */
    private Paint titlePaint;

    /**
     * The paint used for the text
     */
    private Paint textPaint;

    /**
     * Flag whether the player is dragging his finger across the screen
     */
    private boolean dragging;

    /**
     * The initial value of the screen offset when drag touch event began
     */
    private int dragOffsetStart;

    /**
     * The magnitude of the canvas translation for the screen offset
     */
    private int screenOffset;

    /**
     * Constructor for the menu.
     *
     * @param   main - The reference to the main view
     * @param   screenWidth - The screen width
     * @param   screenHeight - The screen height
     */
    public Menu(MainView main, int screenWidth, int screenHeight)
    {
        this.mainView = main;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        TITLE_X = Math.round(TITLE_X_PERCENT * screenWidth);
        TITLE_Y = Math.round(TITLE_Y_PERCENT * screenHeight);
        LEVELS_TOP_LEFT_X = Math.round(LEVELS_TOP_LEFT_X_PERCENT * screenWidth);
        LEVELS_TOP_LEFT_Y = Math.round(LEVELS_TOP_LEFT_Y_PERCENT * screenHeight);
        LEVELS_SPACING_X = Math.round(LEVELS_SPACING_X_PERCENT * screenWidth);
        TRANSITION_DISTANCE_X = Math.round(TRANSITION_DISTANCE_X_PERCENT * screenWidth);

        // Setup the paints used for text
        titlePaint = new Paint();
        titlePaint.setColor(Color.argb(255, 255, 255, 255));
        titlePaint.setTextSize(MainView.FONT_SIZE_60_SP);
        titlePaint.setTypeface(MainView.RALEWAY_BOLD_FONT);
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(MainView.FONT_SIZE_20_SP);
        textPaint.setTypeface(MainView.RALEWAY_BOLD_FONT);

        screenOffset = LEVELS_SPACING_X;
        dragging = false;
        dragOffsetStart = screenOffset;
    }

    /**
     * Initializes or re-initializes the menu with the given state.
     * Called on start up and when brought from background.
     *
     * @param   state - The state of the menu
     */
    public void initialize(Bundle state)
    {
        generateBackground();
    }

    /**
     * Handles the menu logic for touch events.
     *
     * @param   motionEvent - The touch motion event
     */
    public void handleTouch(MotionEvent motionEvent)
    {
        int tX = Touch.x + screenOffset;
        int tDownX = Touch.downX + screenOffset;

        if (motionEvent.getAction() == MotionEvent.ACTION_UP)
        {
            dragging = false;
        }
        else
        {
            if (!dragging)
            {
                dragOffsetStart = screenOffset;
            }
            dragging = true;
        }
        screenOffset = dragOffsetStart + tDownX - tX;
        if (screenOffset < 0)
        {
            screenOffset = 0;
        }
        else if (screenOffset > (30 * LEVELS_SPACING_X))
        {
            screenOffset = 30 * LEVELS_SPACING_X;
        }

        // Play
        //mainView.triggerEvent(new CustomEvent(CustomEvent.START_LEVEL, "0"));

        // Random
        // mainView.triggerEvent(new CustomEvent(CustomEvent.NEW_CUSTOM_GAME));

        // Source
        // Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Shake-N-Baker/LunarHexAndroid"));
        // mainView.getContext().startActivity(browserIntent);
    }

    /**
     * Handles the updates to the current canvas and menu frame logic.
     *
     * @param   canvas - The canvas to draw on
     * @param   framesPerSecond - The frames per second for debugging
     */
    public void update(Canvas canvas, float framesPerSecond)
    {
        if (!dragging)
        {
            if(screenOffset % LEVELS_SPACING_X != 0)
            {
                int distanceLeft;
                if (screenOffset % LEVELS_SPACING_X > (LEVELS_SPACING_X / 2))
                {
                    distanceLeft = LEVELS_SPACING_X - (screenOffset % LEVELS_SPACING_X);
                    if (distanceLeft > TRANSITION_DISTANCE_X)
                    {
                        screenOffset += TRANSITION_DISTANCE_X;
                    }
                    else
                    {
                        screenOffset += distanceLeft;
                    }
                }
                else
                {
                    distanceLeft = screenOffset % LEVELS_SPACING_X;
                    if (distanceLeft > TRANSITION_DISTANCE_X)
                    {
                        screenOffset -= TRANSITION_DISTANCE_X;
                    }
                    else
                    {
                        screenOffset -= distanceLeft;
                    }
                }
            }
        }
        float titleTransparency = 1 - ((float) Math.abs(LEVELS_SPACING_X - screenOffset) / (float) LEVELS_SPACING_X);
        if (titleTransparency < 0f)
        {
            titleTransparency = 0f;
        }
        titlePaint.setColor(Color.argb((int) (titleTransparency * 255), 255, 255, 255));
        drawMenu(canvas);
    }

    /**
     * Draws the menu.
     *
     * @param canvas - The canvas to draw the menu on
     */
    private void drawMenu(Canvas canvas)
    {
        int defaultMatrix = canvas.save();

        // Clear board
        canvas.drawARGB(0xff, 0x00, 0x00, 0x00);

        // Draw the background shifted to a smaller degree
        canvas.translate(-1 * (screenOffset / 10), 0);
        canvas.drawBitmap(background, 0, 0, null);

        // Shift everything else the full amount
        canvas.restoreToCount(defaultMatrix);
        canvas.translate(-1 * screenOffset, 0);

        // Draw title text
        canvas.drawText("LUNAR HEX", TITLE_X, TITLE_Y, titlePaint);

        // Draw random level text
        canvas.drawText("RANDOM", LEVELS_TOP_LEFT_X, LEVELS_TOP_LEFT_Y, textPaint);

        // Draw level texts
        for (int row = 1; row < 31; row++)
        {
            canvas.drawText(Integer.toString(row), (LEVELS_SPACING_X * row) + LEVELS_TOP_LEFT_X, LEVELS_TOP_LEFT_Y, textPaint);
        }

        canvas.restoreToCount(defaultMatrix);

        /// TODO: Remove draw debug down cursor
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        canvas.drawCircle(Touch.downX + screenOffset, Touch.downY, 5, paint);
        /// TODO: Remove draw debug cursor
        paint.setColor(Color.GREEN);
        canvas.drawCircle(Touch.x + screenOffset, Touch.y, 5, paint);
    }

    /**
     * Generates a new background.
     */
    public void generateBackground()
    {
        if (background == null)
        {
            background = Bitmap.createBitmap((int) (screenWidth * 2.29f), screenHeight, Bitmap.Config.ARGB_8888);
        }
        Utils.generateBackground(background, (int) (screenWidth * 2.29f), screenHeight);
    }
}