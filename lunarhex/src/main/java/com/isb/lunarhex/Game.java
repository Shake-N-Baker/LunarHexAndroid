package com.isb.lunarhex;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The game class.
 *
 * @author Ian Baker
 */
public class Game implements InteractiveView
{
    /**
     * Reference to the main view.
     */
    MainView mainView;

    /**
     * Constants
     */
    private static final String OPTIONS_TITLE_1 = "NEW BOARDS";
    private static final String OPTIONS_TITLE_2 = "SOLVABLE IN";
    private static final String OPTIONS_MAXIMUM = "MAXIMUM MOVES:";
    private static final String OPTIONS_MINIMUM = "MINIMUM MOVES:";
    private static final String CLEAR = "CLEARED!";
    private static final String PERFECT_CLEAR = "PERFECT!";
    private static final String INSTRUCTIONS_1 = "SLIDE THE RED PIECE TO THE MIDDLE TO WIN";
    private static final String INSTRUCTIONS_2 = "PIECES MAY ONLY SLIDE INTO OTHER PIECES";
    public static final float HEX_WIDTH_PERCENT = 18f / 100f;
    public static final float HEX_HEIGHT_PERCENT = 14f / 100f;
    public static final float BOARD_X_PERCENT = 14f / 100f;
    public static final float BOARD_Y_PERCENT = 8f / 100f;
    private static final float EXIT_X_PERCENT = 7f / 100f;
    private static final float EXIT_Y_PERCENT = 12f / 100f;
    private static final float BUTTON_X_PERCENT = 92f / 100f;
    private static final float BUTTON_Y_PERCENT = 12f / 100f;
    private static final float BUTTON_SPACING_Y_PERCENT = 25f / 100f;
    private static final float CLOSE_OPTIONS_X_PERCENT = 69f / 100f;
    private static final float CLOSE_OPTIONS_Y_PERCENT = 27f / 100f;
    private static final float MOVES_PLUS_X_PERCENT = 62f / 100f;
    private static final float MOVES_MINUS_X_PERCENT = 37f / 100f;
    private static final float MOVES_MIN_Y_PERCENT = 70f / 100f;
    private static final float MOVES_MAX_Y_PERCENT = 53f / 100f;
    private static final float OPTIONS_PANEL_X_PERCENT = 27f / 100f;
    private static final float OPTIONS_PANEL_Y_PERCENT = 20f / 100f;
    private static final float OPTIONS_PANEL_WIDTH_PERCENT = 46f / 100f;
    private static final float OPTIONS_PANEL_HEIGHT_PERCENT = 60f / 100f;
    private static final float OPTIONS_PANEL_TEXT_TITLE_1_Y_PERCENT = 30f / 100f;
    private static final float OPTIONS_PANEL_TEXT_TITLE_2_Y_PERCENT = 38f / 100f;
    private static final float OPTIONS_PANEL_TEXT_MAX_Y_PERCENT = 47f / 100f;
    private static final float OPTIONS_PANEL_TEXT_MIN_Y_PERCENT = 63f / 100f;
    private static final float TEXT_BACKGROUND_PANEL_HEIGHT_PERCENT = 1f / 8f;
    private static int HEX_WIDTH;
    private static int HEX_HEIGHT;
    private static int HEX_DEPTH;
    private static int BOARD_X;
    private static int BOARD_Y;
    private static int BUTTON_RADIUS;
    private static int EXIT_X;
    private static int EXIT_Y;
    private static int CLOSE_OPTIONS_X;
    private static int CLOSE_OPTIONS_Y;
    private static int MOVES_PLUS_X;
    private static int MOVES_MINUS_X;
    private static int MOVES_MIN_Y;
    private static int MOVES_MAX_Y;
    private static int OPTIONS_PANEL_X;
    private static int OPTIONS_PANEL_Y;
    private static int OPTIONS_PANEL_WIDTH;
    private static int OPTIONS_PANEL_HEIGHT;
    private static int OPTIONS_PANEL_TEXT_TITLE_1_X;
    private static int OPTIONS_PANEL_TEXT_TITLE_1_Y;
    private static int OPTIONS_PANEL_TEXT_TITLE_2_X;
    private static int OPTIONS_PANEL_TEXT_TITLE_2_Y;
    private static int OPTIONS_PANEL_TEXT_MAX_X;
    private static int OPTIONS_PANEL_TEXT_MAX_Y;
    private static int OPTIONS_PANEL_TEXT_MIN_X;
    private static int OPTIONS_PANEL_TEXT_MIN_Y;
    private static int OPTIONS_PANEL_VALUE_X;
    private static int OPTIONS_PANEL_VALUE_2_DIGIT_X;
    private static int OPTIONS_PANEL_VALUE_MAX_Y;
    private static int OPTIONS_PANEL_VALUE_MIN_Y;
    private static int BUTTON_X;
    private static int BUTTON_1_Y;
    private static int BUTTON_2_Y;
    private static int BUTTON_3_Y;
    private static int BUTTON_4_Y;
    private static int TEXT_Y;
    private static int CLEAR_X;
    private static int PERFECT_CLEAR_X;
    private static int INSTRUCTIONS_1_X;
    private static int INSTRUCTIONS_2_X;

    /**
     * The locations of the various buttons on screen
     */
    private int generateX;
    private int generateY;
    private int generateOptionsX;
    private int generateOptionsY;
    private int retryX;
    private int retryY;
    private int hintX;
    private int hintY;

    /**
     * The options menu background image
     */
    private static Bitmap optionsBackground;

    /**
     * The cached image of the current game with options menu open to speed up drawing
     */
    private static Bitmap cachedGameSceneWithOptionsOpen;

    /**
     * The cached image of the current game to speed up drawing
     */
    private static Bitmap cachedGameScene;

    /**
     * The text background panel for the clear and instruction texts
     */
    private static Bitmap textBackground;

    /**
     * Flag whether the generate options is open
     */
    public boolean optionsOpen;

    /**
     * The rectangle containing the coordinates for the options panel
     */
    private Rect optionsPanelRect;

    /**
     * Whether the game is fading in
     */
    private boolean fadingIn;

    /**
     * Whether the game is fading out
     */
    private boolean fadingOut;

    /**
     * The number of frames left before the fade is finished
     */
    private int fadeFrame;

    /**
     * The screen width
     */
    private int screenWidth;

    /**
     * The screen height
     */
    private int screenHeight;

    /**
     * Reference to the list of boards to be used in the main set of boards.
     */
    private List<String> mainBoardSet;

    /**
     * Reference to the list of lists of boards. Each list represents boards of
     * index + 1 length minimum number of moves to solve. i.e. boardSet[0] is a
     * list of boards solved in 1 move. boardSet[1] = 2 move solves. etc.
     */
    private List<List<String>> boardSet;

    /**
     * The number of frames a slide (move) will take to finish
     */
    private static int SLIDE_FRAMES = 20;

    /**
     * Boolean for ignoring the current touch event, used when a slide animation occurs to ignore
     * all events until the next touch down event.
     */
    private boolean ignoreTouch = false;

    /**
     * The paint used for text
     */
    private Paint textPaint;

    /**
     * The paint used for drawing the icon bitmaps
     */
    private Paint iconPaint;

    /**
     * The paint used for the options panel
     */
    private Paint optionsPaint;

    /// TODO: Remove me!
    private Paint debugPaint;

    /**
     * List of the bounding boxes for each hexagon tile
     */
    private List<Rect> boundingBoxes;

    /**
     * Whether the user is currently tapping the screen
     */
    private boolean tapping = false;

    /**
     * Whether the hexagon selection was set this tap event
     */
    private boolean selectionSetThisTap = false;

    /**
     * The index of the hexagon currently selected
     */
    private int hexSelect = -1;

    /**
     * The index of the hexagon selected when cached image was stored
     */
    private int cachedHexSelect = -1;

    /**
     * A hexagon bitmap to check mouse against
     */
    private Bitmap hexCheck;

    /**
     * String representation of the board with COLOR-INDEX where
     * COLOR: R, G, B, Y, O, P
     * INDEX: 0-26
     */
    public String boardState = "";

    /**
     * The initial board state prior to changes being made
     */
    public String initialBoardState = "";

    /**
     * The set of states leading to the solution
     */
    public ArrayList<String> solution;

    /**
     * Whether a piece is currently moving
     */
    private boolean moving;

    /**
     * The number of frames left in the slide move
     */
    private int slideFrame;

