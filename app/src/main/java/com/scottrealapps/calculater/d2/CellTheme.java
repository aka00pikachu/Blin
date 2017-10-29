package com.scottrealapps.calculater.d2;

import android.graphics.Paint;

/**
 * These are the various Paints we might use on Cells.
 */
public class CellTheme {
    public Paint badCell = new Paint();
    /**
     * Bad click, or good cell which is escaping off the bottom of the screen.
     */
    public Paint failedCell = new Paint();
    public Paint goodUnclickedCell = new Paint();
    public Paint goodClickedCell = new Paint();
    /**
     * The lines around the outside of cells.
     */
    public Paint cellBox = new Paint();
    /**
     * The text showing how much time is remaining
     */
    public Paint timeRemaining = new Paint();
}
