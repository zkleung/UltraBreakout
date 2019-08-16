package com.example.ultrabreakout;

import android.graphics.RectF;

import static com.example.ultrabreakout.UltraBreakout.statsBarOffset;

/*
 * A pause button at the extreme top right of the screen.
 */

public class PauseButton {
    float buttonWidth;
    float buttonHeight;
    RectF hitbox;

    public PauseButton ( int screenWidth, int screenHeight) {
        this.buttonWidth = screenWidth / (float) 10;
        this.buttonHeight = screenHeight / (float) 10;

        hitbox = new RectF(screenWidth - statsBarOffset , 0, screenWidth,statsBarOffset);
    }

    public boolean collides(float x, float y){
        if (x < this.hitbox.right && x > this.hitbox.left &&  y > this.hitbox.top && y < this.hitbox.bottom){
            return true;
        }
        else{
            return false;
        }

    }

}