    /**
     * The starting index of the slide move
     */
    private int slideStart;

    /**
     * The ending index of the slide move
     */
    private int slideEnd;

    /**
     * The direction of the slide move
     */
    private int slideDirection;

    /**
     * The board state after the slide move is complete
     */
    private String slideToBoard;

    /**
     * The minimum number of moves (shortest path) a newly generated board will take
     */
    public int generationMinMoves = 4;

    /**
     * The maximum number of moves (shortest path) a newly generated board will take
     */
    public int generationMaxMoves = 8;

    /**
     * The name of the button being held down
     */
    private String buttonHeldDown;

    /**
     * The length of time the button has been held down for
     */
    private int buttonHeldDownFrames;

    /**
     * The current level that the player is on (zero based) or -1 if random
     */
    public int currentLevel = -1;

    /**
     * The level the player was on when cached image was stored
     */
    public int cachedLevel = -1;

    /**
     * The current number of moves the player has taken since the initial board state
     */
    public int currentMove = 0;

    /**
     * The shortest number of moves to clear the board
     */
    public int shortestMoves = 99;

    /**
     * Whether the player is in a board clear state for the current board
     */
    private boolean playerWon = false;

    /**
     * Whether the player is in a board clear state when cached image was stored
     */
    private boolean cachedPlayerWon = false;

    /**
     * The indices along the paths of the currently selected piece
     */
    private List<Integer> moveIndices;

    /**
     * The indices along the paths of the selected piece when cached image was stored
     */
    private List<Integer> cachedMoveIndices;

    /**
     * The indices at the end of the paths of the currently selected piece
     */
    private List<Integer> stopIndices;

    /**
     * The indices at the end of the paths of the selected piece when cached image was stored
     */
    private List<Integer> cachedStopIndices;

    /**
     * The icon bitmap image to use for drawing the icons
     */
    private static Bitmap iconBitmap;

    /**
     * Bitmaps containing the various hexagons to draw for pieces and the board
     */
    private static Bitmap hexBoardWhiteBitmap;
    private static Bitmap hexBoardRedBitmap;
    private static Bitmap hexBoardLightGreyBitmap;
    private static Bitmap hexBoardDarkGreyBitmap;
    private static Bitmap hexBoardYellowBitmap;
    private static Bitmap hexBoardOrangeBitmap;
    private static Bitmap hexBoardHighlightYellowBitmap;
    private static Bitmap hexBoardHighlightOrangeBitmap;
    private static Bitmap hexRedBitmap;
    private static Bitmap hexGreenBitmap;
    private static Bitmap hexBlueBitmap;
    private static Bitmap hexOrangeBitmap;
    private static Bitmap hexYellowBitmap;
    private static Bitmap hexPurpleBitmap;

    /**
     * Constructor for the game.
     *
     * @param   main - The reference to the main view
     * @param   screenWidth - The screen width
     * @param   screenHeight - The screen height
     * @param   mainBoardSet - The set of main boards
     * @param   boardSet - The set of random boards
     * @param   state - The bundle state of the game
     */
    public Game(MainView main, int screenWidth, int screenHeight, List<String> mainBoardSet, List<List<String>> boardSet, Bundle state)
    {
        this.mainView = main;
        this.mainBoardSet = mainBoardSet;
        this.boardSet = boardSet;

        solution = new ArrayList<String>();
        slideFrame = 0;
        slideStart = -1;
        slideEnd = -1;
        slideDirection = -1;
        moving = false;

        moveIndices = new ArrayList<Integer>();
        cachedMoveIndices = new ArrayList<Integer>();
        stopIndices = new ArrayList<Integer>();
        cachedStopIndices = new ArrayList<Integer>();

        optionsOpen = false;
        buttonHeldDown = "";
        buttonHeldDownFrames = 0;

        // Setup the paint for text boxes
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(mainView.FONT_SIZE_20_SP);
        textPaint.setTypeface(mainView.LATO_FONT);
        optionsPaint = new Paint();
        optionsPaint.setColor(Color.argb(196, 0, 0, 0));
        optionsPaint.setStyle(Paint.Style.FILL);
        iconPaint = new Paint();
        debugPaint = new Paint();
        debugPaint.setColor(Color.GREEN);
        debugPaint.setTextSize(mainView.FONT_SIZE_20_SP);
        debugPaint.setTypeface(mainView.LATO_FONT);

        // Reload the bundle state if the app was on the game view prior to going to the background or similar event
        if(state != null)
        {
            String savedView = state.getString(MainActivity.STATE_VIEW);
            if (savedView.equals("game"))
            {
                // Reload the bundle state, re-setup all variables involved in tracking the state
                currentLevel = state.getInt(MainActivity.STATE_LEVEL);
                currentMove = state.getInt(MainActivity.STATE_MOVES_TAKEN);
                shortestMoves = state.getInt(MainActivity.STATE_SHORTEST_MOVES);
                boardState = state.getString(MainActivity.STATE_BOARD);
                initialBoardState = state.getString(MainActivity.STATE_INITIAL_BOARD);
                solution = state.getStringArrayList(MainActivity.STATE_SOLUTION);
                optionsOpen = state.getBoolean(MainActivity.STATE_GAME_OPTIONS_OPEN);
                generationMaxMoves = state.getInt(MainActivity.STATE_GENERATE_MAX_SOLVE);
                generationMinMoves = state.getInt(MainActivity.STATE_GENERATE_MIN_SOLVE);
            }
        }

        setSize(screenWidth, screenHeight);
    }

