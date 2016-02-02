package com.isb.lunarhex.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * The background panel class holds information for drawing a background panel
 * on a canvas to place user interface elements on top of.
 *
 * @author Ian Baker
 */
public class BackgroundPanel
{
    /**
     * The paint to use when drawing the panel to a canvas
     */
    private static Paint panelBackgroundPaint;
    private static Paint panelBorderPaint;

    /**
     * The rectangle for the background panel to fill
     */
    private RectF rect;

    /**
     * The path of the line separator of the panel
     */
    private Path lineSeparator;

    /**
     * Whether the panel has a line separator visible
     */
    public boolean hasLineSeparator;

    /**
     * Constructor for a background panel.
     *
     * @param   x - The x position of the panel
     * @param   y - The y position of the panel
     * @param   width - The width of the panel
     * @param   height - The height of the panel
     */
    public BackgroundPanel(int x, int y, int width, int height)
    {
        if (BackgroundPanel.panelBackgroundPaint == null)
        {
            BackgroundPanel.panelBackgroundPaint = new Paint();
            BackgroundPanel.panelBackgroundPaint.setStyle(Paint.Style.FILL);
            BackgroundPanel.panelBackgroundPaint.setColor(0x80B4D2FA);
            BackgroundPanel.panelBackgroundPaint.setStrokeWidth(0);
        }
        if (BackgroundPanel.panelBorderPaint == null)
        {
            BackgroundPanel.panelBorderPaint = new Paint();
            BackgroundPanel.panelBorderPaint.setStyle(Paint.Style.STROKE);
            BackgroundPanel.panelBorderPaint.setColor(0xFFB4D2FA);
            BackgroundPanel.panelBorderPaint.setStrokeWidth(5);
        }

        this.rect = new RectF(x, y, x + width, y + height);
        this.lineSeparator = new Path();
        this.lineSeparator.moveTo(x + (0.1f * width), y + (0.12f * height));
        this.lineSeparator.lineTo(x + (0.9f * width), y + (0.12f * height));
        this.lineSeparator.close();
        this.hasLineSeparator = true;
    }

    /**
     * Draws this panel onto the canvas.
     *
     * @param   canvas - The canvas to draw on
     */
    public void draw(Canvas canvas)
    {
        canvas.drawRoundRect(rect, 10, 10, BackgroundPanel.panelBackgroundPaint);
        canvas.drawRoundRect(rect, 10, 10, BackgroundPanel.panelBorderPaint);
        if (this.hasLineSeparator)
        {
            canvas.drawPath(lineSeparator, BackgroundPanel.panelBorderPaint);
        }
    }
}