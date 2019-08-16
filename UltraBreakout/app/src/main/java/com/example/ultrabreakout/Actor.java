package com.example.ultrabreakout;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.graphics.Bitmap;
import androidx.annotation.DrawableRes;

/*
 * Parent class for all in-game objects.
 * They all have position, Velocity, and images.
 * Everything is a rectangle, by the way.
 *
 * In retrospect, it may have been easier had all the children
 *  contained ArrayLists of all instances of them.
 * Additionally, all actors originally were intended to be able to
 *  be mobile depending on the level, including Spikes.
 *
 */

class Actor {

    class Velocity {

        float x;
        float y;

        Velocity (float _x, float _y){
            x = _x;
            y = _y;
        }

        //Actual velocity; speed and direction
        void setVelocity(float _x, float _y){
            x = _x;
            y = _y;
        }

        //Just speed, not direction
        void setSpeed(float speed_multiplier){
            x *= speed_multiplier;
            y *= speed_multiplier;
        }

        void reverseX(){
            x = -x;
        }

        void reverseY(){
            y = -y;
        }

    }
    public static Resources sprites;

    RectF hitbox;
    Velocity velocity;

    //Should be constant width/height, unless size is increased
    float width;
    float height;
    Bitmap sprite;

    Actor (float x_pos, float y_pos, float x_vel, float y_vel,
           float _width, float _height,
           Bitmap _sprite){
        width = _width;
        height = _height;
        hitbox = new RectF(x_pos,y_pos,x_pos + width,y_pos + height);
        velocity = new Velocity(x_vel, y_vel);
        sprite = _sprite;
    }

    //Sets the sprite
    public void setSprite(@DrawableRes int sprite_id) {
        sprite = BitmapFactory.decodeResource(sprites,sprite_id);
    }

    //Returns if this actor collides with another one.
    public boolean intersects (Actor actor){
        return RectF.intersects(this.hitbox, actor.hitbox);
    }

    //Puts the actor in another position
    public void reposition (float x_pos, float y_pos){
        this.hitbox.left = x_pos;
        this.hitbox.top = y_pos;
        this.hitbox.right = x_pos + width;
        this.hitbox.bottom = y_pos + height;
    }

    //Updates pos based on velocity, shouldn't need to be called outside
    public void updatePos (float fps){
        hitbox.left += velocity.x / fps;
        hitbox.top += velocity.y / fps;
        hitbox.right = hitbox.left + width;
        hitbox.bottom = hitbox.top + height;
    }
}
