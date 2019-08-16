package com.example.ultrabreakout;

/*
 * Handles most of the in-game functions as part of
 *  an observer model.
 * It can be roughly divided into four parts:
 *  Initialization:     (Re)Creates all needed objects for all functions
 *  Updating:           Actual gameplay frame-by-frame updating
 *  Drawing:            Draws objects on-screen
 *  Menu, Touch, Pause: Menu and touchscreen handling
 */

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import static com.example.ultrabreakout.Actor.sprites;

public class UltraBreakout extends SurfaceView implements Runnable {

    private int screenWidth;                    //Usually tested on Google Pixel,
    private int screenHeight;                   // 1920x1080
    static int statsBarOffset;                  //For keeping all objects visible

    private float fps;                          //Frames per second for updating
    private UltraBreakoutActivity GameActivity;
    private SurfaceHolder holder;
    private Canvas canvas;
    private Paint paint;
    private Input input;
    private ArrayList<Paddle> paddles;          //For multiple paddles; unused
    private ArrayList<Ball> balls;              //For multiple Balls
    private ArrayList<Actor> actors;            //Does not include Paddle/Ball
    private Level level;
    private Stats stats;
    private Sound sound;
    private PauseButton pauseButton;
    private PauseMenu pauseMenu;
    private GameOverMenu gameOverMenu;
    private StatsBar statsBar;
    private boolean gameOver;
    private boolean victory;

    // Keeps track whether the main thread should be running or not.
    // Volatile so that it is thread-safe.
    private volatile boolean playing;

    // Keeps track if the game is paused for whatever reason (lost focus, etc).
    private boolean paused;

    // The main thread running the game.
    private Thread gameThread;

    long frameTimeNow, frameTimePrev;

    public UltraBreakout(Context context, int screenWidth, int screenHeight, Level level, UltraBreakoutActivity GameActivity) {
        super(context);
        sprites = getResources();   //For Actor
        this.GameActivity = GameActivity;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.level = level;
 
        Brick.BRICK_WIDTH = screenWidth/Level.NUM_COLUMNS;
        Brick.BRICK_HEIGHT = screenHeight / (Level.NUM_ROWS * 2);
        this.statsBarOffset = Brick.BRICK_HEIGHT * 2;

        Spike.SPIKE_WIDTH = screenWidth/Level.NUM_COLUMNS;
        Spike.SPIKE_HEIGHT = (screenHeight * 4) / (Level.NUM_ROWS * 2);
        // Initialize for drawing objects on screen.
        holder = getHolder();
        paint = new Paint();
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/8bit.TTF");
        paint.setTypeface(typeface);
        // Actors and functions related to the game.
        stats = new Stats();
        statsBar = new StatsBar(stats, screenWidth, typeface);
        input = new Input(screenWidth, screenHeight);
        generateActors();

        sound = Sound.getInstance();
        sound.play_background(context, R.raw.background_2);
        gameOver = false;
        victory = false;
        fps = 0;

        // Initialize paused game.
        paused = false;
        gameThread = null;

        this.pauseButton = new PauseButton(screenWidth, screenHeight);
        this.pauseMenu = new PauseMenu(screenHeight, screenWidth);
        this.gameOverMenu = new GameOverMenu(screenHeight, screenWidth);

        frameTimeNow = frameTimePrev = System.currentTimeMillis();


        System.out.println("INITIALIZING THE GAME");
    }

    @Override
    public void run() {
        while(playing) {
            if (!paused) {
                // Calculate the frame rate for physics purposes.
                frameTimeNow = System.currentTimeMillis();
                fps = 1000 / ((float)(frameTimeNow - frameTimePrev));
                if (fps > 0) {
                    update();
                }
                draw();
                frameTimePrev = frameTimeNow;
            }
            if (stats.lives <= 0){
                gameOver();
            }
            if (stats.bricksRemaining <= 0){
                win();
            }
        }
    }

    public void win(){
        victory = true;
        gameOver = true;
        draw();
        pause();
    }

    public void gameOver(){
        gameOver = true;
        draw();
        pause();
    }

