package com.isb.lunarhex;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

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
    private static final String RANDOM_GAME_TEXT = "RANDOM";
    private static final String ABOUT_TEXT = "ABOUT";
    private static final String TITLE_TEXT = "LUNAR HEX";
    private static final float TITLE_X_PERCENT = 23f / 100f;
    private static final float TITLE_Y_PERCENT = 20f / 100f;
    private static final float SELECTION_CIRCLE_X_PERCENT = 84f / 100f;
    private static final float SELECTION_CIRCLE_Y_PERCENT = 73f / 100f;
    private static final float LEVELS_TOP_LEFT_X_PERCENT = 50f / 100f;
    private static final float LEVELS_TOP_LEFT_Y_PERCENT = 74f / 100f;
    private static final float LEVELS_SPACING_X_PERCENT = 20f / 100f;
    private static final float TRANSITION_DISTANCE_X_PERCENT = 2f / 100f;
    private static final float MAX_DRAG_VELOCITY_X_PERCENT = 80f / 1000f;
    private static final float DRAG_VELOCITY_RESISTANCE_X_PERCENT = 1f / 1000f;
    private static final int BACKGROUND_OFFSET_DAMPENING_MAGNITUDE = 10;
    private static final int DRAGGING_VELOCITY_DAMPENING_MAGNITUDE = 10;
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
     * The offsets of the level texts in the X direction
     */
    private List<Integer> textCenterOffsetX;

    /**
     * The offsets of the level texts in the Y direction
     */
    private List<Integer> textCenterOffsetY;

    /**
     * The previous x coordinates of this drag event
     */
    private List<Integer> dragPathX;

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
        circlePaint.setColor(Color.argb(255, 168, 183, 225));
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(5f);

        // Setup the offset from center rectangles for each level text
        textCenterOffsetX = new ArrayList<Integer>();
        textCenterOffsetY = new ArrayList<Integer>();
        Rect temp = new Rect();
        textPaint.getTextBounds(RANDOM_GAME_TEXT, 0, RANDOM_GAME_TEXT.length(), temp);
        textCenterOffsetX.add(temp.width() / 2);
        textCenterOffsetY.add(temp.height() / 2);
        for (int i = 1; i < 31; i++)
        {
            temp = new Rect();
            textPaint.getTextBounds(String.valueOf(i), 0, String.valueOf(i).length(), temp);
            textCenterOffsetX.add(temp.width() / 2);
            textCenterOffsetY.add(temp.height() / 2);
        }
        temp = new Rect();
        textPaint.getTextBounds(ABOUT_TEXT, 0, ABOUT_TEXT.length(), temp);
        textCenterOffsetX.add(temp.width() / 2);
        textCenterOffsetY.add(temp.height() / 2);

        // Setup the screen offset and dragging variables
        screenOffset = 0;
        dragging = false;
        dragVelocity = 0;
        dragOffsetStart = screenOffset;
        dragPathX = new ArrayList<Integer>();
        for (int i = 0; i < 15; i++)
        {
            dragPathX.add(0);
        }
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
                    else if (level == 31)
                    {
                        // View Github page
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Shake-N-Baker/LunarHexAndroid"));
                        mainView.getContext().startActivity(browserIntent);
                    }
                    else
                    {
                        // Play selected level
                        mainView.triggerEvent(new CustomEvent(CustomEvent.START_LEVEL, String.valueOf(level - 1)));
                    }
                }
            }
            // Finished dragging, update momentum
            dragging = false;
            if (dragVelocity * ((dragPathX.get(dragPathX.size() - 1) + screenOffset) - tX) > 0)
            {
                // Dragging in the same direction, add onto the velocity
                dragVelocity += ((dragPathX.get(dragPathX.size() - 1) + screenOffset) - tX) / DRAGGING_VELOCITY_DAMPENING_MAGNITUDE;
            }
            else
            {
                // Dragging in opposite direction, reset the velocity
                dragVelocity = ((dragPathX.get(dragPathX.size() - 1) + screenOffset) - tX) / DRAGGING_VELOCITY_DAMPENING_MAGNITUDE;
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
                // Start of a drag event, record the screen offset, reset the path
                dragOffsetStart = screenOffset;
                for (int i = 0; i < dragPathX.size(); i++)
                {
                    dragPathX.set(i, Touch.x);
                }
            }
            dragging = true;

            // Shift the screen offset
            screenOffset = dragOffsetStart + tDownX - tX;
            if (screenOffset < 0)
            {
                screenOffset = 0;
            }
            else if (screenOffset > (31 * LEVELS_SPACING_X))
            {
                screenOffset = 31 * LEVELS_SPACING_X;
            }
        }
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
                else if (screenOffset > (31 * LEVELS_SPACING_X))
                {
                    screenOffset = 31 * LEVELS_SPACING_X;
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
        else
        {
            // Update the recent dragging path
            dragPathX.add(0, Touch.x);
            dragPathX.remove(dragPathX.size() - 1);
        }

        // Adjust the title text transparency to fade out when scrolling to levels other than the initial one
        float titleTransparency = 1 - ((float) screenOffset / (float) LEVELS_SPACING_X);
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
        canvas.translate(-1 * (screenOffset / BACKGROUND_OFFSET_DAMPENING_MAGNITUDE), 0);
        canvas.drawBitmap(background, 0, 0, null);

        // Shift everything else the full amount
        canvas.restoreToCount(defaultMatrix);
        canvas.translate(-1 * screenOffset, 0);

        // Draw title text
        canvas.drawText(TITLE_TEXT, TITLE_X, TITLE_Y, titlePaint);

        // Draw random level text
        canvas.drawText(RANDOM_GAME_TEXT, LEVELS_TOP_LEFT_X - textCenterOffsetX.get(0), LEVELS_TOP_LEFT_Y + textCenterOffsetY.get(0), textPaint);

        // Draw level texts
        for (int level = 1; level < 31; level++)
        {
            canvas.drawText(Integer.toString(level), (LEVELS_SPACING_X * level) + LEVELS_TOP_LEFT_X  - textCenterOffsetX.get(level), LEVELS_TOP_LEFT_Y + textCenterOffsetY.get(level), textPaint);
        }

        // Draw about game text
        canvas.drawText(ABOUT_TEXT, (LEVELS_SPACING_X * 31) + LEVELS_TOP_LEFT_X - textCenterOffsetX.get(31), LEVELS_TOP_LEFT_Y + textCenterOffsetY.get(31), textPaint);

        // Draw the selection circle
        int diff = screenOffset % LEVELS_SPACING_X;
        if (diff > (LEVELS_SPACING_X / 2))
        {
            diff -= LEVELS_SPACING_X;
            diff *= -1;
        }
        if (diff < SELECTION_CIRCLE_RADIUS)
        {
            circlePaint.setColor(Color.argb((int) (255 * (1f - ((float) diff / (float) SELECTION_CIRCLE_RADIUS))), 168, 183, 225));
            canvas.drawCircle(SELECTION_CIRCLE_X + screenOffset, SELECTION_CIRCLE_Y, SELECTION_CIRCLE_RADIUS - diff, circlePaint);
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
            background = Bitmap.createBitmap((int) (screenWidth * (1.00f + ((LEVELS_SPACING_X_PERCENT * 31f) / (float) BACKGROUND_OFFSET_DAMPENING_MAGNITUDE))), screenHeight, Bitmap.Config.ARGB_8888);
        }
        Utils.generateBackground(background, (int) (screenWidth * (1.00f + ((LEVELS_SPACING_X_PERCENT * 31f) / (float) BACKGROUND_OFFSET_DAMPENING_MAGNITUDE))), screenHeight);
    }
}