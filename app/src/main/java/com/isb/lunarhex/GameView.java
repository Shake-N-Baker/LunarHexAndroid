package com.isb.lunarhex;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * The game class.
 *
 * @author Ian Baker
 */
public class GameView extends SurfaceView implements Runnable
{
    /**
     * Constants
     */
    private static final int FRAME_RATE = 60;
    private static final float HEX_WIDTH_PERCENT = 100f / 640f;
    private static final float HEX_HEIGHT_PERCENT = 80f / 576f;
    private static final float BOARD_X_PERCENT = 10f / 640f;
    private static final float BOARD_Y_PERCENT = 48f / 576f;
    private static final float BUTTONS_X_PERCENT = 440f / 640f;
    private static final float BUTTONS_Y_PERCENT = 100f / 576f;
    private static final float BUTTONS_BUFFER_Y_PERCENT = 50f / 576f;
    private static final float BUTTONS_WIDTH_PERCENT = 150f / 640f;
    private static final float BUTTONS_HEIGHT_PERCENT = 30f / 576f;
    private static int MS_PER_CYCLE;
    private static int HEX_WIDTH;
    private static int HEX_HEIGHT;
    private static int HEX_DEPTH;
    private static int BOARD_X;
    private static int BOARD_Y;
    private static int SCREEN_WIDTH;
    private static int SCREEN_HEIGHT;
    private static int BUTTONS_X;
    private static int BUTTONS_Y;
    private static int BUTTONS_BUFFER_Y;
    private static int BUTTONS_WIDTH;
    private static int BUTTONS_HEIGHT;

    /**
     * The number of frames a slide (move) will take to finish
     */
    private static int SLIDE_FRAMES = 20;

    /**
     * The start time in milliseconds of this cycle
     */
    private volatile long startTime;

    /**
     * Whether the game is running or not
     */
    public volatile boolean running = false;

    /**
     * The context
     */
    public Context context;

    /**
     * The surface holder which contains the canvas
     */
    public SurfaceHolder holder;

    /**
     * The thread to run rendering in
     */
    public Thread renderThread = null;

    /**
     * The background image
     */
    private static Bitmap background;

    /**
     * The cursor position
     */
    private float cursorX = 0, cursorY = 0;

    /**
     * The cursor down position
     */
    private float cursorDownX = 0, cursorDownY = 0;

    /// TODO: Remove debug FPS tracking variable
    /**
     * Float measurement of the frames per second
     */
    private float framesPerSecond = 0f;
    private List<Integer> totalTimes = new ArrayList<Integer>();

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
     * The paint used for buttons
     */
    private Paint buttonPaint;

    /**
     * The generate new textbox
     */
    private TextBox textboxGenerateNew;

    /**
     * The reset textbox
     */
    private TextBox textboxReset;

    /**
     * The moves textbox
     */
    private TextBox textboxMoves;

    /**
     * The hint textbox
     */
    private TextBox textboxHint;

    /**
     * The max moves textbox
     */
    private TextBox textboxMaxMoves;

    /**
     * The max moves minus textbox
     */
    private TextBox textboxMaxMovesMinus;

    /**
     * The max moves plus textbox
     */
    private TextBox textboxMaxMovesPlus;

    /**
     * The min moves textbox
     */
    private TextBox textboxMinMoves;

    /**
     * The min moves minus textbox
     */
    private TextBox textboxMinMovesMinus;

    /**
     * The min moves plus textbox
     */
    private TextBox textboxMinMovesPlus;

    /**
     * The exit textbox
     */
    private TextBox textboxExit;

    /**
     * List of textboxes
     */
    private List<TextBox> textBoxes;

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
     * The minimum number of moves (shortest path) a newly generated board can take
     */
    private int minMoves = 1;

    /**
     * The maximum number of moves (shortest path) a newly generated board can take
     */
    private int maxMoves = 8;

    /**
     * The current level that the player is on (zero based) or -1 if random
     */
    private int currentLevel = -1;

    /**
     * The current number of moves the player has taken since the initial board state
     */
    private int currentMove = 0;

    /**
     * The indices along the paths of the currently selected piece
     */
    private List<Integer> moveIndices;

    /**
     * The indices at the end of the paths of the currently selected piece
     */
    private List<Integer> stopIndices;

    /**
     * Constructor for the game canvas.
     *
     * @param context - The context
     */
    public GameView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        MS_PER_CYCLE = 1000 / FRAME_RATE;
        holder = getHolder();

