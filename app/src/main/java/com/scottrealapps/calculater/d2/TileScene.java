package com.scottrealapps.calculater.d2;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.scottrealapps.calculater.GameOverActivity;
import com.scottrealapps.calculater.R;
import com.scottrealapps.calculater.StartGameActivity;
import com.scottrealapps.calculater.TileActivity;
import com.scottrealapps.calculater.util.AscendingSpeed;
import com.scottrealapps.calculater.util.OscillatingSpeed;
import com.scottrealapps.calculater.util.SpeedAdjuster;

import java.util.ArrayList;
import java.util.Random;

/**
 * This manages all the objects we're looking at.  Warning, there's also an
 * android.transition.Scene class; maybe this should've been named something
 * else.
 */
public class TileScene implements Scene, View.OnTouchListener {

    private static final String LOGBIT = "ELEE!";

    public enum SpeedType {
        Ascending,
        Oscillating,
        Boomerang
    };

    /**
     * This is one rectangle on the screen.
     */
    private class Cell {
        // True if this is supposed to be clicked on, false if it's not.
        boolean correct;
        boolean hasBeenClicked;
        private Paint paint;

        /**
         * Call to reuse/reinitialize a Cell.
         */
        public void reset(boolean correct) {
            this.correct = correct;
            hasBeenClicked = false;
            if (correct) {
                paint = goodUnclickedCell;
            } else {
                paint = badCell;
            }
        }

        /**
         * Call this when the cell is clicked on, unless it's not correct.
         */
        public void clicked() {
            if (!correct) return;
            if (hasBeenClicked == false) {
                paint = goodClickedCell;
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
    }

    private class Row {
        public Row(int columns) {
            cells = new Cell[columns];
            for (int column = 0; column < columns; ++column) {
                cells[column] = new Cell();
            }
        }
        int height;  //  in pixels
        Cell[] cells;  //  this is our list of cells

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
                    cells[ii].setPaint(failedCell);
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
                canvas.drawRect(youGotRect, cells[ii].paint);
                youGotRect.left += colWidth;
                youGotRect.right += colWidth;
            }
            //  now draw the top line
            if (currentRowTop >= 0) {
                canvas.drawLine(0, currentRowTop, colWidth * cells.length, currentRowTop, cellBox);
            }
        }
////            int ballColor = 0xff000000;
////            ballColor |= ((128 + (updateCount % 128)) << 16);
////ballPaint.setColor(ballColor);
//
//        //  draw them back-to-front.
//            for (int ii = balls.size() - 1; ii >= 0; --ii) {
//            balls.get(ii).draw(canvas);
//        }
    }


    //  Unfortunately, we need a TileActivity for startActivityForResult()
    private TileActivity context;

    //  In case we're applying gravity etc. on one thread & rendering on another,
    //  we synchronize on this so that the scene isn't changing *while* we're
    //  drawing it.
    private Integer sceneLock = new Integer(666);
    private int updateCount = 0;  //  the total number of updates; may roll.

    int columns = 4;
    int speed = 8;
    SpeedAdjuster speedAdjuster;
    int topVisibleRow = 0;
    int topVisibleRowOffset = 0;
    int score;

//    private float gravity = 10f;
//    private boolean openTopped = true;
    private int height;
    private int width;

    private int updatesThisSecond = 0;
    private int currentUpdateSecond = 0;

    private Paint badCell = new Paint();
    //  bad click, or good cell which is escaping off the bottom
    private Paint failedCell = new Paint();
    private Paint goodUnclickedCell = new Paint();
    private Paint goodClickedCell = new Paint();
    private Paint cellBox = new Paint();
    private Random rand = new Random();


//    //  We could have one Paint per Ball, or pass separate Paint objects into
//    //  Ball.draw(Canvas), but in case we want to share the same Paint object
//    //  among multiple Balls, here it is.
//    private Paint ballPaint = new Paint();

    //  The list of balls we're keeping track of.  This is in Z-order; the first
    //  ball in the list will be painted last, making it look like it's in front.
    private ArrayList<Row> rows = new ArrayList<Row>();

