package com.isb.lunarhex;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;

import com.isb.lunarhex.ui.BackgroundPanel;
import com.isb.lunarhex.ui.RoundedButton;
import com.isb.lunarhex.ui.Text;

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
    private static final float HEX_WIDTH_PERCENT = 100f / 640f;
    private static final float HEX_HEIGHT_PERCENT = 80f / 576f;
    private static final float BOARD_X_PERCENT = 10f / 640f;
    private static final float BOARD_Y_PERCENT = 48f / 576f;
    private static int HEX_WIDTH;
    private static int HEX_HEIGHT;
    private static int HEX_DEPTH;
    private static int BOARD_X;
    private static int BOARD_Y;

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
     * The paint used for buttons
     */
    private Paint buttonPaint;

    /**
     * The background panel for the user interface to appear on
     */
    private BackgroundPanel backgroundPanel;

    /**
     * The level text
     */
    private Text levelText;

    /**
     * The generate new button
     */
    private RoundedButton buttonGenerateNew;

    /**
     * The reset button
     */
    private RoundedButton buttonReset;

    /**
     * The text for how many moves a board is solveable in
     */
    private Text textMoves;

    /**
     * The hint button
     */
    private RoundedButton buttonHint;

    /**
     * The static text describing new boards solvable moves range
     */
    private Text textBoardsSolvable;

    /**
     * The max moves minus button
     */
    private RoundedButton buttonMaxMovesMinus;

    /**
     * The max moves plus button
     */
    private RoundedButton buttonMaxMovesPlus;

    /**
     * The text showing maximum moves solvable to generate new boards
     */
    private Text textMovesRangeMax;

    /**
     * The text showing minimum moves solvable to generate new boards
     */
    private Text textMovesRangeMin;

    /**
     * The text showing the range from minimum to maximum
     */
    private Text textMovesRangeTo;

    /**
     * The min moves minus button
     */
    private RoundedButton buttonMinMovesMinus;

    /**
     * The min moves plus button
     */
    private RoundedButton buttonMinMovesPlus;

    /**
     * The exit button
     */
    private RoundedButton buttonExit;

    /**
     * The button to mute and un-mute music
     */
    private RoundedButton buttonToggleMusic;

    /**
     * The button to mute and un-mute the sound
     */
    private RoundedButton buttonToggleSound;

    /**
     * List of buttons
     */
    private List<RoundedButton> roundedButtons;

    /**
     * List of texts
     */
    private List<Text> texts;

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
     * The minimum number of moves (shortest path) a newly generated board can take
     */
    private int minMoves = 2;

    /**
     * The maximum number of moves (shortest path) a newly generated board can take
     */
    private int maxMoves = 8;

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
     * Constructor for the game.
     *
     * @param   main - The reference to the main view
     * @param   screenWidth - The screen width
     * @param   screenHeight - The screen height
     * @param   mainBoardSet - The set of main boards
     * @param   boardSet - The set of random boards
     */
    public Game(MainView main, int screenWidth, int screenHeight, List<String> mainBoardSet, List<List<String>> boardSet)
    {
        this.mainView = main;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.mainBoardSet = mainBoardSet;
        this.boardSet = boardSet;
        HEX_WIDTH = Math.round(HEX_WIDTH_PERCENT * screenWidth);
        HEX_HEIGHT = Math.round(HEX_HEIGHT_PERCENT * screenHeight);
        HEX_DEPTH = Math.round(HEX_HEIGHT / 10.0f);
        BOARD_X = Math.round(BOARD_X_PERCENT * screenWidth);
        BOARD_Y = Math.round(BOARD_Y_PERCENT * screenHeight);

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
        textPaint.setTextSize(mainView.FONT_SIZE_15_SP);
        textPaint.setTypeface(mainView.RALEWAY_BOLD_FONT);
        debugPaint = new Paint();
        debugPaint.setColor(Color.GREEN);
        debugPaint.setTextSize(mainView.FONT_SIZE_15_SP);
        debugPaint.setTypeface(mainView.RALEWAY_BOLD_FONT);
        buttonPaint = new Paint();
        buttonPaint.setStyle(Paint.Style.FILL);
        buttonPaint.setColor(0xFF50D040);
        buttonPaint.setStrokeWidth(0);

        backgroundPanel = new BackgroundPanel(Math.round(screenWidth * 0.65f), Math.round(screenHeight * 0.05f), Math.round(screenWidth * 0.33f), Math.round(screenHeight * 0.9f));

        // Setup the text boxes
        roundedButtons = new ArrayList<RoundedButton>();
        texts = new ArrayList<Text>();
        levelText = new Text("LEVEL: XX", Math.round(screenWidth * 0.68f), Math.round(screenHeight * 0.08f), Math.round(screenWidth * 0.27f), Math.round(screenHeight * 0.08f));
        texts.add(levelText);
        buttonReset = new RoundedButton("Reset", Math.round(screenWidth * 0.69f), Math.round(screenHeight * 0.68f), Math.round(screenWidth * 0.11f), Math.round(screenHeight * 0.11f), Math.round(screenHeight * 0.004f));
        roundedButtons.add(buttonReset);
        textMoves = new Text("Moves: XX", Math.round(screenWidth * 0.68f), Math.round(screenHeight * 0.50f), Math.round(screenWidth * 0.27f), Math.round(screenHeight * 0.06f));
        texts.add(textMoves);
        buttonHint = new RoundedButton("Step Hint", Math.round(screenWidth * 0.76f), Math.round(screenHeight * 0.56f), Math.round(screenWidth * 0.11f), Math.round(screenHeight * 0.11f), Math.round(screenHeight * 0.004f));
        roundedButtons.add(buttonHint);
        buttonGenerateNew = new RoundedButton("Generate New", Math.round(screenWidth * 0.68f), Math.round(screenHeight * 0.08f), Math.round(screenWidth * 0.27f), Math.round(screenHeight * 0.08f), Math.round(screenHeight * 0.004f));
        roundedButtons.add(buttonGenerateNew);
        textBoardsSolvable = new Text("Boards solvable in:", Math.round(screenWidth * 0.66f), Math.round(screenHeight * 0.165f), Math.round(screenWidth * 0.27f), Math.round(screenHeight * 0.06f));
        texts.add(textBoardsSolvable);
        buttonMaxMovesMinus = new RoundedButton("-", Math.round(screenWidth * 0.88f), Math.round(screenHeight * 0.45f), Math.round(screenWidth * 0.06f), Math.round(screenHeight * 0.07f), Math.round(screenHeight * 0.004f));
        roundedButtons.add(buttonMaxMovesMinus);
        buttonMaxMovesPlus = new RoundedButton("+", Math.round(screenWidth * 0.88f), Math.round(screenHeight * 0.38f), Math.round(screenWidth * 0.06f), Math.round(screenHeight * 0.07f), Math.round(screenHeight * 0.004f));
        roundedButtons.add(buttonMaxMovesPlus);
        textMovesRangeMin = new Text(String.valueOf(minMoves) + " min moves", Math.round(screenWidth * 0.73f), Math.round(screenHeight * 0.25f), Math.round(screenWidth * 0.11f), Math.round(screenHeight * 0.06f));
        texts.add(textMovesRangeMin);
        textMovesRangeMax = new Text(String.valueOf(maxMoves) + " max moves", Math.round(screenWidth * 0.73f), Math.round(screenHeight * 0.41f), Math.round(screenWidth * 0.11f), Math.round(screenHeight * 0.06f));
        texts.add(textMovesRangeMax);
        textMovesRangeTo = new Text("to", Math.round(screenWidth * 0.73f), Math.round(screenHeight * 0.33f), Math.round(screenWidth * 0.11f), Math.round(screenHeight * 0.06f));
        texts.add(textMovesRangeTo);
        buttonMinMovesMinus = new RoundedButton("-", Math.round(screenWidth * 0.88f), Math.round(screenHeight * 0.29f), Math.round(screenWidth * 0.06f), Math.round(screenHeight * 0.07f), Math.round(screenHeight * 0.004f));
        roundedButtons.add(buttonMinMovesMinus);
        buttonMinMovesPlus = new RoundedButton("+", Math.round(screenWidth * 0.88f), Math.round(screenHeight * 0.22f), Math.round(screenWidth * 0.06f), Math.round(screenHeight * 0.07f), Math.round(screenHeight * 0.004f));
        roundedButtons.add(buttonMinMovesPlus);
        buttonExit = new RoundedButton("Exit", Math.round(screenWidth * 0.69f), Math.round(screenHeight * 0.80f), Math.round(screenWidth * 0.11f), Math.round(screenHeight * 0.11f), Math.round(screenHeight * 0.004f));
        roundedButtons.add(buttonExit);
        buttonToggleSound = new RoundedButton("Sound", Math.round(screenWidth * 0.83f), Math.round(screenHeight * 0.68f), Math.round(screenWidth * 0.11f), Math.round(screenHeight * 0.11f), Math.round(screenHeight * 0.004f));
        roundedButtons.add(buttonToggleSound);
        buttonToggleMusic = new RoundedButton("Music", Math.round(screenWidth * 0.83f), Math.round(screenHeight * 0.80f), Math.round(screenWidth * 0.11f), Math.round(screenHeight * 0.11f), Math.round(screenHeight * 0.004f));
        roundedButtons.add(buttonToggleMusic);

        // Construct checking hex to compare taps to when determining which hexagon is selected
        hexCheck = Bitmap.createBitmap(HEX_WIDTH, HEX_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas temp = new Canvas(hexCheck);
        Utils.drawHex(temp, 0, 0, HEX_WIDTH, HEX_HEIGHT, 0xFF0000, 0, false);
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
        currentLevel = state.getInt(MainActivity.STATE_LEVEL);
        currentMove = state.getInt(MainActivity.STATE_MOVES_TAKEN);
        shortestMoves = state.getInt(MainActivity.STATE_SHORTEST_MOVES);
        boardState = state.getString(MainActivity.STATE_BOARD);
        initialBoardState = state.getString(MainActivity.STATE_INITIAL_BOARD);
        solution = state.getStringArrayList(MainActivity.STATE_SOLUTION);

        /// TODO: Add level logic for re-initializing state
        textMoves.setText("Moves: " + String.valueOf(shortestMoves));

        // Generate a background to use
        updateUIState();
        generateBackground();
    }

    /**
     * Updates the user interface state to show and hide the correct buttons.
     */
    private void updateUIState()
    {
        if (currentLevel != -1)
        {
            backgroundPanel.hasLineSeparator = true;
            levelText.isVisible = true;
            levelText.setText("LEVEL: " + (currentLevel + 1));
            buttonGenerateNew.isVisible = false;
            buttonMaxMovesMinus.isVisible = false;
            buttonMaxMovesPlus.isVisible = false;
            buttonMinMovesMinus.isVisible = false;
            buttonMinMovesPlus.isVisible = false;
            textBoardsSolvable.isVisible = false;
            textMovesRangeMin.isVisible = false;
            textMovesRangeMax.isVisible = false;
            textMovesRangeTo.isVisible = false;
            textMoves.isVisible = false;
        }
        else
        {
            backgroundPanel.hasLineSeparator = false;
            levelText.isVisible = false;
            buttonGenerateNew.isVisible = true;
            buttonMaxMovesMinus.isVisible = true;
            buttonMaxMovesPlus.isVisible = true;
            buttonMinMovesMinus.isVisible = true;
            buttonMinMovesPlus.isVisible = true;
            textBoardsSolvable.isVisible = true;
            textMovesRangeMin.isVisible = true;
            textMovesRangeMax.isVisible = true;
            textMovesRangeTo.isVisible = true;
            textMoves.isVisible = true;
        }
    }

    /**
     * Handles starting up a new level (zero based) or random board if -1.
     */
    public void newBoardState()
    {
        updateUIState();
        if (currentLevel != -1)
        {
            setBoardState(currentLevel);
        }
        else
        {
            randomBoardState(minMoves, maxMoves);
        }
        generateBackground();
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
        /// TODO: Remove draw debug frames per second
        canvas.drawText(String.valueOf(framesPerSecond), 10, 40, debugPaint);
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
                playerWon = Utils.boardSolved(boardState);
                if (playerWon && currentLevel != -1)
                {
                    // Cleared the board, player wins
                    PlayerData.setSolveMoves(currentLevel, currentMove);
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
                }
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
                // Check for buttons pressed
                // ...
                if (buttonGenerateNew.isToggled(Touch.downX, Touch.downY, Touch.x, Touch.y)) // Generate New Board
                {
//                    SoundManager.play(SoundManager.BUTTON);
                    newBoardState();
                }
                else if (buttonReset.isToggled(Touch.downX, Touch.downY, Touch.x, Touch.y)) // Reset
                {
//                    SoundManager.play(SoundManager.BUTTON);
                    boardState = initialBoardState;
                    currentMove = 0;
                    hexSelect = -1;
                    moveIndices.clear();
                    stopIndices.clear();
                }
                else if (buttonHint.isToggled(Touch.downX, Touch.downY, Touch.x, Touch.y)) // Step Hint
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
                else if (buttonMaxMovesMinus.isToggled(Touch.downX, Touch.downY, Touch.x, Touch.y)) // Maximum moves minus
                {
                    maxMoves--;
                    if (maxMoves < minMoves) maxMoves = minMoves;
                    if (maxMoves > 1)
                    {
                        textMovesRangeMax.setText(String.valueOf(maxMoves) + " max moves");
                    }
                    else
                    {
                        textMovesRangeMax.setText(String.valueOf(maxMoves) + " max move");
                    }
                }
                else if (buttonMaxMovesPlus.isToggled(Touch.downX, Touch.downY, Touch.x, Touch.y)) // Maximum moves plus
                {
                    maxMoves++;
                    if (maxMoves > 20) maxMoves = 20;
                    textMovesRangeMax.setText(String.valueOf(maxMoves) + " max moves");
                }
                else if (buttonMinMovesMinus.isToggled(Touch.downX, Touch.downY, Touch.x, Touch.y)) // Minimum moves minus
                {
                    minMoves--;
                    if (minMoves < 1) minMoves = 1;
                    if (minMoves > 1)
                    {
                        textMovesRangeMin.setText(String.valueOf(minMoves) + " min moves");
                    }
                    else
                    {
                        textMovesRangeMin.setText(String.valueOf(minMoves) + " min move");
                    }
                }
                else if (buttonMinMovesPlus.isToggled(Touch.downX, Touch.downY, Touch.x, Touch.y)) // Minimum moves plus
                {
                    minMoves++;
                    if (minMoves > maxMoves) minMoves = maxMoves;
                    textMovesRangeMin.setText(String.valueOf(minMoves) + " min moves");
                }
                else if (buttonExit.isToggled(Touch.downX, Touch.downY, Touch.x, Touch.y)) // Exit game
                {
//                    SoundManager.play(SoundManager.BUTTON);
                    mainView.handleEvent(new CustomEvent(CustomEvent.EXIT_GAME));
                }
//                else if (textboxes[11].isClicked(click_point)) // Next level
//                {
//                    SoundManager.play(SoundManager.BUTTON);
//                    if (currentLevel < 29)
//                    {
//                        currentLevel += 1;
//                        newBoardState();
//                    }
//                }
//                else if (textboxes[12].isClicked(click_point)) // Back level
//                {
//                    SoundManager.play(SoundManager.BUTTON);
//                    if (currentLevel > 0)
//                    {
//                        currentLevel -= 1;
//                        newBoardState();
//                    }
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
//        instructionsTextfield.visible = (currentLevel == 0);
//        textboxes[15].textfield.text = "Level: " + (currentLevel + 1);
//        if (PlayerData.solveMoves[level] == -1) textboxes[14].textfield.text = "Your Clear: 99";
//        else textboxes[14].textfield.text = "Your Clear: " + PlayerData.solveMoves[level];
//        textboxes[12].isAButton = (currentLevel != 0);
//        textboxes[11].isAButton = (currentLevel != 29 && PlayerData.solveMoves[level] != -1);
        parseSolution(mainBoardSet.get(level));
//        if (PlayerData.solveMoves[level] == -1) {
//            // Hide step hint and best/your clear
//            textboxes[3].visible = false;
//            textboxes[13].visible = false;
//            textboxes[14].visible = false;
//        } else if (PlayerData.solveMoves[level] > (solution.length - 1)) {
//            // Hide step hint
//            textboxes[3].visible = false;
//            textboxes[13].visible = true;
//            textboxes[14].visible = true;
//        } else {
//            textboxes[3].visible = true;
//            textboxes[13].visible = true;
//            textboxes[14].visible = true;
//        }
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
        textMoves.setText("Moves: " + String.valueOf(shortestMoves));
//        textboxes[13].textfield.text = "Best Clear: " + moves.toString();
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
        // Clear board
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
        backgroundPanel.draw(canvas);
        for (int i = 0; i < roundedButtons.size(); i++)
        {
            if (roundedButtons.get(i).isVisible) roundedButtons.get(i).draw(canvas, textPaint, buttonPaint);
        }
        for (int i = 0; i < texts.size(); i++)
        {
            if (texts.get(i).isVisible) texts.get(i).draw(canvas);
        }
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
     * Generates a new background.
     */
    public void generateBackground()
    {
        if (background == null)
        {
            background = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        }
        Utils.generateBackground(background, screenWidth, screenHeight);
    }
}
