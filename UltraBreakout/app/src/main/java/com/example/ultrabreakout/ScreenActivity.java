package com.example.ultrabreakout;

import android.graphics.Point;
import android.view.Display;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

/*
 * Class containing functions to set up screen and get display information;
 */

public class ScreenActivity extends AppCompatActivity{

    //Make full screen, removes navigation bar, title bar, and status bar
    //Note the bitwise ORs.
    //https://stackoverflow.com/questions/48124248/android-full-screen-on-only-one-activity
    public void configureScreen(){
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public Point obtainScreenSize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        return size;
    }



}