    public TileScene(TileActivity context, int columns, SpeedType speedType) {
        this.context = context;
        this.columns = columns;
//        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball);
//        Bitmap bm2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lucifer1);
//        Bitmap bm3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lucifer2);
        badCell.setColor(context.getResources().getColor(R.color.badCellColor));
        failedCell.setColor(context.getResources().getColor(R.color.failedCellColor));
        goodClickedCell.setColor(context.getResources().getColor(R.color.goodClickedCellColor));
        goodUnclickedCell.setColor(context.getResources().getColor(R.color.goodUnclickedCellColor));
        badCell.setAlpha(255);
        failedCell.setAlpha(255);
        goodClickedCell.setAlpha(255);
        goodUnclickedCell.setAlpha(255);
        cellBox.setStrokeWidth(2f);
        cellBox.setColor(Color.BLACK);
        cellBox.setAlpha(255);

        if (speedType.equals(SpeedType.Oscillating)) {
            speedAdjuster = new OscillatingSpeed(speed);
        } else {
            speedAdjuster = new AscendingSpeed();
        }

//        balls.add(new ImageBall(bm2, ballPaint));
////        balls.add(new Ball(ballPaint));
//        //  let's make this one use an image.
//        balls.add(new ImageBall(bm, ballPaint));
////        balls.add(new Ball(ballPaint));
//        balls.add(new ImageBall(bm2, ballPaint));
//        balls.add(new ImageBall(bm3, ballPaint));
    }

    @Override
    public float getGravity() {
        return 0f;
    }
    @Override
    public void setGravity(float dy) {
    }

    @Override
    public boolean isOpenTopped() {
        return false;
    }
    @Override
    public void setOpenTopped(boolean set) {
    }

    @Override
    public int getScreenH() {
        return height;
    }
    @Override
    public int getScreenW() {
        return width;
    }

    @Override
    public void setViewSize(int width, int height) {
        this.height = height;
        this.width = width;
        rows = new ArrayList<Row>(5);
        for (int ii = 0; ii < 5; ++ii) {
            rows.add(new Row(columns));
            rows.get(ii).setHeight(height / 4);
            //  this is garbage, but it'll initialize with a diagonal pattern
            rows.get(ii).reset(ii);
        }
        //  Let's make the last cell in the last row a bad cell, so that they
        //  don't have to tap the last row when the game starts up.
        rows.get(rows.size() - 2).cells[columns - 1].reset(false);

//        int minD = (width < height) ? width : height;
//        synchronized (sceneLock) {
//            //  just some assorted initial positions.
//            for (int ii = 0; ii < balls.size(); ++ii) {
//                Ball ball = balls.get(ii);
//                ball.setRadius((minD / 20f) * (ii + 1));
//                ball.setPosition(width / 2f, height / 2f);
//                ball.setVelocity(10 * (ii + 1), 0);
//            }
//        }
    }

    @Override
    public void accelerateEverything(float dx, float dy) {
//        synchronized (sceneLock) {
//            for (int ii = 0; ii < balls.size(); ++ii) {
//                balls.get(ii).adjustVelocity(dx, dy);
//            }
//        }
    }

    @Override
    public void update(int width, int height) {
        ++updateCount;

        //  Let's gather some data on how often this is getting called.
        //  Note that we're also increasing the speed in here!
        long now = System.currentTimeMillis();
        int nowSecond = (int)(now / 1000L);
        if (nowSecond != currentUpdateSecond) {
            //  we've crossed from one second to the next, so print some stuff.
            Log.d(LOGBIT, updatesThisSecond + " updates this second, now == " + now);
            currentUpdateSecond = nowSecond;
            updatesThisSecond = 1;  //  reset it
            if (speed != 0) {  //  if it's 0. we haven't started moving yet
                speed = speedAdjuster.adjustSpeed(speed);
            }
        } else {
            ++updatesThisSecond;
        }

        topVisibleRowOffset += speed;
        if (topVisibleRowOffset > 0) {
            //  That means we need to switch to a new topVisibleRow, reset() it
            //  with a random good-tile, and we need to adjust
            //  topVisibleRowOffest back up above the top of the screen
            //  (making it negative).
            //  Somewhere in here, we also need to check to see whether the
            //  row which is falling off the bottom has any unclicked good cells.

            //  What's the last visible row?
            int bottomVisibleRow = topVisibleRow - 1;
            if (bottomVisibleRow < 0) {
                bottomVisibleRow = rows.size() - 1;
            }
            if (rows.get(bottomVisibleRow).hasUnclickedGoodCells()) {
                //  put the topVisibleRowOffset back where it was
                topVisibleRowOffset -= speed;
                rows.get(bottomVisibleRow).setUnclickedGoodRowsFailed();
                gameOver();
                return;
            }

            if (topVisibleRow == 0) {
                topVisibleRow = rows.size() - 1;
            } else {
                --topVisibleRow;
            }
            Row row = rows.get(topVisibleRow);
            row.reset(rand.nextInt(row.cells.length));
            topVisibleRowOffset -= row.height;
        }


//        this.height = height;
//        this.width = width;
//        synchronized (sceneLock) {
//            for (int ii = 0; ii < balls.size(); ++ii) {
//                balls.get(ii).applyGravity(this);
//            }
//        }
    }

