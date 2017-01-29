package com.scottrealapps.calculater.d2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.scottrealapps.calculater.R;

import java.util.ArrayList;

/**
 * This manages all the objects we're looking at.  Warning, there's also an
 * android.transition.Scene class; maybe this should've been named something
 * else.
 */
public class Scene implements View.OnTouchListener {
    //  In case we're applying gravity etc. on one thread & rendering on another,
    //  we synchronize on this so that the scene isn't changing *while* we're
    //  drawing it.
    private Integer sceneLock = new Integer(666);
    private int updateCount = 0;  //  the total number of updates; may roll.

    private float gravity = 10f;

    //  We could have one Paint per Ball, or pass separate Paint objects into
    //  Ball.draw(Canvas), but in case we want to share the same Paint object
    //  among multiple Balls, here it is.
    private Paint ballPaint = new Paint();

    //  The list of balls we're keeping track of.  This is in Z-order; the first
    //  ball in the list will be painted last, making it look like it's in front.
    private ArrayList<Ball> balls = new ArrayList<Ball>();

    public Scene(Context context) {
//        Drawable someImage = getResources().getDrawable(R.drawable.)
        ballPaint.setColor(context.getResources().getColor(R.color.ballColor));
        ballPaint.setAlpha(255);

        balls.add(new Ball(ballPaint));
        balls.add(new Ball(ballPaint));
        balls.add(new Ball(ballPaint));
    }

    public void setViewSize(int width, int height) {
        int minD = (width < height) ? width : height;
        synchronized (sceneLock) {
            //  just some assorted initial positions.
            for (int ii = 0; ii < balls.size(); ++ii) {
                Ball ball = balls.get(ii);
                ball.setRadius((minD / 20f) * (ii + 1));
                ball.setPosition(width / 2f, height / 2f);
                ball.setVelocity(10 * (ii + 1), 0);
            }
        }
    }

    public void update(int width, int height) {
        ++updateCount;
        synchronized (sceneLock) {
            for (int ii = 0; ii < balls.size(); ++ii) {
                balls.get(ii).applyGravity(gravity, width, height);
            }
        }
    }

    public void draw(Canvas canvas) {
        synchronized (sceneLock) {
//            int ballColor = 0xff000000;
//            ballColor |= ((128 + (updateCount % 128)) << 16);
//ballPaint.setColor(ballColor);

            //  draw them back-to-front.
            for (int ii = balls.size() - 1; ii >= 0; --ii) {
                balls.get(ii).draw(canvas);
            }
        }
    }

    private Ball touchingBall = null;
    private float lastMoveX;
    private float lastMoveX2;  //  keeping just the last move wasn't good enough

    /**
     * OnTouchListener method.
     */
    @Override
    public boolean onTouch(View view, MotionEvent ev) {
//Log.d("Scene", "onTouch(view, " + ev + ")");
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            touchingBall = null;
            float x = ev.getX();
            float y = ev.getY();
            for (int ii = 0; ii < balls.size(); ++ii) {
                Ball ball = balls.get(ii);
                if (ball.contains(x, y)) {  //  probably should synchronize on sceneLock
//Log.d("Scene", "YOU'RE TOUCHING MY BALL " + ball);
                    touchingBall = ball;
                    lastMoveX = lastMoveX2 = x;
                    return true;
                }
            }
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            lastMoveX2 = lastMoveX;
            lastMoveX = ev.getX();
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (touchingBall != null) {
                synchronized (sceneLock) {
                    touchingBall.adjustVelocity((ev.getX() - lastMoveX2), -150);
                }
                touchingBall = null;
                return true;
            }
        }
        return false;
    }

}
