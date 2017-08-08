package com.scottrealapps.calculater.d2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.scottrealapps.calculater.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This manages all the objects we're looking at.  Warning, there's also an
 * android.transition.Scene class; maybe this should've been named something
 * else.
 */
public class CrimeScene implements Scene, View.OnTouchListener, MediaPlayer.OnCompletionListener {
    private final int MAX_HOLES = 50;
    /**
     * The number of holes we'll distribute the fade over.
     */
    private final int HOLE_FADE = 10;

    private final int POINTS_PER_GOOD_HIT = 3;
    private final int POINTS_PER_BAD_HIT = -5;
    private final int POINTS_PER_GOOD_ESCAPE = -2;
    private final int POINTS_PER_BAD_ESCAPE = 1;

    public interface ScoreListener {
        void scoreChanged(CrimeScene scene);
    };
    private Context context;

    private ReentrantReadWriteLock sceneLock = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock readLock = sceneLock.readLock();
    private ReentrantReadWriteLock.WriteLock writeLock = sceneLock.writeLock();
    //  In case we're applying gravity etc. on one thread & rendering on another,
    //  we synchronize on this so that the scene isn't changing *while* we're
    //  drawing it.
//    private Integer sceneLock = new Integer(666);
    private int updateCount = 0;  //  the total number of updates; may roll.
    private int panicLevel = 15;
    private int score = 0;
    private Random rand = new Random();
    private ArrayList<ScoreListener> scoreListeners = new ArrayList<ScoreListener>(2);

    private Set<MediaPlayer> activePlayers = Collections.synchronizedSet(new HashSet<MediaPlayer>());

    private Bitmap lucifer1;
    private Bitmap lucifer2;
    private Paint targetPaint = new Paint();

//    private float gravity = 10f;
    private int height;
    private int width;

    //  We could have one Paint per Ball, or pass separate Paint objects into
    //  Ball.draw(Canvas), but in case we want to share the same Paint object
    //  among multiple Balls, here it is.
    private Paint holePaint = new Paint();
    private Paint[] holeFadePaint = new Paint[HOLE_FADE];

    private Paint flashPaint1 = new Paint();
    private Paint flashPaint2 = new Paint();
    private Paint flashPaint3 = new Paint();
    private Paint[] flashPaints = new Paint[] {
            flashPaint1,
            flashPaint1,
            flashPaint1,
            flashPaint1,
            flashPaint2,
            flashPaint2,
            flashPaint2,
            flashPaint2,
            flashPaint2,
            flashPaint3,
            flashPaint3,
            flashPaint3,
            flashPaint3,
            flashPaint3,
            flashPaint3,
            null
    };

    private enum TargetType {
        Good, Bad, None;
    };
    private static class Target {
        private Ball ball;
        private TargetType type;
        public Target(Ball ball, TargetType type) {
            this.ball = ball;
            this.type = type;
        }
    }

    //  The list of balls we're keeping track of.  This is in Z-order; the first
    //  ball in the list will be painted last, making it look like it's in front.
    private ArrayList<Ball> holes = new ArrayList<Ball>(MAX_HOLES);
    private int oldestHole = -1;
    private ArrayList<Flash> flashes = new ArrayList<Flash>();
    private int oldestFlash = -1;
    private int newestFlash = -1;
    //  List of moving targets
    private ArrayList<Target> targets = new ArrayList<Target>();

