package com.example.ultrabreakout;

import android.util.Log;

/* Handles the logic for user input.
 *
 * Deals with converting raw user input (ex location on screen pressed)
 * to the logical equivalent for the game (ie pressed button to go right).
 * This abstracts the user input, and makes it easier if we decide to change
 * the method of user input / add new methods of user input.
 *
 * The current method of user input is to move the paddle right if the user
 * clicked in the right half of the screen, and left if on the left side.
 */

public class Input {
    // Keep track of last user state.
    private boolean pressedRight;
    private boolean pressedLeft;
    private boolean pressedPause;
    // Variables for internal logic.
    private int screenWidth;
    private int screenHeight;

    public Input(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        pressedPause = false;
        pressedRight = false;
        pressedLeft = false;
    }

    /* Handle logic for user click on screen.
     *
     */
    public void touchDownEvent(float x, float y) {
        Log.d("Touch: ", x + ", " + y);
        if (y < 100) {
            if (pressedPause == true) {
                pressedPause = false;
            } else {
                Log.d("Touch: ", "Pause True");
                pressedPause = true;
            }
        } else if (x <= screenWidth / 2) {
            pressedLeft = true;
        } else if (x > screenWidth / 2) {
            pressedRight = true;
        }
    }

    /* Handle logic for user removing a finger from screen.
     *
     */
    public void touchUpEvent(float x, float y) {
        if (x <= screenWidth / 2) {
            pressedLeft = false;
        } else if (x > screenWidth / 2) {
            pressedRight = false;
        }
    }

    // Getters for user input.
    public boolean isPressRight() { return pressedRight && !pressedLeft; }
    public boolean isPressLeft() { return pressedLeft && !pressedRight; }

}