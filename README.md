In Android Studio 2.2.3:

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
or a media device... probably camera) and wait for an astounding green
triangle to show up on its screen.

FROM HERE you have some options:

- To make code changes, click the "Project" tab on the left edge of the Android
  Studio window; your code is under app -> java -> com.scottrealapps.calculater
  (GLRenderer and MainActivity) and, under that, "shapes" (Square, Triangle).
  That's showing you (for example) the com.scottrealapps.calculater.Triangle
  class, and the actual source file (which you probably don't want to screw
  with outside of Android Studio) would be
  Calculater/app/src/main/java/com/scottrealapps/calculater/Triangle.java (if
  "Calculater" is the name of the directory you chose to clone the GitHub
  repository into in step 2 above).
  
  IN FACT, let's test that.  In Android Studio, open Triangle.java (by clicking
  the "Project" tab, then app -> java -> com.scottrealapps.calculater -> shapes -> Triangle); in line 57, change this:

      float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

  to this (just cranking the red all the way up):

      float color[] = { 1.0f, 0.76953125f, 0.22265625f, 1.0f };
 
  Then click that green triangle next to the "app" button to compile & run the
  changes on your phone.  IF YOU SEE A SICKLY YELLOW TRIANGLE then everything
  is right.

- When you make changes, you can see what you've done by clicking the "Version
  Control" tab at the bottom of the Android Studio window.  On each of those
  files, you can right-click -> Show Diff (or Ctrl+D) to see what's changed in
  your local copy of the repository.  Note that some of those changes may have
  been made for you by Android Studio; you don't *have* to check them in if
  you don't want to.  If you changed the Triangle class above, Triangle.java
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

  Now, I don't remember whether that pushes your changes to GitHub, or just
  commits them to your local copy of the git repository... well, we can figure
  that out later.

OK, son, have fun!  **I love you**, and this is a public repository, so everyone
in the entire world knows I said that.  *Sorry.*
