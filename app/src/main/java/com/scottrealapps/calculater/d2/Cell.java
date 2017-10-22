package com.scottrealapps.calculater.d2;

import android.graphics.Paint;

/**
 * This is one rectangle on the screen.
 */
public class Cell {
    // True if this is supposed to be clicked on, false if it's not.
    private boolean correct;
    private boolean hasBeenClicked;
    private Paint paint;
    private CellTheme theme;

    public Cell(CellTheme theme) {
        this.theme = theme;
    }

    /**
     * Call to reuse/reinitialize a Cell.
     */
    public void reset(boolean correct) {
        this.correct = correct;
        hasBeenClicked = false;
        if (correct) {
            paint = theme.goodUnclickedCell;
        } else {
            paint = theme.badCell;
        }
    }

    /**
     * Call this when the cell is clicked on, unless it's not correct.
     */
    public void clicked() {
        if (!correct) return;
        if (hasBeenClicked == false) {
            paint = theme.goodClickedCell;
//  change our color or image
            hasBeenClicked = true;
        }
    }

    /**
     * Returns true if this cell is OK to click on; false if not.
     */
    public boolean okToClick() {
        return correct;
    }

    public boolean hasBeenClicked() {
        return hasBeenClicked;
    }

    /**
     * @param newPaint must not be null.
     */
    public void setPaint(Paint newPaint) {
        paint = newPaint;
    }

    public Paint getPaint() {
        return paint;
    }

    public CellTheme getTheme() {
        return theme;
    }
}
