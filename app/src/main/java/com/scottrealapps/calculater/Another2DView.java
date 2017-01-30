package com.scottrealapps.calculater;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

import com.scottrealapps.calculater.d2.Scene;

/**
 * This is a slightly different approach at a 2D view.  Both this and A2DView
 * let the Scene handle the positions of the various objects.
 */
public class Another2DView extends SurfaceView {

    private Scene scene;

    public Another2DView(Context context) {
        super(context);
        initStuff(context);
    }
    public Another2DView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initStuff(context);
    }

    private void initStuff(Context context) {
        scene = new Scene(context);
    }

    public Scene getScene() {
        return scene;
    }

    /**
     * In the base class, this adds an OnTouchListener.
     */
    public void setupListeners() {
        setOnTouchListener(scene);
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
Log.d("Another2DView", "onLayout(" + changed + ", left " + left + ", top " + top + ", right " + right + ", bottom " + bottom + ") hit!");
        super.onLayout(changed, left, top, right, bottom);
//        buf = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        scene.setViewSize(right - left, bottom - top);
    }

    /**
     * This is where we apply gravity etc.  This does not call
     * invalidate()/postInvalidate()/onDraw() etc.
     */
    public void updateScene() {
        scene.update(getWidth(), getHeight());
    }

    @Override
    public void onDraw(Canvas canvas) {
        //  Unlike A2DView, which repaints its own background before every
        //  onDraw() call, this one has to fill in every pixel in the Canvas.
        canvas.drawColor(Color.BLACK);
        scene.draw(canvas);
    }
}
