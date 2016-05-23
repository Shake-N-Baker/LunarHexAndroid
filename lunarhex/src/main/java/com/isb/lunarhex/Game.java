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
    private static final float HEX_WIDTH_PERCENT = 17f / 100f;
    private static final float HEX_HEIGHT_PERCENT = 14f / 100f;
    private static final float BOARD_X_PERCENT = 16f / 100f;
    private static final float BOARD_Y_PERCENT = 8f / 100f;
    private static final float EXIT_X_PERCENT = 8f / 100f;
    private static final float EXIT_Y_PERCENT = 13f / 100f;
    private static final float GENERATE_X_PERCENT = 92f / 100f;
    private static final float GENERATE_Y_PERCENT = 13f / 100f;
    private static final float RETRY_X_PERCENT = 92f / 100f;
    private static final float RETRY_Y_PERCENT = 39f / 100f;
    private static final float HINT_X_PERCENT = 92f / 100f;
    private static final float HINT_Y_PERCENT = 65f / 100f;
    private static final float MOVES_PLUS_X_PERCENT = 92f / 100f;
    private static final float MOVES_PLUS_Y_PERCENT = 13f / 100f;
    private static final float MOVES_MINUS_X_PERCENT = 92f / 100f;
    private static final float MOVES_MINUS_Y_PERCENT = 91f / 100f;
    private static int HEX_WIDTH;
    private static int HEX_HEIGHT;
    private static int HEX_DEPTH;
    private static int BOARD_X;
    private static int BOARD_Y;
    private static int BUTTON_RADIUS;
    private static int EXIT_X;
    private static int EXIT_Y;
    private static int GENERATE_X;
    private static int GENERATE_Y;
    private static int RETRY_X;
    private static int RETRY_Y;
    private static int HINT_X;
    private static int HINT_Y;
    private static int MOVES_PLUS_X;
    private static int MOVES_PLUS_Y;
    private static int MOVES_MINUS_X;
    private static int MOVES_MINUS_Y;

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
     * The background image
     */
    private static Bitmap background;

    /**
     * Boolean for ignoring the current touch event, used when a slide animation occurs to ignore
     * all events until the next touch down event.
     */
    private boolean ignoreTouch = false;

    /**
     * The paint used for text
     */
    private Paint textPaint;

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
     * The number of moves (shortest path) a newly generated board will take
     */
    private int generationMoves = 4;

    /**
     * The current level that the player is on (zero based) or -1 if random
     */
    public int currentLevel = -1;

    /**
     * The current number of moves the player has taken since the initial board state
     */
    /// TODO: Update current move
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
     * The indices along the paths of the currently selected piece
     */
    private List<Integer> moveIndices;

    /**
     * The indices at the end of the paths of the currently selected piece
     */
    private List<Integer> stopIndices;

    /**
     * The icon bitmap image to use for drawing the icons
     */
    private static Bitmap iconBitmap;

    /**
     * Constructor for the game.
     *
     * @param   main - The reference to the main view
     * @param   screenWidth - The screen width
     * @param   screenHeight - The screen height
     * @param   background - The background image to use
     * @param   mainBoardSet - The set of main boards
     * @param   boardSet - The set of random boards
     * @param   state - The bundle state of the game
     */
    public Game(MainView main, int screenWidth, int screenHeight, Bitmap background, List<String> mainBoardSet, List<List<String>> boardSet, Bundle state)
    {
        this.mainView = main;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.background = background;
        this.mainBoardSet = mainBoardSet;
        this.boardSet = boardSet;
        HEX_WIDTH = Math.round(HEX_WIDTH_PERCENT * screenWidth);
        HEX_HEIGHT = Math.round(HEX_HEIGHT_PERCENT * screenHeight);
        HEX_DEPTH = Math.round(HEX_HEIGHT / 10.0f);
        BOARD_X = Math.round(BOARD_X_PERCENT * screenWidth);
        BOARD_Y = Math.round(BOARD_Y_PERCENT * screenHeight);
        BUTTON_RADIUS = (int) (Utils.distanceBetweenPoints(0, 0, screenWidth, screenHeight) / 15);
        EXIT_X = Math.round(EXIT_X_PERCENT * screenWidth);
        EXIT_Y = Math.round(EXIT_Y_PERCENT * screenHeight);
        RETRY_X = Math.round(RETRY_X_PERCENT * screenWidth);
        RETRY_Y = Math.round(RETRY_Y_PERCENT * screenHeight);
        GENERATE_X = Math.round(GENERATE_X_PERCENT * screenWidth);
        GENERATE_Y = Math.round(GENERATE_Y_PERCENT * screenHeight);
        HINT_X = Math.round(HINT_X_PERCENT * screenWidth);
        HINT_Y = Math.round(HINT_Y_PERCENT * screenHeight);
        MOVES_PLUS_X = Math.round(MOVES_PLUS_X_PERCENT * screenWidth);
        MOVES_PLUS_Y = Math.round(MOVES_PLUS_Y_PERCENT * screenHeight);
        MOVES_MINUS_X = Math.round(MOVES_MINUS_X_PERCENT * screenWidth);
        MOVES_MINUS_Y = Math.round(MOVES_MINUS_Y_PERCENT * screenHeight);

        boundingBoxes = Utils.getBoundingBoxes(HEX_WIDTH, HEX_HEIGHT, BOARD_X, BOARD_Y);

        solution = new ArrayList<String>();
        slideFrame = 0;
        slideStart = -1;
        slideEnd = -1;
        slideDirection = -1;
        moving = false;

        moveIndices = new ArrayList<Integer>();
        stopIndices = new ArrayList<Integer>();

        // Setup the paint for text boxes
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(mainView.FONT_SIZE_20_SP);
        textPaint.setTypeface(mainView.LATO_FONT);
        debugPaint = new Paint();
        debugPaint.setColor(Color.GREEN);
        debugPaint.setTextSize(mainView.FONT_SIZE_20_SP);
        debugPaint.setTypeface(mainView.LATO_FONT);

        // Construct checking hex to compare taps to when determining which hexagon is selected
        hexCheck = Bitmap.createBitmap(HEX_WIDTH, HEX_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas temp = new Canvas(hexCheck);
        Utils.drawHex(temp, 0, 0, HEX_WIDTH, HEX_HEIGHT, 0xFF0000, 0, false);

        // Generate the icon bitmap to draw all the icons at once
        if (iconBitmap == null)
        {
            iconBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        }

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
            }
        }

        // Update UI for levels vs random
        updateUIState();
    }

    /**
     * Updates the user interface state to show and hide the correct buttons.
     */
    private void updateUIState()
    {
        playerWon = Utils.boardSolved(boardState);
        if (currentLevel != -1)
        {
            /// TODO: Add instructions
            /// Lv 1 - Slide the red piece to the middle to win
            /// Lv 2 - Pieces may only slide into other pieces

            // Draw only level icons
            iconBitmap.eraseColor(Color.argb(0, 0, 0, 0));
            Utils.drawIconHome(new Canvas(iconBitmap), EXIT_X, EXIT_Y, BUTTON_RADIUS);
            Utils.drawIconHome(new Canvas(iconBitmap), RETRY_X, RETRY_Y, BUTTON_RADIUS);
            Utils.drawIconHome(new Canvas(iconBitmap), HINT_X, HINT_Y, BUTTON_RADIUS);
        }
        else
        {
            // Draw only non-level icons
            iconBitmap.eraseColor(Color.argb(0, 0, 0, 0));
            Utils.drawIconHome(new Canvas(iconBitmap), EXIT_X, EXIT_Y, BUTTON_RADIUS);
            Utils.drawIconHome(new Canvas(iconBitmap), RETRY_X, RETRY_Y, BUTTON_RADIUS);
            Utils.drawIconHome(new Canvas(iconBitmap), GENERATE_X, GENERATE_Y, BUTTON_RADIUS);
            Utils.drawIconHome(new Canvas(iconBitmap), HINT_X, HINT_Y, BUTTON_RADIUS);
            Utils.drawIconHome(new Canvas(iconBitmap), MOVES_PLUS_X, MOVES_PLUS_Y, BUTTON_RADIUS);
            Utils.drawIconHome(new Canvas(iconBitmap), MOVES_MINUS_X, MOVES_MINUS_Y, BUTTON_RADIUS);
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
            randomBoardState(generationMoves, generationMoves);
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
        processSlide();

        // Draw
        drawBoard(canvas);
        drawHighlight(canvas);
        drawObjectsOnBoard(canvas);

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
                if (foundHex != -1)
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
                else // Selecting outside of the board, clear selection
                {
                    hexSelect = -1;
                    moveIndices.clear();
                    stopIndices.clear();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                /// TODO: Slide response
                break;
            case MotionEvent.ACTION_UP:
                if ((Utils.distanceBetweenPoints(Touch.x, Touch.y, GENERATE_X, GENERATE_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, GENERATE_X, GENERATE_Y) < BUTTON_RADIUS)) // Generate New Board
                {
//                    SoundManager.play(SoundManager.BUTTON);
                    newBoardState();
                }
                else if ((Utils.distanceBetweenPoints(Touch.x, Touch.y, RETRY_X, RETRY_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, RETRY_X, RETRY_Y) < BUTTON_RADIUS)) // Reset
                {
//                    SoundManager.play(SoundManager.BUTTON);
                    boardState = initialBoardState;
                    currentMove = 0;
                    hexSelect = -1;
                    moveIndices.clear();
                    stopIndices.clear();
                }
                else if ((Utils.distanceBetweenPoints(Touch.x, Touch.y, HINT_X, HINT_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, HINT_X, HINT_Y) < BUTTON_RADIUS)) // Step Hint
                {
//                    SoundManager.play(SoundManager.BUTTON);
                    int solutionIndex = solution.indexOf(boardState);
                    if (solutionIndex == -1) {
                        boardState = solution.get(0);
                    } else if (solutionIndex != solution.size() - 1) {
                        List<Integer> move_index = Utils.getMoveIndices(boardState, solution.get(solutionIndex + 1));
                        attemptMove(move_index.get(0), move_index.get(1));
                    }
                    hexSelect = -1;
                    moveIndices.clear();
                    stopIndices.clear();
                }
                else if ((Utils.distanceBetweenPoints(Touch.x, Touch.y, MOVES_MINUS_X, MOVES_MINUS_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, MOVES_MINUS_X, MOVES_MINUS_Y) < BUTTON_RADIUS)) // Moves minus
                {
//                    SoundManager.play(SoundManager.BUTTON);
                    generationMoves--;
                    if (generationMoves < 1) generationMoves = 1;
//                    if (generationMoves > 1)
//                    {
//                        textMovesRangeMax.setText("at most " + String.valueOf(generationMoves) + " moves");
//                    }
//                    else
//                    {
//                        textMovesRangeMax.setText("at most " + String.valueOf(generationMoves) + " move");
//                    }
                }
                else if ((Utils.distanceBetweenPoints(Touch.x, Touch.y, MOVES_PLUS_X, MOVES_PLUS_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, MOVES_PLUS_X, MOVES_PLUS_Y) < BUTTON_RADIUS)) // Moves plus
                {
//                    SoundManager.play(SoundManager.BUTTON);
                    generationMoves++;
                    if (generationMoves > 20) generationMoves = 20;
//                    textMovesRangeMax.setText("at most " + String.valueOf(generationMoves) + " moves");
                }
                else if ((Utils.distanceBetweenPoints(Touch.x, Touch.y, EXIT_X, EXIT_Y) < BUTTON_RADIUS) && (Utils.distanceBetweenPoints(Touch.downX, Touch.downY, EXIT_X, EXIT_Y) < BUTTON_RADIUS)) // Exit game
                {
//                    SoundManager.play(SoundManager.BUTTON);
                    mainView.handleEvent(new CustomEvent(CustomEvent.EXIT_GAME));
                }
                else if (foundHex != -1) // Attempt to move selected hexagon to hexagon at the release point of the touch
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
//                    SoundManager.play(SoundManager.SLIDE);
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
        /// TODO: Setup text here for the Shortest moves to clear textfield(s)
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
     * Draws the game board.
     *
     * @param canvas - The canvas to draw the board on
     */
    private void drawBoard(Canvas canvas)
    {
        int defaultMatrix = canvas.save();

        // Clear board
        canvas.drawARGB(0xff, 0x00, 0x00, 0x00);

        // Draw the background shifted based on the current level
        canvas.translate(-1 * getBackgroundOffset(), 0);
        canvas.drawBitmap(background, 0, 0, null);

        // Remove the translation shift before drawing the rest of the game
        canvas.restoreToCount(defaultMatrix);
        defaultMatrix = canvas.save();

        // Draw the hexagon tiles from back to front
        float width = HEX_WIDTH;
        // Squish the hexagons into each other slightly to prevent small visual oddities along edges
        float placementWidth = width - 1;
        float height = HEX_HEIGHT;
        float startX = BOARD_X;
        float startY = BOARD_Y;
        float x = startX + (width * 0.75f);
        float y = startY;
        int depth = HEX_DEPTH;
        int index = 1;
        for (int y_index = 0; y_index < 11; y_index++)
        {
            for (int x_index = 0; x_index < 3; x_index++)
            {
                if ((y_index % 2 == 0) && x_index == 2) break;
                if (hexSelect == index) Utils.drawHex(canvas, x, y, width, height, 0xFFCC00, depth, true);
                else if (stopIndices.indexOf(index) != -1) Utils.drawHex(canvas, x, y, width, height, 0x707070, depth, true);
                else if (moveIndices.indexOf(index) != -1) Utils.drawHex(canvas, x, y, width, height, 0xD8D8D8, depth, true);
                else if (index == 12) Utils.drawHex(canvas, x, y, width, height, 0xFF0000, depth, true);
                else Utils.drawHex(canvas, x, y, width, height, 0xFFFFFF, depth, true);
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
        canvas.drawBitmap(iconBitmap, 0, 0, null);
        if (playerWon)
        {
            canvas.drawText("Cleared!", (28 * screenWidth) / 100, screenHeight / 10, debugPaint);
        }
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
                            Utils.drawHex(canvas, boundingBoxes.get(moveIndices.get(i)).left, boundingBoxes.get(moveIndices.get(i)).top, HEX_WIDTH, HEX_HEIGHT, 0xFFCC00, 0, true);
                        }
                    }
                    for (i = 0; i < stopIndices.size(); i++)
                    {
                        if (Utils.getMoveDirection(hexSelect, stopIndices.get(i)) == dir)
                        {
                            Utils.drawHex(canvas, boundingBoxes.get(stopIndices.get(i)).left, boundingBoxes.get(stopIndices.get(i)).top, HEX_WIDTH, HEX_HEIGHT, 0xDD7700, 0, true);
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
            if (slideFrame <= 0 || slideStart != intIndex)
            {
                // Draw the piece if it is not currently sliding
                Utils.drawHex(canvas, boundingBoxes.get(intIndex).left + (HEX_WIDTH / 4), boundingBoxes.get(intIndex).top + (HEX_HEIGHT / 4) - (HEX_DEPTH / 2), HEX_WIDTH / 2, HEX_HEIGHT / 2, colorValue, HEX_DEPTH / 2, true);
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
//                        SoundManager.play(SoundManager.HIT);
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
                Utils.drawHex(canvas, tx, ty, HEX_WIDTH / 2, HEX_HEIGHT / 2, colorValue, HEX_DEPTH / 2, true);
            }
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

    }
}
