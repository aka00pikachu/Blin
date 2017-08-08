package com.scottrealapps.calculater;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.scottrealapps.calculater.d2.BouncyScene;

/**
 * This is one approach at a 2D view.
 */
public class A2DView extends View {

    private BouncyScene scene;

    public A2DView(Context context) {
        super(context);
        initStuff(context);
    }
    public A2DView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initStuff(context);
    }

    private void initStuff(Context context) {
        scene = new BouncyScene(context);
        setOnTouchListener(scene);
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
Log.d("A2DView", "onLayout(" + changed + ", left " + left + ", top " + top + ", right " + right + ", bottom " + bottom + ") hit!");
        super.onLayout(changed, left, top, right, bottom);
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
        scene.draw(canvas);
    }
}
