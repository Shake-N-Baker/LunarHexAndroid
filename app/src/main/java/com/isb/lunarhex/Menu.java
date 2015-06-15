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
    private static final float TITLE_X_PERCENT = 23f / 100f;
    private static final float TITLE_Y_PERCENT = 20f / 100f;
    private static final float PLAY_HEX_X_PERCENT = 16f / 100f;
    private static final float PLAY_HEX_Y_PERCENT = 28f / 100f;
    private static final float RANDOM_HEX_X_PERCENT = 56f / 100f;
    private static final float RANDOM_HEX_Y_PERCENT = 28f / 100f;
    private static final float SOURCE_HEX_X_PERCENT = 36f / 100f;
    private static final float SOURCE_HEX_Y_PERCENT = 60f / 100f;
    private static final float HEX_BUTTON_WIDTH_PERCENT = 30f / 100f;
    private static final float HEX_BUTTON_HEIGHT_PERCENT = 35f / 100f;
    private static int TITLE_X;
    private static int TITLE_Y;
    private static int PLAY_HEX_X;
    private static int PLAY_HEX_Y;
    private static int RANDOM_HEX_X;
    private static int RANDOM_HEX_Y;
    private static int SOURCE_HEX_X;
    private static int SOURCE_HEX_Y;
    private static int HEX_BUTTON_WIDTH;
    private static int HEX_BUTTON_HEIGHT;

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
     * The paint used for the buttons
     */
    private Paint buttonPaint;

    /**
     * The paint used for the text
     */
    private Paint textPaint;

    /**
     * The play hexagon button bounding rectangle
     */
    private Rect playHexRect;

    /**
     * The rectangle bounding the play text
     */
    private Rect playTextRect;

    /**
     * The random hexagon button bounding rectangle
     */
    private Rect randomHexRect;

    /**
     * The rectangle bounding the random text
     */
    private Rect randomTextRect;

    /**
     * The source hexagon button bounding rectangle
     */
    private Rect sourceHexRect;

    /**
     * The rectangle bounding the source text
     */
    private Rect sourceTextRect;

    /**
     * A hexagon bitmap to check mouse against buttons
     */
    private Bitmap hexButtonCheck;

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

        TITLE_X = Math.round(TITLE_X_PERCENT * screenWidth);
        TITLE_Y = Math.round(TITLE_Y_PERCENT * screenHeight);
        PLAY_HEX_X = Math.round(PLAY_HEX_X_PERCENT * screenWidth);
        PLAY_HEX_Y = Math.round(PLAY_HEX_Y_PERCENT * screenHeight);
        RANDOM_HEX_X = Math.round(RANDOM_HEX_X_PERCENT * screenWidth);
        RANDOM_HEX_Y = Math.round(RANDOM_HEX_Y_PERCENT * screenHeight);
        SOURCE_HEX_X = Math.round(SOURCE_HEX_X_PERCENT * screenWidth);
        SOURCE_HEX_Y = Math.round(SOURCE_HEX_Y_PERCENT * screenHeight);
        HEX_BUTTON_WIDTH = Math.round(HEX_BUTTON_WIDTH_PERCENT * screenWidth);
        HEX_BUTTON_HEIGHT = Math.round(HEX_BUTTON_HEIGHT_PERCENT * screenHeight);

        playHexRect = new Rect(PLAY_HEX_X, PLAY_HEX_Y, PLAY_HEX_X + HEX_BUTTON_WIDTH, PLAY_HEX_Y + HEX_BUTTON_HEIGHT);
        randomHexRect = new Rect(RANDOM_HEX_X, RANDOM_HEX_Y, RANDOM_HEX_X + HEX_BUTTON_WIDTH, RANDOM_HEX_Y + HEX_BUTTON_HEIGHT);
        sourceHexRect = new Rect(SOURCE_HEX_X, SOURCE_HEX_Y, SOURCE_HEX_X + HEX_BUTTON_WIDTH, SOURCE_HEX_Y + HEX_BUTTON_HEIGHT);

        // Setup the paints used for text
        titlePaint = new Paint();
        titlePaint.setColor(Color.WHITE);
        titlePaint.setTextSize(mainView.FONT_SIZE_60_SP);
        buttonPaint = new Paint();
        buttonPaint.setColor(Color.WHITE);
        buttonPaint.setTextSize(mainView.FONT_SIZE_30_SP);
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(mainView.FONT_SIZE_15_SP);

        playTextRect = new Rect();
        buttonPaint.getTextBounds("PLAY", 0, "PLAY".length(), playTextRect);
        randomTextRect = new Rect();
        buttonPaint.getTextBounds("RANDOM", 0, "RANDOM".length(), randomTextRect);
        sourceTextRect = new Rect();
        buttonPaint.getTextBounds("SOURCE", 0, "SOURCE".length(), sourceTextRect);

        // Construct checking hex to compare taps to when determining which hexagon is selected
        hexButtonCheck = Bitmap.createBitmap(HEX_BUTTON_WIDTH, HEX_BUTTON_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas temp = new Canvas(hexButtonCheck);
        Utils.drawHex(temp, 0, 0, HEX_BUTTON_WIDTH, HEX_BUTTON_HEIGHT, 0xFF0000, 0, false);
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
        if (playHexRect.contains(Touch.x, Touch.y) && playHexRect.contains(Touch.downX, Touch.downY) && motionEvent.getAction() == MotionEvent.ACTION_UP)
        {
            if (Integer.toHexString(hexButtonCheck.getPixel(Touch.x - playHexRect.left, Touch.y - playHexRect.top)).equals("ffff0000"))
            {
                if (Integer.toHexString(hexButtonCheck.getPixel(Touch.downX - playHexRect.left, Touch.downY - playHexRect.top)).equals("ffff0000"))
                {
                    /// TODO: Handle level select
                }
            }
        }
        else if (randomHexRect.contains(Touch.x, Touch.y) && randomHexRect.contains(Touch.downX, Touch.downY) && motionEvent.getAction() == MotionEvent.ACTION_UP)
        {
            if (Integer.toHexString(hexButtonCheck.getPixel(Touch.x - randomHexRect.left, Touch.y - randomHexRect.top)).equals("ffff0000"))
            {
                if (Integer.toHexString(hexButtonCheck.getPixel(Touch.downX - randomHexRect.left, Touch.downY - randomHexRect.top)).equals("ffff0000"))
                {
                    mainView.handleEvent(new CustomEvent(CustomEvent.NEW_CUSTOM_GAME));
                }
            }
        }
        if (sourceHexRect.contains(Touch.x, Touch.y) && sourceHexRect.contains(Touch.downX, Touch.downY) && motionEvent.getAction() == MotionEvent.ACTION_UP)
        {
            if (Integer.toHexString(hexButtonCheck.getPixel(Touch.x - sourceHexRect.left, Touch.y - sourceHexRect.top)).equals("ffff0000"))
            {
                if (Integer.toHexString(hexButtonCheck.getPixel(Touch.downX - sourceHexRect.left, Touch.downY - sourceHexRect.top)).equals("ffff0000"))
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Shake-N-Baker/LunarHexAndroid"));
                    mainView.getContext().startActivity(browserIntent);
                }
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
        // Clear board
        canvas.drawARGB(0xff, 0x00, 0x00, 0x00);
        canvas.drawBitmap(background, 0, 0, null);

        Utils.drawHex(canvas, PLAY_HEX_X, PLAY_HEX_Y, HEX_BUTTON_WIDTH, HEX_BUTTON_HEIGHT, Color.RED, 0, true);
        Utils.drawHex(canvas, RANDOM_HEX_X, RANDOM_HEX_Y, HEX_BUTTON_WIDTH, HEX_BUTTON_HEIGHT, Color.GREEN, 0, true);
        Utils.drawHex(canvas, SOURCE_HEX_X, SOURCE_HEX_Y, HEX_BUTTON_WIDTH, HEX_BUTTON_HEIGHT, Color.BLUE, 0, true);

        // Draw texts
        canvas.drawText("LUNAR HEX", TITLE_X, TITLE_Y, titlePaint);
        canvas.drawText("PLAY", ((PLAY_HEX_X + (PLAY_HEX_X + HEX_BUTTON_WIDTH)) / 2) - (playTextRect.width() / 2), ((PLAY_HEX_Y + (PLAY_HEX_Y + HEX_BUTTON_HEIGHT)) / 2) - ((buttonPaint.ascent() + buttonPaint.descent()) / 2), buttonPaint);
        canvas.drawText("RANDOM", ((RANDOM_HEX_X + (RANDOM_HEX_X + HEX_BUTTON_WIDTH)) / 2) - (randomTextRect.width() / 2), ((RANDOM_HEX_Y + (RANDOM_HEX_Y + HEX_BUTTON_HEIGHT)) / 2) - ((buttonPaint.ascent() + buttonPaint.descent()) / 2), buttonPaint);
        canvas.drawText("SOURCE", ((SOURCE_HEX_X + (SOURCE_HEX_X + HEX_BUTTON_WIDTH)) / 2) - (sourceTextRect.width() / 2), ((SOURCE_HEX_Y + (SOURCE_HEX_Y + HEX_BUTTON_HEIGHT)) / 2) - ((buttonPaint.ascent() + buttonPaint.descent()) / 2), buttonPaint);
        canvas.drawText("Created By: Ian Baker", 10, screenHeight - 10, textPaint);

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