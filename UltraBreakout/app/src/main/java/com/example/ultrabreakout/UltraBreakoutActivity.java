package com.example.ultrabreakout;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

/* Main activity for project, simply sets up app and sends it to UltraBreakout
 * class.
 */

public class UltraBreakoutActivity extends ScreenActivity {

    // Class that contains all the main logic for the game.
    private UltraBreakout ultraBreakout;
    private Level level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get display of size, then pass of app to UltraBreakout class.
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();

        // Read in level file
        String level_file = null;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            level_file = bundle.getString("csv_file");
        }
        level = new Level(level_file, this);

        configureScreen();
        size = obtainScreenSize();

        //places in front
        //ultraBreakout.setBackground(getResources().getDrawable(R.drawable.game_background2));

        ultraBreakout = new UltraBreakout(this, size.x, size.y, level, this);
        setContentView(ultraBreakout);

    }

    public void returnToMainMenu(){
        Intent intent = new Intent(UltraBreakoutActivity.this, Title.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        //From what I've read online, this top section should be sufficient for returning to the title screen.
        //However calling the code above from the GameOverMenu screen somehow causes the app to freeze, while
        //calling it from the PauseMenu somehow does not cause it to freeze. I tried switching to using only
        //system.exit(0) instead of finish, and i got issues with the fullScreen resetting. Somehow calling both finish()
        //and system.exit(0) fixes both problems, although I don't think this is the correct documented way to do this.
        System.exit(0);
    }




    @Override
    protected void onResume() {
        super.onResume();

        ultraBreakout.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        ultraBreakout.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ultraBreakout.destroy();
    }

}
