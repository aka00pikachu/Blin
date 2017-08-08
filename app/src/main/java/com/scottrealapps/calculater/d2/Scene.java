package com.scottrealapps.calculater.d2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import com.scottrealapps.calculater.R;

import java.util.ArrayList;

/**
 * This manages all the objects we're looking at.  Warning, there's also an
 * android.transition.Scene class; maybe this should've been named something
 * else.
 */
public interface Scene {
    /**
     * Gets the acceleration toward the bottom of the screen applied every time
     * we update.  Probably defaults to 10.
     */
    public float getGravity();
    public void setGravity(float dy);
    /**
     * Returns true if objects should be allowed to fly off the top of the
     * screen, false if not.
     */
    public boolean isOpenTopped();
    public void setOpenTopped(boolean set);
    public int getScreenH();
    public int getScreenW();
    public void setViewSize(int width, int height);
    public void accelerateEverything(float dx, float dy);
    public void update(int width, int height);
    public void draw(Canvas canvas);
}
