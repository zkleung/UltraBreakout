package com.example.ultrabreakout;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/*
 * Handles the Title screen.
 * Initializes the actual image and music, plus
 *  the level select.
 */

public class Title extends ScreenActivity {

    Sound sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureScreen();
        setContentView(R.layout.activity_ultra_breakout__title_screen);
        configureTitle();
        configureLevelsButton();
        sound = Sound.getInstance();
        sound.play_background(getApplicationContext(), R.raw.background_1);
    }

    //Set Up text and font for title
    private void configureTitle(){
        Button title = findViewById(R.id.title);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/8bit.TTF");
        title.setTypeface(typeface);
    }


    //Sets up On Click for Levels Button
    private void configureLevelsButton() {
        Button Levels_button = findViewById(R.id.LevelsButton);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/8bit.TTF");
        Levels_button.setTypeface(typeface);
        Levels_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(Title.this, LevelSelectMenu.class));
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();

        sound.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        sound.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
