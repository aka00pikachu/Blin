package com.scottrealapps.calculater.d2;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

/**
 * A solid-colored ball.
 */
public class Ball {
    //  bounciness of 1.0 means it will bounce back up off the ground as fast as
    //  it landed.
    protected float bounciness = 0.9f;
    protected float xpos = 0f;  //  center
    protected float ypos = 0f;  //  center
    protected float radius = 0f;
    protected float dx = 0f;
    protected float dy = 0f;
    protected Paint paint;

    public Ball(Paint paint) {
        this.paint = paint;
    }
    public float getRadius() {
        return radius;
    }
    public void setRadius(float radius) {
        this.radius = radius;
    }
    public void setPaint(Paint paint) {
        this.paint = paint;
    }
    /**
     * The center of the ball.
     */
    public void setPosition(float xpos, float ypos) {
        this.xpos = xpos;
        this.ypos = ypos;
    }
    public void setVelocity(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
    }
    public void adjustVelocity(float dx, float dy) {
        this.dx += dx;
        this.dy += dy;
    }

    /**
     * Returns true if the given point is inside the ball.  Or, possibly,
     * inside the ball's bounding box, which may include corners which mumble
     * mumble.
     */
    public boolean contains(float x, float y) {
        //  OK, we're just checking the bounding box.
        return (x >= (xpos - radius)) && (x <= (xpos + radius)) &&
               (y >= (ypos - radius)) && (y <= (ypos + radius));
    }

    /**
     * Adjusts the ball's position and velocity; returns true if the ball moved.
     */
    public boolean applyGravity(float gravity, int screenW, int screenH) {
        xpos = xpos + dx;
        ypos = ypos + dy;
        if ((xpos + radius) > screenW) {
            dx = -dx * bounciness;
            xpos = screenW - radius;// - (screenW - (xpos + radius));
        } else if (xpos < radius) {
            dx = -dx * bounciness;
            xpos = radius;// + (radius - xpos);
        }
        //  let's let it fly off the top of the screen.  This bit checks for
        //  bounce along the bottom.
        if ((ypos + radius) > screenH) {
            dy = -dy * bounciness;
            ypos = screenH - radius;// - (screenH - (ypos + radius));
        } else if ((dy != 0f) || ((ypos + radius) != screenH)) {
            dy += gravity;
        }
        return (ypos != 0f) || (xpos != 0f);
    }

    /**
     * Draws this Ball on the given Canvas.  If, instead of a circle, we wanted
     * to paint a flat sprite--some image loaded from file earlier--we would
     * do that here instead.
     */
    public void draw(Canvas canvas) {
//Log.d("Ball", "canvas.drawCircle(" + xpos + ", " + ypos + ", " + radius + ", paint)");
        canvas.drawCircle(xpos, ypos, radius, paint);
    }
}