    //Reinitializes a level.
    public void restart(){
        gameOver = false;
        victory = false;
        paddles.get(0).reposition(
                (screenWidth/2) - Paddle.PADDLE_WIDTH/2,
                paddles.get(0).hitbox.top);
        paddles.get(0).velocity.setSpeed(0);
        balls.get(0).reset(paddles.get(0));
        input = new Input(screenWidth, screenHeight);
        stats = new Stats();
        generateActors();

        statsBar.stats = stats;
    }

    //Updates every single Actor.
    //Rather ugly due to needing to access the list of Actors and stats;
    //  refactoring attempts didn't succeed as a result.
    public void update(){
        stats.updatetime();
        for (int i = actors.size() - 1; i >= 0; i--){
            Actor curActor = actors.get(i);
            for (int j = balls.size() - 1; j >= 0; j--){
                Ball ball = balls.get(j);
                if (curActor.intersects(ball)){
                    //Determines what child of Actor we have
                    switch (curActor.getClass().getSimpleName()){

                        case("Brick"):
                            Brick curBrick = ((Brick)curActor);
                            curBrick.collide(ball);
                            if(curBrick.health <= 0) {
                                if (curBrick.powerup != PowerUpType.NONE){
                                    actors.add(new Item(ball.hitbox.left,ball.hitbox.top,0,450,curBrick.powerup));
                                }
                                actors.remove(i);
                                stats.decrementRemainingBricks();
                                stats.incrementDestroyedBricks();
                                Log.d("remaining Bricks: ", ""+stats.bricksRemaining);
                                stats.incrementScore();
                            }
                            break;

                        case ("Spike"):
                            Spike curSpike = ((Spike)curActor);
                            if (curSpike.intersects(ball)) {
                                //FIXME Make unified ball killing funct
                                if (balls.size() == 1){
                                    balls.get(0).die(paddles.get(0));
                                    stats.decrementLives();
                                    stats.decrementScore();
                                }
                                else {
                                    balls.remove(balls.get(i));
                                }
                            }
                            break;
                    }
                }
            }
            for (int k = paddles.size() - 1; k >= 0; k--){
                Paddle paddle = paddles.get(k);
                if (curActor.intersects(paddle)) {
                    switch (curActor.getClass().getSimpleName()) {
                        case ("Item"):
                            if (paddle.intersects(actors.get(i))){
                                paddle.powerup(((Item)curActor), balls, stats);
                                actors.remove(i);
                            }
                            else if(((Item)curActor).hasFallen(screenHeight)){
                                actors.remove(i);
                            }
                    }
                }
            }

        }

        for (int i = balls.size() - 1; i >= 0; i--) {

            //First update the paddle velocity based on user input; goes in direction of paddle
            if (balls.get(0).velocity.x == 0
                && balls.get(0).velocity.y == 0
                && (input.isPressLeft() || input.isPressRight())
               )
            {
                balls.get(0).velocity.setVelocity(input.isPressLeft() ? -Ball.X_VELOCITY : Ball.X_VELOCITY, -Ball.Y_VELOCITY);
            }

            balls.get(i).update(fps, screenWidth);
            //checks the bounds of the ball, dies if below the screen
            if (balls.get(i).hasFallen(screenHeight)) {
                if (balls.size() == 1){
                    balls.get(0).die(paddles.get(0));
                    stats.decrementLives();
                    stats.decrementScore();
                }
                else {
                    balls.remove(balls.get(i));
                    continue;
                }
            }
        }

        for (Paddle paddle : paddles) {
            for (Ball ball : balls) {
                if (paddle.intersects(ball) && ball.velocity.y > 0) {
                    paddle.collide(ball);
                }
            }
            paddle.update(fps, input, screenWidth);
        }

        for (Actor actor: actors) {
            switch (actor.getClass().getSimpleName()){
                case("Item"):
                    ((Item)actor).update(fps);
            }

        }
    }

