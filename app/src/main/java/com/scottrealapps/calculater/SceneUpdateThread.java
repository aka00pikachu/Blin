package com.scottrealapps.calculater;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Pretty much copied straight from
 * http://www.edu4java.com/en/androidgame/androidgame3.html.
 *
 * <p>This is only used for Another2DView; A2DView has its updateScene()
 * method called from a Handler in CanvasActivity.
 */
public class SceneUpdateThread extends Thread {
    private long FPS = 30;  //  frames per second
    //  no matter how slow we get, we will never sleep less than 10ms per draw
    //  loop.  Naps are important, you know!
    private final long MIN_SLEEP_MS = 10;
    private final Another2DView view;
    private boolean running = false;
    private boolean paused = false;

    /**
     * Creates a new thread & adds it as a listener on the given view; when
     * the surface is created, this thread will start running.
     */
    public SceneUpdateThread(Another2DView view) {
        this.view = view;
        view.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                setRunning(true);
                start();
            }
            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                boolean retry = true;
                setRunning(false);
                while (retry) {
                    try {
                        join();
                        retry = false;
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
    }

    public void setRunning(boolean run) {
        running = run;
    }
    public void setPaused(boolean pause) {
        paused = pause;
    }

    @SuppressLint("WrongCall")
    @Override
    public void run() {
        while (running) {
//XXX this logic can't be right; it looks like we busy-wait when paused??
            if (!paused) {
                Canvas c = null;
                long startTime = 0;
                SurfaceHolder sh = view.getHolder();
                try {
                    startTime = System.currentTimeMillis();
                    c = sh.lockCanvas();
//not sure it's necessary to synchronize here; lockCanvas() javadoc says it
//holds an internal lock until the call to unlockCanvasAndPost().
//                synchronized (sh) {
                    if (c != null) {
                        view.updateScene();
                        view.onDraw(c);
                    }
//                }
                } finally {
                    if (c != null) {
                        sh.unlockCanvasAndPost(c);
                    }
                }
                long sleepTime = (1000 / FPS) - (System.currentTimeMillis() - startTime);
                try {
//Log.d("SceneUpdateThread", "sleeping " + (sleepTime > MIN_SLEEP_MS ? sleepTime : MIN_SLEEP_MS) + "ms");
                    sleep(sleepTime > MIN_SLEEP_MS ? sleepTime : MIN_SLEEP_MS);
                } catch (Exception e) {
                }
            } else {
                //  we're paused...
            }
        }
    }
}
