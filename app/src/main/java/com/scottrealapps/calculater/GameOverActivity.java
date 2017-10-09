package com.scottrealapps.calculater;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.scottrealapps.calculater.util.EnterNameDialogFragment;
import com.scottrealapps.calculater.util.HighScore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GameOverActivity extends AppCompatActivity
        implements EnterNameDialogFragment.NameEnteredListener {

    List<HighScore> scores;
    HighScore theirScore;


    public void done(View view) {
        finish();
    }

    /**
     * This is how we know the user has entered their name on a new high
     * score.
     *
     * @param name
     */
    public void nameEntered(String name) {
        //  We're going to add their new score to the list in the right place.
        //  Then we're going to whack the list to five elements.
        theirScore.setName(name);
        if (scores.isEmpty()) {
            scores.add(theirScore);
        } else {
            //  We have to figure out where to put their score.
            boolean addedScore = false;
            for (int ii = 0; ii < scores.size(); ++ii) {
                if (theirScore.getScore() > scores.get(ii).getScore()) {
                    scores.add(ii, theirScore);
                    addedScore = true;
                    if (scores.size() > 5) {
                        scores.remove(5);
                    }
                    break;
                }
            }
            //  If we didn't add their score, hopefully that means it's lower
            //  than the lowest in the list, and we got here because the list
            //  was fewer than 5 scores.
            if (!addedScore) scores.add(theirScore);
        }
        //  Then we're going to write the file.
        HighScore.writeScores(this, scores);

        //  Then we're going to update the list of high scores that we're
        //  displaying.
        updateScoreTable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        TextView scoreText = (TextView)findViewById(R.id.scoreText);
        TextView message = (TextView)findViewById(R.id.message);

        //  Hopefully we were passed information about their score.
        Intent intent = getIntent();
        if (intent != null) {
            int score = intent.getIntExtra(StartGameActivity.INTENT_SCORE, 0);
            int speed = intent.getIntExtra(StartGameActivity.INTENT_SPEED, 0);
            theirScore = new HighScore(null, score, speed);
            scoreText.setText(score + ", speed " + speed);
message.setText("You tried?");
        }

        scores = HighScore.readScores(this);
        if (scores == null) {
            scores = new ArrayList<HighScore>();
        }
        updateScoreTable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (theirScore == null) return;
        if ((scores.size() < 5) ||
            (theirScore.getScore() > scores.get(scores.size() - 1).getScore())) {
            DialogFragment dialog = new EnterNameDialogFragment();
            dialog.show(getSupportFragmentManager(), "EnterNameDialogFragment");
        }
    }

    /**
     * Call this when our list of scores changes.
     */
    private void updateScoreTable() {
        TableLayout highScores = (TableLayout) findViewById(R.id.highScores);
        SimpleDateFormat fmt = new SimpleDateFormat("d/M/yy");
        for (int ii = 0; ii < scores.size(); ++ii) {
            setScoreStuff((TableRow)(highScores.getChildAt(ii + 1)), scores.get(ii), fmt);
        }
    }

    private void setScoreStuff(TableRow row, HighScore score, DateFormat fmt) {
        if (row == null) return;
        TextView tv = (TextView)(row.getChildAt(0));
        tv.setText(score.getName());
        tv = (TextView)(row.getChildAt(1));
        tv.setText(Integer.toString(score.getScore()));
        tv = (TextView)(row.getChildAt(2));
        tv.setText(Integer.toString(score.getSpeed()));
        tv = (TextView)(row.getChildAt(3));
        tv.setText(fmt.format(score.getDate()));
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
