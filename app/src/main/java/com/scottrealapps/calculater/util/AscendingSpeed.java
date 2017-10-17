package com.scottrealapps.calculater.util;

public class AscendingSpeed implements SpeedAdjuster {
    /**
     * Implemented to just return speed + 1 every time.
     */
    @Override
    public int adjustSpeed(int speed) {
        return speed + 1;
    }
}
