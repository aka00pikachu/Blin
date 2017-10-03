package com.scottrealapps.calculater;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id == R.id.action_canvas) {
            //  There are a couple of ways to launch a child Activity; if you
            //  want to get back the user's input etc. from the child Activity,
            //  then use startActivityForResult(Intent, int); but, we expect
            //  this to be a one-way trip--they launch the CanvasActivity, and
            //  never come back--so use startActivity(Intent).
            Intent intent = new Intent(this, CanvasActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_canvas2) {
            Intent intent = new Intent(this, CanvasActivity.class);
            //  Here's an example of passing additional information to the
            //  activity being launched.  In both cases, CanvasActivity
            //  instances will be created, but this time, we're passing a flag
            //  saying we want it to do some things differently.
            intent.putExtra(CanvasActivity.INTENT_DOUBLE_BUFFER, true);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_crime) {
            Intent intent = new Intent(this, CanvasActivity.class);
            intent.putExtra(CanvasActivity.INTENT_SHOOTY, true);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_motion) {
            Intent intent = new Intent(this, MotionActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_gravity) {
            Intent intent = new Intent(this, GravityActivity.class);
            startActivity(intent);
            return true;
        }
//        if (id == R.id.action_tiles) {
//            Intent intent = new Intent(this, StartGameActivity.class);
//            startActivity(intent);
//            return true;
//        }
        return super.onOptionsItemSelected(item);
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