    public CrimeScene(Context context) {
        this.context = context;
        lucifer1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lucifer1);
        lucifer2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lucifer2);
        targetPaint.setColor(context.getResources().getColor(R.color.ballColor));
        targetPaint.setAlpha(255);

        holePaint.setColor(0xff000000);
        flashPaint1.setColor(0xffffffff);
        flashPaint2.setColor(0x80ff0000);
        flashPaint3.setColor(0x10000000);
        for (int ii = 0; ii < holeFadePaint.length; ++ii) {
            holeFadePaint[ii] = new Paint();
            holeFadePaint[ii].setAlpha(255 - ((255 / HOLE_FADE) * ii));
        }
    }

    public void addScoreListener(ScoreListener sl) {
        if (!scoreListeners.contains(sl)) scoreListeners.add(sl);
    }
    public int getScore() {
        return score;
    }
    public int getShotCount() {
        return shotCount;
    }
    public int getGoodHits() {
        return goodHits;
    }
    public int getBadHits() {
        return badHits;
    }
    public int getGoodEscapes() {
        return goodEscapes;
    }
    public int getBadEscapes() {
        return badEscapes;
    }
    private int shotCount = 0;
    private int goodHits = 0;
    private int badHits = 0;
    private int goodEscapes = 0;
    private int badEscapes = 0;

    /**
     * Call this in your activity onStop().
     */
    public void onStop() {
        for (MediaPlayer mp : activePlayers) {
            mp.release();
        }
        activePlayers.clear();
    }

    //  MediaPlayer.OnCompletionListener
    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.release();
        activePlayers.remove(mp);
    }

    private void playSound(int resID) {
        MediaPlayer mp = MediaPlayer.create(context, resID);
        activePlayers.add(mp);
        mp.setOnCompletionListener(this);
        //mp.setOnErrorListener(this);
        mp.start();
    }

    /**
     * Overridden to return 0.
     */
    @Override
    public float getGravity() { return 0f; }
    /**
     * Overridden to do nothing.
     */
    @Override
    public void setGravity(float dy) { }
    /**
     * Returns false.
     */
    @Override
    public boolean isOpenTopped() { return false; }
    /**
     * Overridden to do nothing.
     */
    @Override
    public void setOpenTopped(boolean set) { }

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
//        int minD = (width < height) ? width : height;
        try {
            writeLock.lock();
//            //  just some assorted initial positions.
//            for (int ii = 0; ii < balls.size(); ++ii) {
//                Ball ball = balls.get(ii);
//                ball.setRadius((minD / 20f) * (ii + 1));
//                ball.setPosition(width / 2f, height / 2f);
//                ball.setVelocity(10 * (ii + 1), 0);
//            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void accelerateEverything(float dx, float dy) {
        try {
            writeLock.lock();
//            for (int ii = 0; ii < balls.size(); ++ii) {
//                balls.get(ii).adjustVelocity(dx, dy);
//            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void update(int width, int height) {
        this.height = height;
        this.width = width;
        ++updateCount;
        try {
            writeLock.lock();
            for (int ii = 0; ii < targets.size(); ++ii) {
                Target tt = targets.get(ii);
                if (tt != null) {
                    tt.ball.applyVelocity();
                    if (!tt.ball.intersects(0, 0, width, height)) {
                        //  Target has left the screen; remove it.
                        targets.set(ii, null);
                        if (tt.type == TargetType.Good) {
                            ++goodEscapes;
                            adjustScore(POINTS_PER_GOOD_ESCAPE);
                        } else if (tt.type == TargetType.Bad) {
                            ++badEscapes;
                            adjustScore(POINTS_PER_BAD_ESCAPE);
                        }
                    }
                }
            }
            if ((updateCount % panicLevel) == 0) {
                boolean added = false;
                for (int ii = 0; ii < targets.size(); ++ii) {
                    if (targets.get(ii) == null) {
                        targets.set(ii, createTarget(width, height, ii));
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    targets.add(createTarget(width, height, targets.size()));
                }
            }
//            for (int ii = 0; ii < balls.size(); ++ii) {
//                balls.get(ii).applyGravity(gravity, width, height);
//            }
            for (int ii = 0; ii < flashes.size(); ++ii) {
                flashes.get(ii).update();
            }
//            if ((updateCount % 30) == 0) {
//                addTarget();
//            }
        } finally {
            writeLock.unlock();
        }
    }

    private Target createTarget(int width, int height, int zpos) {
        int minD = (width < height) ? width : height;
        int startingPosition = rand.nextInt((width * 2) + (height * 2));
        TargetType tt = null;
        Ball tb = null;
        if ((startingPosition % 2) == 0) {
            tt = TargetType.Bad;
            tb = new ImageBall(lucifer1, targetPaint);
        } else {
            tt = TargetType.Good;
            tb = new ImageBall(lucifer2, targetPaint);
        }
        float tr = (minD / 10f) - (5 * zpos);
        tb.setRadius(tr > 20f ? tr : 20f);  //  let's say 20f is the minimum target size
        int startingVelocity = 5 * (rand.nextInt(15) + 1);
        float initialOffset = 10f;
        if (startingPosition < width) {
            //  top
            tb.setPosition(startingPosition, initialOffset - tb.getRadius());
            tb.setVelocity(0f, startingVelocity);
        } else if (startingPosition < (width + height)) {
            //  right
            tb.setPosition(width - initialOffset + tb.getRadius(), startingPosition - width);
            tb.setVelocity(-startingVelocity, 0f);
        } else if (startingPosition < (width + width + height)) {
            //  bottom
            tb.setPosition(startingPosition - width - height, height - initialOffset + tb.getRadius());
            tb.setVelocity(0f, -startingVelocity);
        } else {
            //  left
            tb.setPosition(initialOffset - tb.getRadius(), startingPosition - width - width - height);
            tb.setVelocity(startingVelocity, 0f);
        }

        return new Target(tb, tt);
    }



    @Override
    public void draw(Canvas canvas) {
        try {
            readLock.lock();
//            int ballColor = 0xff000000;
//            ballColor |= ((128 + (updateCount % 128)) << 16);
//ballPaint.setColor(ballColor);

            //  draw them back-to-front.

            //  holes
            if (holes.size() < MAX_HOLES) {
                for (int ii = holes.size() - 1; ii >= 0; --ii) {
                    holes.get(ii).draw(canvas);
                }
            } else {
                for (int ii = oldestHole - 1; ii >= 0; --ii) {
                    holes.get(ii).draw(canvas);
                }
                for (int ii = holes.size() - 1; ii >= oldestHole; --ii) {
                    holes.get(ii).draw(canvas);
                }
            }

            //  targets
            for (int ii = targets.size() - 1; ii >= 0; --ii) {
                Target tt = targets.get(ii);
                if (tt != null) tt.ball.draw(canvas);
            }

            for (int ii = flashes.size() - 1; ii >= 0; --ii) {
                flashes.get(ii).draw(canvas);
            }
        } finally {
            readLock.unlock();
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
//Log.d("Scene", "onTouch(view, " + ev + ")");
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            shotFired(ev.getX(), ev.getY());
//            touchingBall = null;
//            float x = ev.getX();
//            float y = ev.getY();
//            for (int ii = 0; ii < balls.size(); ++ii) {
//                Ball ball = balls.get(ii);
//                if (ball.contains(x, y)) {  //  probably should synchronize on sceneLock
////Log.d("Scene", "YOU'RE TOUCHING MY BALL " + ball);
//                    touchingBall = ball;
//                    lastMoveX = lastMoveX2 = x;
//                    return true;
//                }
//            }
//        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
//            lastMoveX2 = lastMoveX;
//            lastMoveX = ev.getX();
//        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
//            if (touchingBall != null) {
//                synchronized (sceneLock) {
//                    touchingBall.adjustVelocity((ev.getX() - lastMoveX2), -150);
//                }
//                touchingBall = null;
//                return true;
//            }
        }
        return false;
    }

    private void shotFired(float fx, float fy) {
        ++shotCount;
        playSound(R.raw.shot);
        TargetType hit = null;

        try {
            writeLock.lock();

            Flash useFlash = null;
            for (int ii = 0; ii < flashes.size(); ++ii) {
                Flash tf = flashes.get(ii);
                if (!tf.isVisible()) {
                    useFlash = tf;
                }
            }
            if (useFlash == null) {
                useFlash = new Flash(flashPaints);
//XXX wrong, use display density
                useFlash.setRadius(300f);
                flashes.add(useFlash);
            }
            useFlash.setPosition(fx, fy);

            for (int ii = 0; ii < targets.size(); ++ii) {
                Target tt = targets.get(ii);
                if ((tt != null) && tt.ball.contains(fx, fy)) {
                    //  Hit target; "do stuff" other than just eliminating it.
                    targets.set(ii, null);
                    hit = tt.type;
                    if (hit == TargetType.Good) {
                        ++goodHits;
                        adjustScore(POINTS_PER_GOOD_HIT);
                    } else if (hit == TargetType.Bad) {
                        ++badHits;
                        adjustScore(POINTS_PER_BAD_HIT);
                    }
                    break;
                }
            }

            if (hit == null) {
                //  A miss, so draw a bullet hole in the background.
                if (holes.size() >= MAX_HOLES) {
                    Ball tb = holes.get(oldestHole++);
                    tb.setPosition(fx, fy);
                    tb.setPaint(holePaint);
                    if (oldestHole >= MAX_HOLES) {
                        oldestHole = 0;
                    }
//                for (int ii = 0; ii < HOLE_FADE; ++ii) {
//                    Log.d("CrimeScene", "ii " + ii + ", oldestHole " + oldestHole);
//                    Ball tb = holes.get((ii <= oldestHole) ?
//                            (oldestHole + MAX_HOLES - (ii + 1)) :
//                            (oldestHole - (ii + 1)));
//                    tb.setPaint(holeFadePaint[ii]);
//                }
                } else {
                    Ball tb = new Ball(holePaint);
                    tb.setPosition(fx, fy);
                    tb.setVelocity(0f, 0f);
//XXX wrong, use display density
                    tb.setRadius(15f);
                    holes.add(tb);
                    oldestHole = 0;
//                for (int ii = holes.size() - MAX_HOLES + HOLE_FADE; ii >= 0; --ii) {
//Log.d("CrimeScene", "ii " + ii + ", oldestHole " + oldestHole);
//                    holes.get(ii).setPaint(holeFadePaint[ii]);
//                }
                }
            }
        } finally {
            writeLock.unlock();
        }

        int soundID = R.raw.miss;
        if (hit == TargetType.Bad) {
            switch (rand.nextInt(3)) {
                case 0:  soundID = R.raw.badhit1; break;
                case 1:  soundID = R.raw.badhit2; break;
                default: soundID = R.raw.badhit3;
            }
        } else if (hit == TargetType.Good) {
            switch (rand.nextInt(3)) {
                case 0:  soundID = R.raw.goodhit1; break;
                case 1:  soundID = R.raw.goodhit2; break;
                default: soundID = R.raw.goodhit3;
            }
        }
        playSound(soundID);
    }

    private void adjustScore(int amount) {
        score += amount;
//Log.d("CrimeScene", "score now " + score + ", " + scoreListeners.size() + " listeners");
        for (int ii = scoreListeners.size() - 1; ii >= 0; --ii) {
            scoreListeners.get(ii).scoreChanged(this);
        }
    }

}
