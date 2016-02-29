package com.isb.lunarhex;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * The interface for an interactive view, such as the game view or the menu view.
 *
 * @author Ian Baker
 */
public interface InteractiveView
{
    void initialize(Bundle state);
    void update(Canvas canvas, float framesPerSecond);
    void handleTouch(MotionEvent motionEvent);
    void generateBackground();
}