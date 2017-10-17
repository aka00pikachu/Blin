package com.scottrealapps.calculater.util;

/**
 * An object which tells us how our speed should change.  adjustSpeed() will
 * be called roughly once per second with the current speed; it's expected to
 * return the new speed.
 */
public interface SpeedAdjuster {
    int adjustSpeed(int speed);
}
