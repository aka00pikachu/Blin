package com.scottrealapps.calculater;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
        TableLayout highScores = (TableLayout) findViewById(R.id.highScores);

scoreText.setText(score + ", speed " + speed);
message.setText("You are great!");
//some bogus high scores
setScoreStuff((TableRow)(highScores.getChildAt(1)), "EPB", 666, 123, "2017-10-3");
setScoreStuff((TableRow)(highScores.getChildAt(2)), "EPB", 665, 123, "2017-10-2");
setScoreStuff((TableRow)(highScores.getChildAt(3)), "RDB", 101,  21, "2017-10-1");
setScoreStuff((TableRow)(highScores.getChildAt(4)), "EPB",  57,  12, "2017-10-1");
setScoreStuff((TableRow)(highScores.getChildAt(5)), "RDB",   3,   6, "2017-10-1");
    }

    private void setScoreStuff(TableRow row, String name, int score, int speed, String date) {
        if (row == null) return;
        TextView tv = (TextView)(row.getChildAt(0));
        tv.setText(name);
        tv = (TextView)(row.getChildAt(1));
        tv.setText(Integer.toString(score));
        tv = (TextView)(row.getChildAt(2));
        tv.setText(Integer.toString(speed));
        tv = (TextView)(row.getChildAt(3));
        tv.setText(date);
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
