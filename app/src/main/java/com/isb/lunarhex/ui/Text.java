package com.isb.lunarhex.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.isb.lunarhex.MainView;

/**
 * The text class holds information for drawing a line of text
 * on a canvas.
 *
 * @author Ian Baker
 */
public class Text
{
    /**
     * The paint to use when drawing the text to a canvas
     */
    private static Paint textFillPaint;
    private static Paint textBorderPaint;

    /**
     * The text to display
     */
    private String text;

    /**
     * The left x position of the bounding rectangle
     */
    private int rectLeft;

    /**
     * The right x position of the bounding rectangle
     */
    private int rectRight;

    /**
     * The top y position of the bounding rectangle
     */
    private int rectTop;

    /**
     * The bottom y position of the bounding rectangle
     */
    private int rectBottom;

    /**
     * The x position to draw the text on the canvas
     */
    private int x;

    /**
     * The y position to draw the text on the canvas
     */
    private int y;

    /**
     * Whether the text is currently visible
     */
    public boolean isVisible;

    /**
     * Constructor for a text.
     *
     * @param   text - The text to display
     * @param   x - The x position of the rectangle the text is centered in
     * @param   y - The y position of the rectangle the text is centered in
     * @param   width - The width of the rectangle the text is centered in
     * @param   height - The height of the rectange the text is centered in
     */
    public Text(String text, int x, int y, int width, int height)
    {
        if (Text.textFillPaint == null)
        {
            Text.textFillPaint = new Paint();
            Text.textFillPaint.setColor(0xFFFFFFFF);
            Text.textFillPaint.setTextSize(MainView.FONT_SIZE_15_SP);
            Text.textFillPaint.setTypeface(MainView.RALEWAY_BOLD_FONT);
        }
        if (Text.textBorderPaint == null)
        {
            Text.textBorderPaint = new Paint();
            Text.textBorderPaint.setStyle(Paint.Style.STROKE);
            Text.textBorderPaint.setStrokeWidth(2);
            Text.textBorderPaint.setColor(0xFF000000);
            Text.textBorderPaint.setTextSize(MainView.FONT_SIZE_15_SP);
            Text.textBorderPaint.setTypeface(MainView.RALEWAY_BOLD_FONT);
        }

        this.text = text;
        this.rectLeft = x;
        this.rectRight = x + width;
        this.rectTop = y;
        this.rectBottom = y + height;
        Rect textBounds = new Rect();
        Text.textFillPaint.getTextBounds(this.text, 0, this.text.length(), textBounds);
        this.x = ((this.rectLeft + this.rectRight) / 2) - (textBounds.width() / 2);
        this.y = ((this.rectTop + this.rectBottom) / 2) - (int)((Text.textFillPaint.descent() + Text.textFillPaint.ascent()) / 2);
        this.isVisible = true;
    }

    /**
     * Draws this text onto the canvas.
     *
     * @param   canvas - The canvas to draw on
     */
    public void draw(Canvas canvas)
    {
        if (this.isVisible)
        {
            canvas.drawText(text, x, y, Text.textFillPaint);
            canvas.drawText(text, x, y, Text.textBorderPaint);
        }
    }

    /**
     * Sets the text to equal the new text and adjusts
     * the position of the text relative to the rectangle.
     *
     * @param   newText - The new text to display
     */
    public void setText(String newText)
    {
        this.text = newText;
        Rect textBounds = new Rect();
        Text.textFillPaint.getTextBounds(this.text, 0, this.text.length(), textBounds);
        this.x = ((this.rectLeft + this.rectRight) / 2) - (textBounds.width() / 2);
        this.y = ((this.rectTop + this.rectBottom) / 2) - (int)((Text.textFillPaint.descent() + Text.textFillPaint.ascent()) / 2);
    }
}