        // Set the screen resolution
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        SCREEN_HEIGHT = dm.heightPixels;
        SCREEN_WIDTH = dm.widthPixels;
        HEX_WIDTH = Math.round(HEX_WIDTH_PERCENT * SCREEN_WIDTH);
        HEX_HEIGHT = Math.round(HEX_HEIGHT_PERCENT * SCREEN_HEIGHT);
        HEX_DEPTH = Math.round(HEX_HEIGHT / 10.0f);
        BOARD_X = Math.round(BOARD_X_PERCENT * SCREEN_WIDTH);
        BOARD_Y = Math.round(BOARD_Y_PERCENT * SCREEN_HEIGHT);
        BUTTONS_X = Math.round(BUTTONS_X_PERCENT * SCREEN_WIDTH);
        BUTTONS_Y = Math.round(BUTTONS_Y_PERCENT * SCREEN_HEIGHT);
        BUTTONS_BUFFER_Y = Math.round(BUTTONS_BUFFER_Y_PERCENT * SCREEN_HEIGHT);
        BUTTONS_WIDTH = Math.round(BUTTONS_WIDTH_PERCENT * SCREEN_WIDTH);
        BUTTONS_HEIGHT = Math.round(BUTTONS_HEIGHT_PERCENT * SCREEN_HEIGHT);

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
        textPaint.setTextSize(25);
        buttonPaint = new Paint();
        buttonPaint.setStyle(Paint.Style.FILL);
        buttonPaint.setColor(Color.WHITE);
        buttonPaint.setStrokeWidth(0);

        // Setup the text boxes
        textBoxes = new ArrayList<TextBox>();
        textboxReset = new TextBox("Reset", true, BUTTONS_X, BUTTONS_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT, 0, 0);
        textBoxes.add(textboxReset);
        textboxMoves = new TextBox("Moves: XX", true, BUTTONS_X, BUTTONS_Y + BUTTONS_BUFFER_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT, 0, 0);
        textBoxes.add(textboxMoves);
        textboxHint = new TextBox("Step Hint", true, BUTTONS_X, BUTTONS_Y + (2 * BUTTONS_BUFFER_Y), BUTTONS_WIDTH, BUTTONS_HEIGHT, 0, 0);
        textBoxes.add(textboxHint);
        textboxGenerateNew = new TextBox("Generate New", true, BUTTONS_X, BUTTONS_Y + (3 * BUTTONS_BUFFER_Y), BUTTONS_WIDTH, BUTTONS_HEIGHT, 0, 0);
        textBoxes.add(textboxGenerateNew);
        textboxMaxMoves = new TextBox("Max Moves: XX", true, BUTTONS_X, BUTTONS_Y + (4 * BUTTONS_BUFFER_Y), BUTTONS_WIDTH, BUTTONS_HEIGHT, 0, 0);
        textBoxes.add(textboxMaxMoves);
        textboxMinMoves = new TextBox("Min Moves: XX", true, BUTTONS_X, BUTTONS_Y + (5 * BUTTONS_BUFFER_Y), BUTTONS_WIDTH, BUTTONS_HEIGHT, 0, 0);
        textBoxes.add(textboxMinMoves);
        textboxExit = new TextBox("Exit", true, BUTTONS_X, BUTTONS_Y + (7 * BUTTONS_BUFFER_Y), BUTTONS_WIDTH, BUTTONS_HEIGHT, 0, 0);
        textBoxes.add(textboxExit);

        // Construct checking hex to compare taps to when determining which hexagon is selected
        hexCheck = Bitmap.createBitmap(HEX_WIDTH, HEX_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas temp = new Canvas(hexCheck);
        Utils.drawHex(temp, 0, 0, HEX_WIDTH, HEX_HEIGHT, 0xFF0000, 0, false);

        loadBoardSets(context);
    }

    /**
     * Initializes or re-initializes the board with the given state.
     * Called on start up and when brought from background.
     *
     * @param   state - The state of the game
     */
    public void initialize(Bundle state)
    {
        // Reload the state, re-setup all variables involved in tracking the state
        if (state != null)
        {
            // Load the old state variables
            boardState = state.getString("boardState");
            initialBoardState = state.getString("initialBoardState");
            solution = state.getStringArrayList("solution");

            // Generate a background to use
            generateBackground();
        }

        // If no board state then start up new game state
        if (boardState.equals(""))
        {
            if (currentLevel != -1)
            {
                /// TODO: Level logic
            }
            else
            {
                randomBoardState(minMoves, maxMoves);
            }
        }
    }

