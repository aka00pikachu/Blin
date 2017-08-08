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
    public void setViewSize(int width, int height);
    public void accelerateEverything(float dx, float dy);
    public void update(int width, int height);
    public void draw(Canvas canvas);
}
