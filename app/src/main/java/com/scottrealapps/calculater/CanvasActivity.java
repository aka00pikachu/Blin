package com.scottrealapps.calculater;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;

/**
 * This Activity contains either a A2DView or a Another2DView,depending on
 * arguments passed in.
 */
public class CanvasActivity extends AppCompatActivity {
    //  well, this is mis-named...
    public static final String INTENT_DOUBLE_BUFFER = "doubleBuffer";

    //  This runs the timer which calls MyView.updateScene() regularly.
    //  Copied from SnakeView.java under <SDL>/samples/android-21/legacy/Snake.
    //  THIS CAN BE DELETED if we get rid of A2DView.
    private class AnimationHandler extends Handler {
        //  this controls how fast we'll update our scene: sleeping 100ms between
        //  runs = about 10 times per second.
        private long sleepMS = 33L;
        private final A2DView view;
        AnimationHandler(A2DView view) {
            this.view = view;
        }

        @Override
        public void handleMessage(Message msg) {
            view.updateScene();
            view.invalidate();  //  OK?  not postInvalidate() ?
            sleep(sleepMS);
        }
        public void sleep(long sleepMS) {
            removeMessages(0);
            sendMessageDelayed(obtainMessage(0), sleepMS);
        }
    }
    private AnimationHandler animationTimer;// = new AnimationHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if ((intent != null) && (intent.getBooleanExtra(INTENT_DOUBLE_BUFFER, false))) {
            setContentView(R.layout.activity_canvas2);
            Another2DView view = (Another2DView)(findViewById(R.id.myView));
            //  This logic here copied from
            //  http://www.edu4java.com/en/androidgame/androidgame2.html and
            //  http://www.edu4java.com/en/androidgame/androidgame3.html
            final SceneUpdateThread sceneUpdateThread = new SceneUpdateThread(view);
            view.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    sceneUpdateThread.setRunning(true);
                    sceneUpdateThread.start();
                }
                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                }
                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    boolean retry = true;
                    sceneUpdateThread.setRunning(false);
                    while (retry) {
                        try {
                            sceneUpdateThread.join();
                            retry = false;
                        } catch (InterruptedException e) {
                        }
                    }
                }
            });
        } else {
            setContentView(R.layout.activity_canvas);
            A2DView view = (A2DView)(findViewById(R.id.myView));
            animationTimer = new AnimationHandler(view);
            animationTimer.handleMessage(null);  //  start the animation running
        }
    }
}