    /**
     * Sets the sizes of the elements in the game based on
     * the screen dimensions.
     *
     * @param   screenWidth - The screen width
     * @param   screenHeight - The screen height
     */
    public void setSize(int screenWidth, int screenHeight)
    {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        HEX_WIDTH = Math.round(HEX_WIDTH_PERCENT * screenWidth);
        HEX_HEIGHT = Math.round(HEX_HEIGHT_PERCENT * screenHeight);
        HEX_DEPTH = Math.round(HEX_HEIGHT / 10.0f);
        BOARD_X = Math.round(BOARD_X_PERCENT * screenWidth);
        BOARD_Y = Math.round(BOARD_Y_PERCENT * screenHeight);
        BUTTON_RADIUS = (int) (Utils.distanceBetweenPoints(0, 0, screenWidth, screenHeight) / 20);
        BUTTON_X = Math.round(BUTTON_X_PERCENT * screenWidth);
        BUTTON_1_Y = Math.round(BUTTON_Y_PERCENT * screenHeight);
        BUTTON_2_Y = Math.round((BUTTON_Y_PERCENT + BUTTON_SPACING_Y_PERCENT) * screenHeight);
        BUTTON_3_Y = Math.round((BUTTON_Y_PERCENT + (2f * BUTTON_SPACING_Y_PERCENT)) * screenHeight);
        BUTTON_4_Y = Math.round((BUTTON_Y_PERCENT + (3f * BUTTON_SPACING_Y_PERCENT)) * screenHeight);
        EXIT_X = Math.round(EXIT_X_PERCENT * screenWidth);
        EXIT_Y = Math.round(EXIT_Y_PERCENT * screenHeight);
        CLOSE_OPTIONS_X = Math.round(CLOSE_OPTIONS_X_PERCENT * screenWidth);
        CLOSE_OPTIONS_Y = Math.round(CLOSE_OPTIONS_Y_PERCENT * screenHeight);
        MOVES_PLUS_X = Math.round(MOVES_PLUS_X_PERCENT * screenWidth);
        MOVES_MINUS_X = Math.round(MOVES_MINUS_X_PERCENT * screenWidth);
        MOVES_MIN_Y = Math.round(MOVES_MIN_Y_PERCENT * screenHeight);
        MOVES_MAX_Y = Math.round(MOVES_MAX_Y_PERCENT * screenHeight);
        OPTIONS_PANEL_X = Math.round(OPTIONS_PANEL_X_PERCENT * screenWidth);
        OPTIONS_PANEL_Y = Math.round(OPTIONS_PANEL_Y_PERCENT * screenHeight);
        OPTIONS_PANEL_WIDTH = Math.round(OPTIONS_PANEL_WIDTH_PERCENT * screenWidth);
        OPTIONS_PANEL_HEIGHT = Math.round(OPTIONS_PANEL_HEIGHT_PERCENT * screenHeight);
        Rect tempRect = new Rect();
        textPaint.getTextBounds(OPTIONS_TITLE_1, 0, OPTIONS_TITLE_1.length(), tempRect);
        OPTIONS_PANEL_TEXT_TITLE_1_X = Math.round(0.5f * screenWidth) - Math.round(tempRect.width() / 2f);
        OPTIONS_PANEL_TEXT_TITLE_1_Y = Math.round(OPTIONS_PANEL_TEXT_TITLE_1_Y_PERCENT * screenHeight);
        textPaint.getTextBounds(OPTIONS_TITLE_2, 0, OPTIONS_TITLE_2.length(), tempRect);
        OPTIONS_PANEL_TEXT_TITLE_2_X = Math.round(0.5f * screenWidth) - Math.round(tempRect.width() / 2f);
        OPTIONS_PANEL_TEXT_TITLE_2_Y = Math.round(OPTIONS_PANEL_TEXT_TITLE_2_Y_PERCENT * screenHeight);
        textPaint.getTextBounds(OPTIONS_MAXIMUM, 0, OPTIONS_MAXIMUM.length(), tempRect);
        OPTIONS_PANEL_TEXT_MAX_X = Math.round(0.5f * screenWidth) - Math.round(tempRect.width() / 2f);
        OPTIONS_PANEL_TEXT_MAX_Y = Math.round(OPTIONS_PANEL_TEXT_MAX_Y_PERCENT * screenHeight);
        textPaint.getTextBounds(OPTIONS_MINIMUM, 0, OPTIONS_MINIMUM.length(), tempRect);
        OPTIONS_PANEL_TEXT_MIN_X = Math.round(0.5f * screenWidth) - Math.round(tempRect.width() / 2f);
        OPTIONS_PANEL_TEXT_MIN_Y = Math.round(OPTIONS_PANEL_TEXT_MIN_Y_PERCENT * screenHeight);
        textPaint.getTextBounds("1", 0, "1".length(), tempRect);
        OPTIONS_PANEL_VALUE_X = MOVES_MINUS_X + (int) ((MOVES_PLUS_X - MOVES_MINUS_X) / 2f) - (int) (tempRect.width() / 2f);
        textPaint.getTextBounds("20", 0, "20".length(), tempRect);
        OPTIONS_PANEL_VALUE_2_DIGIT_X = MOVES_MINUS_X + (int) ((MOVES_PLUS_X - MOVES_MINUS_X) / 2f) - (int) (tempRect.width() / 2f);
        OPTIONS_PANEL_VALUE_MAX_Y = MOVES_MAX_Y + (int) (tempRect.height() / 2f);
        OPTIONS_PANEL_VALUE_MIN_Y = MOVES_MIN_Y + (int) (tempRect.height() / 2f);
        textPaint.getTextBounds(CLEAR, 0, CLEAR.length(), tempRect);
        TEXT_Y = (int) ((Math.round(TEXT_BACKGROUND_PANEL_HEIGHT_PERCENT * screenHeight) / 2f) + (tempRect.height() / 2f));
        CLEAR_X = (int) ((screenWidth / 2f) - (tempRect.width() / 2f));
        textPaint.getTextBounds(PERFECT_CLEAR, 0, PERFECT_CLEAR.length(), tempRect);
        PERFECT_CLEAR_X = (int) ((screenWidth / 2f) - (tempRect.width() / 2f));
        textPaint.getTextBounds(INSTRUCTIONS_1, 0, INSTRUCTIONS_1.length(), tempRect);
        INSTRUCTIONS_1_X = (int) ((screenWidth / 2f) - (tempRect.width() / 2f));
        textPaint.getTextBounds(INSTRUCTIONS_2, 0, INSTRUCTIONS_2.length(), tempRect);
        INSTRUCTIONS_2_X = (int) ((screenWidth / 2f) - (tempRect.width() / 2f));
        retryX = BUTTON_X;
        generateX = BUTTON_X;
        generateOptionsX = BUTTON_X;
        hintX = BUTTON_X;

        boundingBoxes = Utils.getBoundingBoxes(HEX_WIDTH, HEX_HEIGHT, BOARD_X, BOARD_Y);
        optionsPanelRect = new Rect(OPTIONS_PANEL_X, OPTIONS_PANEL_Y, OPTIONS_PANEL_X + OPTIONS_PANEL_WIDTH, OPTIONS_PANEL_Y + OPTIONS_PANEL_HEIGHT);

        // Construct checking hex to compare taps to when determining which hexagon is selected
        hexCheck = Bitmap.createBitmap(HEX_WIDTH, HEX_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas temp = new Canvas(hexCheck);
        Utils.drawHex(temp, 0, 0, HEX_WIDTH, HEX_HEIGHT, 0xFF0000, 0, false);

        // Generate the icon bitmap to draw all the icons at once
        iconBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);

        optionsBackground = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        optionsBackground.eraseColor(Color.argb(128, 0, 0, 0));
        Canvas c = new Canvas(optionsBackground);
        c.drawRect(new Rect(OPTIONS_PANEL_X, OPTIONS_PANEL_Y, OPTIONS_PANEL_X + OPTIONS_PANEL_WIDTH, OPTIONS_PANEL_Y + OPTIONS_PANEL_HEIGHT), optionsPaint);
        Utils.drawIcon(new Canvas(optionsBackground), "plus", MOVES_PLUS_X, MOVES_MIN_Y, BUTTON_RADIUS / 2f);
        Utils.drawIcon(new Canvas(optionsBackground), "minus", MOVES_MINUS_X, MOVES_MIN_Y, BUTTON_RADIUS / 2f);
        Utils.drawIcon(new Canvas(optionsBackground), "plus", MOVES_PLUS_X, MOVES_MAX_Y, BUTTON_RADIUS / 2f);
        Utils.drawIcon(new Canvas(optionsBackground), "minus", MOVES_MINUS_X, MOVES_MAX_Y, BUTTON_RADIUS / 2f);
        Utils.drawIcon(new Canvas(optionsBackground), "close", CLOSE_OPTIONS_X, CLOSE_OPTIONS_Y, BUTTON_RADIUS / 2f);

        textBackground = Bitmap.createBitmap(screenWidth, (int) (screenHeight * TEXT_BACKGROUND_PANEL_HEIGHT_PERCENT), Bitmap.Config.ARGB_8888);
        textBackground.eraseColor(Color.argb(128, 0, 0, 0));

        hexBoardWhiteBitmap = Bitmap.createBitmap(HEX_WIDTH, HEX_HEIGHT + HEX_DEPTH, Bitmap.Config.ARGB_8888);
        c = new Canvas(hexBoardWhiteBitmap);
        Utils.drawHex(c, 0, 0, HEX_WIDTH, HEX_HEIGHT, 0xFFFFFF, HEX_DEPTH, true);
        hexBoardRedBitmap = Bitmap.createBitmap(HEX_WIDTH, HEX_HEIGHT + HEX_DEPTH, Bitmap.Config.ARGB_8888);
        c = new Canvas(hexBoardRedBitmap);
        Utils.drawHex(c, 0, 0, HEX_WIDTH, HEX_HEIGHT, 0xFF0000, HEX_DEPTH, true);
        hexBoardDarkGreyBitmap = Bitmap.createBitmap(HEX_WIDTH, HEX_HEIGHT + HEX_DEPTH, Bitmap.Config.ARGB_8888);
        c = new Canvas(hexBoardDarkGreyBitmap);
        Utils.drawHex(c, 0, 0, HEX_WIDTH, HEX_HEIGHT, 0x707070, HEX_DEPTH, true);
        hexBoardLightGreyBitmap = Bitmap.createBitmap(HEX_WIDTH, HEX_HEIGHT + HEX_DEPTH, Bitmap.Config.ARGB_8888);
        c = new Canvas(hexBoardLightGreyBitmap);
        Utils.drawHex(c, 0, 0, HEX_WIDTH, HEX_HEIGHT, 0xD8D8D8, HEX_DEPTH, true);
        hexBoardYellowBitmap = Bitmap.createBitmap(HEX_WIDTH, HEX_HEIGHT + HEX_DEPTH, Bitmap.Config.ARGB_8888);
        c = new Canvas(hexBoardYellowBitmap);
        Utils.drawHex(c, 0, 0, HEX_WIDTH, HEX_HEIGHT, 0xFFCC00, HEX_DEPTH, true);
        hexBoardOrangeBitmap = Bitmap.createBitmap(HEX_WIDTH, HEX_HEIGHT + HEX_DEPTH, Bitmap.Config.ARGB_8888);
        c = new Canvas(hexBoardOrangeBitmap);
        Utils.drawHex(c, 0, 0, HEX_WIDTH, HEX_HEIGHT, 0xDD7700, HEX_DEPTH, true);
        hexBoardHighlightYellowBitmap = Bitmap.createBitmap(HEX_WIDTH, HEX_HEIGHT, Bitmap.Config.ARGB_8888);
        c = new Canvas(hexBoardHighlightYellowBitmap);
        Utils.drawHex(c, 0, 0, HEX_WIDTH, HEX_HEIGHT, 0xFFCC00, 0, true);
        hexBoardHighlightOrangeBitmap = Bitmap.createBitmap(HEX_WIDTH, HEX_HEIGHT, Bitmap.Config.ARGB_8888);
        c = new Canvas(hexBoardHighlightOrangeBitmap);
        Utils.drawHex(c, 0, 0, HEX_WIDTH, HEX_HEIGHT, 0xDD7700, 0, true);
        hexRedBitmap = Bitmap.createBitmap(HEX_WIDTH / 2, (HEX_HEIGHT / 2) + (HEX_DEPTH / 2), Bitmap.Config.ARGB_8888);
        c = new Canvas(hexRedBitmap);
        Utils.drawHex(c, 0, 0, HEX_WIDTH / 2, HEX_HEIGHT / 2, 0xAA0000, HEX_DEPTH / 2, true);
        hexGreenBitmap = Bitmap.createBitmap(HEX_WIDTH / 2, (HEX_HEIGHT / 2) + (HEX_DEPTH / 2), Bitmap.Config.ARGB_8888);
        c = new Canvas(hexGreenBitmap);
        Utils.drawHex(c, 0, 0, HEX_WIDTH / 2, HEX_HEIGHT / 2, 0x009900, HEX_DEPTH / 2, true);
        hexBlueBitmap = Bitmap.createBitmap(HEX_WIDTH / 2, (HEX_HEIGHT / 2) + (HEX_DEPTH / 2), Bitmap.Config.ARGB_8888);
        c = new Canvas(hexBlueBitmap);
        Utils.drawHex(c, 0, 0, HEX_WIDTH / 2, HEX_HEIGHT / 2, 0x333399, HEX_DEPTH / 2, true);
        hexYellowBitmap = Bitmap.createBitmap(HEX_WIDTH / 2, (HEX_HEIGHT / 2) + (HEX_DEPTH / 2), Bitmap.Config.ARGB_8888);
        c = new Canvas(hexYellowBitmap);
        Utils.drawHex(c, 0, 0, HEX_WIDTH / 2, HEX_HEIGHT / 2, 0xFFFF00, HEX_DEPTH / 2, true);
        hexOrangeBitmap = Bitmap.createBitmap(HEX_WIDTH / 2, (HEX_HEIGHT / 2) + (HEX_DEPTH / 2), Bitmap.Config.ARGB_8888);
        c = new Canvas(hexOrangeBitmap);
        Utils.drawHex(c, 0, 0, HEX_WIDTH / 2, HEX_HEIGHT / 2, 0xDD7700, HEX_DEPTH / 2, true);
        hexPurpleBitmap = Bitmap.createBitmap(HEX_WIDTH / 2, (HEX_HEIGHT / 2) + (HEX_DEPTH / 2), Bitmap.Config.ARGB_8888);
        c = new Canvas(hexPurpleBitmap);
        Utils.drawHex(c, 0, 0, HEX_WIDTH / 2, HEX_HEIGHT / 2, 0x9900AA, HEX_DEPTH / 2, true);

        // Update button UI for levels vs random
        updateUIState();

        // Cache image of the game scene to speed up drawing
        cachedGameScene = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        cachedGameScene.eraseColor(0xFF000000);

        // Cache image of the current game scene with options menu open to speed up drawing
        cachedGameSceneWithOptionsOpen = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        cachedGameSceneWithOptionsOpen.eraseColor(0xFF000000);
        c = new Canvas(cachedGameSceneWithOptionsOpen);
        drawBoardTextAndIcons(c);
        drawHighlight(c);
        if (!boardState.equals(""))
        {
            drawObjectsOnBoard(c);
        }
        c.drawBitmap(optionsBackground, 0, 0, null);
        c.drawText(OPTIONS_TITLE_1, OPTIONS_PANEL_TEXT_TITLE_1_X, OPTIONS_PANEL_TEXT_TITLE_1_Y, textPaint);
        c.drawText(OPTIONS_TITLE_2, OPTIONS_PANEL_TEXT_TITLE_2_X, OPTIONS_PANEL_TEXT_TITLE_2_Y, textPaint);
        c.drawText(OPTIONS_MAXIMUM, OPTIONS_PANEL_TEXT_MAX_X, OPTIONS_PANEL_TEXT_MAX_Y, textPaint);
        c.drawText(OPTIONS_MINIMUM, OPTIONS_PANEL_TEXT_MIN_X, OPTIONS_PANEL_TEXT_MIN_Y, textPaint);
    }

