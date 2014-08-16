package com.detel.infinite.controllers;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.detel.infinite.models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Derick on 4/3/14.
 */
public class KnifeController {

    //knives and world that the calculations will be performed for
    private ArrayList<ThrowingKnife> throwingKnives = new ArrayList<>();
    private World world;

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
    private static final float MAX_VEL = 15f;

    private static final int viewDistance = 20;

    private boolean jumpingPressed = false;
    private long jumpTime;

    //simple constructor that sets the world and player to calculate for
    public KnifeController(World world){
        this.world = world;
        throwingKnives = world.getThrowingKnives();
    }

    /*update method.
    *Calls the handleInput method that will set the player's acceleration,
    *Puts acceleration in state time and adds it to velocity
    *Makes sure velocity isn't too large and sets it to its max value if so
    *Calls the blockCollision method which stops the player from going ghost
    */
    public void update(float v){

        for(ThrowingKnife knife : throwingKnives){

            knife.getAcceleration().scl(v);
            knife.getVelocity().add(knife.getAcceleration());

            if(knife.getVelocity().x > MAX_VEL) knife.getVelocity().x = MAX_VEL;
            if(knife.getVelocity().x < -MAX_VEL) knife.getVelocity().x = -MAX_VEL;

            checkBlockCollision(knife, v);
            checkEnemyCollsion(knife);
        }

        for(ThrowingKnife knife : throwingKnives){
            if(knife.getVelocity().equals(new Vector2(0,0))){
                throwingKnives.remove(knife);
                break;
            }
        }
    }

    /*Block collision method
    *Puts velocity into state time
    *Obtains player rectangle from pool and sets it to the player's rectangle
    *Creates the startX, endX, startY, and endY variables, [[incomplete]]
    */
    private void checkBlockCollision(ThrowingKnife knife,float v){

        knife.getVelocity().scl(v);

        /*This segment grabs the block on either the left or right of the player based on their direction and the block
         *above that,if the blocks are solid, and checks them for collision
         */
        Rectangle knifeRectangle = rectPool.obtain();
        knifeRectangle.set(knife.getBounds());

        int startX, endX;
        int startY = (int) knife.getBounds().y;
        int endY = (int) (knife.getBounds().y + knife.getSize().y);
        if(knife.getVelocity().x < 0) startX = endX = (int) StrictMath.floor(knife.getBounds().x + knife.getVelocity().x);
        else startX = endX = (int) StrictMath.floor(knife.getBounds().x + knife.getSize().x + knife.getVelocity().x);

        int playerChunkIndex = (int)Math.ceil(knife.getPosition().x/ Chunk.getSIZE().x) - 1;
        getCollidableBlocks(playerChunkIndex,startX,endX,startY,endY);

        knifeRectangle.x += knife.getVelocity().x;

        for (Block block : collidableBlocks){
            if(knifeRectangle.overlaps(block.getBounds())){
                if(knife.getVelocity().x < 0) knife.getPosition().x = block.getBounds().x + block.getBounds().width;
                else knife.getPosition().x = block.getBounds().x - knife.getSize().x;
                knife.getVelocity().x = 0;
                break;
            }
        }

        knifeRectangle.x = knife.getPosition().x;

        /*This segment grabs the block on either the top or bottom of the player based on their direction and the block
         *to the right of that,if they are solid, and checks them for collision
         */
        startX = (int) knife.getBounds().x;
        endX = (int) (knife.getBounds().x + knife.getSize().x);
        if(knife.getVelocity().y < 0) startY = endY = (int) StrictMath.floor(knife.getBounds().y + knife.getVelocity().y);
        else startY = endY = (int) StrictMath.floor(knife.getBounds().y + knife.getSize().y + knife.getVelocity().y);

        getCollidableBlocks(playerChunkIndex,startX,endX,startY,endY);

        knifeRectangle.y += knife.getVelocity().y;

        for (Block block : collidableBlocks){
            if(knifeRectangle.overlaps(block.getBounds())){
                if(knife.getVelocity().y < 0){
                    knife.getPosition().y = block.getBounds().y + block.getBounds().height;
                }else{
                    knife.getPosition().y = block.getBounds().y - knife.getSize().y;
                }
                knife.getVelocity().y = 0;
                break;
            }
        }

        //updates the player, takes velocity out of state time, and clears the collidable array
        knife.getPosition().add(knife.getVelocity());
        knife.getBounds().x = knife.getPosition().x;
        knife.getBounds().y = knife.getPosition().y;
        knife.getVelocity().scl(1/v);
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

    private void checkEnemyCollsion(ThrowingKnife knife){
        ArrayList<Enemy> enemies = world.getEnemies();

        for(Enemy enemy : enemies){
            if(knife.getBounds().overlaps(enemy.getHitBox())){
                knife.getVelocity().x = 0;
                knife.getVelocity().y = 0;
            }
        }


    }
}
