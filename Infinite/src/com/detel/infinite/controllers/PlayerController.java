package com.detel.infinite.controllers;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.detel.infinite.models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Derick on 2/22/14.
 */
public class PlayerController {

    //player and world that the calculations will be performed for
    private Player player;
    private World world;

    //Creation and storage of keys that the player uses to interact with the world
    public enum Keys{
        Left,Right,Jump,Attack
    }

    static Map<Keys,Boolean> keys = new HashMap<PlayerController.Keys, Boolean>();
    static{
        keys.put(Keys.Left,false);
        keys.put(Keys.Right,false);
        keys.put(Keys.Jump,false);
        keys.put(Keys.Attack,false);
    }

    //Pools for memory saving (not sure if this works?)
    private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };

    //this array holds the blocks that the player can collide with
    private Array<Block> collidableBlocks = new Array<>();

    //These are all the values the game uses to calculate acceleration, immunity, and jump length
    //multiplying by two gives you real-world values in m/s
    private static final float ACCELERATION = 10f;
    private static float GRAVITY = -18f;
    private static float MAX_GRAVITY = -25f;
    private static final float MAX_VEL = 7f;

    private static final long MAX_JUMP_TIME = 300L;
    private static float MAX_JUMP_VEL = 5f;

    private static final long MAX_IMMUNE_TIME = 300L;
    private static float MAX_HOP_VEL = 3f;

    private static final long THROW_COOLDOWN_TIME = 300L;

    private boolean jumpingPressed = false;
    private long jumpTime;

    private boolean immunity = true;
    private long immuneTime;

    private boolean coolDown = false;
    private long coolTime;

    //simple constructor that sets the world and player to calculate for
    public PlayerController(World world){
        this.world = world;
        player = world.getPlayer();
    }

    /*update method.
    *Calls the handleInput method that will set the player's acceleration,
    *Puts acceleration in state time and adds it to velocity
    *Makes sure velocity isn't too large and sets it to its max value if so
    *Calls the blockCollision method which stops the player from going ghost
    */
    public void update(float v){
        handleInput();

        player.getAcceleration().y += GRAVITY;
        player.getAcceleration().scl(v);
        player.getVelocity().add(player.getAcceleration());

        if(player.getVelocity().x > MAX_VEL) player.getVelocity().x = MAX_VEL;
        if(player.getVelocity().x < -MAX_VEL) player.getVelocity().x = -MAX_VEL;

        checkBlockCollision(v);
        checkCollisionWithEnemies();
    }

    /*Block collision method
    *Puts velocity into state time
    *Obtains player rectangle from pool and sets it to the player's rectangle
    *Creates the startX, endX, startY, and endY variables, [[incomplete]]
    */
    private void checkBlockCollision(float v){

        player.getVelocity().scl(v);

        /*This segment grabs the block on either the left or right of the player based on their direction and the block
         *above that,if the blocks are solid, and checks them for collision
         */
        Rectangle playerRectangle = rectPool.obtain();
        playerRectangle.set(player.getBounds());

        int startX, endX;
        int startY = (int) player.getBounds().y;
        int endY = (int) (player.getBounds().y + player.getSize().y);
        if(player.getVelocity().x < 0) startX = endX = (int) StrictMath.floor(player.getBounds().x + player.getVelocity().x);
        else startX = endX = (int) StrictMath.floor(player.getBounds().x + player.getSize().x + player.getVelocity().x);

        int playerChunkIndex = (int)Math.ceil(player.getPosition().x/ Chunk.getSIZE().x) - 1;
        getCollidableBlocks(playerChunkIndex,startX,endX,startY,endY);

        playerRectangle.x += player.getVelocity().x;

        for (Block block : collidableBlocks){
            if(playerRectangle.overlaps(block.getBounds())){
                if(player.getVelocity().x < 0) player.getPosition().x = block.getBounds().x + block.getBounds().width;
                else player.getPosition().x = block.getBounds().x - player.getSize().x;
                player.getVelocity().x = 0;
                break;
            }
        }

        playerRectangle.x = player.getPosition().x;

        /*This segment grabs the block on either the top or bottom of the player based on their direction and the block
         *to the right of that,if they are solid, and checks them for collision
         */
        startX = (int) player.getBounds().x;
        endX = (int) (player.getBounds().x + player.getSize().x);
        if(player.getVelocity().y < 0) startY = endY = (int) StrictMath.floor(player.getBounds().y + player.getVelocity().y);
        else startY = endY = (int) StrictMath.floor(player.getBounds().y + player.getSize().y + player.getVelocity().y);

        getCollidableBlocks(playerChunkIndex,startX,endX,startY,endY);

        playerRectangle.y += player.getVelocity().y;

        for (Block block : collidableBlocks){
            if(playerRectangle.overlaps(block.getBounds())){
                if(player.getVelocity().y < 0){
                    player.getPosition().y = block.getBounds().y + block.getBounds().height;
                    if(GRAVITY < 0) player.setState(Player.State.Idle);
                }else{
                    player.getPosition().y = block.getBounds().y - player.getSize().y;
                    if(GRAVITY > 0) player.setState(Player.State.Idle);
                }
                player.getVelocity().y = 0;
                break;
            }
        }

        //updates the player, takes velocity out of state time, and clears the collidable array
        player.getPosition().add(player.getVelocity());
        player.getBounds().x = player.getPosition().x;
        player.getBounds().y = player.getPosition().y;
        player.getVelocity().scl(1/v);
        collidableBlocks.clear();
    }

    private void getCollidableBlocks(int i,int startX, int endX, int startY, int endY){
        for(int x = startX; x <= endX; x++){
            for(int y = startY; y <= endY; y++){
                try{
                    if(world.getChunks().get(i).getBlocks()[(int)(x - Chunk.getSIZE().x * i)][y].isSolid()){
                        collidableBlocks.add(world.getChunks().get(i).getBlocks()[(int)(x - Chunk.getSIZE().x * i)][y]);
                    }
                } catch(ArrayIndexOutOfBoundsException e){
                    //collidableBlocks.add(new Block(x,y));
                } catch(IndexOutOfBoundsException e){

                }
            }
        }
    }

    private void checkCollisionWithEnemies(){
        ArrayList<Enemy> enemies = world.getEnemies();

        for(Enemy enemy : enemies){
            if(player.getBounds().overlaps(enemy.getBounds()) && !(immunity)){
                player.setHealth(player.getHealth() - enemy.getAttackPower());
                immunity = true;
                immuneTime = System.currentTimeMillis();
                player.getVelocity().y += MAX_HOP_VEL;
                player.getVelocity().x = (player.getVelocity().x > 0) ? -5f : 5f;
            }else if(immunity && (System.currentTimeMillis() - immuneTime) >= MAX_IMMUNE_TIME){
                immunity = false;
            }
        }
    }

    private void handleInput(){

        if(keys.get(Keys.Left)){
            player.setFacingLeft(true);
            if(!(player.getState().equals(Player.State.Jumping))) player.setState(Player.State.Walking);
            player.getAcceleration().x = -ACCELERATION;
        } else if(keys.get(Keys.Right)){
            player.setFacingLeft(false);
            if(!(player.getState().equals(Player.State.Jumping))) player.setState(Player.State.Walking);
            player.getAcceleration().x = ACCELERATION;
        }else{
            if(player.getState() != Player.State.Jumping) player.setState(Player.State.Idle);
            player.getAcceleration().x = 0;
            player.getVelocity().x = 0;
        }

        if(keys.get(Keys.Jump)){
            if(!(player.getState() == Player.State.Jumping)){
                jumpingPressed = true;
                jumpTime = System.currentTimeMillis();
                player.setState(Player.State.Jumping);
                player.getVelocity().y = MAX_JUMP_VEL;
            }else{
                if(jumpingPressed && (System.currentTimeMillis() - jumpTime) >= MAX_JUMP_TIME){
                    jumpingPressed = false;
                }else if(jumpingPressed){
                    player.getVelocity().y = MAX_JUMP_VEL;
                }
            }
        }

        if(keys.get(Keys.Attack)){

            if(!coolDown){
                if(!(player.isFacingLeft())){
                    ThrowingKnife knife = new ThrowingKnife(player.getPosition().x + 0.5f, player.getPosition().y + 1/1.5f, false);
                    knife.getVelocity().x = knife.getStartingVelocity();
                    world.addThrowingKnife(knife);
                } else {
                    ThrowingKnife knife = new ThrowingKnife(player.getPosition().x + 0.5f, player.getPosition().y + 1/1.5f, true);
                    knife.getVelocity().x = -knife.getStartingVelocity();
                    world.addThrowingKnife(knife);
                }
                coolDown = true;
                coolTime = System.currentTimeMillis();
            }else if(coolDown && (System.currentTimeMillis() - coolTime) >= THROW_COOLDOWN_TIME){
                coolDown = false;
            }
        }

    }

    /*~~~~~~~~~~Key Presses~~~~~~~~~~*/

    public void leftPressed(){
        keys.put(Keys.Left, true);
    }

    public void rightPressed(){
        keys.put(Keys.Right, true);
    }

    public void jumpPressed(){
        keys.put(Keys.Jump, true);
    }

    public void attackPressed(){
        keys.put(Keys.Attack, true);
    }

    public void leftReleased(){
        keys.put(Keys.Left, false);
    }

    public void rightReleased(){
        keys.put(Keys.Right, false);
    }

    public void jumpReleased(){
        keys.put(Keys.Jump, false);
    }

    public void attackReleased(){
        keys.put(Keys.Attack, false);
    }
}
