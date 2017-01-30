package com.scottrealapps.calculater;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.scottrealapps.calculater.d2.Scene;

public class MotionActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    //  Note that https://developer.android.com/guide/topics/sensors/sensors_motion.html
    //  suggests it's generally better to use the rotational vector sensor.
    private Sensor linearSensor = null;
    private Scene scene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);
        Another2DView view = (Another2DView)(findViewById(R.id.myView));
        //  we could go view.setupListeners() here...
        scene = view.getScene();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if ((linearSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)) == null) {
            Toast.makeText(this, getResources().getString(R.string.no_linear_accelerator),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.instructions_shake),
                    Toast.LENGTH_SHORT).show();
        }

        view.setupListeners();
        new SceneUpdateThread(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (linearSensor != null) {
            sensorManager.registerListener(this, linearSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent ev) {
        float dx = ev.values[0];
        float dy = ev.values[1];
//        float dz = ev.values[2];
//Log.d("MotionActivity", "sensor changed: x " + ev.values[0] + ", y " + ev.values[1] + ", z " + ev.values[2]);
        if ((Math.abs(dx) > 1.5f) || (Math.abs(dy) > 1.5f)) {
            scene.accelerateEverything(dx * 5f, dy * 5f);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //  ehh.
    }
}
