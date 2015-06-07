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
    public void touchHandle(MotionEvent motionEvent)
    {
        if (10 < motionEvent.getX() && motionEvent.getX() < 110 && motionEvent.getAction() == MotionEvent.ACTION_UP)
        {
            mainView.handleEvent(new CustomEvent(CustomEvent.NEW_CUSTOM_GAME));
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
        // Clear board
        canvas.drawARGB(0xff, 0x00, 0x00, 0x00);
        canvas.drawBitmap(background, 0, 0, null);

        Utils.drawHex(canvas, 10, 10, 100, 100, Color.RED, 0, true);

        /// TODO: Remove draw debug down cursor
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        canvas.drawCircle(Touch.downX, Touch.downY, 5, paint);
        /// TODO: Remove draw debug cursor
        paint.setColor(Color.GREEN);
        canvas.drawCircle(Touch.x, Touch.y, 5, paint);
    }

    /**
     * Generates a new background.
     */
    private void generateBackground()
    {
        if (background == null)
        {
            background = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        }
        Utils.generateBackground(background, screenWidth, screenHeight);
    }
}