    /**
     * Updates the user interface state to show and hide the correct buttons.
     */
    private void updateUIState()
    {
        playerWon = Utils.boardSolved(boardState);
        if (currentLevel != -1)
        {
            generateY = Math.round(2f * screenHeight);
            generateOptionsY = Math.round(2f * screenHeight);
            retryY = BUTTON_1_Y;
            hintY = BUTTON_2_Y;

            // Draw only level icons
            iconBitmap.eraseColor(Color.argb(0, 0, 0, 0));
            Utils.drawIcon(new Canvas(iconBitmap), "home", EXIT_X, EXIT_Y, BUTTON_RADIUS);
            Utils.drawIcon(new Canvas(iconBitmap), "retry", retryX, retryY, BUTTON_RADIUS);
            if (PlayerData.getLevelClearStates().charAt(currentLevel) == '2')
            {
                Utils.drawIcon(new Canvas(iconBitmap), "hint", hintX, hintY, BUTTON_RADIUS);
            }
        }
        else
        {
            generateY = BUTTON_1_Y;
            generateOptionsY = BUTTON_2_Y;
            retryY = BUTTON_3_Y;
            hintY = BUTTON_4_Y;

            // Draw only non-level icons
            iconBitmap.eraseColor(Color.argb(0, 0, 0, 0));
            Utils.drawIcon(new Canvas(iconBitmap), "home", EXIT_X, EXIT_Y, BUTTON_RADIUS);
            Utils.drawIcon(new Canvas(iconBitmap), "new", generateX, generateY, BUTTON_RADIUS);
            Utils.drawIcon(new Canvas(iconBitmap), "options", generateOptionsX, generateOptionsY, BUTTON_RADIUS);
            Utils.drawIcon(new Canvas(iconBitmap), "retry", retryX, retryY, BUTTON_RADIUS);
            Utils.drawIcon(new Canvas(iconBitmap), "hint", hintX, hintY, BUTTON_RADIUS);
        }
    }

    /**
     * Handles starting up a new level (zero based) or random board if -1.
     */
    public void newBoardState()
    {
        if (currentLevel != -1)
        {
            setBoardState(currentLevel);
        }
        else
        {
            randomBoardState(generationMinMoves, generationMaxMoves);
        }
        updateUIState();
    }

    /**
     * Handles the updates to the current canvas and game frame logic.
     *
     * @param   canvas - The canvas to draw on
     * @param   framesPerSecond - The frames per second for debugging
     */
    public void update(Canvas canvas, float framesPerSecond)
    {
        // Update
        handleFade();
        handleButtonHeldDown();
        processSlide();

        // Draw
        if (optionsOpen)
        {
            drawOptionsMenu(canvas);
        }
        else
        {
            drawBoardTextAndIcons(canvas);
            drawHighlight(canvas);
            drawObjectsOnBoard(canvas);
        }

        /// TODO: Remove draw debug down cursor
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        canvas.drawCircle(Touch.downX, Touch.downY, 5, paint);
        /// TODO: Remove draw debug cursor
        paint.setColor(Color.GREEN);
        canvas.drawCircle(Touch.x, Touch.y, 5, paint);
        /// TODO: Remove draw debug frames per second, ALSO remove framesPerSecond argument (also in interface)
        canvas.drawText(String.valueOf(framesPerSecond), 10, screenHeight - 10, debugPaint);
    }

