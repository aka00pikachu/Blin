# Contributing

These are notes on how to make your own changes.  For notes on getting started,
see README.md.

## Adding a new activity using the "New Android Activity" wizard 

Yeah, well, you might try it.  I think it's what I usually use.

## Adding a new activity by copying an existing one

These are the steps I took to add the Droopy Balls activity, which uses the
device's gravity sensor to move a ball around the screen.  The steps should be
similar for adding your own new activity.

1. Open `app/src/main/res/menu/menu_main.xml` and add a new menu item like this:
    
   ```xml
   <item
       android:id="@+id/action_gravity"
       android:orderInCategory="100"
       android:title="@string/action_gravity"
       app:showAsAction="never"/>
   ```
   (If you're using the "Android" view in the panel on the left side of Android
   Studio, it may be in app -> res -> menu -> menu_main.xml.)
   
   **`@+id/action_gravity`** means Android Studio will generate a new ID as
   needed when building.
   
   **`@string/action_gravity`** means the actual text of the menu item will be
   read from `strings.xml` rather than being hard-coded here.  As a general
   rule, you want user-visible text to be in strings.xml so that it can be
   localized (translated to another language) without affecting your code.

2. Add that `@string/action_gravity` to `app/src/main/res/values/strings.xml`:
   
   ```xml
   <string name="action_gravity">Droopy Balls</string>
   ```

3. Now we're going to add code which "does something" when that menu item is
   clicked on, and confirm that we can build, install, and see our changes.  In
   `MainActivity.java`, in `onOptionsItemSelected()`, add this bit:
   
   ```java
   if (id == R.id.action_gravity) {
       Toast.makeText(this, "HI THERE", Toast.LENGTH_SHORT).show();
       return true;
   }
   ```
   (`Toast.makeText()` displays a little message on the screen.)  Following
   the instructions back in README.md, confirm that you can build & install the
   app on your phone, and that your new menu item shows up, and that you see
   your message when you click on it.  If/when you get to that point, **great!**
   Now we're going to add the new activity, and hook it up to that menu item.

4. Copy an existing Activity class.  In this case, what I want is to draw balls
   on the screen & move them around based on readings from the device's gravity
   sensor, which sounds a lot like the Shaky Balls activity (which moves balls
   around based on readings from the device's linear acceleration sensor), so
   I'm copying `MotionActivity.java` to a new file called
   `GravityActivity.java`.  Initially, I'm not going to make any changes other
   than renaming it; we'll come back to that.

5. Launch your new Activity from the MainActivity `onOptionsSelected()` bit we
   added earlier.  Change this:
   ```java
   if (id == R.id.action_gravity) {
       Toast.makeText(this, "HI THERE", Toast.LENGTH_SHORT).show();
       return true;
   }
   ```
   to this:
   ```java
   if (id == R.id.action_gravity) {
       Intent intent = new Intent(this, GravityActivity.class);
       startActivity(intent);
       return true;
   }
   ```

6. We also need to add the new Activity class to the app's
   `AndroidManifest.xml`.  Find that (`app/src/main/AndroidManifest.xml`, or
   app -> manifests -> AndroidManifest.xml in the Android project view on the
   left) and add this bit:
   ```xml
   <activity android:name=".GravityActivity"
             android:label="@string/action_gravity"
             android:parentActivityName=".MainActivity">
   </activity>
   ```
   Now run it and confirm that your new activity is launched when you choose it
   from the main menu!

7. **Dig in!**  Now you can start making changes to your new Activity class,
   and run them on your device to see how they look.