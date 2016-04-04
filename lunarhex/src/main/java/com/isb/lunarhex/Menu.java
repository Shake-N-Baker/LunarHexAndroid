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
    private static final float SELECTION_CIRCLE_X_PERCENT = 84f / 100f;
    private static final float SELECTION_CIRCLE_Y_PERCENT = 73f / 100f;
    private static final float LEVELS_TOP_LEFT_X_PERCENT = 50f / 100f;
    private static final float LEVELS_TOP_LEFT_Y_PERCENT = 74f / 100f;
    private static final float LEVELS_SPACING_X_PERCENT = 43f / 100f;
    private static final float TRANSITION_DISTANCE_X_PERCENT = 2f / 100f;
    private static final float MAX_DRAG_VELOCITY_X_PERCENT = 80f / 1000f;
    private static final float DRAG_VELOCITY_RESISTANCE_X_PERCENT = 1f / 1000f;
    private static int TITLE_X;
    private static int TITLE_Y;
    private static int SELECTION_CIRCLE_X;
    private static int SELECTION_CIRCLE_Y;
    private static int SELECTION_CIRCLE_RADIUS = 100;
    private static int LEVELS_TOP_LEFT_X;
    private static int LEVELS_TOP_LEFT_Y;
    private static int LEVELS_SPACING_X;
    private static int TRANSITION_DISTANCE_X;
    private static int MAX_DRAG_VELOCITY_X;
    private static int DRAG_VELOCITY_RESISTANCE_X;

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
     * The paint used for drawing the selection circle
     */
    private Paint circlePaint;

    /**
     * Flag whether the player is dragging his finger across the screen
     */
    private boolean dragging;

    /**
     * The velocity of the last drag event
     */
    private int dragVelocity;

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

        // Calculate the values based on screen measurements
        TITLE_X = Math.round(TITLE_X_PERCENT * screenWidth);
        TITLE_Y = Math.round(TITLE_Y_PERCENT * screenHeight);
        SELECTION_CIRCLE_X = Math.round(SELECTION_CIRCLE_X_PERCENT * screenHeight);
        SELECTION_CIRCLE_Y = Math.round(SELECTION_CIRCLE_Y_PERCENT * screenHeight);
        LEVELS_TOP_LEFT_X = Math.round(LEVELS_TOP_LEFT_X_PERCENT * screenWidth);
        LEVELS_TOP_LEFT_Y = Math.round(LEVELS_TOP_LEFT_Y_PERCENT * screenHeight);
        LEVELS_SPACING_X = Math.round(LEVELS_SPACING_X_PERCENT * screenWidth);
        TRANSITION_DISTANCE_X = Math.round(TRANSITION_DISTANCE_X_PERCENT * screenWidth);
        MAX_DRAG_VELOCITY_X = Math.round(MAX_DRAG_VELOCITY_X_PERCENT * screenWidth);
        DRAG_VELOCITY_RESISTANCE_X = Math.round(DRAG_VELOCITY_RESISTANCE_X_PERCENT * screenWidth);
        if (DRAG_VELOCITY_RESISTANCE_X < 1)
        {
            DRAG_VELOCITY_RESISTANCE_X = 1;
        }

        // Setup the paints used for text
        titlePaint = new Paint();
        titlePaint.setColor(Color.argb(255, 255, 255, 255));
        titlePaint.setTextSize(MainView.FONT_SIZE_60_SP);
        titlePaint.setTypeface(MainView.RALEWAY_BOLD_FONT);
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(MainView.FONT_SIZE_20_SP);
        textPaint.setTypeface(MainView.RALEWAY_BOLD_FONT);
        circlePaint = new Paint();
        circlePaint.setColor(Color.argb(210, 168, 183, 225));
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(5f);

        // Setup the screen offset and dragging variables
        screenOffset = LEVELS_SPACING_X;
        dragging = false;
        dragVelocity = 0;
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
            if (Utils.distanceBetweenPoints(tDownX, Touch.downY, tX, Touch.y) < SELECTION_CIRCLE_RADIUS)
            {
                if (Utils.distanceBetweenPoints(SELECTION_CIRCLE_X, SELECTION_CIRCLE_Y, Touch.x, Touch.y) < SELECTION_CIRCLE_RADIUS)
                {
                    if (Utils.distanceBetweenPoints(SELECTION_CIRCLE_X, SELECTION_CIRCLE_Y, Touch.downX, Touch.downY) < SELECTION_CIRCLE_RADIUS)
                    {
                        // Clicked the selection circle
                        int level = screenOffset / LEVELS_SPACING_X;
                        if (screenOffset % LEVELS_SPACING_X > (LEVELS_SPACING_X / 2))
                        {
                            level++;
                        }
                        if (level == 0)
                        {
                            // Random level
                            mainView.triggerEvent(new CustomEvent(CustomEvent.NEW_CUSTOM_GAME));
                        }
                        else
                        {
                            // Play selected level
                            mainView.triggerEvent(new CustomEvent(CustomEvent.START_LEVEL, String.valueOf(level - 1)));
                        }
                    }
                }
            }
            // Finished dragging, update momentum
            dragging = false;
            if (dragVelocity * (tDownX - tX) > 0)
            {
                // Dragging in the same direction, add onto the velocity
                dragVelocity += (tDownX - tX) / 10;
            }
            else
            {
                // Dragging in opposite direction, reset the velocity
                dragVelocity = (tDownX - tX) / 10;
            }
            if (dragVelocity > MAX_DRAG_VELOCITY_X)
            {
                dragVelocity = MAX_DRAG_VELOCITY_X;
            }
            else if (dragVelocity < -MAX_DRAG_VELOCITY_X)
            {
                dragVelocity = -MAX_DRAG_VELOCITY_X;
            }
        }
        else
        {
            // Begin dragging
            if (!dragging)
            {
                // Start of a drag event, record the screen offset
                dragOffsetStart = screenOffset;
            }
            dragging = true;
            screenOffset = dragOffsetStart + tDownX - tX;
            if (screenOffset < 0)
            {
                screenOffset = 0;
            }
            else if (screenOffset > (30 * LEVELS_SPACING_X))
            {
                screenOffset = 30 * LEVELS_SPACING_X;
            }
        }

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
            // Continue moving with the momentum of the finished drag events or scroll to the nearest level
            if (dragVelocity != 0)
            {
                // Adjust the screen offset based on the drag momentum
                screenOffset += dragVelocity;
                if (screenOffset < 0)
                {
                    screenOffset = 0;
                    dragVelocity = 0;
                }
                else if (screenOffset > (30 * LEVELS_SPACING_X))
                {
                    screenOffset = 30 * LEVELS_SPACING_X;
                    dragVelocity = 0;
                }

                // Reduce the velocity of the drag momentum
                if (dragVelocity > 0)
                {
                    dragVelocity -= DRAG_VELOCITY_RESISTANCE_X;
                    if (dragVelocity < 0)
                    {
                        dragVelocity = 0;
                    }
                }
                else
                {
                    dragVelocity += DRAG_VELOCITY_RESISTANCE_X;
                    if (dragVelocity > 0)
                    {
                        dragVelocity = 0;
                    }
                }
            }
            else
            {
                // Adjust the screen offset moving towards the nearest level
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
        }

        // Adjust the title text transparency to fade out when scrolling to levels other than the initial one
        float titleTransparency = 1 - ((float) Math.abs(LEVELS_SPACING_X - screenOffset) / (float) LEVELS_SPACING_X);
        if (titleTransparency < 0f)
        {
            titleTransparency = 0f;
        }
        titlePaint.setColor(Color.argb((int) (titleTransparency * 255), 255, 255, 255));

        // Draw the menu
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

        // Draw the selection circle
        if (!dragging && dragVelocity == 0 && screenOffset % LEVELS_SPACING_X == 0)
        {
            canvas.drawCircle(SELECTION_CIRCLE_X + screenOffset, SELECTION_CIRCLE_Y, SELECTION_CIRCLE_RADIUS, circlePaint);
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