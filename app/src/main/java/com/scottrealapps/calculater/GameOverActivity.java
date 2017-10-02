package com.scottrealapps.calculater;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.scottrealapps.calculater.R;
import com.scottrealapps.calculater.StartGameActivity;

public class GameOverActivity extends AppCompatActivity {

    public void done(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        //  Hopefully we were passed information about their score.
        int score = 0;
        int speed = 0;
        Intent intent = getIntent();
        if (intent != null) {
            score = intent.getIntExtra(StartGameActivity.INTENT_SCORE, score);
            speed = intent.getIntExtra(StartGameActivity.INTENT_SPEED, speed);
        }

        TextView scoreText = (TextView)findViewById(R.id.scoreText);
        TextView message = (TextView)findViewById(R.id.message);
        TextView highScores = (TextView)findViewById(R.id.highScores);

scoreText.setText(score + ", speed: " + speed);
message.setText("You are great!");
highScores.setText("(High scores would go here)");

    }

    /**
     * This is overridden to check for the home button getting hit, and to call
     * finish() in that case.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
