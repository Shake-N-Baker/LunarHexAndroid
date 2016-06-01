package com.isb.lunarhex;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;

/**
 * The Utilities class will have helpful functions for parsing and
 * mutating data, the methods will be static and the class should not
 * need to be constructed.
 *
 * @author Ian Baker
 */
public class Utils
{
    /**
     * The constant used in determining the hexagon outline strength: (hex_width + hex_height) / this
     */
    private static final int HEX_OUTLINE_DIVISOR = 75;

    /**
     * The paint to use with drawing stars
     */
    private static Paint starPaint;

    /**
     * The paint to use with drawing filled in stars
     */
    private static Paint starFilledPaint;

    /**
     * The paint to use with drawing hexagons
     */
    private static Paint hexFillPaint;

    /**
     * The paint to use with drawing icons
     */
    private static Paint iconPaint;

    /**
     * The hexagon outline border, initialized on first draw
     */
    private static int hexOutlineBorder = -1;

    /**
     * Converts the compressed board format into the format used in game.
     *
     * @param	compressedBoard - Boards of the format M...RGBYOP
     * @return	Board in format used by game i.e. R-#,G-#,B-#,Y-#,O-#,P-#
     */
    public static String convertCompressedBoard(String compressedBoard)
    {
        int skip = Integer.parseInt(String.valueOf(compressedBoard.charAt(0)), 36);
        String R, G, B = "", Y = "", O = "", P = "";
        R = String.valueOf(compressedBoard.charAt(skip + 1));
        G = String.valueOf(compressedBoard.charAt(skip + 2));
        if (compressedBoard.length() > skip + 3) B = String.valueOf(compressedBoard.charAt(skip + 3));
        if (compressedBoard.length() > skip + 4) Y = String.valueOf(compressedBoard.charAt(skip + 4));
        if (compressedBoard.length() > skip + 5) O = String.valueOf(compressedBoard.charAt(skip + 5));
        if (compressedBoard.length() > skip + 6) P = String.valueOf(compressedBoard.charAt(skip + 6));
        String gameFormatBoard = "R-" + Integer.parseInt(R, 36) + ",G-" + Integer.parseInt(G, 36);
        if (compressedBoard.length() > skip + 3) gameFormatBoard += ",B-" + Integer.parseInt(B, 36);
        if (compressedBoard.length() > skip + 4) gameFormatBoard += ",Y-" + Integer.parseInt(Y, 36);
        if (compressedBoard.length() > skip + 5) gameFormatBoard += ",O-" + Integer.parseInt(O, 36);
        if (compressedBoard.length() > skip + 6) gameFormatBoard += ",P-" + Integer.parseInt(P, 36);
        return gameFormatBoard;
    }

    /**
     * Returns whether the board has been solved or not.
     *
     * @param	board_state - The board state
     * @return	Whether the board has been solved
     */
    public static boolean boardSolved(String board_state)
    {
        return board_state.contains("R-12");
    }

