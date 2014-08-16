package com.detel.infinite.models;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Derick on 2/24/14.
 */
public class Enemy {

    public enum State{
        Idle,Walking,Jumping
    }

    private State state;
    private Boolean facingLeft;

    private Vector2 position;
    private Vector2 acceleration;
    private Vector2 velocity;

    private int health = 100;
    private int attackPower = 5;

    private boolean jumpingPressed = false;
    private long jumpTime;

    private Rectangle bounds;
    private Rectangle hitBox;
    private static final Vector2 SIZE = new Vector2(1f,2f);
    private static final Vector2 HIT_SIZE = new Vector2(0.6f,2f);

    public Enemy(int x, int y){
        state = State.Idle;

        position = new Vector2(x,y);
        acceleration = new Vector2();
        velocity = new Vector2();

        bounds = new Rectangle(x,y,SIZE.x,SIZE.y);
        hitBox = new Rectangle(x + (SIZE.x - HIT_SIZE.x),y,HIT_SIZE.x,HIT_SIZE.y);
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

    public Vector2 getHitSize(){
        return HIT_SIZE;
    }

    public Rectangle getBounds(){
        return bounds;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public int getHealth(){
        return health;
    }

    public int getAttackPower(){
        return attackPower;
    }

    public Boolean getJumpingPressed(){
        return jumpingPressed;
    }

    public long getJumpTime(){
        return jumpTime;
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

    public void setJumpingPressed(boolean jumpingPressed){
        this.jumpingPressed = jumpingPressed;
    }

    public void setJumpTime(long jumpTime){
        this.jumpTime = jumpTime;
    }
}
