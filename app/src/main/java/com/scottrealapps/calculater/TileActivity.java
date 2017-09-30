package com.scottrealapps.calculater;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.scottrealapps.calculater.d2.BouncyScene;
import com.scottrealapps.calculater.d2.CrimeScene;
import com.scottrealapps.calculater.d2.TileScene;

import java.util.ArrayList;

/**
 * This Activity is basically a reimplemntation of White Tiles.
 */
public class TileActivity extends AppCompatActivity {//implements CrimeScene.ScoreListener {

//    private interface ThingWithName {
//        public String getName();
//        public void setName(String newName);
//    }


//    private static class MultipleClickCell extends Cell {
//        int timesClicked = 0;
//
//        @Override
//        public void clicked() {
//            timesClicked++;
//        }
//
//    }


//    private static class Person implements ThingWithName {
//        Person(String name, float height) {
//            this.name = name;
//            this.height = height;
//        }
//
//        /**
//         *
//         * @param name
//         * @param heightFeet if you're 6'9", this is 6
//         * @param heightInches if you're 6'9", this is 9
//         */
//        Person(String name, float heightFeet, float heightInches) {
//            this.name = name;
//            this.height = ((heightFeet * 12) + heightInches) * 2.5f / 1000f;
//        }
//        String name;
//        float height;  //  in meters
//
//        public float getHeightInCM() {
//            return height * 1000f;
//        }
//        public float getHeightInInches() {
//            return height * 1000f / 2.5f;
//        }
//
//        @Override
//        public String getName() {
//            return name;
//        }
//
//        @Override
//        public void setName(String newName) {
//            name = newName;
//        }
//    }
//    private int columns = 4;
//    private int
    //  How many columns?
    //  How many rows?

//    Person elee = new Person("Elee", 6, 1);
//    Person rusty = new Person("Rusty", 6, 9);
//
//    private void doSomethingWithAPerson(Person thePerson) {
//        System.out.println("This person's name is " + thePerson.getName());
//    }

//    static {
//        Row foo = new Row();
//        foo.setHeight(234);
//    }

//    //  well, this is mis-named...
//    public static final String INTENT_DOUBLE_BUFFER = "doubleBuffer";
//    //  ehh, so is this one.
//    public static final String INTENT_SHOOTY = "shooty";

    private TileScene tileScene = null;
//    private TextView scoreStuff = null;
    private SceneUpdateThread sceneUpdateThread = null;

////seems like the wrong place for this
//    private Runnable scoreUpdater = new Runnable() {
//        @Override
//        public void run() {
//            if (scoreStuff != null) {
//                scoreStuff.setText("Score: " + crimeScene.getScore() +
//                        " Shots " + crimeScene.getShotCount() +
//                        " GH " + crimeScene.getGoodHits() +
//                        " BH " + crimeScene.getBadHits() +
//                        " GE " + crimeScene.getGoodEscapes() +
//                        " BE " + crimeScene.getBadEscapes());
//            }
//        }
//    };

//    @Override
//    public void scoreChanged(CrimeScene scene) {
//        runOnUiThread(scoreUpdater);
//    }

//    //  This runs the timer which calls MyView.updateScene() regularly.
//    //  Copied from SnakeView.java under <SDL>/samples/android-21/legacy/Snake.
//    //  THIS CAN BE DELETED if we get rid of A2DView.
//    private class AnimationHandler extends Handler {
//        //  this controls how fast we'll update our scene: sleeping 100ms between
//        //  runs = about 10 times per second.
//        private long sleepMS = 33L;
//        private final A2DView view;
//        AnimationHandler(A2DView view) {
//            this.view = view;
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            view.updateScene();
//            view.invalidate();  //  OK?  not postInvalidate() ?
//            sleep(sleepMS);
//        }
//        public void sleep(long sleepMS) {
//            removeMessages(0);
//            sendMessageDelayed(obtainMessage(0), sleepMS);
//        }
//    }
//    private AnimationHandler animationTimer;// = new AnimationHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Intent intent = getIntent();
        String instructions = null;
//        if ((intent != null) && (intent.getBooleanExtra(INTENT_DOUBLE_BUFFER, false))) {
            setContentView(R.layout.activity_canvas2);
            Another2DView view = (Another2DView) (findViewById(R.id.myView));
            view.setScene(new TileScene(this, 4));
            view.setupListeners();
            sceneUpdateThread = new SceneUpdateThread(view);
            instructions = getResources().getString(R.string.instructions_tile);
//        } else if ((intent != null) && (intent.getBooleanExtra(INTENT_SHOOTY, false))) {
//            setContentView(R.layout.activity_canvas3);
//            Another2DView view = (Another2DView) (findViewById(R.id.myView));
//            view.setScene(crimeScene = new CrimeScene(this));
//            view.setupListeners();
//            sceneUpdateThread = new SceneUpdateThread(view);
//            instructions = getResources().getString(R.string.instructions_shoot);
//            scoreStuff = (TextView)(findViewById(R.id.scoreStuff));
//            crimeScene.addScoreListener(this);
//scoreChanged(crimeScene);
//        } else {
//            setContentView(R.layout.activity_canvas);
//            A2DView view = (A2DView)(findViewById(R.id.myView));
//            animationTimer = new AnimationHandler(view);
//            animationTimer.handleMessage(null);  //  start the animation running
//            instructions = getResources().getString(R.string.instructions_slap);
//        }
        if (instructions != null) {
            Toast.makeText(this, instructions, Toast.LENGTH_LONG).show();
        }
    }

//    @Override
//    public void onStop() {
//        if (tileScene != null) tileScene.onStop();
//        super.onStop();
//    }

    @Override
    public void onBackPressed() {
        if (sceneUpdateThread != null) {
            sceneUpdateThread.setPaused(true);
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Closing Activity")
                    .setMessage("Are you sure you want to close this activity?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sceneUpdateThread.setPaused(false);
                            sceneUpdateThread.setRunning(false);
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sceneUpdateThread.setPaused(false);
//                            finish();
                        }
                    })
                    .show();
        }
//        Toast.makeText(this, "doing NOTHING", Toast.LENGTH_SHORT).show();
//        super.onBackPressed();
    }


}
