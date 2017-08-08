package com.scottrealapps.calculater;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.scottrealapps.calculater.d2.Scene;

public class GravityActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    //  Note that https://developer.android.com/guide/topics/sensors/sensors_motion.html
    //  suggests it's generally better to use the rotational vector sensor.
    private Sensor gravitySensor = null;
    private Scene scene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);
        Another2DView view = (Another2DView)(findViewById(R.id.myView));
        //  we could go view.setupListeners() here...
        scene = view.getScene();
        scene.setGravity(0f);
        scene.setOpenTopped(false);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if ((gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)) == null) {
            Toast.makeText(this, getResources().getString(R.string.no_gravity_sensor),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.instructions_tilt),
                    Toast.LENGTH_SHORT).show();
        }

        view.setupListeners();
        new SceneUpdateThread(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gravitySensor != null) {
            sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_GAME);  //  or SENSOR_DELAY_NORMAL?
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
        float dz = ev.values[2];
//x > 0 means it's tipped to the left; x < 0 means it's tipped to the right.
//y > 0 means it's standing up; y < 0 means it's tipped top-down.
//z approaching 9.8 means it's laying on its back; z approaching -9.8 means it's laying on its screen.
//Log.d("GravityActivity", "sensor changed: x " + ev.values[0] + ", y " + ev.values[1] + ", z " + ev.values[2]);
        if ((Math.abs(dx) > 1f) || (Math.abs(dy) > 1f)) {
            scene.accelerateEverything(-dx, dy);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //  ehh.
    }
}
