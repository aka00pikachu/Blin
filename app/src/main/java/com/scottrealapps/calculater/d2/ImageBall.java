package com.scottrealapps.calculater.d2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * A ball which draws itself using an image.
 */
public class ImageBall extends Ball {

    private Bitmap original;
    private Bitmap scaled;

    /**
     * If you don't call setRadius(), the given bitmap will be used unscaled.
     */
    public ImageBall(Bitmap bitmap, Paint paint) {
        super(paint);
        this.original = bitmap;
    }

    /**
     * This also scales the image.
     */
    @Override
    public void setRadius(float radius) {
        super.setRadius(radius);

        float diameter = radius * 2;
        //  let's keep the original aspect ratio.
        float mul = diameter / ((original.getHeight() > original.getWidth()) ?
                original.getHeight() : original.getWidth());
        scaled = Bitmap.createScaledBitmap(original, (int)(original.getWidth() * mul),
                (int)(original.getHeight() * mul), false);
    }

    /**
     * Draws this Ball using a scaled image instead of a circle.
     */
    @Override
    public void draw(Canvas canvas) {
        if (scaled == null) {
            scaled = original;  //  hmmm
        }
        canvas.drawBitmap(scaled, xpos - radius, ypos - radius, paint);
    }
}
