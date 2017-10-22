package com.scottrealapps.calculater.util;

public class ConstantSpeed implements SpeedAdjuster {
    /**
     * Implemented to just return the given speed every time.
     */
    @Override
    public int adjustSpeed(int speed) {
        return speed;
    }
}