    private void gameOver() {
        //  ribbet!
        Intent intent = new Intent(context, GameOverActivity.class);
        intent.putExtra(StartGameActivity.INTENT_SCORE, score);
        intent.putExtra(StartGameActivity.INTENT_SPEED, speed);
//XXX this needs to be fixed; we need to send the speed type.
//        intent.putExtra(StartGameActivity.INTENT_SPEED_TYPE, speedType);
        context.startActivityForResult(intent, StartGameActivity.RESULT_GAME_DONE);
        speed = 0;
    }

    @Override
    public void draw(Canvas canvas) {
        int colWidth = width / columns;
        synchronized (sceneLock) {
            int currentRowTop = topVisibleRowOffset;
            for (int ii = 0; ii < rows.size(); ++ii) {
                Row row = rows.get((ii + topVisibleRow) % rows.size());
                row.draw(canvas, currentRowTop, colWidth);
                currentRowTop += row.height;
            }
        }
        //Now draw the vertical lines
        //  because we want to handle an arbitrary number of columns, let's
        //  do this in a loop.
        for (int xpos = colWidth; xpos < width; xpos += colWidth) {
            canvas.drawLine(xpos, 0, xpos, height, cellBox);
        }
    }

//    private Ball touchingBall = null;
//    private float lastMoveX;
//    private float lastMoveX2;  //  keeping just the last move wasn't good enough

    /**
     * OnTouchListener method.
     */
    @Override
    public boolean onTouch(View view, MotionEvent ev) {
Log.d(LOGBIT, "onTouch(view, " + ev + ")");
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            //  how many places are they touching?
            //  For each one:
            //  - figure out what cell was under that point
            //  - decide whether it's good or bad
            //  - "do something"
            for (int pointer = 0; pointer < ev.getPointerCount(); ++pointer) {
                float touchX = ev.getX(pointer);
                float touchY = ev.getY(pointer);
                //  now we know where this pointer is touching.  Find the
                //  rectangle at that point!
                int currentRow = topVisibleRow;
                int currentRowBottom = topVisibleRowOffset;
                while (currentRowBottom <= height) {
                    Row row = rows.get(currentRow);
                    currentRowBottom += row.height;
                    if (touchY <= currentRowBottom) {
                        //  this is our row!
                        //  Find out the cell number.
                        int cellNumber = (int)(touchX / (width / row.cells.length));
                        Cell cell = row.cells[cellNumber];
                        if (cell.okToClick()) {
                            score++;
                            cell.clicked();
                            //  and increment the score?
//Log.d(LOGBIT, "GOOD CLICK ROW " + currentRow + ", CELL " + cellNumber);
                        } else {
                            //this is how we know the wrong tile was clicked
                            cell.setPaint(failedCell);
                            gameOver();
//throw new RuntimeException("FAIL");
                        }
                        currentRowBottom = height + 1;  //  break out of while loop
                        break;
                    }
                    currentRow = (currentRow + 1) % rows.size();
                }

            }
//            touchingBall = null;
//            for (int ii = 0; ii < balls.size(); ++ii) {
//                Ball ball = balls.get(ii);
//                if (ball.contains(x, y)) {  //  probably should synchronize on sceneLock
////Log.d("Scene", "YOU'RE TOUCHING MY BALL " + ball);
//                    touchingBall = ball;
//                    lastMoveX = lastMoveX2 = x;
                    return true;
//                }
//            }
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
//            lastMoveX2 = lastMoveX;
//            lastMoveX = ev.getX();
            return true;
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
//            if (touchingBall != null) {
//                synchronized (sceneLock) {
//                    touchingBall.adjustVelocity((ev.getX() - lastMoveX2), -150);
//                }
//                touchingBall = null;
                return true;
//            }
        }
        return false;
    }

}
