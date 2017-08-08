package com.scottrealapps.calculater.d2;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * A ball which is drawn using a different paint each time.
 */
public class Flash extends Ball {

    private Paint[] paints;
    private int updates = 0;

    public Flash(Paint[] paints) {
        super(paints[0]);
        setVelocity(0f, 0f);
        this.paints = paints;
    }

    public boolean isVisible() {
        return paint != null;
    }

    /**
     * In addition to positioning the flash, this resets its animation state.
     */
    @Override
    public void setPosition(float xpos, float ypos) {
        super.setPosition(xpos, ypos);
        updates = 0;
        setPaint(paints[updates]);
    }
//    /**
//     * Returns true if the given point is inside the ball.  Or, possibly,
//     * inside the ball's bounding box, which may include corners which mumble
//     * mumble.
//     */
//    public boolean contains(float x, float y) {
//        //  OK, we're just checking the bounding box.
//        return (x >= (xpos - radius)) && (x <= (xpos + radius)) &&
//               (y >= (ypos - radius)) && (y <= (ypos + radius));
//    }

    public void update() {
        if (isVisible()) {
//XXX ermm, is this right?  what guards against running off the end of the array?
            setPaint(paints[++updates]);
        }
    }

    /**
     * Draws this Ball on the given Canvas.  If, instead of a circle, we wanted
     * to paint a flat sprite--some image loaded from file earlier--we would
     * do that here instead.
     */
    @Override
    public void draw(Canvas canvas) {
        if (isVisible()) {
            super.draw(canvas);
        }
    }
}
