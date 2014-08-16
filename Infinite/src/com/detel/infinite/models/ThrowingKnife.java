package com.detel.infinite.models;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Derick on 4/3/14.
 */
public class ThrowingKnife {

    private Boolean facingLeft;

    private Vector2 position;
    private Vector2 acceleration;
    private Vector2 velocity;

    private final float STARTING_VELOCITY = 15f;

    private int attackPower = 10;

    private Rectangle bounds;
    private static final Vector2 SIZE = new Vector2(0.75f,0.3f);

    public ThrowingKnife(float x, float y, boolean facingLeft){

        position = new Vector2(x,y);
        acceleration = new Vector2();
        velocity = new Vector2(STARTING_VELOCITY, 0);

        bounds = new Rectangle(x,y,SIZE.x,SIZE.y);

        this.facingLeft = facingLeft;
    }

    public Boolean isFacingLeft(){
        return facingLeft;
    }

    public Vector2 getPosition(){
        return position;
    }

    public Vector2 getAcceleration(){
        return acceleration;
    }

    public Vector2 getVelocity(){
        return velocity;
    }

    public float getStartingVelocity(){
        return STARTING_VELOCITY;
    }

    public int getAttackPower(){
        return attackPower;
    }

    public Vector2 getSize(){
        return SIZE;
    }

    public Rectangle getBounds(){
        return bounds;
    }
}
