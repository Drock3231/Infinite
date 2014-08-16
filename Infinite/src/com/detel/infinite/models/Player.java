package com.detel.infinite.models;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Derick on 2/22/14.
 */
public class Player {

    public enum State{
        Idle,Walking,Jumping
    }

    private State state;
    private Boolean facingLeft = false;

    private Vector2 position;
    private Vector2 acceleration;
    private Vector2 velocity;

    private int health = 100;

    private Rectangle bounds;
    private static final Vector2 SIZE = new Vector2(1f,2f);

    public Player(int x, int y){
        state = State.Idle;

        position = new Vector2(x,y);
        acceleration = new Vector2();
        velocity = new Vector2();

        bounds = new Rectangle(x,y,SIZE.x,SIZE.y);
    }

    public State getState(){
        return state;
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

    public Vector2 getSize(){
        return SIZE;
    }

    public Rectangle getBounds(){
        return bounds;
    }

    public int getHealth(){
        return health;
    }

    public void setState(State state){
        this.state = state;
    }

    public void setFacingLeft(Boolean facingLeft){
        this.facingLeft = facingLeft;
    }

    public void setHealth(int health){
        this.health = health;
    }
}
