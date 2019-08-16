package com.example.ultrabreakout;

import android.graphics.Point;

/*
 * Class for handling Pop-Up type Android screens such as the level selection menu.
 */
public class PopUpScreen extends ScreenActivity {

    public void configurePopUpScreen(){
        setContentView(R.layout.level_popup_window);
        configureScreen();
        Point size;
        size = obtainScreenSize();
        getWindow().setLayout((int)(0.8 * size.x),(int)(0.8* size.y));
    }

}
