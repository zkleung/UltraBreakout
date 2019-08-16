package com.example.ultrabreakout;

import android.graphics.BitmapFactory;

/*
 * Handles Spike objects, which instantly kill Balls on collision.
 * They basically just sit there and do nothing while waiting
 *  to make your day miserable.
 */

class Spike extends Actor {
    public static int SPIKE_HEIGHT;
    public static int SPIKE_WIDTH;


    public Spike(float x_pos, float y_pos) {
        super(x_pos, y_pos,0, 0, SPIKE_WIDTH, SPIKE_HEIGHT,
                BitmapFactory.decodeResource(sprites,R.drawable.spike5));
    }
}
