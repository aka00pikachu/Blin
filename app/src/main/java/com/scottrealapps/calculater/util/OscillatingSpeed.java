package com.scottrealapps.calculater.util;

/**
 * Ways to make this configurable:
 * <ul>
 *     <li>pass seconds to spend at speed; right now it's hard-coded.</li>
 *     <li>pass amount to accelerate each time we decide to speed up; right now
 *         that's hard-coded too.</li>
 *     <li>the way we decide how to slow down & speed back up is pretty rough
 *         too.</li>
 * </ul>
 */
public class OscillatingSpeed implements SpeedAdjuster {
    private int targetSpeed;
    private int secondsAtSpeed = 0;
    private boolean decelerating = false;
    private int secondsToSpendAtSpeed = 5;

    public OscillatingSpeed(int initialSpeed) {
        targetSpeed = initialSpeed;
    }

    /**
     * Implemented to return a speed which maintains for a certain number of
     * seconds, then drops and increases to a new, faster speed.
     */
    @Override
    public int adjustSpeed(int speed) {
        if (speed == targetSpeed) {
            //  we're just cruising along at the same speed.
            ++secondsAtSpeed;
            if (secondsAtSpeed >= secondsToSpendAtSpeed) {
                //  time for us to decelerate.
                targetSpeed += 5;
                decelerating = true;
                return speed - 1;
            } else {
                return speed;
            }
        }
        //  if we're here, we're not at our target speed yet.
        if (decelerating) {
            if (speed > (targetSpeed / 2)) {
                return speed - (targetSpeed / 4);
            } else {
                decelerating = false;
                return speed + 1;
            }
        } else {
            //  we're on our way back up to our target speed.
            speed = speed + (targetSpeed / 4);
            if (speed > targetSpeed) {
                secondsAtSpeed = 0;
                return targetSpeed;
            }
            return speed;
        }
    }
}
