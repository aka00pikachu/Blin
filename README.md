In Android Studio 2.3.3:

1. File -> New -> Project from Version Control -> GitHub

2. Clone Repository:
   - Repository URL: https://github.com/ScottyMcCotty/Calculater.git
   - Parent Directory: (whatever you want; probably
     /home/.../AndroidStudioProjects)
   - Directory Name: (whatever you want; probably Calculater)

That should check the stuff out and start a Gradle build!  (I got a couple of
errors about missing .iml files; I ignored them--I said to *not* remove the
modules it was talking about--and things seemed to build & run correctly.  If
necessary, I can check in those files which I guess were generated during our
original goofing around.)

### TO BUILD & RUN IT

In Android Studio, there may be a rectangular button in
the toolbar below the menu bar which says "app" and which has a green or gray
triangle to the right of it; if you click on that triangle, you might get a
window which lets you choose between your connected device, or an emulated
device (that is, running on a virtual/pretend phone in Android Studio, not
your real phone).  If your phone doesn't show up in that window, something is
wrong.  Choose your phone (plugged in as a USB device, connected as a camera
or a media device... probably camera) and wait for an astounding randomly colored
triangle to show up on its screen.

**There should be** an "action bar" with a menu doodad on the right; there, you
should be able to launch "Slappy Balls" or "Slappy Balls 2" or "Shaky Balls" and then
screw around with bouncy orange balls.

FROM HERE you have some options:

- To make code changes, click the "Project" tab on the left edge of the Android
  Studio window; the 2D code is under app -> java -> com.scottrealapps.calculater
  (A2DView, Another2DView, CanvasActivity) and, under that, "d2" (Ball, Scene).
  That's showing you (for example) the com.scottrealapps.calculater.d2.Ball
  class, and the actual source file (which you probably don't want to screw
  with outside of Android Studio) would be
  Calculater/app/src/main/java/com/scottrealapps/calculater/d2/Ball.java (if
  "Calculater" is the name of the directory you chose to clone the GitHub
  repository into in step 2 above).
  
  IN FACT, let's test that.  In Android Studio, open Scene.java (by clicking
  the "Project" tab, then app -> java -> com.scottrealapps.calculater -> d2 -> Scene);
  after line 44, add another `balls.add(new Ball(ballPaint))` line.
 
  Then click that green triangle next to the "app" button to compile & run the
  changes on your phone, and then choose one of the "Launch 2D Activity" items from
  the Calculater action bar.  IF YOU SEE FOUR ORANGE BALLS then everything
  is right.

- When you make changes, you can see what you've done by clicking the "Version
  Control" tab at the bottom of the Android Studio window.  On each of those
  files, you can right-click -> Show Diff (or Ctrl+D) to see what's changed in
  your local copy of the repository.  Note that some of those changes may have
  been made for you by Android Studio; you don't *have* to check them in if
  you don't want to.  If you changed the Scene class above, Scene.java
  should show up in this tab, and Show Diff on that file should show your
  change.

- To push your changes to GitHub (so that I can pull them!), hit the little
  button which says "VCS" with an up-arrow, or right-click
  and choose "Commit..."  That should give you a window with the list of
  files which have changed; you can select which ones you want to commit, and
  see the changes in each file (fiddle around to see whether you like the
  side-by-side viewer or the unified viewer; I like side-by-side but that's
  just because that's what I'm used to).  When you've chosen the messages to
  commit, WRITE A GOOD COMMIT MESSAGE (that message is for other people, AND
  YOU in six months when you're trying to remember which change this was) and
  hit commit.

  Now, that commits your changes to **your local clone** of the repository; to
  **push them to GitHub**, from the main menu, choose VCS -> Git -> Push; that
  should give you a window with a list of the changes you've committed to your
  local repository, with a "Push" button which should upload them to GitHub.

OK, son, have fun!  **I love you**, and this is a public repository, so everyone
in the entire world knows I said that.  *Sorry.*

### Next things to do

- The Ball.applyGravity() logic is wrong; sometimes balls don't bounce correctly,
  and/or wind up stuck slightly off the bottom of the screen.  Probably the problem
  is in applyGravity(), but it could also be something wrong with the height &
  width values being passed in.  **To test this,** you probably want to drop down
  to one ball in Scene.java, and add log messages in Ball.applyGravity().
- Make the selected ball a different color when it gets bounced.
- ~~Instead of drawCircle(), add a Ball subclass which overrides `draw()` to draw an
  image it loaded from file.  The first half of that sentence is easy; the second
  half is *mostly* easy, but requires that you put the file in the right place.  I
  may add notes on that later.~~ *I did that; see ImageBall.java.  See what happens
  when you make all three balls in Scene.java into ImageBalls!*
- Fiddling with the way touch events are handled.  There are other events we could
  add support for.
- Add a new Activity, like if you want to experiment with the gyroscope or step
  counter.  For instructions on that, see CONTRIBUTING.md.