    /**
     * Updates the fading transition and sends an event when finished.
     */
    private void handleFade()
    {
        if (fadingIn || fadingOut)
        {
            fadeFrame--;
            if (fadeFrame <= 0)
            {
                fadeFrame = 0;
                if (fadingIn)
                {
                    fadingIn = false;
                    fadingOut = false;
                }
                else
                {
                    mainView.handleEvent(new CustomEvent(CustomEvent.EXIT_GAME));
                }
            }
        }
    }

    /**
     * Handles holding a button down, such as generate board min/max solvable moves.
     */
    private void handleButtonHeldDown()
    {
        if (!buttonHeldDown.equals(""))
        {
            buttonHeldDownFrames++;
            if (buttonHeldDown.equals("movesMinMinus") && buttonHeldDownFrames > 30 && buttonHeldDownFrames % 8 == 0)
            {
                generationMinMoves--;
                if (generationMinMoves < 1)
                {
                    generationMinMoves = 1;
                }
                else
                {
                    SoundManager.play(R.raw.tap);
                }
            }
            else if (buttonHeldDown.equals("movesMinPlus") && buttonHeldDownFrames > 30 && buttonHeldDownFrames % 8 == 0)
            {
                generationMinMoves++;
                if (generationMinMoves > 20)
                {
                    generationMinMoves = 20;
                }
                else
                {
                    SoundManager.play(R.raw.tap);
                }
                if (generationMaxMoves < generationMinMoves)
                {
                    generationMaxMoves = generationMinMoves;
                }
            }
            else if (buttonHeldDown.equals("movesMaxMinus") && buttonHeldDownFrames > 30 && buttonHeldDownFrames % 8 == 0)
            {
                generationMaxMoves--;
                if (generationMaxMoves < 1)
                {
                    generationMaxMoves = 1;
                }
                else
                {
                    SoundManager.play(R.raw.tap);
                }
                if (generationMaxMoves < generationMinMoves)
                {
                    generationMinMoves = generationMaxMoves;
                }
            }
            else if (buttonHeldDown.equals("movesMaxPlus") && buttonHeldDownFrames > 30 && buttonHeldDownFrames % 8 == 0)
            {
                generationMaxMoves++;
                if (generationMaxMoves > 20)
                {
                    generationMaxMoves = 20;
                }
                else
                {
                    SoundManager.play(R.raw.tap);
                }
            }
        }
    }

    /**
     * Processes the slide move and updates the game accordingly.
     */
    private void processSlide()
    {
        if (moving)
        {
            slideFrame--;
            if (slideFrame == 0)
            {
                moving = false;
                currentMove++;
                boardState = slideToBoard;
                playerWon = Utils.boardSolved(boardState);
                if (playerWon && currentLevel != -1)
                {
                    // Cleared the board, player wins
                    if (currentMove == (solution.size() - 1))
                    {
                        PlayerData.updateLevelClearStates(currentLevel, 2);
                    }
                    else
                    {
                        PlayerData.updateLevelClearStates(currentLevel, 1);
                    }
                    updateUIState();
                }
            }
        }
    }