    /**
     * Returns a vector of the indices to simulate the movement between the boards.
     *
     * @param	startBoard - The starting board state
     * @param	endBoard - The ending board state
     * @return	Vector of integer indices where 0 = start, 1 = end
     */
    public static List<Integer> getMoveIndices(String startBoard, String endBoard)
    {
        String[] pairs = startBoard.split(",");
        List<String[]> startPieces = new ArrayList<String[]>();
        int i;
        for (i = 0; i < pairs.length; i++)
        {
            String[] pair = new String[2];
            pair[0] = pairs[i].split("-")[0];
            pair[1] = pairs[i].split("-")[1];
            startPieces.add(pair);
        }
        pairs = endBoard.split(",");
        List<String[]> endPieces = new ArrayList<String[]>();
        for (i = 0; i < pairs.length; i++)
        {
            String[] pair = new String[2];
            pair[0] = pairs[i].split("-")[0];
            pair[1] = pairs[i].split("-")[1];
            endPieces.add(pair);
        }
        for (i = 0; i < startPieces.size(); i++)
        {
            if (!startPieces.get(i)[1].equals(endPieces.get(i)[1]))
            {
                List<Integer> result = new ArrayList<Integer>();
                result.add(Integer.parseInt(startPieces.get(i)[1]));
                result.add(Integer.parseInt(endPieces.get(i)[1]));
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the board state after preforming the given move.
     *
     * @param	board - The initial board state
     * @param	move - The move 0 = U, 1 = D, 2 = UR, 3 = UL, 4 = DR, 5 = DL, 0-5 = Red, 6-11 = Green etc.
     * @return	The board state after the given move has happened
     */
    public static String getBoardAfterMove(String board, int move)
    {
        String[] pairs = board.split(",");
        List<String[]> pieces = new ArrayList<String[]>();
        for (int i = 0; i < pairs.length; i++)
        {
            String[] pair = pairs[i].split("-");
            pieces.add(pair);
        }
        int dir = move % 6;
        int color = move / 6;
        Point new_coordinates;
        String new_board = "";

        // Move piece in one direction until hit object or falls outside of board
        new_coordinates = getCoordinatesFromIndex(Integer.parseInt(pieces.get(color)[1]));
        new_coordinates = moveCoordinate(new_coordinates, dir);
        int moves = 1;
        while (getIndexFromCoordinates(new_coordinates) != -1)
        {
            if (pieceAtIndex(getIndexFromCoordinates(new_coordinates), board))
            {
                if (2 <= moves)
                {
                    // Move in opposite direction (just before collision)
                    if (dir == 0) new_coordinates = moveCoordinate(new_coordinates, 1);
                    if (dir == 1) new_coordinates = moveCoordinate(new_coordinates, 0);
                    if (dir == 2) new_coordinates = moveCoordinate(new_coordinates, 5);
                    if (dir == 3) new_coordinates = moveCoordinate(new_coordinates, 4);
                    if (dir == 4) new_coordinates = moveCoordinate(new_coordinates, 3);
                    if (dir == 5) new_coordinates = moveCoordinate(new_coordinates, 2);
                    // Generate new valid board
                    for (int j = 0; j < pieces.size(); j++)
                    {
                        if (j > 0) new_board += ",";
                        if (color == j) new_board += pieces.get(j)[0] + "-" + String.valueOf(getIndexFromCoordinates(new_coordinates));
                        else new_board += pieces.get(j)[0] + "-" + pieces.get(j)[1];
                    }
                    return new_board;
                }
                else break;
            }
            new_coordinates = moveCoordinate(new_coordinates, dir);
            moves++;
        }
        return new_board;
    }

    /**
     * Returns the mutated coordinate_x and coordinate_y to correspond to moving in the given direction.
     *
     * @param	coordinate - X/Y coordinates
     * @param	direction - 0 = U, 1 = D, 2 = UR, 3 = UL, 4 = DR, 5 = DL
     */
    private static Point moveCoordinate(Point coordinate, int direction)
    {
        Point newCoordinate = new Point(coordinate.x, coordinate.y);
        switch (direction)
        {
            case 0: // Up 			Y-1
                newCoordinate.y--;
                break;
            case 1: // Down 		Y+1
                newCoordinate.y++;
                break;
            case 2: // Up-right 	X-even? X+1 : X+1 Y-1
                if (newCoordinate.x % 2 == 0)
                {
                    newCoordinate.x++;
                }
                else
                {
                    newCoordinate.x++;
                    newCoordinate.y--;
                }
                break;
            case 3: // Up-left 		X-even? X-1 : X-1 Y-1
                if (newCoordinate.x % 2 == 0)
                {
                    newCoordinate.x--;
                }
                else
                {
                    newCoordinate.x--;
                    newCoordinate.y--;
                }
                break;
            case 4: // Down-right	X-odd? X+1 : X+1 Y+1
                if (newCoordinate.x % 2 == 1)
                {
                    newCoordinate.x++;
                }
                else
                {
                    newCoordinate.x++;
                    newCoordinate.y++;
                }
                break;
            case 5: // Down-left	x-odd? X-1 : X-1 Y+1
                if (newCoordinate.x % 2 == 1)
                {
                    newCoordinate.x--;
                }
                else
                {
                    newCoordinate.x--;
                    newCoordinate.y++;
                }
                break;
            default:
                break;
        }
        return newCoordinate;
    }

    /**
     * Gets the X/Y index coordinates of the hexagon, Zero-based.
     *
     * @param	index - The index of the hexagon in the list
     * @return	The X/Y index coordinates of the hexagon
     */
    private static Point getCoordinatesFromIndex(int index)
    {
        if (index == 26) return new Point(3, 5);
        if (index == 25) return new Point(1, 5);
        if (0 <= index && index <= 24) return new Point(index % 5, index / 5);
        return null;
    }

    /**
     * Gets the index of the hexagon in the list from the X/Y index coordinates.
     *
     * @param	coordinates - The X/Y index coordinates of the hexagon
     * @return	The index of the hexagon in the list
     */
    private static int getIndexFromCoordinates(Point coordinates)
    {
        if (coordinates.x == 3 && coordinates.y == 5) return 26;
        if (coordinates.x == 1 && coordinates.y == 5) return 25;
        if (0 <= coordinates.x && coordinates.x < 5 && 0 <= coordinates.y && coordinates.y < 5) return (coordinates.y * 5) + coordinates.x;
        return -1;
    }

    /**
     * Returns whether a piece exists at the specified coordinates.
     *
     * @param	index - The index to check
     * @param	board_state - The state of the board to check
     * @return	Whether a piece exists
     */
    public static Boolean pieceAtIndex(int index, String board_state)
    {
        String[] pairs = board_state.split(",");
        for (int i = 0; i < pairs.length; i++)
        {
            if (index == Integer.parseInt(pairs[i].split("-")[1])) return true;
        }
        return false;
    }

    /**
     * Returns the list of bounding boxes for the hexagon tiles.
     *
     * @param	hexWidth - Width of a hexagon
     * @param	hexHeight - Height of a hexagon
     * @param	topLeftX - The top left X point of the board
     * @param	topLeftY - The top left Y point of the board
     * @return	List of bounding boxes
     */
    public static List<Rect> getBoundingBoxes(int hexWidth, int hexHeight, int topLeftX, int topLeftY)
    {
        List<Rect> list = new ArrayList<Rect>();
        float startX = topLeftX;
        float startY = topLeftY + (hexHeight / 2);
        float x = startX;
        float y = startY;
        for (int i = 0; i < 5; i++)
        {
            for (int j = 0; j < 5; j++)
            {
                list.add(new Rect(Math.round(x), Math.round(y), Math.round(x + hexWidth), Math.round(y + hexHeight)));
                x += Math.round(hexWidth * 0.75f);
                if (j % 2 == 0) y -= Math.round(hexHeight * 0.5f);
                else y += Math.round(hexHeight * 0.5f);
            }
            x = startX;
            y += hexHeight * 1.5;
        }
        x = startX + Math.round(0.75f * hexWidth);
        y -= hexHeight * 0.5;
        list.add(new Rect(Math.round(x), Math.round(y), Math.round(x + hexWidth), Math.round(y + hexHeight)));
        x = startX + Math.round(2.25f * hexWidth);
        list.add(new Rect(Math.round(x), Math.round(y), Math.round(x + hexWidth), Math.round(y + hexHeight)));
        return list;
    }

    /**
     * Returns the direction from start index to end index.
     *
     * @param	start - Start index
     * @param	end - End index
     * @return	Direction where -1 = None, 0 = U, 1 = D, 2 = UR, 3 = UL, 4 = DR, 5 = DL
     */
    public static int getMoveDirection(int start, int end)
    {
        // If either index is out of bounds return -1 (no direction)
        if (start < 0 || 26 < start) return -1;
        if (end < 0 || 26 < end) return -1;
        // Set 25 / 26 index to their spacial equivalents on the board
        if (start == 25) start = 26;
        else if (start == 26) start = 28;
        if (end == 25) end = 26;
        else if (end == 26) end = 28;
        // Calculate differences
        int diff = start - end;
        int columnDiff = (start % 5) - (end % 5);
        // Check for up or down direction
        if (start > end && (start - end) % 5 == 0) return 0;
        if (end > start && (end - start) % 5 == 0) return 1;
        if (start % 5 != end % 5) { // Not in the same column, check rest of directions
            if (((start % 5) % 2) == 0) { // Start on even index column
                if (columnDiff < 0 && (diff == -1 || diff == 3 || diff == 2 || diff == 6)) return 2;
                if (columnDiff > 0 && (diff == 1 || diff == 7 || diff == 8 || diff == 14)) return 3;
                if (columnDiff < 0 && (diff == -6 || diff == -7 || diff == -13 || diff == -14)) return 4;
                if (columnDiff > 0 && (diff == -4 || diff == -3 || diff == -7 || diff == -6)) return 5;
            } else { // Start on odd index column
                if (columnDiff < 0 && (diff == 4 || diff == 3 || diff == 7)) return 2;
                if (columnDiff > 0 && (diff == 6 || diff == 7 || diff == 13)) return 3;
                if (columnDiff < 0 && (diff == -1 || diff == -7 || diff == -8)) return 4;
                if (columnDiff > 0 && (diff == 1 || diff == -3 || diff == -2)) return 5;
            }
        }
        return -1;
    }

    /**
     * Draws a star at the specified location with the given size.
     *
     * @param   canvas - The canvas to draw on
     * @param   x - X coordinate of top left bounding box
     * @param   y - Y coordinate of top left bounding box
     * @param   width - The width of the star
     * @param   height - The height of the star
     * @param   completeness - The level of completeness, 0 = none, 1 = complete, 2 = perfect
     * @param   transparency - The alpha value of the star, 0 to 255
     */
    public static void drawStar(Canvas canvas, float x, float y, float width, float height, int completeness, int transparency)
    {
        if (starPaint == null)
        {
            starPaint = new Paint();
            starPaint.setStyle(Paint.Style.STROKE);
            starPaint.setStrokeWidth(2f);
        }
        if (starFilledPaint == null)
        {
            starFilledPaint = new Paint();
            starFilledPaint.setStyle(Paint.Style.FILL);
        }
        if (completeness == 0)
        {
            // Draw more transparent blue star
            starPaint.setColor(Color.argb(transparency / 2, 89, 116, 146));
            starFilledPaint.setColor(Color.argb(transparency / 2, 46, 67, 94));
        }
        else if (completeness == 1)
        {
            // Draw silver star
            starPaint.setColor(Color.argb(transparency, 255, 255, 255));
            starFilledPaint.setColor(Color.argb(transparency, 190, 200, 200));
        }
        else
        {
            // Draw gold star
            starPaint.setColor(Color.argb(transparency, 255, 255, 255));
            starFilledPaint.setColor(Color.argb(transparency, 255, 215, 45));
        }
        Path starPath = new Path();
        starPath.moveTo(x + ((50f / 100f) * width), y);
        starPath.lineTo(x + ((62f / 100f) * width), y + ((38f / 100) * height));
        starPath.lineTo(x + width, y + ((38f / 100) * height));
        starPath.lineTo(x + ((70f / 100f) * width), y + ((62f / 100) * height));
        starPath.lineTo(x + ((80f / 100f) * width), y + height);
        starPath.lineTo(x + ((50f / 100f) * width), y + ((78f / 100) * height));
        starPath.lineTo(x + ((20f / 100f) * width), y + height);
        starPath.lineTo(x + ((30f / 100f) * width), y + ((62f / 100) * height));
        starPath.lineTo(x, y + ((38f / 100) * height));
        starPath.lineTo(x + ((38f / 100f) * width), y + ((38f / 100) * height));
        starPath.close();
        canvas.drawPath(starPath, starFilledPaint);
        canvas.drawPath(starPath, starPaint);
    }

    /**
     * Draws a hexagon at the specified location of the given size.
     *
     * @param canvas - The canvas to draw on
     * @param x - X coordinate of top left bounding box
     * @param y - Y coordinate of top left bounding box
     * @param width - The width of the hexagon
     * @param height - The height of the hexagon
     * @param color - The color of the hexagon
     * @param depth - The depth of the hexagon
     * @param hasOutline - Whether the hexagon has an outline
     */
    public static void drawHex(Canvas canvas, float x, float y, float width, float height, int color, int depth, boolean hasOutline)
    {
        x = Math.round(x);
        y = Math.round(y);
        width = Math.round(width);
        height = Math.round(height);
        float width_side_length = Math.round(width / 2f);
        float width_center_offset = Math.round(width / 4f);

        if (hexFillPaint == null)
        {
            hexFillPaint = new Paint();
            hexFillPaint.setStyle(Paint.Style.FILL);
        }

        if (hexOutlineBorder == -1) hexOutlineBorder = Math.round((width + height) / HEX_OUTLINE_DIVISOR);

        Path topTotalPath;
        Path topInnerPath = null;
        Path bottomTotalPath = null;
        Path bottomInnerPath = null;

        topTotalPath = new Path();
        topTotalPath.moveTo(x + width_center_offset, y);
        topTotalPath.lineTo(x + width_center_offset + width_side_length, y);
        topTotalPath.lineTo(x + width, y + (height / 2));
        topTotalPath.lineTo(x + width_center_offset + width_side_length, y + height);
        topTotalPath.lineTo(x + width_center_offset, y + height);
        topTotalPath.lineTo(x, y + (height / 2));
        topTotalPath.close();

        if (hasOutline)
        {
            topInnerPath = new Path();
            topInnerPath.moveTo(x + width_center_offset, y + hexOutlineBorder);
            topInnerPath.lineTo(x + width_center_offset + width_side_length, y + hexOutlineBorder);
            topInnerPath.lineTo(x + width - hexOutlineBorder, y + (height / 2));
            topInnerPath.lineTo(x + width_center_offset + width_side_length, y + height - hexOutlineBorder);
            topInnerPath.lineTo(x + width_center_offset, y + height - hexOutlineBorder);
            topInnerPath.lineTo(x + hexOutlineBorder, y + (height / 2));
            topInnerPath.close();
        }

        if (depth > 0)
        {
            bottomTotalPath = new Path();
            bottomTotalPath.moveTo(x, y + (height / 2));
            bottomTotalPath.lineTo(x, y + (height / 2) + depth);
            bottomTotalPath.lineTo(x + width_center_offset, y + height + depth);
            bottomTotalPath.lineTo(x + width_center_offset + width_side_length, y + height + depth);
            bottomTotalPath.lineTo(x + width, y + (height / 2) + depth);
            bottomTotalPath.lineTo(x + width, y + (height / 2));
            bottomTotalPath.lineTo(x + width_center_offset + width_side_length, y + height);
            bottomTotalPath.lineTo(x + width_center_offset, y + height);
            bottomTotalPath.close();

            if (hasOutline)
            {
                bottomInnerPath = new Path();
                bottomInnerPath.moveTo(x + hexOutlineBorder, y + (height / 2));
                bottomInnerPath.lineTo(x + hexOutlineBorder, y + (height / 2) + depth);
                bottomInnerPath.lineTo(x + width_center_offset, y + height + depth - hexOutlineBorder);
                bottomInnerPath.lineTo(x + width_center_offset + width_side_length, y + height + depth - hexOutlineBorder);
                bottomInnerPath.lineTo(x + width - hexOutlineBorder, y + (height / 2) + depth);
                bottomInnerPath.lineTo(x + width - hexOutlineBorder, y + (height / 2));
                bottomInnerPath.lineTo(x + width_center_offset + width_side_length, y + height);
                bottomInnerPath.lineTo(x + width_center_offset, y + height);
                bottomInnerPath.close();
            }
        }

        if (depth > 0)
        {
            if (hasOutline)
            {
                hexFillPaint.setColor(0xFF000000);
                canvas.drawPath(bottomTotalPath, hexFillPaint);
                hexFillPaint.setColor(0xFF000000 + magnifyColor(color, 0.5f));
                canvas.drawPath(bottomInnerPath, hexFillPaint);
                hexFillPaint.setColor(0xFF000000);
                canvas.drawPath(topTotalPath, hexFillPaint);
                hexFillPaint.setColor(0xFF000000 + color);
                canvas.drawPath(topInnerPath, hexFillPaint);
            }
            else
            {
                hexFillPaint.setColor(0xFF000000 + color);
                canvas.drawPath(bottomTotalPath, hexFillPaint);
                canvas.drawPath(topTotalPath, hexFillPaint);
            }
        }
        else
        {
            if (hasOutline)
            {
                hexFillPaint.setColor(0xFF000000);
                canvas.drawPath(topTotalPath, hexFillPaint);
                hexFillPaint.setColor(0xFF000000 + color);
                canvas.drawPath(topInnerPath, hexFillPaint);
            }
            else
            {
                hexFillPaint.setColor(0xFF000000 + color);
                canvas.drawPath(topTotalPath, hexFillPaint);
            }
        }
    }

    /**
     * Draws an icon at the specified location with the given size.
     *
     * @param   canvas - The canvas to draw on
     * @param   type - The type of icon to draw
     * @param   x - X coordinate of center of the icon
     * @param   y - Y coordinate of center of the icon
     * @param   radius - The icon radius
     */
    public static void drawIcon(Canvas canvas, String type, float x, float y, float radius)
    {
        if (iconPaint == null)
        {
            iconPaint = new Paint();
            iconPaint.setStyle(Paint.Style.FILL);
            iconPaint.setColor(Color.argb(255, 255, 255, 255));
        }

        float diameter = radius * 2f;
        float width = (float) Math.sqrt((diameter * diameter) / 2f);
        float height = width;
        float innerRadius = width / 2f;
        x -= innerRadius;
        y -= innerRadius;

        Path iconPath;
        if (type.toLowerCase().equals("home"))
        {
            iconPath = new Path();
            iconPath.moveTo(x, y + (height / 2f));
            iconPath.lineTo(x + (width / 2f), y);
            iconPath.lineTo(x + ((70f * width) / 100f), y + ((19f * height) / 100f));
            iconPath.lineTo(x + ((70f * width) / 100f), y);
            iconPath.lineTo(x + ((83f * width) / 100f), y);
            iconPath.lineTo(x + ((83f * width) / 100f), y + ((34f * height) / 100f));
            iconPath.lineTo(x + width, y + (height / 2f));
            iconPath.lineTo(x + ((94f * width) / 100f), y + ((58f * height) / 100f));
            iconPath.lineTo(x + (width / 2f), y + ((13f * height) / 100f));
            iconPath.lineTo(x + ((6f * width) / 100f), y + ((58f * height) / 100f));
            iconPath.close();
            canvas.drawPath(iconPath, iconPaint);
            iconPath = new Path();
            iconPath.moveTo(x + ((17f * width) / 100f), y + ((58f * height) / 100f));
            iconPath.lineTo(x + ((50f * width) / 100f), y + ((24f * height) / 100f));
            iconPath.lineTo(x + ((83f * width) / 100f), y + ((58f * height) / 100f));
            iconPath.lineTo(x + ((83f * width) / 100f), y + height);
            iconPath.lineTo(x + ((60f * width) / 100f), y + height);
            iconPath.lineTo(x + ((60f * width) / 100f), y + ((70f * height) / 100f));
            iconPath.lineTo(x + ((40f * width) / 100f), y + ((70f * height) / 100f));
            iconPath.lineTo(x + ((40f * width) / 100f), y + height);
            iconPath.lineTo(x + ((17f * width) / 100f), y + height);
            iconPath.close();
            canvas.drawPath(iconPath, iconPaint);
        }
        else if (type.toLowerCase().equals("new"))
        {
            iconPath = new Path();
            for (int i = 0; i < 3; i++)
            {
                for (int j = 0; j < 3; j++)
                {
                    if (i == 2 && j == 2)
                    {
                        break;
                    }
                    float left = x + ((3f / 10f) * width * i);
                    float top = y + ((3f / 10f) * height * j);
                    iconPath.addRect(left, top, left + (width / 5f), top + (height / 5f), Path.Direction.CW);
                }
            }
            iconPath.addRect(x + ((58f / 100f) * width), y + ((68f / 100f) * height), x + ((86f / 100f) * width), y + ((76f / 100f) * height), Path.Direction.CW);
            iconPath.addRect(x + ((68f / 100f) * width), y + ((58f / 100f) * height), x + ((76f / 100f) * width), y + ((86f / 100f) * height), Path.Direction.CW);
            canvas.drawPath(iconPath, iconPaint);
            iconPath.addCircle(x + ((72f / 100f) * width), y + ((72f / 100f) * height), (26f / 100f) * height, Path.Direction.CW);
            iconPath.addCircle(x + ((72f / 100f) * width), y + ((72f / 100f) * height), (22f / 100f) * height, Path.Direction.CW);
            iconPath.setFillType(Path.FillType.EVEN_ODD);
            canvas.drawPath(iconPath, iconPaint);
        }
        else if (type.toLowerCase().equals("plus"))
        {
            iconPath = new Path();
            iconPath.addRect(x + ((5f / 100f) * width), y + ((45f / 100f) * height), x + ((95f / 100f) * width), y + ((55f / 100f) * height), Path.Direction.CW);
            iconPath.addRect(x + ((45f / 100f) * width), y + ((5f / 100f) * height), x + ((55f / 100f) * width), y + ((95f / 100f) * height), Path.Direction.CW);
            canvas.drawPath(iconPath, iconPaint);
        }
        else if (type.toLowerCase().equals("minus"))
        {
            iconPath = new Path();
            iconPath.addRect(x + ((5f / 100f) * width), y + ((45f / 100f) * height), x + ((95f / 100f) * width), y + ((55f / 100f) * height), Path.Direction.CW);
            canvas.drawPath(iconPath, iconPaint);
        }
        else if (type.toLowerCase().equals("close"))
        {
            iconPath = new Path();
            iconPath.moveTo(x + ((5f * width) / 100f), y + ((8f * height) / 100f));
            iconPath.lineTo(x + ((30f * width) / 100f), y + ((8f * height) / 100f));
            iconPath.lineTo(x + ((50f * width) / 100f), y + ((33f * height) / 100f));
            iconPath.lineTo(x + ((70f * width) / 100f), y + ((8f * height) / 100f));
            iconPath.lineTo(x + ((95f * width) / 100f), y + ((8f * height) / 100f));
            iconPath.lineTo(x + ((62f * width) / 100f), y + ((50f * height) / 100f));
            iconPath.lineTo(x + ((95f * width) / 100f), y + ((92f * height) / 100f));
            iconPath.lineTo(x + ((70f * width) / 100f), y + ((92f * height) / 100f));
            iconPath.lineTo(x + ((50f * width) / 100f), y + ((66f * height) / 100f));
            iconPath.lineTo(x + ((30f * width) / 100f), y + ((92f * height) / 100f));
            iconPath.lineTo(x + ((5f * width) / 100f), y + ((92f * height) / 100f));
            iconPath.lineTo(x + ((37f * width) / 100f), y + ((50f * height) / 100f));
            iconPath.close();
            canvas.drawPath(iconPath, iconPaint);
        }
        else if (type.toLowerCase().equals("options"))
        {
            iconPath = new Path();
            iconPath.moveTo(x + ((40f / 100f) * width), y + ((2f / 100f) * height));
            iconPath.lineTo(x + ((60f / 100f) * width), y + ((2f / 100f) * height));
            iconPath.lineTo(x + ((60f / 100f) * width), y + ((13f / 100f) * height));
            iconPath.lineTo(x + ((69f / 100f) * width), y + ((16f / 100f) * height));
            iconPath.lineTo(x + ((77f / 100f) * width), y + ((9f / 100f) * height));
            iconPath.lineTo(x + ((90f / 100f) * width), y + ((22f / 100f) * height));
            iconPath.lineTo(x + ((83f / 100f) * width), y + ((30f / 100f) * height));
            iconPath.lineTo(x + ((87f / 100f) * width), y + ((39f / 100f) * height));
            iconPath.lineTo(x + ((98f / 100f) * width), y + ((40f / 100f) * height));
            iconPath.lineTo(x + ((98f / 100f) * width), y + ((60f / 100f) * height));
            iconPath.lineTo(x + ((87f / 100f) * width), y + ((60f / 100f) * height));
            iconPath.lineTo(x + ((83f / 100f) * width), y + ((68f / 100f) * height));
            iconPath.lineTo(x + ((90f / 100f) * width), y + ((77f / 100f) * height));
            iconPath.lineTo(x + ((77f / 100f) * width), y + ((89f / 100f) * height));
            iconPath.lineTo(x + ((69f / 100f) * width), y + ((82f / 100f) * height));
            iconPath.lineTo(x + ((60f / 100f) * width), y + ((86f / 100f) * height));
            iconPath.lineTo(x + ((60f / 100f) * width), y + ((98f / 100f) * height));
            iconPath.lineTo(x + ((40f / 100f) * width), y + ((98f / 100f) * height));
            iconPath.lineTo(x + ((40f / 100f) * width), y + ((86f / 100f) * height));
            iconPath.lineTo(x + ((31f / 100f) * width), y + ((82f / 100f) * height));
            iconPath.lineTo(x + ((23f / 100f) * width), y + ((89f / 100f) * height));
            iconPath.lineTo(x + ((10f / 100f) * width), y + ((77f / 100f) * height));
            iconPath.lineTo(x + ((17f / 100f) * width), y + ((69f / 100f) * height));
            iconPath.lineTo(x + ((13f / 100f) * width), y + ((60f / 100f) * height));
            iconPath.lineTo(x + ((2f / 100f) * width), y + ((60f / 100f) * height));
            iconPath.lineTo(x + ((2f / 100f) * width), y + ((40f / 100f) * height));
            iconPath.lineTo(x + ((13f / 100f) * width), y + ((40f / 100f) * height));
            iconPath.lineTo(x + ((18f / 100f) * width), y + ((31f / 100f) * height));
            iconPath.lineTo(x + ((10f / 100f) * width), y + ((22f / 100f) * height));
            iconPath.lineTo(x + ((23f / 100f) * width), y + ((10f / 100f) * height));
            iconPath.lineTo(x + ((31f / 100f) * width), y + ((17f / 100f) * height));
            iconPath.lineTo(x + ((40f / 100f) * width), y + ((13f / 100f) * height));
            iconPath.close();
            iconPath.addCircle(x + innerRadius, y + innerRadius, innerRadius / 2f, Path.Direction.CW);
            iconPath.setFillType(Path.FillType.EVEN_ODD);
            canvas.drawPath(iconPath, iconPaint);
        }
        else if (type.toLowerCase().equals("retry"))
        {
            iconPath = new Path();
            iconPath.moveTo(x + (width / 2f), y);
            iconPath.lineTo(x + (width / 2f), y + ((7f / 20f) * height));
            iconPath.lineTo(x + ((27f / 40f) * width), y + ((7f / 40f) * height));
            iconPath.close();
            canvas.drawPath(iconPath, iconPaint);
            iconPath.addArc(new RectF(x + ((1f / 10f) * width), y + ((1f / 10f) * height), x + ((9f / 10f) * width), y + ((9f / 10f) * height)), 0, 180);
            iconPath.addArc(new RectF(x + ((2f / 10f) * width), y + ((2f / 10f) * height), x + ((8f / 10f) * width), y + ((8f / 10f) * height)), 0, 180);
            iconPath.setFillType(Path.FillType.EVEN_ODD);
            canvas.drawPath(iconPath, iconPaint);
            iconPath.addArc(new RectF(x + ((1f / 10f) * width), y + ((1f / 10f) * height), x + ((9f / 10f) * width), y + ((9f / 10f) * height)), 90, 180);
            iconPath.addArc(new RectF(x + ((2f / 10f) * width), y + ((2f / 10f) * height), x + ((8f / 10f) * width), y + ((8f / 10f) * height)), 90, 180);
            canvas.drawPath(iconPath, iconPaint);
        }
        else if (type.toLowerCase().equals("hint"))
        {
            iconPath = new Path();
            iconPath.addArc(new RectF(x + ((17f / 100f) * width), y + ((0f / 100f) * height), x + ((82f / 100f) * width), y + ((65f / 100f) * height)), 135, 270);
            iconPath.lineTo(x + (width / 2f), y + (height / 2f));
            iconPath.addArc(new RectF(x + ((23f / 100f) * width), y + ((5f / 100f) * height), x + ((77f / 100f) * width), y + ((631f / 1000f) * height)), 135, 270);
            iconPath.lineTo(x + (width / 2f), y + (height / 2f));
            iconPath.setFillType(Path.FillType.EVEN_ODD);
            canvas.drawPath(iconPath, iconPaint);
            iconPath.reset();
            iconPath.moveTo(x + ((26f / 100f) * width), y + ((54f / 100f) * height));
            iconPath.lineTo(x + ((34f / 100f) * width), y + ((66f / 100f) * height));
            iconPath.lineTo(x + ((39f / 100f) * width), y + ((66f / 100f) * height));
            iconPath.lineTo(x + ((31f / 100f) * width), y + ((54f / 100f) * height));
            iconPath.close();
            canvas.drawPath(iconPath, iconPaint);
            iconPath.reset();
            iconPath.moveTo(x + ((73f / 100f) * width), y + ((54f / 100f) * height));
            iconPath.lineTo(x + ((65f / 100f) * width), y + ((66f / 100f) * height));
            iconPath.lineTo(x + ((60f / 100f) * width), y + ((66f / 100f) * height));
            iconPath.lineTo(x + ((68f / 100f) * width), y + ((54f / 100f) * height));
            iconPath.close();
            canvas.drawPath(iconPath, iconPaint);
            iconPath.reset();
            iconPath.addRect(x + ((34f / 100f) * width), y + ((66f / 100f) * height), x + ((65f / 100f) * width), y + ((87f / 100f) * height), Path.Direction.CW);
            iconPath.addRect(x + ((39f / 100f) * width), y + ((71f / 100f) * height), x + ((60f / 100f) * width), y + ((82f / 100f) * height), Path.Direction.CW);
            iconPath.addRect(x + ((38f / 100f) * width), y + ((88f / 100f) * height), x + ((61f / 100f) * width), y + ((93f / 100f) * height), Path.Direction.CW);
            iconPath.addRect(x + ((40f / 100f) * width), y + ((94f / 100f) * height), x + ((59f / 100f) * width), y + ((99f / 100f) * height), Path.Direction.CW);
            canvas.drawPath(iconPath, iconPaint);
            iconPath.reset();
            iconPath.moveTo(x + ((48f / 100f) * width), y + ((66f / 100f) * height));
            iconPath.lineTo(x + ((53f / 100f) * width), y + ((66f / 100f) * height));
            iconPath.lineTo(x + ((53f / 100f) * width), y + ((52f / 100f) * height));
            iconPath.lineTo(x + ((48f / 100f) * width), y + ((49f / 100f) * height));
            iconPath.lineTo(x + ((57f / 100f) * width), y + ((46f / 100f) * height));
            iconPath.lineTo(x + ((57f / 100f) * width), y + ((43f / 100f) * height));
            iconPath.lineTo(x + ((46f / 100f) * width), y + ((34f / 100f) * height));
            iconPath.lineTo(x + ((44f / 100f) * width), y + ((37f / 100f) * height));
            iconPath.lineTo(x + ((52f / 100f) * width), y + ((43f / 100f) * height));
            iconPath.lineTo(x + ((40f / 100f) * width), y + ((45f / 100f) * height));
            iconPath.lineTo(x + ((40f / 100f) * width), y + ((50f / 100f) * height));
            iconPath.lineTo(x + ((48f / 100f) * width), y + ((54f / 100f) * height));
            iconPath.close();
            canvas.drawPath(iconPath, iconPaint);
        }
    }

    /**
     * Magnifies the color by the given percent and returns the new value.
     *
     * @param 	color - The color in hexadecimal
     * @param	percent - The percent to magnify the color with
     * @return	The new color value after magnification
     */
    public static int magnifyColor(int color, float percent)
    {
        int r = color & 0xFF0000;
        r = r >> 16;
        int g = color & 0x00FF00;
        g = g >> 8;
        int b = color & 0x0000FF;
        r *= percent;
        g *= percent;
        b *= percent;
        if (r < 0) r = 0;
        if (r > 255) r = 255;
        if (g < 0) g = 0;
        if (g > 255) g = 255;
        if (b < 0) b = 0;
        if (b > 255) b = 255;
        return (r << 16 | g << 8 | b);
    }

    /**
     * Function for easing out.
     *
     * @param	t - The current time from 0 to duration inclusive
     * @param	b - The initial value of the animation property
     * @param	c - The total change in the animation property
     * @param	d - The duration of the motion
     * @return	The value of the interpolated property at the specified time
     */
    public static double easeOut(double t, double b, double c, double d)
    {
        return -c * (t /= d) * (t - 2) + b;
    }

    /**
     * Function for easing in.
     *
     * @param	t - The current time from 0 to duration inclusive
     * @param	b - The initial value of the animation property
     * @param	c - The total change in the animation property
     * @param	d - The duration of the motion
     * @return	The value of the interpolated property at the specified time
     */
    public static double easeIn(double t, double b, double c, double d)
    {
        return c * (t /= d) * t + b;
    }

    /**
     * Returns the path indices as [0] = indices along the path, [1] = indices
     * at the end of the path.
     *
     * @param	board - The board state
     * @param	selected_hex_index - The selected piece index
     * @return	The path indices [0] = indices along the path, [1] = indices at the end of the path
     */
    public static List<List<Integer>> getPathIndices(String board, int selected_hex_index)
    {
        List<List<Integer>> pathIndices = new ArrayList<List<Integer>>();
        pathIndices.add(new ArrayList<Integer>());
        pathIndices.add(new ArrayList<Integer>());
        Point new_coordinates;
        int moves;
        for (int dir = 0; dir < 6; dir++)
        {
            // Move piece in one direction until hit object or falls outside of board
            new_coordinates = getCoordinatesFromIndex(selected_hex_index);
            new_coordinates = moveCoordinate(new_coordinates, dir);
            moves = 1;
            while (getIndexFromCoordinates(new_coordinates) != -1)
            {
                if (pieceAtIndex(getIndexFromCoordinates(new_coordinates), board)) {
                    if (2 <= moves) {
                        // Move in opposite direction (just before collision)
                        if (dir == 0) new_coordinates = moveCoordinate(new_coordinates, 1);
                        if (dir == 1) new_coordinates = moveCoordinate(new_coordinates, 0);
                        if (dir == 2) new_coordinates = moveCoordinate(new_coordinates, 5);
                        if (dir == 3) new_coordinates = moveCoordinate(new_coordinates, 4);
                        if (dir == 4) new_coordinates = moveCoordinate(new_coordinates, 3);
                        if (dir == 5) new_coordinates = moveCoordinate(new_coordinates, 2);
                        pathIndices.get(1).add(getIndexFromCoordinates(new_coordinates));
                        break;
                    }
                    else break;
                }
                pathIndices.get(0).add(getIndexFromCoordinates(new_coordinates));
                new_coordinates = moveCoordinate(new_coordinates, dir);
                moves++;
            }
        }
        // Remove indices from those along the path that are the path end (no duplicates)
        for (int i = pathIndices.get(0).size() - 1; i >= 0; i--) {
        if (pathIndices.get(1).indexOf(pathIndices.get(0).get(i)) != -1) pathIndices.get(0).remove(i);
    }
        return pathIndices;
    }

    /**
     * Generates a random background.
     *
     * @param   background - The background to draw the background on
     * @param   screenWidth - The width of the screen
     * @param   screenHeight - The height of the screen
     */
    public static void generateBackground(Bitmap background, int screenWidth, int screenHeight)
    {
        background.eraseColor(Color.rgb(14, 21, 42));
        Canvas canvas = new Canvas(background);
        Paint paint = new Paint();
        Random rand = new Random();
        int colorIndex;
        int color;
        int[] red =     {255, 200, 253};
        int[] green =   {255, 200, 200};
        int[] blue =    {255, 247, 222};
        for(int i = 0; i < 400; i++)
        {
            int starX = rand.nextInt(screenWidth);
            int starY = rand.nextInt(screenHeight);
            int starSize = (int) Math.ceil((screenWidth + screenHeight) / 1216);
            paint.setColor(Color.WHITE);
            canvas.drawRect(new RectF(starX, starY, starX + starSize, starY + starSize), paint);
        }
        for(int i = 0; i < 100; i++)
        {
            colorIndex = rand.nextInt(red.length);
            int transparency = 55 + rand.nextInt(145);
            color = Color.argb(transparency, red[colorIndex], green[colorIndex], blue[colorIndex]);
            int starX = rand.nextInt(screenWidth);
            int starY = rand.nextInt(screenHeight);
            int starSize = rand.nextInt(16) + 4;
            RadialGradient radialGradient = new RadialGradient(starX, starY, starSize / 2,
                    new int[] {color, ((int) (transparency * 0.6f) << 24) + (color & 0x00FFFFFF), (color & 0x00FFFFFF)},
                    new float[] {0.0f, rand.nextFloat() * 0.5f + 0.05f,  1.0f},
                    Shader.TileMode.CLAMP);
            paint.setShader(radialGradient);
            canvas.drawCircle(starX, starY, starSize, paint);
        }
    }

    /**
     * Returns the distance between two points.
     *
     * @param   x1 - First point x coordinate
     * @param   y1 - First point y coordinate
     * @param   x2 - Second point x coordinate
     * @param   y2 - Second point y coordinate
     * @return  The distance between both points
     */
    public static double distanceBetweenPoints(double x1, double y1, double x2, double y2)
    {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
}