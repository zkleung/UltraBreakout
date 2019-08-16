package com.example.ultrabreakout;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import java.util.ArrayList;

/*
 * Class for drawing the GameOverMenu.
 */

public class GameOverMenu extends DrawableMenu{

    public GameOverMenu(int height, int width){
        Area = new RectF(0,0,width,height);
        this.paint = new Paint();
        this.canvas = new Canvas();
        this.width = width;
        this.height = height;

        ButtonsList = new ArrayList<>();
        paint.setTextSize(height/8);
        ButtonsList.add(buildDrawableButton("EXIT", width/2,
                (int)((height * 9 / 12) - ( paint.descent() + paint.ascent()) / 2) ));
        ButtonsList.add(buildDrawableButton("RETRY", width/2,
                (int)((height * 7 / 12) - ( paint.descent() + paint.ascent()) / 2) ));
    }

}