    /**
     * Handles the game logic for touch events.
     *
     * @param   motionEvent - The touch motion event
     */
    public void handleTouch(MotionEvent motionEvent)
    {
        if (!fadingIn && !fadingOut)
        {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            {
                if (slideFrame <= 0) ignoreTouch = false;
            }

            // Don't allow touch events while animating the slide move
            if (slideFrame > 0 || ignoreTouch)
            {
                ignoreTouch = true;
                return;
            }

            int foundHex = findHex();

            // Respond to touch event with proper game response
            switch (motionEvent.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    tapping = true;
                    if (!optionsOpen && foundHex != -1)
                    {
                        // Select the hexagon if a piece exists on top of it and its not already selected
                        if(hexSelect != foundHex)
                        {
                            if (Utils.pieceAtIndex(foundHex, boardState))
                            {
                                hexSelect = foundHex;
                                selectionSetThisTap = true;
                                List<List<Integer>> pathIndices = Utils.getPathIndices(boardState, hexSelect);
                                moveIndices = pathIndices.get(0);
                                stopIndices = pathIndices.get(1);
                            }
                        }
                    }
                    else if (!optionsOpen) // Selecting outside of the board, clear selection
                    {
                        hexSelect = -1;
                        moveIndices.clear();
                        stopIndices.clear();
                    }
                    else if (optionsOpen) // Start tracking which options button is being held down
                    {
                        if ((Utils.distanceBetweenPoints(Touch.x, Touch.y, MOVES_MINUS_X, MOVES_MIN_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, MOVES_MINUS_X, MOVES_MIN_Y) < BUTTON_RADIUS)) // Moves min minus
                        {
                            buttonHeldDown = "movesMinMinus";
                        }
                        else if ((Utils.distanceBetweenPoints(Touch.x, Touch.y, MOVES_PLUS_X, MOVES_MIN_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, MOVES_PLUS_X, MOVES_MIN_Y) < BUTTON_RADIUS)) // Moves min plus
                        {
                            buttonHeldDown = "movesMinPlus";
                        }
                        else if ((Utils.distanceBetweenPoints(Touch.x, Touch.y, MOVES_MINUS_X, MOVES_MAX_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, MOVES_MINUS_X, MOVES_MAX_Y) < BUTTON_RADIUS)) // Moves max minus
                        {
                            buttonHeldDown = "movesMaxMinus";
                        }
                        else if ((Utils.distanceBetweenPoints(Touch.x, Touch.y, MOVES_PLUS_X, MOVES_MAX_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, MOVES_PLUS_X, MOVES_MAX_Y) < BUTTON_RADIUS)) // Moves max plus
                        {
                            buttonHeldDown = "movesMaxPlus";
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    // Clear held button
                    buttonHeldDown = "";
                    buttonHeldDownFrames = 0;
                    if (!optionsOpen && ((Utils.distanceBetweenPoints(Touch.x, Touch.y, generateX, generateY) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, generateX, generateY) < BUTTON_RADIUS))) // Generate New Board
                    {
                        SoundManager.play(R.raw.tap);
                        newBoardState();
                    }
                    else if (!optionsOpen && ((Utils.distanceBetweenPoints(Touch.x, Touch.y, generateOptionsX, generateOptionsY) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, generateOptionsX, generateOptionsY) < BUTTON_RADIUS))) // Open Board Options
                    {
                        SoundManager.play(R.raw.tap);
                        optionsOpen = true;

                        // Cache image of the current game scene with options menu open to speed up drawing
                        cachedGameSceneWithOptionsOpen.eraseColor(0xFF000000);
                        Canvas c = new Canvas(cachedGameSceneWithOptionsOpen);
                        drawBoardTextAndIcons(c);
                        drawHighlight(c);
                        drawObjectsOnBoard(c);
                        c.drawBitmap(optionsBackground, 0, 0, null);
                        c.drawText(OPTIONS_TITLE_1, OPTIONS_PANEL_TEXT_TITLE_1_X, OPTIONS_PANEL_TEXT_TITLE_1_Y, textPaint);
                        c.drawText(OPTIONS_TITLE_2, OPTIONS_PANEL_TEXT_TITLE_2_X, OPTIONS_PANEL_TEXT_TITLE_2_Y, textPaint);
                        c.drawText(OPTIONS_MAXIMUM, OPTIONS_PANEL_TEXT_MAX_X, OPTIONS_PANEL_TEXT_MAX_Y, textPaint);
                        c.drawText(OPTIONS_MINIMUM, OPTIONS_PANEL_TEXT_MIN_X, OPTIONS_PANEL_TEXT_MIN_Y, textPaint);
                    }
                    else if (!optionsOpen && ((Utils.distanceBetweenPoints(Touch.x, Touch.y, retryX, retryY) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, retryX, retryY) < BUTTON_RADIUS))) // Reset
                    {
                        SoundManager.play(R.raw.tap);
                        boardState = initialBoardState;
                        currentMove = 0;
                        hexSelect = -1;
                        moveIndices.clear();
                        stopIndices.clear();
                    }
                    else if (!optionsOpen && ((Utils.distanceBetweenPoints(Touch.x, Touch.y, hintX, hintY) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, hintX, hintY) < BUTTON_RADIUS))) // Step Hint
                    {
                        boolean enabled = true;
                        if (currentLevel != -1)
                        {
                            if (PlayerData.getLevelClearStates().charAt(currentLevel) != '2')
                            {
                                enabled = false;
                            }
                        }
                        if (enabled)
                        {
                            int solutionIndex = solution.indexOf(boardState);
                            if (solutionIndex == -1) {
                                boardState = solution.get(0);
                                currentMove = 0;
                            } else if (solutionIndex != solution.size() - 1) {
                                List<Integer> move_index = Utils.getMoveIndices(boardState, solution.get(solutionIndex + 1));
                                attemptMove(move_index.get(0), move_index.get(1));
                            }
                            hexSelect = -1;
                            moveIndices.clear();
                            stopIndices.clear();
                        }
                    }
                    else if (optionsOpen && ((Utils.distanceBetweenPoints(Touch.x, Touch.y, CLOSE_OPTIONS_X, CLOSE_OPTIONS_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, CLOSE_OPTIONS_X, CLOSE_OPTIONS_Y) < BUTTON_RADIUS))) // Close Board Options
                    {
                        SoundManager.play(R.raw.tap);
                        optionsOpen = false;
                    }
                    else if (optionsOpen && ((Utils.distanceBetweenPoints(Touch.x, Touch.y, MOVES_MINUS_X, MOVES_MIN_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, MOVES_MINUS_X, MOVES_MIN_Y) < BUTTON_RADIUS))) // Moves min minus
                    {
                        generationMinMoves--;
                        if (generationMinMoves < 1)
                        {
                            generationMinMoves = 1;
                        }
                        else
                        {
                            SoundManager.play(R.raw.tap);
                        }
                    }
                    else if (optionsOpen && ((Utils.distanceBetweenPoints(Touch.x, Touch.y, MOVES_PLUS_X, MOVES_MIN_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, MOVES_PLUS_X, MOVES_MIN_Y) < BUTTON_RADIUS))) // Moves min plus
                    {
                        generationMinMoves++;
                        if (generationMinMoves > 20)
                        {
                            generationMinMoves = 20;
                        }
                        else
                        {
                            SoundManager.play(R.raw.tap);
                        }
                        if (generationMaxMoves < generationMinMoves)
                        {
                            generationMaxMoves = generationMinMoves;
                        }
                    }
                    else if (optionsOpen && ((Utils.distanceBetweenPoints(Touch.x, Touch.y, MOVES_MINUS_X, MOVES_MAX_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, MOVES_MINUS_X, MOVES_MAX_Y) < BUTTON_RADIUS))) // Moves max minus
                    {
                        generationMaxMoves--;
                        if (generationMaxMoves < 1)
                        {
                            generationMaxMoves = 1;
                        }
                        else
                        {
                            SoundManager.play(R.raw.tap);
                        }
                        if (generationMaxMoves < generationMinMoves)
                        {
                            generationMinMoves = generationMaxMoves;
                        }
                    }
                    else if (optionsOpen && ((Utils.distanceBetweenPoints(Touch.x, Touch.y, MOVES_PLUS_X, MOVES_MAX_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, MOVES_PLUS_X, MOVES_MAX_Y) < BUTTON_RADIUS))) // Moves max plus
                    {
                        generationMaxMoves++;
                        if (generationMaxMoves > 20)
                        {
                            generationMaxMoves = 20;
                        }
                        else
                        {
                            SoundManager.play(R.raw.tap);
                        }
                    }
                    else if (!optionsOpen && ((Utils.distanceBetweenPoints(Touch.x, Touch.y, EXIT_X, EXIT_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, EXIT_X, EXIT_Y) < BUTTON_RADIUS))) // Exit game
                    {
                        SoundManager.play(R.raw.tap);
                        fadingOut = true;
                        fadeFrame = MainView.TRANSITION_FRAMES;
                    }
                    else if (optionsOpen && (!optionsPanelRect.contains(Touch.x, Touch.y) && !optionsPanelRect.contains(Touch.downX, Touch.downY))) // Touching outside options panel
                    {
                        optionsOpen = false;
                    }
                    else if (!optionsOpen && foundHex != -1) // Attempt to move selected hexagon to hexagon at the release point of the touch
                    {
                        if (moveIndices.indexOf(foundHex) != -1)
                        {
                            // Released touch on the path, attempt to move to the end of the path
                            int pathDir = Utils.getMoveDirection(hexSelect, foundHex);
                            for (int i = 0; i < stopIndices.size(); i++)
                            {
                                if (Utils.getMoveDirection(hexSelect, stopIndices.get(i)) == pathDir)
                                {
                                    attemptMove(hexSelect, stopIndices.get(i));
                                    break;
                                }
                            }
                        }
                        else
                        {
                            attemptMove(hexSelect, foundHex);
                        }
                        if (moving || !selectionSetThisTap)
                        {
                            hexSelect = -1;
                            moveIndices.clear();
                            stopIndices.clear();
                        }
                    }
                    selectionSetThisTap = false;
                    tapping = false;
                    playerWon = Utils.boardSolved(boardState);
                    break;
                case MotionEvent.ACTION_MOVE:
                    // Check for touchs moving off a held button
                    if (optionsOpen)
                    {
                        if (buttonHeldDown == "movesMinMinus")
                        {
                            if (!((Utils.distanceBetweenPoints(Touch.x, Touch.y, MOVES_MINUS_X, MOVES_MIN_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, MOVES_MINUS_X, MOVES_MIN_Y) < BUTTON_RADIUS))) // Moves min minus
                            {
                                buttonHeldDown = "";
                                buttonHeldDownFrames = 0;
                            }
                        }
                        else if (buttonHeldDown == "movesMinPlus")
                        {
                            if (!((Utils.distanceBetweenPoints(Touch.x, Touch.y, MOVES_PLUS_X, MOVES_MIN_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, MOVES_PLUS_X, MOVES_MIN_Y) < BUTTON_RADIUS))) // Moves min plus
                            {
                                buttonHeldDown = "";
                                buttonHeldDownFrames = 0;
                            }
                        }
                        else if (buttonHeldDown == "movesMaxMinus")
                        {
                            if (!((Utils.distanceBetweenPoints(Touch.x, Touch.y, MOVES_MINUS_X, MOVES_MAX_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, MOVES_MINUS_X, MOVES_MAX_Y) < BUTTON_RADIUS))) // Moves max minus
                            {
                                buttonHeldDown = "";
                                buttonHeldDownFrames = 0;
                            }
                        }
                        else if (buttonHeldDown == "movesMaxPlus")
                        {
                            if (!((Utils.distanceBetweenPoints(Touch.x, Touch.y, MOVES_PLUS_X, MOVES_MAX_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, MOVES_PLUS_X, MOVES_MAX_Y) < BUTTON_RADIUS))) // Moves max plus
                            {
                                buttonHeldDown = "";
                                buttonHeldDownFrames = 0;
                            }
                        }
                    }
                    break;
            }
        }
    }

    /**
     * Attempts to move the piece at starting index to ending index.
     *
     * @param	start - Start index
     * @param	end - End index
     */
    private void attemptMove(int start, int end)
    {
        String newBoard = "";
        String[] pairs = boardState.split(",");
        String color;
        String index;
        String movingColor = "";
        for (int i = 0; i < pairs.length; i++)
        {
            color = pairs[i].split("-")[0];
            index = pairs[i].split("-")[1];
            if (Integer.parseInt(index) == start)
            {
                index = String.valueOf(end);
                movingColor = color;
            }
            if (i > 0) newBoard += ",";
            newBoard += color + "-" + index;
        }
        if (!newBoard.equals(boardState))
        {
            int dir = Utils.getMoveDirection(start, end);
            if (dir != -1) {
                int encodedMove = dir;
                if (movingColor.equals("R")) encodedMove += 0;
                else if (movingColor.equals("G")) encodedMove += 6;
                else if (movingColor.equals("B")) encodedMove += 12;
                else if (movingColor.equals("Y")) encodedMove += 18;
                else if (movingColor.equals("O")) encodedMove += 24;
                else if (movingColor.equals("P")) encodedMove += 30;
                else return;
                if (Utils.getBoardAfterMove(boardState, encodedMove).equals(newBoard))
                {
                    SoundManager.play(R.raw.slide);
                    slideStart = start;
                    slideEnd = end;
                    slideFrame = SLIDE_FRAMES;
                    slideDirection = dir;
                    slideToBoard = newBoard;
                    moving = true;
                }
            }
        }
    }

    /**
     * Sets the board state to the given level.
     *
     * @param	level - The level to set the board state to (zero based)
     */
    private void setBoardState(int level)
    {
        parseSolution(mainBoardSet.get(level));
        boardState = Utils.convertCompressedBoard(mainBoardSet.get(level));
        currentMove = 0;
        hexSelect = -1;
        moveIndices.clear();
        stopIndices.clear();
        initialBoardState = boardState;
    }

    /**
     * Randomly generates a board state.
     *
     * @param	low - The lowest number of moves acceptable
     * @param	high - The highest number of moves acceptable
     */
    private void randomBoardState(int low, int high)
    {
        int totalApplicableBoards = 0;
        int i;
        for (i = low - 1; i < high; i++) totalApplicableBoards += boardSet.get(i).size();
        Random rand = new Random();
        int randomApplicableBoard = rand.nextInt(totalApplicableBoards);
        int sum = 0;
        int index = boardSet.size() - 1;
        for (i = low - 1; i < high; i++)
        {
            sum += boardSet.get(i).size();
            if (sum > randomApplicableBoard)
            {
                index = i;
                break;
            }
        }
        parseSolution(boardSet.get(index).get(randomApplicableBoard - (sum - boardSet.get(index).size())));
        boardState = Utils.convertCompressedBoard(boardSet.get(index).get(randomApplicableBoard - (sum - boardSet.get(index).size())));
        currentMove = 0;
        hexSelect = -1;
        moveIndices.clear();
        stopIndices.clear();
        initialBoardState = boardState;
    }

    /**
     * Parses the compressed format board to set the solution for the board.
     *
     * @param	compressedBoard - The board in compressed format
     */
    private void parseSolution(String compressedBoard)
    {
        shortestMoves = Integer.parseInt(String.valueOf(compressedBoard.charAt(0)), 36);
        List<Integer> encodedMoves = new ArrayList<Integer>();
        int i;
        for (i = 1; i <= shortestMoves; i++)
        {
            encodedMoves.add(Integer.parseInt(String.valueOf(compressedBoard.charAt(i)), 36));
        }
        String next = Utils.convertCompressedBoard(compressedBoard);
        solution.clear();
        solution.add(next);
        for (i = 0; i < encodedMoves.size(); i++)
        {
            next = Utils.getBoardAfterMove(next, encodedMoves.get(i));
            solution.add(next);
        }
    }

    /**
     * Returns the index of the hexagon the mouse is currently over or -1.
     *
     * @return	The hexagon the mouse is over or -1
     */
    private int findHex()
    {
        for (int i = 0; i < boundingBoxes.size(); i++)
        {
            if (boundingBoxes.get(i).contains(Touch.x, Touch.y))
            {
                if (Integer.toHexString(hexCheck.getPixel(Touch.x - boundingBoxes.get(i).left, Touch.y - boundingBoxes.get(i).top)).equals("ffff0000"))
                {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Returns whether the game board/text/icons have changed.
     *
     * @return  Whether the game board/text/icons have changed
     */
    private boolean gameBoardTextOrIconsChanged()
    {
        boolean changed = false;
        if (fadingIn || fadingOut) changed = true;
        if (cachedHexSelect != hexSelect) changed = true;
        if (cachedPlayerWon != playerWon) changed = true;
        if (cachedLevel != currentLevel) changed = true;
        if (!cachedMoveIndices.equals(moveIndices)) changed = true;
        if (!cachedStopIndices.equals(stopIndices)) changed = true;
        if (changed)
        {
            // Store new cached variables for current state
            cachedHexSelect = hexSelect;
            cachedPlayerWon = playerWon;
            cachedLevel = currentLevel;
            cachedMoveIndices.clear();
            cachedStopIndices.clear();
            int i;
            for(i = 0; i < moveIndices.size(); i++)
            {
                cachedMoveIndices.add(moveIndices.get(i));
            }
            for(i = 0; i < stopIndices.size(); i++)
            {
                cachedStopIndices.add(stopIndices.get(i));
            }
        }
        return changed;
    }

    /**
     * Draws the game board, text and icons.
     *
     * @param canvas - The canvas to draw the board, text and icons on
     */
    private void drawBoardTextAndIcons(Canvas canvas)
    {
        if (gameBoardTextOrIconsChanged())
        {
            // Save new cached board/text/icon bitmap
            Canvas cachedCanvas = new Canvas(cachedGameScene);

            int defaultMatrix = cachedCanvas.save();

            // Clear board
            cachedCanvas.drawARGB(0xff, 0x00, 0x00, 0x00);

            // Draw the background shifted based on the current level
            cachedCanvas.translate(-1 * getBackgroundOffset(), 0);
            cachedCanvas.drawBitmap(MainView.background, 0, 0, null);

            // Remove the translation shift before drawing the rest of the game
            cachedCanvas.restoreToCount(defaultMatrix);
            defaultMatrix = cachedCanvas.save();

            // Draw the hexagon tiles from back to front
            float width = HEX_WIDTH;
            // Squish the hexagons into each other slightly to prevent small visual oddities along edges
            float placementWidth = width - 1;
            float height = HEX_HEIGHT;
            float startX = BOARD_X;
            float startY = BOARD_Y;
            float x = startX + (width * 0.75f);
            float y = startY;
            int index = 1;
            for (int y_index = 0; y_index < 11; y_index++)
            {
                for (int x_index = 0; x_index < 3; x_index++)
                {
                    if ((y_index % 2 == 0) && x_index == 2) break;
                    if (hexSelect == index) cachedCanvas.drawBitmap(hexBoardYellowBitmap, x, y, null);
                    else if (stopIndices.indexOf(index) != -1) cachedCanvas.drawBitmap(hexBoardDarkGreyBitmap, x, y, null);
                    else if (moveIndices.indexOf(index) != -1) cachedCanvas.drawBitmap(hexBoardLightGreyBitmap, x, y, null);
                    else if (index == 12) cachedCanvas.drawBitmap(hexBoardRedBitmap, x, y, null);
                    else cachedCanvas.drawBitmap(hexBoardWhiteBitmap, x, y, null);
                    x += (placementWidth * 1.5);
                    if (index == 25) index++;
                    else index += 2;
                }
                if (y_index % 2 == 0)
                {
                    x = startX;
                    index -= 5;
                }
                else
                {
                    x = startX + (placementWidth * 0.75f);
                    if (index == 26) index = 25;
                }
                y += (height * 0.5);
            }

            // Draw icons and text
            String textToDraw = "";
            int textX = 0;
            boolean textVisible = false;
            if (playerWon)
            {
                if (currentMove == (solution.size() - 1))
                {
                    textToDraw = PERFECT_CLEAR;
                    textX = PERFECT_CLEAR_X;
                    textVisible = true;
                }
                else
                {
                    textToDraw = CLEAR;
                    textX = CLEAR_X;
                    textVisible = true;
                }
            }
            else
            {
                if (currentLevel == 0)
                {
                    textToDraw = INSTRUCTIONS_1;
                    textX = INSTRUCTIONS_1_X;
                    textVisible = true;
                }
                else if (currentLevel == 1)
                {
                    textToDraw = INSTRUCTIONS_2;
                    textX = INSTRUCTIONS_2_X;
                    textVisible = true;
                }
            }
            // Set icon and text transparency
            if (fadingIn || fadingOut)
            {
                if (fadingIn)
                {
                    textPaint.setColor(Color.argb((int) ((1f - ((float) fadeFrame / MainView.TRANSITION_FRAMES)) * 255), 255, 255, 255));
                    iconPaint.setAlpha((int) ((1f - ((float) fadeFrame / MainView.TRANSITION_FRAMES)) * 255));
                }
                else
                {
                    textPaint.setColor(Color.argb((int) (((float) fadeFrame / MainView.TRANSITION_FRAMES) * 255), 255, 255, 255));
                    iconPaint.setAlpha((int) (((float) fadeFrame / MainView.TRANSITION_FRAMES) * 255));
                }
            }
            else
            {
                textPaint.setColor(Color.argb(255, 255, 255, 255));
                iconPaint.setAlpha(255);
            }
            if (textVisible)
            {
                cachedCanvas.drawBitmap(textBackground, 0, 0, iconPaint);
                cachedCanvas.drawBitmap(iconBitmap, 0, 0, iconPaint);
                cachedCanvas.drawText(textToDraw, textX, TEXT_Y, textPaint);
            }
            else
            {
                cachedCanvas.drawBitmap(iconBitmap, 0, 0, iconPaint);
            }
        }
        // Draw the cached game scene
        canvas.drawBitmap(cachedGameScene, 0, 0, null);
    }

    /**
     * Draws the highlight of what hexagons are currently selected.
     */
    private void drawHighlight(Canvas canvas)
    {
        // Highlight when over a hexagon and not sliding
        int cursorOverHex = findHex();
        if (cursorOverHex != -1 && slideFrame <= 0)
        {
            if (tapping)
            {
                if (moveIndices.indexOf(cursorOverHex) != -1 || stopIndices.indexOf(cursorOverHex) != -1)
                {
                    // Highlight the path the mouse is currently over
                    int dir = Utils.getMoveDirection(hexSelect, cursorOverHex);
                    int i;
                    for (i = 0; i < moveIndices.size(); i++)
                    {
                        if (Utils.getMoveDirection(hexSelect, moveIndices.get(i)) == dir)
                        {
                            canvas.drawBitmap(hexBoardHighlightYellowBitmap, boundingBoxes.get(moveIndices.get(i)).left, boundingBoxes.get(moveIndices.get(i)).top, null);
                        }
                    }
                    for (i = 0; i < stopIndices.size(); i++)
                    {
                        if (Utils.getMoveDirection(hexSelect, stopIndices.get(i)) == dir)
                        {
                            canvas.drawBitmap(hexBoardHighlightOrangeBitmap, boundingBoxes.get(stopIndices.get(i)).left, boundingBoxes.get(stopIndices.get(i)).top, null);
                        }
                    }
                }
            }
        }
    }

    /**
     * Draws the objects on the game board.
     *
     * @param canvas - The canvas to draw the board on
     */
    private void drawObjectsOnBoard(Canvas canvas)
    {
        String[] pairs = boardState.split(",");
        for (int i = 0; i < pairs.length; i++)
        {
            Character color = pairs[i].split("-")[0].charAt(0);
            String index = pairs[i].split("-")[1];
            int intIndex = Integer.parseInt(index);
            Bitmap colorHex;
            switch (color)
            {
                case 'R':
                    colorHex = hexRedBitmap;
                    break;
                case 'G':
                    colorHex = hexGreenBitmap;
                    break;
                case 'B':
                    colorHex = hexBlueBitmap;
                    break;
                case 'Y':
                    colorHex = hexYellowBitmap;
                    break;
                case 'O':
                    colorHex = hexOrangeBitmap;
                    break;
                case 'P':
                    colorHex = hexPurpleBitmap;
                    break;
                default:
                    colorHex = hexPurpleBitmap;
                    break;
            }
            if (slideFrame <= 0 || slideStart != intIndex)
            {
                // Draw the piece if it is not currently sliding
                canvas.drawBitmap(colorHex, boundingBoxes.get(intIndex).left + (HEX_WIDTH / 4), boundingBoxes.get(intIndex).top + (HEX_HEIGHT / 4) - (HEX_DEPTH / 2), null);
            }
            else
            {
                // Set collision offset to one tile past the destination
                int collisionOffsetX = 0;
                int collisionOffsetY = 0;
                if (slideDirection == 0)
                {
                    // Up
                    collisionOffsetY = (int) (HEX_HEIGHT * -1.0);
                }
                else if (slideDirection == 1)
                {
                    // Down
                    collisionOffsetY = (int) (HEX_HEIGHT * 1.0);
                }
                else if (slideDirection == 2)
                {
                    // Up-right
                    collisionOffsetX = (int) (HEX_WIDTH * 0.75);
                    collisionOffsetY = (int) (HEX_HEIGHT * -0.5);
                }
                else if (slideDirection == 3)
                {
                    // Up-left
                    collisionOffsetX = (int) (HEX_WIDTH * -0.75);
                    collisionOffsetY = (int) (HEX_HEIGHT * -0.5);
                }
                else if (slideDirection == 4)
                {
                    // Down-right
                    collisionOffsetX = (int) (HEX_WIDTH * 0.75);
                    collisionOffsetY = (int) (HEX_HEIGHT * 0.5);
                }
                else if (slideDirection == 5)
                {
                    // Down-left
                    collisionOffsetX = (int) (HEX_WIDTH * -0.75);
                    collisionOffsetY = (int) (HEX_HEIGHT * 0.5);
                }
                // Reduce the collision offset to less than a full hexagon past the destination
                collisionOffsetX *= 0.5;
                collisionOffsetY *= 0.5;
                int totalTime = 0;
                int startX = 0;
                int startY = 0;
                int endX;
                int endY;
                int tx = 0;
                int ty = 0;
                if (slideFrame < (SLIDE_FRAMES * 0.5))
                {
                    // Ease out of the piece past the destination
                    if (slideFrame + 1 == (SLIDE_FRAMES * 0.5))
                    {
                        SoundManager.play(R.raw.hit);
                    }
                    totalTime = (int) (SLIDE_FRAMES * 0.5);
                    startX = boundingBoxes.get(slideEnd).left + (HEX_WIDTH / 4) + collisionOffsetX;
                    startY = boundingBoxes.get(slideEnd).top + (HEX_HEIGHT / 4) - (HEX_DEPTH / 2) + collisionOffsetY;
                    endX = boundingBoxes.get(slideEnd).left + (HEX_WIDTH / 4);
                    endY = boundingBoxes.get(slideEnd).top + (HEX_HEIGHT / 4) - (HEX_DEPTH / 2);
                    tx = (int) Utils.easeOut(totalTime - slideFrame, startX, endX - startX, totalTime);
                    ty = (int) Utils.easeOut(totalTime - slideFrame, startY, endY - startY, totalTime);
                }
                else
                {
                    // Ease into the piece past the destination
                    totalTime = (int) (SLIDE_FRAMES * 0.5);
                    startX = boundingBoxes.get(slideStart).left + (HEX_WIDTH / 4);
                    startY = boundingBoxes.get(slideStart).top + (HEX_HEIGHT / 4) - (HEX_DEPTH / 2);
                    endX = boundingBoxes.get(slideEnd).left + (HEX_WIDTH / 4) + collisionOffsetX;
                    endY = boundingBoxes.get(slideEnd).top + (HEX_HEIGHT / 4) - (HEX_DEPTH / 2) + collisionOffsetY;
                    tx = (int) Utils.easeIn(SLIDE_FRAMES - slideFrame, startX, endX - startX, totalTime);
                    ty = (int) Utils.easeIn(SLIDE_FRAMES - slideFrame, startY, endY - startY, totalTime);
                }
                canvas.drawBitmap(colorHex, tx, ty, null);
            }
        }
    }

    /**
     * Draws the options menu if it is open.
     *
     * @param canvas - The canvas to draw on
     */
    private void drawOptionsMenu(Canvas canvas)
    {
        // Draw cached game scene with options menu open
        canvas.drawBitmap(cachedGameSceneWithOptionsOpen, 0, 0, null);

        if (generationMaxMoves > 9)
        {
            canvas.drawText(String.valueOf(generationMaxMoves), OPTIONS_PANEL_VALUE_2_DIGIT_X, OPTIONS_PANEL_VALUE_MAX_Y, textPaint);
        }
        else
        {
            canvas.drawText(String.valueOf(generationMaxMoves), OPTIONS_PANEL_VALUE_X, OPTIONS_PANEL_VALUE_MAX_Y, textPaint);
        }
        if (generationMinMoves > 9)
        {
            canvas.drawText(String.valueOf(generationMinMoves), OPTIONS_PANEL_VALUE_2_DIGIT_X, OPTIONS_PANEL_VALUE_MIN_Y, textPaint);
        }
        else
        {
            canvas.drawText(String.valueOf(generationMinMoves), OPTIONS_PANEL_VALUE_X, OPTIONS_PANEL_VALUE_MIN_Y, textPaint);
        }
    }

    /**
     * Returns the background translation offset based on the current level.
     *
     * @return  The background offset
     */
    private int getBackgroundOffset()
    {
        return (int) (((currentLevel + 1) * Menu.LEVELS_SPACING_X_PERCENT * screenWidth) / Menu.BACKGROUND_OFFSET_DAMPENING_MAGNITUDE);
    }

    /**
     * Starts the fade in transition.
     */
    public void startFadeIn()
    {
        fadingIn = true;
        fadingOut = false;
        fadeFrame = MainView.TRANSITION_FRAMES;
    }
}
