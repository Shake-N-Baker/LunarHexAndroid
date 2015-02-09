package com.isb.lunarhex;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * The textbox class holds the data for the textbox, a field with a given
 * rectangle area and the text to display. May or may not be a button.
 *
 * @author Ian Baker
 */
public class TextBox
{
    /**
     * The rectangle encompassing the textbox
     */
    public Rect rect;

    /**
     * The offset point from the top left of the rectangle to display the text
     */
    public Point offset;

    /**
     * The text to display
     */
    public String text;

    /**
     * Whether the textbox is a button
     */
    public boolean isButton;

    /**
     * Whether the textbox is currently visible
     */
    public boolean isVisible;

    /**
     * Constructor for the textbox.
     *
     * @param   text - The text to display
     * @param   isButton - Whether the textbox is a button
     * @param   x - The X position of the button top left
     * @param   y - The Y position of the button top left
     * @param   width - The width of the button
     * @param   height - The height of the button
     * @param   offsetX - The X offset from the button bottom left to display text
     * @param   offsetY - The Y offset from the buttom bottom left to display text
     */
    public TextBox(String text, boolean isButton, int x, int y, int width, int height, int offsetX, int offsetY)
    {
        this.text = text;
        this.isButton = isButton;
        this.rect = new Rect(x, y, x + width, y + height);
        this.offset = new Point(offsetX, offsetY);
        this.isVisible = true;
    }

    /**
     * Returns whether this textbox has been toggled.
     *
     * @param   startX - The X of the touch down event
     * @param   startY - The Y of the touch down event
     * @param   endX - The X of the touch up event
     * @param   endY - The Y of the touch up event
     * @return  Whether the textbox has been toggled
     */
    public boolean isToggled(int startX, int startY, int endX, int endY)
    {
        if (isButton && isVisible)
        {
            return (rect.contains(startX, startY) && rect.contains(endX, endY));
        }
        return false;
    }

    /**
     * Draws this textbox onto the canvas with the given textPaint.
     *
     * @param   canvas - The canvas to draw on
     * @param   textPaint - The text paint to use
     * @param   buttonPaint - The button paint to use
     */
    public void draw(Canvas canvas, Paint textPaint, Paint buttonPaint)
    {
        canvas.drawRect(rect, buttonPaint);
        canvas.drawText(this.text, this.rect.left + this.offset.x, this.rect.bottom - this.offset.y, textPaint);
    }
}