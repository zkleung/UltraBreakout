package com.example.ultrabreakout;

import android.graphics.BitmapFactory;
import android.os.Handler;
import static com.example.ultrabreakout.UltraBreakout.statsBarOffset;

/*
 * Handles the ball(s).
 * When the game updates, other actors handle the collisions,
 * not the balls themselves since they don't change upon
 * colliding with other actors (besides position and velocity).
 */

class Ball extends Actor {

    public enum BallState{
        NORMAL,
        INCREASE,
        DECREASE,
        GOLDEN
    }

    public static final int BALL_HEIGHT = 20;
    public static final int BALL_WIDTH = 20;

    // The initial velocities for the ball in the x and y components.
    public static final int X_VELOCITY = 450;
    public static final int Y_VELOCITY = 450;

    public static final int BALL_POWERUP_TIME = 4000;
    public Handler ballTimer;
    private Runnable ballCallback;
    // Timer and handler to implement ball width powerup object.
    public BallState ballState = BallState.NORMAL;

    public Ball(float x_pos, float y_pos, float x_vel, float y_vel) {
        super(x_pos, y_pos, x_vel, y_vel, BALL_WIDTH, BALL_HEIGHT,
                BitmapFactory.decodeResource(sprites,R.drawable.ball3));
        ballTimer = new Handler();
        ballCallback = new Runnable() {
            @Override
            public void run() {
                normalBall();
            }
        };
    }

    //Clone constructor
    public  Ball(Ball ball){
        super(
                ball.hitbox.left,
                ball.hitbox.top,
                ball.velocity.x,
                ball.velocity.y,
                BALL_WIDTH,
                BALL_HEIGHT,
                BitmapFactory.decodeResource(sprites,R.drawable.ball3)
        );
        ballTimer = new Handler();
        ballCallback = new Runnable() {
            @Override
            public void run() {
                normalBall();
            }
        };

    }

    public void setGoldenBall(){
        ballTimer.removeCallbacks(ballCallback);
        ballTimer.postDelayed(ballCallback, BALL_POWERUP_TIME);
        this.ballState = ballState.GOLDEN;
        this.setSprite(R.drawable.goldenball);
    }

    public void increaseBallSpeed(){
        ballTimer.removeCallbacks(ballCallback);
        ballTimer.postDelayed(ballCallback, BALL_POWERUP_TIME);

        if (this.ballState == ballState.NORMAL){
            this.ballState = ballState.INCREASE;
            this.velocity.setSpeed(2.0f);
        }
        else if (this.ballState == ballState.DECREASE){
            this.velocity.setSpeed((.5f));
            normalBall();
        }
    }
    public void decreaseBallSpeed(){
        ballTimer.removeCallbacks(ballCallback);
        ballTimer.postDelayed(ballCallback, BALL_POWERUP_TIME);
        if (this.ballState == BallState.NORMAL){
            this.ballState = BallState.DECREASE;
            this.velocity.setSpeed(0.25f);
        }
        else if (this.ballState == BallState.INCREASE){
            this.velocity.setSpeed(4f);
            normalBall();
        }

    }

    public void normalBall(){
        if (this.velocity.x > 0){
            this.velocity.x = X_VELOCITY;
        }
        else if(this.velocity.x < 0){
            this.velocity.x = -X_VELOCITY;
        }
        if (this.velocity.y > 0){
            this.velocity.y = Y_VELOCITY;
        }
        else if (this.velocity.y < 0){
            this.velocity.y = -Y_VELOCITY;
        }
        if (this.ballState == BallState.GOLDEN){
            this.setSprite(R.drawable.ball3);
        }
        this.ballState = BallState.NORMAL;
    }

    //Reflects the ball if at screen edges, then update position
    public void update (float fps, float screenWidth){
        if ((hitbox.right > screenWidth && velocity.x > 0)
                || (hitbox.left < 0 && velocity.x < 0)){
            velocity.reverseX();
        }
        if ((hitbox.top < statsBarOffset && velocity.y < 0)){
            velocity.reverseY();
        }
        updatePos(fps);
    }

    //Returns true if the ball has fallen down off the screen
    public boolean hasFallen (int screenHeight){
        if (hitbox.bottom > screenHeight && velocity.y > 0){
            return true;
        }
        return false;
    }

    //Kills and resets ball zero.
    public void die (Paddle paddle_zero){
        reset (paddle_zero);
        normalBall();
        paddle_zero.paddleWidthNormal();
        velocity.setSpeed(0);
    }

    //Resets the ball to on top of the original paddle.
    public void reset (Paddle paddle_zero){
        reposition(paddle_zero.hitbox.centerX(),
                paddle_zero.hitbox.top - paddle_zero.hitbox.height() * 2);
        velocity.setSpeed(0);
    }

    public static float FstInvSqrt(float x) {
        //https://stackoverflow.com/questions/11513344/
        // how-to-implement-the-fast-inverse-square-root-in-java
        //Come on, it's our namesake
        float xhalf = 0.5f * x;
        int i = Float.floatToIntBits(x);
        i = 0x5f3759df - (i >> 1);
        x = Float.intBitsToFloat(i);
        x *= (1.5f - xhalf * x * x);
        return x;
    }

}