    //Generates the Actors in predefined positions from a .csv file
    public void generateActors(){

        balls = new ArrayList<>();
        paddles = new ArrayList<>();
        actors= new ArrayList<>();

        balls.add(
                new Ball(
                        screenWidth/2 - Ball.BALL_WIDTH/2,
                        screenHeight - Paddle.PADDLE_HEIGHT * 8 + statsBarOffset + 20,
                        0,
                        0)
        );

        paddles.add(
                new Paddle(
                        (screenWidth/2) - Paddle.PADDLE_WIDTH/2,
                        screenHeight - Paddle.PADDLE_HEIGHT * 4)
        );

        for (int i = 0; i < level.NUM_ROWS; i++){
            for (int j = 0; j < level.NUM_COLUMNS; j++){
                switch (level.csv_file_data.get(i).get(j)){
                    case ("1"):
                        stats.incrementRemainingBricks();
                        actors.add(Brick.generateBrick(j,i));
                        break;
                    case ("2"):
                        actors.add(
                                new Spike(
                                        Spike.SPIKE_WIDTH * j,
                                        Spike.SPIKE_HEIGHT * i + statsBarOffset*3/2)
                        );
                        break;

                      //Old version with ball cavities; removed due to needing too many variables
/*                    case ("3"):
                        balls.add(
                                new Ball (
                                        Ball.BALL_WIDTH * j,
                                        Ball.BALL_HEIGHT * i + 10, //Minor offset to not get stuck
                                        0,
                                        Ball.Y_VELOCITY)
                        );
                        break;*/

                    default:
                        break;
                }
            }
        }
    }

    //Draws all actors, note the wildcard ?
    public void drawActorList(ArrayList<? extends Actor> actor_list) {
        for (Actor a : actor_list){
            canvas.drawBitmap(a.sprite, null, a.hitbox, null);
        }
    }


    void draw() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();

            canvas.drawColor(Color.rgb(0, 0, 0));


            drawActorList(balls);
            drawActorList(paddles);
            drawActorList(actors);

            paint.setColor(Color.WHITE);
            paint.setTextSize(50);


            statsBar.draw(canvas);

            canvas.drawBitmap(
                    BitmapFactory.decodeResource(
                        sprites,R.drawable.breakout_tiles_46),
                    null,pauseButton.hitbox,null);

            if(paused && !gameOver){
                pauseMenu.draw(canvas,paint, "Paused");
            } else if (paused && gameOver && !victory){
                gameOverMenu.draw(canvas,paint, "YOU LOSE", victory);
            } else if (paused && gameOver  && victory) {
                gameOverMenu.draw(canvas,paint,"YOU WIN", victory);
            }


            holder.unlockCanvasAndPost(canvas);


        }
    }

    //Touch event for getting paddle movement
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int index = motionEvent.getActionIndex();
        float x = motionEvent.getX(index);
        float y = motionEvent.getY(index);

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                input.touchDownEvent(x, y);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                input.touchUpEvent(x, y);
                break;
        }

        checkForPause(x , y);
        handleInGameMenu(x ,y);

        return true;
    }

    //Check whether pause button has been pressed, pause if it has been
    public void checkForPause(float x, float y){
        if (!paused) {
            if (pauseButton.collides(x,y)){
                pause();
            }
        }
    }

    // Determine which menu we are in and handle option selection in game Menu
    public void handleInGameMenu(float x,float y){
        if (paused) {
            if (gameOver){
                handleGameOverMenu(x, y);
            }
            else{
               handlePauseMenu(x,y);
            }
        }
    }

    // Handle option selection in gameOverMenu
    public void handleGameOverMenu(float x, float y) {
        int option;
        option = gameOverMenu.handleClick(x, y);
        if (option == 2) {
            restart();
            resume();
        } else if (option == 1) {
            this.GameActivity.returnToMainMenu();
        }
    }

    // Handle option selection in pauseMenu
    public void handlePauseMenu(float x, float y){
        int option = pauseMenu.handleClick(x , y);
        if (option == 2){
            resume();
        } else if (option == 1){
            this.GameActivity.returnToMainMenu();
        }
    }


    public void pause() {
        sound.pause();
        paused = true;
        playing = false;
        draw();
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            System.err.println("Could not pause game, error joining thread: " + e.getMessage());
        }
    }

    public void resume() {
        sound.resume();
        paused = false;
        playing = true;
        gameOver = false;
        frameTimeNow = frameTimePrev = System.currentTimeMillis();
        //stats.lives = 3;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void destroy() {
        for (Paddle paddle : paddles)
            paddle.destroy();
    }
}