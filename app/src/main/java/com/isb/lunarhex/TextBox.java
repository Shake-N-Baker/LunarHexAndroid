package com.isb.lunarhex;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * The textbox class is a field with a given
 * rectangle area and the text to display.
 *
 * @author Ian Baker
 */
public class TextBox
{
    /**
     * Static text bounds variable to avoid mass creation of rectangles on drawing
     */
    private static Rect textBounds;

    /**
     * The rectangle encompassing the textbox
     */
    public RectF rect;

    /**
     * The text to display
     */
    public String text;

    /**
     * Whether the textbox is currently visible
     */
    public boolean isVisible;

    /**
     * Constructor for the textbox.
     *
     * @param   text - The text to display
     * @param   x - The X position of the textbox top left
     * @param   y - The Y position of the textbox top left
     * @param   width - The width of the textbox
     * @param   height - The height of the textbox
     */
    public TextBox(String text, int x, int y, int width, int height)
    {
        this.text = text;
        this.rect = new RectF(x, y, x + width, y + height);
        this.isVisible = true;
        if (TextBox.textBounds == null)
        {
            TextBox.textBounds = new Rect();
        }
    }

    /**
     * Draws this textbox onto the canvas with the given textPaint.
     *
     * @param   canvas - The canvas to draw on
     * @param   textPaint - The text paint to use
     * @param   backgroundPaint - The background paint to use
     */
    public void draw(Canvas canvas, Paint textPaint, Paint backgroundPaint)
    {
        canvas.drawRoundRect(rect, 10, 10, backgroundPaint);
        textPaint.getTextBounds(this.text, 0, this.text.length(), TextBox.textBounds);
        int textWidth = TextBox.textBounds.width();
        canvas.drawText(this.text, ((this.rect.left + this.rect.right) / 2) - (textWidth / 2), ((this.rect.top + this.rect.bottom) / 2) - ((textPaint.descent() + textPaint.ascent()) / 2), textPaint);
    }
}