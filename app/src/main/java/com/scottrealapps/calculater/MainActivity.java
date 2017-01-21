package com.scottrealapps.calculater;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.scottrealapps.calculater.shapes.Square;
import com.scottrealapps.calculater.shapes.Triangle;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glView;

    //  based on
    //  https://developer.android.com/training/graphics/opengl/environment.html
    class MyGLSurfaceView extends GLSurfaceView {

        private final GLRenderer renderer;

        public MyGLSurfaceView(Context context){
            super(context);

            // Create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2);

            renderer = new GLRenderer();

            // Set the Renderer for drawing on the GLSurfaceView
            setRenderer(renderer);

            // Render the view only when there is a change in the drawing data
            //  "This setting prevents the GLSurfaceView frame from being
            //  redrawn until you call requestRender(), which is more efficient
            //  for this sample app."
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }

        private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
        float prevX;
        float prevY;
        @Override
        public boolean onTouchEvent(MotionEvent me) {
            // MotionEvent reports input details from the touch screen
            // and other input controls. In this case, you are only
            // interested in events where the touch position changed.

            float x = me.getX();
            float y = me.getY();

if (me.getPointerCount() > 1) Log.d("Scott", "POINTER COUNT IS " + me.getPointerCount());
            if (me.getAction() == MotionEvent.ACTION_MOVE) {
                if (x < getWidth() / 5) {
//Log.d("Scott", "we think we're zooming in/out");
                    float zoom = 1.0f;
                    if (y > prevY) {
                        zoom = 1.1f;
                    } else if (y < prevY) {
                        zoom = 0.9f;
                    }
                    renderer.setZoom(renderer.getZoom() * zoom);
                    requestRender();
                } else {
Log.d("Scott", "got ACTION_MOVE " + x + ", " + y);

                    float dx = x - prevX;
                    float dy = y - prevY;

                    // reverse direction of rotation above the mid-line
                    if (y > getHeight() / 2) {
                        dx = dx * -1;
                    }

                    // reverse direction of rotation to left of the mid-line
                    if (x < getWidth() / 2) {
                        dy = dy * -1;
                    }

                    renderer.setAngle(
                            renderer.getAngle() +
                                    ((dx + dy) * -TOUCH_SCALE_FACTOR));
                    requestRender();
                }
            } else {
Log.d("Scott", "got MotionEvent " + me);
            }

            prevX = x;
            prevY = y;
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Find that content_main RelativeLayout and make its child our
        //  MyGLSurfaceView
        RelativeLayout container = (RelativeLayout)(findViewById(R.id.content_main));
        glView = new MyGLSurfaceView(this);
        //setContentView(glView);
        container.removeAllViews();
        container.addView(glView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
