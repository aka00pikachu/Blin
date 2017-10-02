package com.scottrealapps.calculater;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class StartGameActivity extends AppCompatActivity {

    public static final int RESULT_GAME_DONE = RESULT_FIRST_USER + 1;
    public static final String INTENT_SCORE = "StartGameActivity.score";
    public static final String INTENT_SPEED = "StartGameActivity.speed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);
    }

    /**
     * Called when our Settings button is clicked on; currently does nothing.
     */
    public void openSettings(View view) {
Toast.makeText(this, "Open settings!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when our Start Game button is clicked on; launches the
     * TileActivity.
     */
    public void startGame(View view) {
        Intent intent = new Intent(this, TileActivity.class);
        startActivityForResult(intent, RESULT_GAME_DONE);
    }

    /**
     * This is what gets called when our TileActivity finishes.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_GAME_DONE) {
            //  we probably want to check to see whether our high scores have
            //  changed.
Toast.makeText(this, "Welcome back:(", Toast.LENGTH_SHORT).show();
        } else {
            //  it's not something we know about, so pass it on to the superclass
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