    /**
     * Load and setup the sets of boards.
     *
     * @param context - The context
     */
    private void loadBoardSets(Context context)
    {
        mainBoardSet = new ArrayList<String>();
        boardSet = new ArrayList<List<String>>();
        for (int i = 0; i < 20; i++)
        {
            List<String> moveSet = new ArrayList<String>();
            boardSet.add(moveSet);
        }

        // Fetch the boards from the raw resources and store them
        try
        {
            BufferedReader inputMainBoards = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.boards_main)));
            BufferedReader inputSmallSetBoards = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.boards_small)));
            try
            {
                String mainBoardString = inputMainBoards.readLine();
                String[] mainBoards = mainBoardString.split(",");
                for (int i = 0; i < mainBoards.length; i++)
                {
                    mainBoardSet.add(mainBoards[i]);
                }
                String smallBoardString = inputSmallSetBoards.readLine();
                String[] smallBoards = smallBoardString.split(",");
                for (int i = 0; i < smallBoards.length; i++)
                {
                    if (mainBoardSet.indexOf(smallBoards[i]) == -1)
                    {
                        int solveMoves = Integer.parseInt(String.valueOf(smallBoards[i].charAt(0)), 36);
                        boardSet.get(solveMoves - 1).add(smallBoards[i]);
                    }
                }
            }
            finally
            {
                inputMainBoards.close();
            }
        }
        catch (FileNotFoundException e)
        {
            Log.e("LunarHex", "Error loading file: " + e);
        }
        catch (IOException e)
        {
            Log.e("LunarHex", "Error reading file: " + e);
        }
    }

    /**
     * Called to resume the game.
     */
    public void resume()
    {
        running = true;
        startTime = System.currentTimeMillis();
        renderThread = new Thread(this);
        renderThread.start();
    }

    /**
     * Starts executing the active part of the class' code. This method is called when
     * a thread is started that has been created with a class which implements Runnable.
     */
    @Override
    public void run()
    {
        while(running)
        {
            if(!holder.getSurface().isValid())
            {
                // Skip this while loop iteration and wait until it becomes valid
                continue;
            }

            // Sleep extra time to produce the desired frame rate
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            if (totalTime < MS_PER_CYCLE)
            {
                try
                {
                    Thread.sleep(MS_PER_CYCLE - totalTime);
                }
                catch(InterruptedException e)
                {
                    Log.e("LunarHex", "Thread sleep exception: " + e);
                }
            }
            startTime = System.currentTimeMillis();

            /// TODO: Remove debug set frames per second
            framesPerSecond = 1000 / totalTime;
            totalTimes.add((int) totalTime);
            if (totalTimes.size() > 60)
            {
                totalTimes.remove(0);
                int sum = 0;
                for (int i = 0; i < totalTimes.size(); i++)
                {
                    sum += totalTimes.get(i);
                }
                framesPerSecond = (60 * 1000) / sum;
            }

            cycle();
        }
    }

    /**
     * Called to pause the game.
     */
    public void pause()
    {
        running = false;
        boolean retry = true;
        while(retry)
        {
            try
            {
                renderThread.join();
                retry = false;
            }
            catch (InterruptedException e)
            {
                // Retry
            }
        }
    }

    /**
     * Handles the frame based game logic.
     */
    private void cycle()
    {
        // Update
        processSlide();

        // Draw
        Canvas canvas = holder.lockCanvas();
        drawBoard(canvas);
        drawHighlight(canvas);
        drawObjectsOnBoard(canvas);

        /// TODO: Remove draw debug down cursor
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        canvas.drawCircle(cursorDownX, cursorDownY, 5, paint);
        /// TODO: Remove draw debug cursor
        paint.setColor(Color.GREEN);
        canvas.drawCircle(cursorX, cursorY, 5, paint);
        /// TODO: Remove draw debug frames per second
        canvas.drawText(String.valueOf(framesPerSecond), 10, 40, textPaint);

        holder.unlockCanvasAndPost(canvas);
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
                // Hide the instructions if a move is complete on level 1
//                instructionsTextfield.visible = (currentLevel == 0 && currentMove == 0);
                boardState = slideToBoard;
//                youWinTextfield.visible = Utils.boardSolved(boardState);
//                if (Utils.boardSolved(boardState) && currentLevel != -1)
//                {
//                    // Cleared the board, player wins
//                    PlayerData.setSolveMoves(currentLevel, currentMove);
//                    textboxes[11].isAButton = (currentLevel != 29 && PlayerData.solveMoves[currentLevel] != -1);
//                    textboxes[14].textfield.text = "Your Clear: " + PlayerData.solveMoves[currentLevel];
//                    // Show best/your clear
//                    textboxes[13].visible = true;
//                    textboxes[14].visible = true;
//                    if (PlayerData.solveMoves[currentLevel] == (solution.length - 1))
//                    {
//                        // Show step hint
//                        textboxes[3].visible = true;
//                        PlayerData.setLevelState(currentLevel, 1);
//                    }
//                }
            }
        }
    }

    /**
     * Handles the game logic for touch events.
     *
     * @param   motionEvent - The touch motion event
     */
    public void touchHandle(MotionEvent motionEvent)
    {
        // Update the cursor
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        cursorX = x;
        cursorY = y;
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            cursorDownX = x;
            cursorDownY = y;
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
                // Check for buttons pressed
                // ...
                if (textboxGenerateNew.isToggled((int) cursorDownX, (int) cursorDownY, (int) cursorX, (int) cursorY)) // Generate New Board
                {
//                    SoundManager.play(SoundManager.BUTTON);
                    randomBoardState(minMoves, maxMoves);
                }
                else if (textboxReset.isToggled((int) cursorDownX, (int) cursorDownY, (int) cursorX, (int) cursorY)) // Reset
                {
//                    SoundManager.play(SoundManager.BUTTON);
                    boardState = initialBoardState;
                    currentMove = 0;
                    hexSelect = -1;
                    moveIndices.clear();
                    stopIndices.clear();
                }
                else if (textboxHint.isToggled((int) cursorDownX, (int) cursorDownY, (int) cursorX, (int) cursorY)) // Step Hint
                {
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
//                else if (textboxes[5].isClicked(click_point)) // Maximum moves minus
//                {
//                    maxMoves--;
//                    if (maxMoves < minMoves) maxMoves = minMoves;
//                    textboxes[4].textfield.text = "Max Moves: " + maxMoves;
//                }
//                else if (textboxes[6].isClicked(click_point)) // Maximum moves plus
//                {
//                    maxMoves++;
//                    if (maxMoves > 20) maxMoves = 20;
//                    textboxes[4].textfield.text = "Max Moves: " + maxMoves;
//                }
//                else if (textboxes[8].isClicked(click_point)) // Minimum moves minus
//                {
//                    minMoves--;
//                    if (minMoves < 1) minMoves = 1;
//                    textboxes[7].textfield.text = "Min Moves: " + minMoves;
//                }
//                else if (textboxes[9].isClicked(click_point)) // Minimum moves plus
//                {
//                    minMoves++;
//                    if (minMoves > maxMoves) minMoves = maxMoves;
//                    textboxes[7].textfield.text = "Min Moves: " + minMoves;
//                }
//                else if (textboxes[10].isClicked(click_point)) // Exit game
//                {
//                    SoundManager.play(SoundManager.BUTTON);
//                    dispatchEvent(new CustomEvent(CustomEvent.EXIT));
//                }
//                else if (textboxes[11].isClicked(click_point)) // Next level
//                {
//                    SoundManager.play(SoundManager.BUTTON);
//                    if (currentLevel < 29) setBoardState(currentLevel + 1);
//                }
//                else if (textboxes[12].isClicked(click_point)) // Back level
//                {
//                    SoundManager.play(SoundManager.BUTTON);
//                    if (currentLevel > 0) setBoardState(currentLevel - 1);
//                }
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
        generateBackground();
    }

    /**
     * Parses the compressed format board to set the solution for the board.
     *
     * @param	compressedBoard - The board in compressed format
     */
    private void parseSolution(String compressedBoard)
    {
        int moves = Integer.parseInt(String.valueOf(compressedBoard.charAt(0)), 36);
        /// TODO: Setup text here for the Shortest moves to clear textfield(s)
//        textboxes[2].textfield.text = "Moves: " + moves.toString();
//        textboxes[13].textfield.text = "Best Clear: " + moves.toString();
        List<Integer> encodedMoves = new ArrayList<Integer>();
        int i;
        for (i = 1; i <= moves; i++)
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
            if (boundingBoxes.get(i).contains((int) cursorX, (int) cursorY))
            {
                if (Integer.toHexString(hexCheck.getPixel((int) cursorX - boundingBoxes.get(i).left, (int) cursorY - boundingBoxes.get(i).top)).equals("ffff0000"))
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
        // Clear board
        canvas.drawARGB(0xff, 0x00, 0x00, 0x00);
        canvas.drawBitmap(background, 0, 0, null);

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
        // Draw buttons and texts
        for (int i = 0; i < textBoxes.size(); i++)
        {
            if (textBoxes.get(i).isVisible) textBoxes.get(i).draw(canvas, textPaint, buttonPaint);
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
                // Reduce the collision offset to less than a full hexagon past the desination
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
     * Generates a new background.
     */
    private void generateBackground()
    {
        if (background == null)
        {
            background = Bitmap.createBitmap(SCREEN_WIDTH, SCREEN_HEIGHT, Bitmap.Config.ARGB_8888);
        }
        Utils.generateBackground(background, SCREEN_WIDTH, SCREEN_HEIGHT);
    }
}