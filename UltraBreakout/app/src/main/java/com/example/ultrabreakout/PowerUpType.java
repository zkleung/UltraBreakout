package com.example.ultrabreakout;

/*
 * Types of powerups that can be dropped from Bricks.
 * All but the bottom two are handled by Timers, and
 *      only last a set amount of time.
 */

public enum PowerUpType {
        NONE,                   // Normal block.
        PADDLE_WIDTH_INCREASE,  // Increase the width of the paddle.
        NUM_POWERUP_TYPES,      // Unused
        GOLDEN_BALL,            // Ball "pierces" through Bricks, no reflection
        PADDLE_WIDTH_DECREASE,  // Paddle shrinks horizontally
        BALL_SPEED_INCREASE,    // Ball increases speed
        BALL_SPEED_DECREASE,    // Ball decreases speed
        EXTRA_LIFE,             // 1-up
        DOUBLE_BALL,            // Spawns another ball at current ball
}
