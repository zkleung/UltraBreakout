package com.example.ultrabreakout;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import java.util.ArrayList;

/*
 * Handles creation of in-game drawable menus created using
 *  canvas and paint, as opposed to Android menus
 */

public class DrawableMenu {
    RectF Area;
    int width;
    int height;
    Paint paint;
    Canvas canvas;

    ArrayList<DrawableMenuButton> ButtonsList;

    //Adjust Area obtained from getTextBounds by offset to match actual drawText Area
    public void adjustBoxArea(Rect Area, int offsetX, int offsetY) {
        int uncenter = (Area.right - Area.left) / 2;
        Area.bottom = Area.bottom + offsetY;
        Area.top = Area.top + offsetY;
        Area.left = Area.left + offsetX - 2 * uncenter;
        Area.right = Area.right + offsetX;
    }

    //Check which button has been pressed, return int corresponding to which button has been pressed
    public int handleClick(float x, float y) {
        for (int i = 0; i < ButtonsList.size(); i++) {
            if (ButtonsList.get(i).contains(x, y)) {
                return i + 1;
            }
        }
        return 0;
    }

    //Draw a PauseMenu
    public void draw(Canvas canvas, Paint paint, String str){
        //Draw filter
        paint.setARGB(100,130,130,180);
        canvas.drawRect(Area,paint);

        //Draw str in middle of the screen
        paint.setARGB(255,255,255,255);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(height/5);
        int centerX = width/2;
        int centerY = (int)((height / 4) - ( paint.descent() + paint.ascent()) / 2);
        canvas.drawText(str, centerX, centerY, paint);

        //Draw Buttons in ButtonsList
        for (DrawableMenuButton button : ButtonsList){
            button.draw(canvas,paint);
        }

    }

    //Draw a gameOverMenu
    public void draw(Canvas canvas, Paint paint, String str, boolean victory){
        //Draw filter based on victory or defeat
        if (victory) {
            paint.setARGB(80,70,130,150);
        } else {
            paint.setARGB(120, 220, 30, 30);
        }
        canvas.drawRect(Area,paint);

        //Draw str in middle of the screen
        paint.setARGB(255,255,255,255);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(height/5);
        int centerX = width/2;
        int centerY = (int)((height / 4) - ( paint.descent() + paint.ascent()) / 2);
        canvas.drawText(str, centerX, centerY, paint);

        //Draw Buttons in ButtonsList
        for (DrawableMenuButton button : ButtonsList){
            button.draw(canvas,paint);
        }

    }

    public DrawableMenuButton buildDrawableButton(String Text, int x, int y){
        Rect Area = new Rect();
        paint.getTextBounds(Text, 0, Text.length() , Area);
        adjustBoxArea(Area, x, y);
        DrawableMenuButton button = new DrawableMenuButton(Area, x, y, height/8, Text);
        return button;
    }

}
