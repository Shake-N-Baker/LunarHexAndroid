package com.isb.lunarhex;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * The button class holds data for the button, a field with a given
 * rectangle area and the text to display.
 *
 * @author Ian Baker
 */
public class RoundedButton
{
    /**
     * The rectangle encompassing the button
     */
    public RectF rect;

    /**
     * The inner rectangle of the button excluding the border
     */
    private RectF innerRect;

    /**
     * The shaded rectangle portion of the button
     */
    private Rect shadingRect;

    /**
     * The text to display
     */
    public String text;

    /**
     * Whether the button is currently enabled for interaction
     */
    public boolean isEnabled;

    /**
     * Whether the button is currently visible
     */
    public boolean isVisible;

    /**
     * The amount of space the border takes up
     */
    private int border;

    /**
     * Constructor for the button.
     *
     * @param   text - The text to display
     * @param   x - The X position of the button top left
     * @param   y - The Y position of the button top left
     * @param   width - The width of the button
     * @param   height - The height of the button
     * @param   border - The amount of space the border takes up
     */
    public RoundedButton(String text, int x, int y, int width, int height, int border)
    {
        this.text = text;
        this.rect = new RectF(x, y, x + width, y + height);
        this.innerRect = new RectF(x + border, y + border, x + width - border, y + height - border);
        this.shadingRect = new Rect(x + (3 * border), y + (height / 2), x + width - (3 * border), y + height - (3 * border));
        this.border = border;
        this.isVisible = true;
        this.isEnabled = true;
    }

    /**
     * Returns whether this button has been toggled.
     *
     * @param   startX - The X of the touch down event
     * @param   startY - The Y of the touch down event
     * @param   endX - The X of the touch up event
     * @param   endY - The Y of the touch up event
     * @return  Whether the button has been toggled
     */
    public boolean isToggled(int startX, int startY, int endX, int endY)
    {
        if (isEnabled && isVisible)
        {
            return (rect.contains(startX, startY) && rect.contains(endX, endY));
        }
        return false;
    }

    /**
     * Draws this button onto the canvas with the given textPaint.
     *
     * @param   canvas - The canvas to draw on
     * @param   textPaint - The text paint to use
     * @param   buttonPaint - The button paint to use
     */
    public void draw(Canvas canvas, Paint textPaint, Paint buttonPaint)
    {
        int color = buttonPaint.getColor();
        int darkerColor = 0xFF000000 + Utils.magnifyColor(color & 0x00FFFFFF, 0.85f);
        int darkestColor = 0xFF000000 +  Utils.magnifyColor(darkerColor & 0x00FFFFFF, 0.5f);
        buttonPaint.setColor(darkestColor);
        canvas.drawRoundRect(rect, 10, 10, buttonPaint);
        buttonPaint.setColor(color);
        canvas.drawRoundRect(innerRect, 10, 10, buttonPaint);
        buttonPaint.setColor(darkerColor);
        canvas.drawRect(shadingRect, buttonPaint);
        buttonPaint.setColor(color);
        canvas.drawText(this.text, this.rect.left + this.border, (((this.rect.top + this.border) + (this.rect.bottom - this.border)) / 2) - ((textPaint.descent() + textPaint.ascent()) / 2), textPaint);
    }
}