package com.scottrealapps.calculater.d2;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.scottrealapps.calculater.GameOverActivity;
import com.scottrealapps.calculater.R;
import com.scottrealapps.calculater.StartGameActivity;
import com.scottrealapps.calculater.TileActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * In this one, we're maintaining a variable list of rows, and possibly
 * deleting rows from the middle of the list as they get clicked on.
 */
public class TimeTileScene implements Scene, View.OnTouchListener {

    private static final String LOGBIT = "ELEE!";

    private static class ShrinkyRow extends Row {
        private boolean shrinking = false;
        public ShrinkyRow(int columns, CellTheme theme) {
            super(columns, theme);
        }
    }

    //  Unfortunately, we need a TileActivity for startActivityForResult()
    private TileActivity context;

    //  In case we're applying gravity etc. on one thread & rendering on another,
    //  we synchronize on this so that the scene isn't changing *while* we're
    //  drawing it.
    private Integer sceneLock = new Integer(666);
    private int updateCount = 0;  //  the total number of updates; may roll.

    int allowedTime;  //  seconds
    long startTime = 0;  //  we'll set this the first time a tile is tapped on
    long timeRemaining;  //  milliseconds
    int columns = 4;
    int speed = 20;  //  the speed at which a row collapses.
    int score;

//    private float gravity = 10f;
//    private boolean openTopped = true;
    private int height;
    private int width;

    private int updatesThisSecond = 0;
    private int currentUpdateSecond = 0;

    private CellTheme theme = new CellTheme();
    private Random rand = new Random();
    private DecimalFormat timeRemainingFormat = new DecimalFormat("0.0");

    private ArrayList<ShrinkyRow> rows = new ArrayList<>();

    public TimeTileScene(TileActivity context, int columns, int allowedTime) {
        this.context = context;
        this.columns = columns;
        this.allowedTime = allowedTime;
        timeRemaining = allowedTime * 1000;
//        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball);
//        Bitmap bm2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lucifer1);
//        Bitmap bm3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lucifer2);
        theme.badCell.setColor(context.getResources().getColor(R.color.badCellColor));
        theme.failedCell.setColor(context.getResources().getColor(R.color.failedCellColor));
        theme.goodClickedCell.setColor(context.getResources().getColor(R.color.goodClickedCellColor));
        theme.goodUnclickedCell.setColor(context.getResources().getColor(R.color.goodUnclickedCellColor));
        theme.badCell.setAlpha(255);
        theme.failedCell.setAlpha(255);
        theme.goodClickedCell.setAlpha(255);
        theme.goodUnclickedCell.setAlpha(255);
        theme.cellBox.setStrokeWidth(2f);
        theme.cellBox.setColor(Color.BLACK);
        theme.cellBox.setAlpha(255);
        theme.timeRemaining.setTypeface(Typeface.DEFAULT_BOLD);
//        theme.timeRemaining.setTextAlign(Align.);
        theme.timeRemaining.setColor(Color.GRAY);
        theme.timeRemaining.setAlpha(127);
        theme.timeRemaining.setAntiAlias(true);
        theme.timeRemaining.setTextSize(200);
    }

    /**
     * If this is false, the user is just sitting there staring at the screen.
     */
    public boolean isGameStarted() {
        return startTime != 0L;
    }
    public boolean isGameOver() {
        return timeRemaining <= 0L;
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
        rows = new ArrayList<>();
        for (int ii = 0; ii < 4; ++ii) {
            rows.add(new ShrinkyRow(columns, theme));
            rows.get(ii).setHeight(height / 4);
            //  this is garbage, but it'll initialize with a diagonal pattern
            rows.get(ii).reset(ii);
        }

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
        } else {
            ++updatesThisSecond;
        }

        //  Is the game over?
        if (isGameOver() || (!isGameStarted())) {
            return;
        }

        timeRemaining = (startTime + (allowedTime * 1000)) - now;
//Log.d("ELEE", "" + timeRemaining + "ms remaining!");
        if (isGameOver()) {
            gameOver();
            return;
        }

        //  This is the amount by which we're moving the current row down the
        //  screen.  It will be 0 until we hit a row which is shrinking; at that
        //  point, it will increase by "speed", and will stay that way until we
        //  hit the next shrinking row.
        int topOfTopRow = height;
        //  We want to start with the last row on the screen.
        for (int ii = rows.size() - 1; ii >= 0; --ii) {
            ShrinkyRow row = rows.get(ii);
            boolean removed = false;
            if (row.shrinking) {
                row.setHeight(row.getHeight() - speed);
                if (row.getHeight() <= 0) {
                    //  now it's invisible; remove it from the list!
                    rows.remove(ii);
//XXX now, one cool thing we could do is add this to some list of unused rows,
//and reuse it below instead of throwing it away & creating a new one.
                    removed = true;
                }
            }
            if (!removed) {
                topOfTopRow -= row.getHeight();
            }
        }
        //  Now, we've run through all of the rows in our list; is there space
        //  at the top of the screen?
        while (topOfTopRow > 0) {
            ShrinkyRow newRow = new ShrinkyRow(columns, theme);
            newRow.setHeight(height / 4);
            newRow.reset(rand.nextInt(newRow.getCellCount()));
            rows.add(0, newRow);
            topOfTopRow -= newRow.getHeight();
        }
    }

    private void gameOver() {
        timeRemaining = 0;
        //  ribbet!
        Intent intent = new Intent(context, GameOverActivity.class);
        intent.putExtra(StartGameActivity.INTENT_GAME_ID, "tt" + allowedTime);
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
            int currentRowTop = height;
            for (int ii = rows.size() - 1; ii >= 0; --ii) {
                ShrinkyRow row = rows.get(ii);
                currentRowTop -= row.getHeight();
                row.draw(canvas, currentRowTop, colWidth);
            }
        }
        //Now draw the vertical lines
        //  because we want to handle an arbitrary number of columns, let's
        //  do this in a loop.
        for (int xpos = colWidth; xpos < width; xpos += colWidth) {
            canvas.drawLine(xpos, 0, xpos, height, theme.cellBox);
        }

        //  Draw the time remaining
        float sr = timeRemaining / 1000.0f;
        String ts = timeRemainingFormat.format(sr);
//Log.d("ELEE", "timeRemaining " + timeRemaining + ", ts " + ts);
        canvas.drawText(ts, 20, theme.timeRemaining.getTextSize() + 20, theme.timeRemaining);
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
                int currentRowTop = height;
                for (int ii = rows.size() -1; ii >= 0; --ii) {
                    ShrinkyRow row = rows.get(ii);
                    currentRowTop -= row.getHeight();
                    if (touchY >= currentRowTop) {
                        //  this is our row!
                        //  Find out the cell number.
                        int cellNumber = (int)(touchX / (width / row.getCellCount()));
                        Cell cell = row.getCell(cellNumber);
                        if (cell.okToClick()) {
                            score++;
                            cell.clicked();
                            row.shrinking = true;
                            //  and increment the score?
                            //  If the clock isn't running... start it!
                            if (startTime == 0) {
                                startTime = System.currentTimeMillis();
                            }
                        } else {
                            //this is how we know the wrong tile was clicked
                            cell.setPaint(theme.failedCell);
                            gameOver();
                        }
                        break;
                    }
                }

            }
            return true;
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
