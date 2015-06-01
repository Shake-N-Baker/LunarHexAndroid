package com.isb.lunarhex;

import android.graphics.Canvas;
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
     * Constructor for the menu.
     *
     * @param   screenWidth - The screen width
     * @param   screenHeight - The screen height
     */
    public Menu(int screenWidth, int screenHeight)
    {

    }

    public void initialize(Bundle state)
    {

    }

    public void touchHandle(MotionEvent motionEvent)
    {

    }

    public void update(Canvas canvas, float framesPerSecond)
    {
        // Clear board
        canvas.drawARGB(0xff, 0x00, 0x00, 0x00);
    }
}