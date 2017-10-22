package com.scottrealapps.calculater.d2;

import android.graphics.Canvas;
import android.graphics.Rect;

public class Row {
    private CellTheme theme;

    public Row(int columns, CellTheme theme) {
        this.theme = theme;
        cells = new Cell[columns];
        for (int column = 0; column < columns; ++column) {
            cells[column] = new Cell(theme);
        }
    }

    private int height;  //  in pixels
    private Cell[] cells;  //  this is our list of cells

    public boolean hasUnclickedGoodCells() {
        for (int ii = 0; ii < cells.length; ++ii) {
            if (cells[ii].okToClick() && !cells[ii].hasBeenClicked()) {
                return true;
            }
        }
        return false;
    }

    public void setUnclickedGoodRowsFailed() {
        for (int ii = 0; ii < cells.length; ++ii) {
            if (cells[ii].okToClick() && !cells[ii].hasBeenClicked()) {
                cells[ii].setPaint(theme.failedCell);
            }
        }
    }

    /**
     * This is how you set the height.  Here's an important note you really
     * need to know.
     *
     * @param newHeight must be greater than zero.
     */
    public void setHeight(int newHeight) {
        height = newHeight;
    }

    public int getHeight() {
        return height;
    }

    public int getCellCount() {
        return cells.length;
    }

    public Cell getCell(int index) {
        return cells[index];
    }

    /**
     * Call this to reset/reinitialize a row.
     *
     * @param goodCell which cell should be marked as correct. -1 if none
     *                 are correct.
     */
    public void reset(int goodCell) {
        for (int column = 0; column < cells.length; ++column) {
            cells[column].reset((column == goodCell) ? true : false);
        }
    }

    public void draw(Canvas canvas, int currentRowTop, int colWidth) {
        Rect youGotRect = new Rect(0, (currentRowTop < 0) ? 0 : currentRowTop, colWidth, currentRowTop + height);
        for (int ii = 0; ii < cells.length; ++ii) {
//set(int left, int top, int right, int bottom)
            //  now we want to draw a rectangle!
            canvas.drawRect(youGotRect, cells[ii].getPaint());
            youGotRect.left += colWidth;
            youGotRect.right += colWidth;
        }
        //  now draw the top line
        if (currentRowTop >= 0) {
            canvas.drawLine(0, currentRowTop, colWidth * cells.length, currentRowTop, theme.cellBox);
        }
    }
}
