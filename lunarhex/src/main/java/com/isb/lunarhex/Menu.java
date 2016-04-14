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
    private static final String TITLE_TEXT = "LUNAR HEX";
    private static final String GITHUB_LINK_TEXT = "VIEW SOURCE CODE ON GITHUB";
    private static final String AUDIO_HEADER_TEXT = "AUDIO";
    private static final String CREDITS_HEADER_TEXT = "CREDITS";
    private static final String SOUND_VOLUME_TEXT = "SOUND:";
    private static final String MUSIC_VOLUME_TEXT = "MUSIC:";
    private static final String CREATED_BY_TEXT = "CREATED BY:   IAN BAKER";
    private static final String CREATED_BY_EXTENDED_TEXT = "FOLLOW ME ON TWITTER @IANDEVSGAMES";
    // TODO: Add music by artist name
    private static final String MUSIC_BY_TEXT = "MUSIC BY:   TODO";
    private static final String INSPIRED_BY_TEXT = "INSPIRED BY:   LUNAR LOCKOUT";
    private static final float TITLE_Y_PERCENT = 20f / 100f;
    private static final float HAMBURGER_MENU_X_PERCENT = 90f / 100f;
    private static final float HAMBURGER_MENU_WIDTH_PERCENT = 6f / 100f;
    private static final float HAMBURGER_MENU_Y_PERCENT = 6f / 100f;
    private static final float HAMBURGER_MENU_SPACING_Y_PERCENT = 4f / 100f;
    private static final float HAMBURGER_MENU_TOUCH_BUFFER_PERCENT = 5f / 100f;
    private static final float HAMBURGER_HEADER_TEXT_X_PERCENT = 8f / 100f;
    private static final float HAMBURGER_TEXT_X_PERCENT = 12f / 100f;
    private static final float VOLUME_TOUCH_BUFFER_PERCENT = 3f / 100f;
    private static final float AUDIO_TEXT_Y_PERCENT = 14f / 100f;
    private static final float CREDITS_TEXT_Y_PERCENT = 63f / 100f;
    private static final float SOUND_TEXT_Y_PERCENT = 24f / 100f;
    private static final float MUSIC_TEXT_Y_PERCENT = 35f / 100f;
    private static final float VOLUME_CONTROL_X_PERCENT = 32f / 100f;
    private static final float VOLUME_CONTROL_WIDTH_PERCENT = 47f / 100f;
    private static final float GITHUB_LINK_X_PERCENT = 8f / 100f;
    private static final float GITHUB_LINK_Y_PERCENT = 49f / 100f;
    private static final float GITHUB_LINK_TOUCH_BUFFER_PERCENT = 4f / 100f;
    private static final float SELECTION_CIRCLE_X_PERCENT = 50f / 100f;
    private static final float SELECTION_CIRCLE_Y_PERCENT = 73f / 100f;
    private static final float PREVIEW_BOARD_X_PERCENT = 34f / 100f;
    private static final float PREVIEW_BOARD_Y_PERCENT = 15f / 100f;
    private static final float PREVIEW_BOARD_SPACING_X_PERCENT = 8f / 100f;
    private static final float PREVIEW_BOARD_SPACING_Y_PERCENT = 6f / 100f;
    private static final float LEVELS_TOP_LEFT_X_PERCENT = 50f / 100f;
    private static final float LEVELS_TOP_LEFT_Y_PERCENT = 74f / 100f;
    private static final float LEVELS_SPACING_X_PERCENT = 20f / 100f;
    private static final float TRANSITION_DISTANCE_X_PERCENT = 2f / 100f;
    private static final float MAX_DRAG_VELOCITY_X_PERCENT = 80f / 1000f;
    private static final float DRAG_VELOCITY_RESISTANCE_X_PERCENT = 1f / 1000f;
    private static final int BACKGROUND_OFFSET_DAMPENING_MAGNITUDE = 10;
    private static final int DRAGGING_VELOCITY_DAMPENING_MAGNITUDE = 10;
    private static final int SELECTION_CIRCLE_TO_PREVIEW_BOARD_RATIO = 8;
    private static final int SELECTION_CIRCLE_TO_VOLUME_CONTROL_RATIO = 8;
    private static int TITLE_X;
    private static int TITLE_Y;
    private static int HAMBURGER_MENU_X;
    private static int HAMBURGER_MENU_Y;
    private static int HAMBURGER_MENU_WIDTH;
    private static int HAMBURGER_MENU_SPACING_Y;
    private static int HAMBURGER_MENU_TOUCH_BUFFER_X;
    private static int HAMBURGER_MENU_TOUCH_BUFFER_Y;
    private static int HAMBURGER_HEADER_TEXT_X;
    private static int HAMBURGER_TEXT_X;
    private static int AUDIO_TEXT_Y;
    private static int CREDITS_TEXT_Y;
    private static int SOUND_TEXT_Y;
    private static int MUSIC_TEXT_Y;
    private static int VOLUME_CONTROL_X;
    private static int VOLUME_CONTROL_WIDTH;
    private static int VOLUME_TOUCH_BUFFER_X;
    private static int VOLUME_TOUCH_BUFFER_Y;
    private static int GITHUB_LINK_X;
    private static int GITHUB_LINK_Y;
    private static int GITHUB_LINK_TOUCH_BUFFER_X;
    private static int GITHUB_LINK_TOUCH_BUFFER_Y;
    private static int SELECTION_CIRCLE_X;
    private static int SELECTION_CIRCLE_Y;
    private static int SELECTION_CIRCLE_RADIUS;
    private static int PREVIEW_BOARD_X;
    private static int PREVIEW_BOARD_Y;
    private static int PREVIEW_BOARD_SPACING_X;
    private static int PREVIEW_BOARD_SPACING_Y;
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
     * List of levels, each level is a list of colors, each color is a list of x, y, color value.
     */
    private List<List<List<Integer>>> boards;

    /**
     * The background image
     */
    private static Bitmap background;

    /**
     * The hamburger menu shading background image
     */
    private static Bitmap hamburgerBackground;

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
     * The paint used for drawing the preview board and volume controls
     */
    private Paint circleFilledPaint;

    /**
     * The paint used for drawing the hamburger menu
     */
    private Paint hamburgerMenuPaint;

    /**
     * The width of the github link text
     */
    private int githubLinkWidth;

    /**
     * The height of the hamburger menu texts, measured with github link text
     */
    private int hamburgerMenuTextHeight;

    /**
     * The height of text (non-headers) in the hamburger menu
     */
    private int hamburgerTextSpacingY;

    /**
     * The offsets of the level texts in the X direction
     */
    private List<Integer> levelTextCenterOffsetX;

    /**
     * The offsets of the level texts in the Y direction
     */
    private List<Integer> levelTextCenterOffsetY;

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
     * The flag whether the hamburger menu is open
     */
    private boolean hamburgerMenuOpen;

    /**
     * The sound volume level from 0 to 100
     */
    private int soundVolume;

    /**
     * The music volume level from 0 to 100
     */
    private int musicVolume;

    /**
     * Constructor for the menu.
     *
     * @param   main - The reference to the main view
     * @param   screenWidth - The screen width
     * @param   screenHeight - The screen height
     * @param   mainBoards - The set of main boards
     */
    public Menu(MainView main, int screenWidth, int screenHeight, List<String> mainBoards)
    {
        this.mainView = main;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        parseBoards(mainBoards);

        // Calculate the values based on screen measurements
        TITLE_Y = Math.round(TITLE_Y_PERCENT * screenHeight);
        HAMBURGER_MENU_X = Math.round(HAMBURGER_MENU_X_PERCENT * screenWidth);
        HAMBURGER_MENU_Y = Math.round(HAMBURGER_MENU_Y_PERCENT * screenHeight);
        HAMBURGER_MENU_WIDTH = Math.round(HAMBURGER_MENU_WIDTH_PERCENT * screenWidth);
        HAMBURGER_MENU_SPACING_Y = Math.round(HAMBURGER_MENU_SPACING_Y_PERCENT * screenHeight);
        HAMBURGER_MENU_TOUCH_BUFFER_X = Math.round(HAMBURGER_MENU_TOUCH_BUFFER_PERCENT * screenWidth);
        HAMBURGER_MENU_TOUCH_BUFFER_Y = Math.round(HAMBURGER_MENU_TOUCH_BUFFER_PERCENT * screenHeight);
        HAMBURGER_HEADER_TEXT_X = Math.round(HAMBURGER_HEADER_TEXT_X_PERCENT * screenWidth);
        HAMBURGER_TEXT_X = Math.round(HAMBURGER_TEXT_X_PERCENT * screenWidth);
        AUDIO_TEXT_Y = Math.round(AUDIO_TEXT_Y_PERCENT * screenHeight);
        CREDITS_TEXT_Y = Math.round(CREDITS_TEXT_Y_PERCENT * screenHeight);
        SOUND_TEXT_Y = Math.round(SOUND_TEXT_Y_PERCENT * screenHeight);
        MUSIC_TEXT_Y = Math.round(MUSIC_TEXT_Y_PERCENT * screenHeight);
        VOLUME_CONTROL_X = Math.round(VOLUME_CONTROL_X_PERCENT * screenWidth);
        VOLUME_CONTROL_WIDTH = Math.round(VOLUME_CONTROL_WIDTH_PERCENT * screenWidth);
        GITHUB_LINK_X = Math.round(GITHUB_LINK_X_PERCENT * screenWidth);
        GITHUB_LINK_Y = Math.round(GITHUB_LINK_Y_PERCENT * screenHeight);
        GITHUB_LINK_TOUCH_BUFFER_X = Math.round(GITHUB_LINK_TOUCH_BUFFER_PERCENT * screenWidth);
        GITHUB_LINK_TOUCH_BUFFER_Y = Math.round(GITHUB_LINK_TOUCH_BUFFER_PERCENT * screenHeight);
        VOLUME_TOUCH_BUFFER_X = Math.round(VOLUME_TOUCH_BUFFER_PERCENT * screenWidth);
        VOLUME_TOUCH_BUFFER_Y = Math.round(VOLUME_TOUCH_BUFFER_PERCENT * screenHeight);
        SELECTION_CIRCLE_X = Math.round(SELECTION_CIRCLE_X_PERCENT * screenWidth);
        SELECTION_CIRCLE_Y = Math.round(SELECTION_CIRCLE_Y_PERCENT * screenHeight);
        SELECTION_CIRCLE_RADIUS = (int) (Utils.distanceBetweenPoints(0, 0, screenWidth, screenHeight) / 13);
        LEVELS_TOP_LEFT_X = Math.round(LEVELS_TOP_LEFT_X_PERCENT * screenWidth);
        LEVELS_TOP_LEFT_Y = Math.round(LEVELS_TOP_LEFT_Y_PERCENT * screenHeight);
        LEVELS_SPACING_X = Math.round(LEVELS_SPACING_X_PERCENT * screenWidth);
        PREVIEW_BOARD_X = Math.round(PREVIEW_BOARD_X_PERCENT * screenWidth);
        PREVIEW_BOARD_Y = Math.round(PREVIEW_BOARD_Y_PERCENT * screenHeight);
        PREVIEW_BOARD_SPACING_X = Math.round(PREVIEW_BOARD_SPACING_X_PERCENT * screenWidth);
        PREVIEW_BOARD_SPACING_Y = Math.round(PREVIEW_BOARD_SPACING_Y_PERCENT * screenHeight);
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
        titlePaint.setTypeface(MainView.LATO_HEAVY_FONT);
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(MainView.FONT_SIZE_20_SP);
        textPaint.setTypeface(MainView.LATO_FONT);
        circlePaint = new Paint();
        circlePaint.setColor(Color.argb(255, 168, 183, 225));
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(5f);
        circleFilledPaint = new Paint();
        circleFilledPaint.setColor(Color.argb(255, 255, 255, 255));
        circleFilledPaint.setStyle(Paint.Style.FILL);
        hamburgerMenuPaint = new Paint();
        hamburgerMenuPaint.setColor(Color.argb(255, 255, 255, 255));
        hamburgerMenuPaint.setStyle(Paint.Style.STROKE);
        hamburgerMenuPaint.setStrokeWidth(12f);
        hamburgerMenuPaint.setStrokeCap(Paint.Cap.ROUND);

        Rect temp = new Rect();
        titlePaint.getTextBounds(TITLE_TEXT, 0, TITLE_TEXT.length(), temp);
        TITLE_X = Math.round((float) screenWidth / 2f) - Math.round((float) temp.width() / 2f);

        // Setup the offset from center rectangles for each level text
        levelTextCenterOffsetX = new ArrayList<Integer>();
        levelTextCenterOffsetY = new ArrayList<Integer>();
        temp = new Rect();
        textPaint.getTextBounds(RANDOM_GAME_TEXT, 0, RANDOM_GAME_TEXT.length(), temp);
        levelTextCenterOffsetX.add(temp.width() / 2);
        levelTextCenterOffsetY.add(temp.height() / 2);
        for (int i = 1; i < 31; i++)
        {
            temp = new Rect();
            textPaint.getTextBounds(String.valueOf(i), 0, String.valueOf(i).length(), temp);
            levelTextCenterOffsetX.add(temp.width() / 2);
            levelTextCenterOffsetY.add(temp.height() / 2);
        }
        temp = new Rect();
        textPaint.getTextBounds(GITHUB_LINK_TEXT, 0, GITHUB_LINK_TEXT.length(), temp);
        githubLinkWidth = temp.width();
        hamburgerMenuTextHeight = temp.height();
        temp = new Rect();
        textPaint.getTextBounds(CREATED_BY_TEXT, 0, CREATED_BY_TEXT.length(), temp);
        hamburgerTextSpacingY = (int) (temp.height() * 1.6f);

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

        hamburgerMenuOpen = false;
        soundVolume = PlayerData.getSoundVolume();
        musicVolume = PlayerData.getMusicVolume();
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
        if (hamburgerMenuOpen)
        {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP)
            {
                if (((HAMBURGER_MENU_X - HAMBURGER_MENU_TOUCH_BUFFER_X) <= Touch.x) && (Touch.x <= (HAMBURGER_MENU_X + HAMBURGER_MENU_WIDTH + HAMBURGER_MENU_TOUCH_BUFFER_X)) && ((HAMBURGER_MENU_Y - HAMBURGER_MENU_TOUCH_BUFFER_Y) <= Touch.y) && (Touch.y <= (HAMBURGER_MENU_Y + (HAMBURGER_MENU_SPACING_Y * 2) + HAMBURGER_MENU_TOUCH_BUFFER_Y)))
                {
                    if (((HAMBURGER_MENU_X - HAMBURGER_MENU_TOUCH_BUFFER_X) <= Touch.downX) && (Touch.downX <= (HAMBURGER_MENU_X + HAMBURGER_MENU_WIDTH + HAMBURGER_MENU_TOUCH_BUFFER_X)) && ((HAMBURGER_MENU_Y - HAMBURGER_MENU_TOUCH_BUFFER_Y) <= Touch.downY) && (Touch.downY <= (HAMBURGER_MENU_Y + (HAMBURGER_MENU_SPACING_Y * 2) + HAMBURGER_MENU_TOUCH_BUFFER_Y)))
                    {
                        // Close the hamburger menu
                        hamburgerMenuOpen = false;
                        PlayerData.setSoundVolume(soundVolume);
                        PlayerData.setMusicVolume(musicVolume);
                    }
                }
                if (((GITHUB_LINK_X - GITHUB_LINK_TOUCH_BUFFER_X) <= Touch.x) && (Touch.x <= (GITHUB_LINK_X + githubLinkWidth + GITHUB_LINK_TOUCH_BUFFER_X)) && ((GITHUB_LINK_Y - GITHUB_LINK_TOUCH_BUFFER_Y) <= Touch.y) && (Touch.y <= (GITHUB_LINK_Y + hamburgerMenuTextHeight + GITHUB_LINK_TOUCH_BUFFER_Y)))
                {
                    if (((GITHUB_LINK_X - GITHUB_LINK_TOUCH_BUFFER_X) <= Touch.downX) && (Touch.downX <= (GITHUB_LINK_X + githubLinkWidth + GITHUB_LINK_TOUCH_BUFFER_X)) && ((GITHUB_LINK_Y - GITHUB_LINK_TOUCH_BUFFER_Y) <= Touch.downY) && (Touch.downY <= (GITHUB_LINK_Y + hamburgerMenuTextHeight + GITHUB_LINK_TOUCH_BUFFER_Y)))
                    {
                        // View Github page
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Shake-N-Baker/LunarHexAndroid"));
                        mainView.getContext().startActivity(browserIntent);
                    }
                }
            }
            if (((VOLUME_CONTROL_X - VOLUME_TOUCH_BUFFER_X) <= Touch.x) && (Touch.x <= (VOLUME_CONTROL_X + VOLUME_CONTROL_WIDTH + VOLUME_TOUCH_BUFFER_X)) && ((SOUND_TEXT_Y - VOLUME_TOUCH_BUFFER_Y) <= Touch.y) && (Touch.y <= (SOUND_TEXT_Y + VOLUME_TOUCH_BUFFER_Y)))
            {
                if (((VOLUME_CONTROL_X - VOLUME_TOUCH_BUFFER_X) <= Touch.downX) && (Touch.downX <= (VOLUME_CONTROL_X + VOLUME_CONTROL_WIDTH + VOLUME_TOUCH_BUFFER_X)) && ((SOUND_TEXT_Y - VOLUME_TOUCH_BUFFER_Y) <= Touch.downY) && (Touch.downY <= (SOUND_TEXT_Y + VOLUME_TOUCH_BUFFER_Y)))
                {
                    // Sound volume control
                    soundVolume = (int) (((float) (Touch.x - VOLUME_CONTROL_X) / (float) VOLUME_CONTROL_WIDTH) * 100);
                    if (soundVolume > 100)
                    {
                        soundVolume = 100;
                    }
                    else if (soundVolume < 0)
                    {
                        soundVolume = 0;
                    }
                }
            }
            if (((VOLUME_CONTROL_X - VOLUME_TOUCH_BUFFER_X) <= Touch.x) && (Touch.x <= (VOLUME_CONTROL_X + VOLUME_CONTROL_WIDTH + VOLUME_TOUCH_BUFFER_X)) && ((MUSIC_TEXT_Y - VOLUME_TOUCH_BUFFER_Y) <= Touch.y) && (Touch.y <= (MUSIC_TEXT_Y + VOLUME_TOUCH_BUFFER_Y)))
            {
                if (((VOLUME_CONTROL_X - VOLUME_TOUCH_BUFFER_X) <= Touch.downX) && (Touch.downX <= (VOLUME_CONTROL_X + VOLUME_CONTROL_WIDTH + VOLUME_TOUCH_BUFFER_X)) && ((MUSIC_TEXT_Y - VOLUME_TOUCH_BUFFER_Y) <= Touch.downY) && (Touch.downY <= (MUSIC_TEXT_Y + VOLUME_TOUCH_BUFFER_Y)))
                {
                    // Music volume control
                    musicVolume = (int) (((float) (Touch.x - VOLUME_CONTROL_X) / (float) VOLUME_CONTROL_WIDTH) * 100);
                    if (musicVolume > 100)
                    {
                        musicVolume = 100;
                    }
                    else if (musicVolume < 0)
                    {
                        musicVolume = 0;
                    }
                }
            }
        }
        else
        {
            int tX = Touch.x + screenOffset;
            int tDownX = Touch.downX + screenOffset;

            if (motionEvent.getAction() == MotionEvent.ACTION_UP)
            {
                dragging = false;
                boolean nothingClicked = true;
                if (((HAMBURGER_MENU_X - HAMBURGER_MENU_TOUCH_BUFFER_X) <= Touch.x) && (Touch.x <= (HAMBURGER_MENU_X + HAMBURGER_MENU_WIDTH + HAMBURGER_MENU_TOUCH_BUFFER_X)) && ((HAMBURGER_MENU_Y - HAMBURGER_MENU_TOUCH_BUFFER_Y) <= Touch.y) && (Touch.y <= (HAMBURGER_MENU_Y + (HAMBURGER_MENU_SPACING_Y * 2) + HAMBURGER_MENU_TOUCH_BUFFER_Y)))
                {
                    if (((HAMBURGER_MENU_X - HAMBURGER_MENU_TOUCH_BUFFER_X) <= Touch.downX) && (Touch.downX <= (HAMBURGER_MENU_X + HAMBURGER_MENU_WIDTH + HAMBURGER_MENU_TOUCH_BUFFER_X)) && ((HAMBURGER_MENU_Y - HAMBURGER_MENU_TOUCH_BUFFER_Y) <= Touch.downY) && (Touch.downY <= (HAMBURGER_MENU_Y + (HAMBURGER_MENU_SPACING_Y * 2) + HAMBURGER_MENU_TOUCH_BUFFER_Y)))
                    {
                        // Selected the hamburger menu
                        nothingClicked = false;
                        hamburgerMenuOpen = true;
                    }
                }
                if (Utils.distanceBetweenPoints(SELECTION_CIRCLE_X, SELECTION_CIRCLE_Y, Touch.x, Touch.y) < SELECTION_CIRCLE_RADIUS)
                {
                    if (Utils.distanceBetweenPoints(SELECTION_CIRCLE_X, SELECTION_CIRCLE_Y, Touch.downX, Touch.downY) < SELECTION_CIRCLE_RADIUS)
                    {
                        // Clicked the selection circle
                        nothingClicked = false;
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
                        else
                        {
                            // Play selected level
                            mainView.triggerEvent(new CustomEvent(CustomEvent.START_LEVEL, String.valueOf(level - 1)));
                        }
                    }
                }
                if (nothingClicked)
                {
                    // Finished dragging, update momentum
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
                else if (screenOffset > (30 * LEVELS_SPACING_X))
                {
                    screenOffset = 30 * LEVELS_SPACING_X;
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
                else if (screenOffset > (30 * LEVELS_SPACING_X))
                {
                    screenOffset = 30 * LEVELS_SPACING_X;
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

        // Draw elements fixed in place on the screen
        canvas.restoreToCount(defaultMatrix);
        defaultMatrix = canvas.save();

        // Calculate the distance from being centered on a level
        int differenceFromCenter = screenOffset % LEVELS_SPACING_X;
        if (differenceFromCenter > (LEVELS_SPACING_X / 2))
        {
            differenceFromCenter -= LEVELS_SPACING_X;
            differenceFromCenter *= -1;
        }
        float viewingLevel = (float) screenOffset / (float) LEVELS_SPACING_X;

        if (differenceFromCenter < SELECTION_CIRCLE_RADIUS)
        {
            int transparency = (int) (255 * (1f - ((float) differenceFromCenter / (float) SELECTION_CIRCLE_RADIUS)));

            // Draw the preview board
            if (1 <= Math.round(viewingLevel) && Math.round(viewingLevel) <= 30)
            {
                List<List<Integer>> board = boards.get(Math.round(viewingLevel - 1));

                for (int x = 0; x < 5; x++)
                {
                    for (int y = 0; y < 6; y++)
                    {
                        // The final row only has 2 spaces instead of 5
                        if (y == 5)
                        {
                            if (x != 1 && x != 3)
                            {
                                continue;
                            }
                        }

                        // Search the colors to see if this index matches their coordinates
                        boolean colorFound = false;
                        for (List<Integer> color : board)
                        {
                            if (x == color.get(0) && y == color.get(1))
                            {
                                circleFilledPaint.setColor((transparency << 24) + (color.get(2) & 0x00FFFFFF));
                                colorFound = true;
                                break;
                            }
                        }

                        // Tint the center red and color the rest of the spaces white
                        if (!colorFound)
                        {
                            if (x == 2 && y == 2)
                            {
                                circleFilledPaint.setColor(Color.argb(transparency, 255, 196, 196));
                            }
                            else
                            {
                                circleFilledPaint.setColor(Color.argb(transparency, 255, 255, 255));
                            }
                        }

                        // Columns of the board alternate in height
                        if (x % 2 == 0)
                        {
                            canvas.drawCircle((PREVIEW_BOARD_SPACING_X * x) + PREVIEW_BOARD_X, (PREVIEW_BOARD_SPACING_Y * ((float) y + 0.5f)) + PREVIEW_BOARD_Y, (float) (SELECTION_CIRCLE_RADIUS - differenceFromCenter) / SELECTION_CIRCLE_TO_PREVIEW_BOARD_RATIO, circleFilledPaint);
                        }
                        else
                        {
                            canvas.drawCircle((PREVIEW_BOARD_SPACING_X * x) + PREVIEW_BOARD_X, (PREVIEW_BOARD_SPACING_Y * y) + PREVIEW_BOARD_Y, (float) (SELECTION_CIRCLE_RADIUS - differenceFromCenter) / SELECTION_CIRCLE_TO_PREVIEW_BOARD_RATIO, circleFilledPaint);
                        }
                    }
                }
            }

            // Draw the selection circle
            circlePaint.setColor(Color.argb(transparency, 168, 183, 225));
            canvas.drawCircle(SELECTION_CIRCLE_X, SELECTION_CIRCLE_Y, SELECTION_CIRCLE_RADIUS - differenceFromCenter, circlePaint);
        }

        // Shift everything else the full screen offset amount
        canvas.translate(-1 * screenOffset, 0);

        // Draw title text
        canvas.drawText(TITLE_TEXT, TITLE_X, TITLE_Y, titlePaint);

        // Draw random level text
        float levelsFromText = viewingLevel;
        textPaint.setColor(Color.argb((int) ((1f - (levelsFromText / 3f)) * 255), 255, 255, 255));
        canvas.drawText(RANDOM_GAME_TEXT, LEVELS_TOP_LEFT_X - levelTextCenterOffsetX.get(0), LEVELS_TOP_LEFT_Y + levelTextCenterOffsetY.get(0), textPaint);

        // Draw level texts
        for (int level = 1; level < 31; level++)
        {
            levelsFromText = viewingLevel - (float) level;
            if (levelsFromText < 0f)
            {
                levelsFromText *= -1f;
            }
            textPaint.setColor(Color.argb((int) ((1f - (levelsFromText / 3f)) * 255), 255, 255, 255));
            canvas.drawText(Integer.toString(level), (LEVELS_SPACING_X * level) + LEVELS_TOP_LEFT_X  - levelTextCenterOffsetX.get(level), LEVELS_TOP_LEFT_Y + levelTextCenterOffsetY.get(level), textPaint);
        }

        // Draw hamburger menu fixed elements
        canvas.restoreToCount(defaultMatrix);

        if (hamburgerMenuOpen)
        {
            // Draw hamburger menu shaded background
            canvas.drawBitmap(hamburgerBackground, 0, 0, null);
            textPaint.setColor(Color.argb(255, 255, 255, 255));
            textPaint.setTextSize(MainView.FONT_SIZE_30_SP);

            // Draw Headers
            canvas.drawText(AUDIO_HEADER_TEXT, HAMBURGER_HEADER_TEXT_X, AUDIO_TEXT_Y, textPaint);
            canvas.drawText(CREDITS_HEADER_TEXT, HAMBURGER_HEADER_TEXT_X, CREDITS_TEXT_Y, textPaint);
            textPaint.setTextSize(MainView.FONT_SIZE_20_SP);

            // Draw the sound volume control
            circleFilledPaint.setColor(Color.argb(255, 255, 255, 255));
            canvas.drawText(SOUND_VOLUME_TEXT, HAMBURGER_TEXT_X, SOUND_TEXT_Y + (hamburgerMenuTextHeight / 2), textPaint);
            canvas.drawLine(VOLUME_CONTROL_X, SOUND_TEXT_Y, VOLUME_CONTROL_X + VOLUME_CONTROL_WIDTH, SOUND_TEXT_Y, hamburgerMenuPaint);
            canvas.drawCircle(VOLUME_CONTROL_X + ((soundVolume / 100f) * VOLUME_CONTROL_WIDTH), SOUND_TEXT_Y, SELECTION_CIRCLE_RADIUS / SELECTION_CIRCLE_TO_VOLUME_CONTROL_RATIO, circleFilledPaint);

            // Draw the music volume control
            canvas.drawText(MUSIC_VOLUME_TEXT, HAMBURGER_TEXT_X, MUSIC_TEXT_Y + (hamburgerMenuTextHeight / 2), textPaint);
            canvas.drawLine(VOLUME_CONTROL_X, MUSIC_TEXT_Y, VOLUME_CONTROL_X + VOLUME_CONTROL_WIDTH, MUSIC_TEXT_Y, hamburgerMenuPaint);
            canvas.drawCircle(VOLUME_CONTROL_X + ((musicVolume / 100f) * VOLUME_CONTROL_WIDTH), MUSIC_TEXT_Y, SELECTION_CIRCLE_RADIUS / SELECTION_CIRCLE_TO_VOLUME_CONTROL_RATIO, circleFilledPaint);

            // Draw the link to the github source
            textPaint.setColor(Color.argb(255, 51, 102, 187));
            textPaint.setUnderlineText(true);
            canvas.drawText(GITHUB_LINK_TEXT, GITHUB_LINK_X, GITHUB_LINK_Y, textPaint);
            textPaint.setColor(Color.argb(255, 255, 255, 255));
            textPaint.setUnderlineText(false);

            // Draw the credits text
            canvas.drawText(CREATED_BY_TEXT, HAMBURGER_TEXT_X, CREDITS_TEXT_Y + hamburgerTextSpacingY, textPaint);
            canvas.drawText(CREATED_BY_EXTENDED_TEXT, HAMBURGER_TEXT_X, CREDITS_TEXT_Y + (2 * hamburgerTextSpacingY), textPaint);
            canvas.drawText(MUSIC_BY_TEXT, HAMBURGER_TEXT_X, CREDITS_TEXT_Y + (3 * hamburgerTextSpacingY), textPaint);
            canvas.drawText(INSPIRED_BY_TEXT, HAMBURGER_TEXT_X, CREDITS_TEXT_Y + (4 * hamburgerTextSpacingY), textPaint);
        }

        // Draw hamburger menu
        canvas.drawLine(HAMBURGER_MENU_X, HAMBURGER_MENU_Y, HAMBURGER_MENU_X + HAMBURGER_MENU_WIDTH, HAMBURGER_MENU_Y, hamburgerMenuPaint);
        canvas.drawLine(HAMBURGER_MENU_X, HAMBURGER_MENU_Y + HAMBURGER_MENU_SPACING_Y, HAMBURGER_MENU_X + HAMBURGER_MENU_WIDTH, HAMBURGER_MENU_Y + HAMBURGER_MENU_SPACING_Y, hamburgerMenuPaint);
        canvas.drawLine(HAMBURGER_MENU_X, HAMBURGER_MENU_Y + (2 * HAMBURGER_MENU_SPACING_Y), HAMBURGER_MENU_X + HAMBURGER_MENU_WIDTH, HAMBURGER_MENU_Y + (2 * HAMBURGER_MENU_SPACING_Y), hamburgerMenuPaint);
    }

    /**
     * Generates a new background.
     */
    public void generateBackground()
    {
        if (hamburgerBackground == null)
        {
            hamburgerBackground = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
            hamburgerBackground.eraseColor(Color.argb(192, 0, 0, 0));
        }
        if (background == null)
        {
            background = Bitmap.createBitmap((int) (screenWidth * (1.00f + ((LEVELS_SPACING_X_PERCENT * 30f) / (float) BACKGROUND_OFFSET_DAMPENING_MAGNITUDE))), screenHeight, Bitmap.Config.ARGB_8888);
        }
        Utils.generateBackground(background, (int) (screenWidth * (1.00f + ((LEVELS_SPACING_X_PERCENT * 30f) / (float) BACKGROUND_OFFSET_DAMPENING_MAGNITUDE))), screenHeight);
    }

    /**
     * Parses the boards into a format used by the menu when previewing boards.
     *
     * @param   mainBoards - The set of main boards
     */
    private void parseBoards(List<String> mainBoards)
    {
        boards = new ArrayList<List<List<Integer>>>();
        for (int level = 0; level < 30; level++)
        {
            String uncompressedBoard = Utils.convertCompressedBoard(mainBoards.get(level));

            List<List<Integer>> levelColors = new ArrayList<List<Integer>>();
            String[] pairs = uncompressedBoard.split(",");
            for (int i = 0; i < pairs.length; i++)
            {
                List<Integer> colorCoordinates = new ArrayList<Integer>();
                Character color = pairs[i].split("-")[0].charAt(0);
                int index = Integer.parseInt(pairs[i].split("-")[1]);
                int x = 0;
                int y = 0;
                if (0 <= index && index <= 24)
                {
                    x = index % 5;
                    y = index / 5;
                }
                else if (index == 25)
                {
                    x = 1;
                    y = 5;
                }
                else if (index == 26)
                {
                    x = 3;
                    y = 5;
                }
                int colorValue;
                switch (color)
                {
                    case 'R':
                        colorValue = 0xFFAA0000;
                        break;
                    case 'G':
                        colorValue = 0xFF009900;
                        break;
                    case 'B':
                        colorValue = 0xFF333399;
                        break;
                    case 'Y':
                        colorValue = 0xFFFFFF00;
                        break;
                    case 'O':
                        colorValue = 0xFFDD7700;
                        break;
                    case 'P':
                        colorValue = 0xFF9900AA;
                        break;
                    default:
                        colorValue = 0xFF808080;
                        break;
                }
                colorCoordinates.add(x);
                colorCoordinates.add(y);
                colorCoordinates.add(colorValue);
                levelColors.add(colorCoordinates);
            }
            boards.add(levelColors);
        }
    }
}