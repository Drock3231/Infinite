package com.detel.infinite.controllers;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.detel.infinite.models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Derick on 4/2/14.
 */
public class EnemyController {

    //player and world that the calculations will be performed for
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private World world;

    public enum Keys{
        Left,Right,Jump,Attack
    }

    static Map<Keys,Boolean> keys = new HashMap<EnemyController.Keys, Boolean>();
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
    private static final float MAX_VEL = 3f;
    private static final long MAX_JUMP_TIME = 300L;
    private static float MAX_JUMP_VEL = 5f;
    private static float MAX_HOP_VEL = 3f;

    private static final int POINTS_FOR_KILL = 100;

    private static final int viewDistance = 20;

    private boolean jumpingPressed = false;
    private long jumpTime;

    //simple constructor that sets the world and player to calculate for
    public EnemyController(World world){
        this.world = world;
        enemies = world.getEnemies();
    }

    /*update method.
    *Calls the handleInput method that will set the player's acceleration,
    *Puts acceleration in state time and adds it to velocity
    *Makes sure velocity isn't too large and sets it to its max value if so
    *Calls the blockCollision method which stops the player from going ghost
    */
    public void update(float v){

        for(Enemy enemy : enemies){
            determineAcceleration(enemy);

            enemy.getAcceleration().y += GRAVITY;
            enemy.getAcceleration().scl(v);
            enemy.getVelocity().add(enemy.getAcceleration());

            if(enemy.getVelocity().x > MAX_VEL) enemy.getVelocity().x = MAX_VEL;
            if(enemy.getVelocity().x < -MAX_VEL) enemy.getVelocity().x = -MAX_VEL;

            if(enemy.getVelocity().y > MAX_VEL) enemy.getVelocity().y = MAX_VEL;

            checkBlockCollision(enemy, v);
            checkKnifeCollsion(enemy);
        }

        for(Enemy enemy: enemies){
            if(enemy.getHealth() <= 0){
                enemies.remove(enemy);
                world.addBonusScore(POINTS_FOR_KILL);
                break;
            }
        }
    }

    private void determineAcceleration(Enemy enemy){
            Player player = world.getPlayer();

            Rectangle viewRadius = rectPool.obtain();
            viewRadius.set(enemy.getPosition().x - viewDistance, enemy.getBounds().y - viewDistance, viewDistance + viewDistance, viewDistance + viewDistance);

            if(viewRadius.overlaps(player.getBounds())){
                enemy.getAcceleration().x = (enemy.getPosition().x > player.getPosition().x) ? -ACCELERATION : ACCELERATION;
            }

        if(enemy.getJumpingPressed()){
            if(!(enemy.getState() == Enemy.State.Jumping)){
                enemy.setJumpTime(System.currentTimeMillis());
                enemy.setState(Enemy.State.Jumping);
                enemy.getVelocity().y = MAX_JUMP_VEL;
            }else{
                if(enemy.getJumpingPressed() && (System.currentTimeMillis() - enemy.getJumpTime()) >= MAX_JUMP_TIME){
                    enemy.setJumpingPressed(false);
                }else if(enemy.getJumpingPressed()){
                    enemy.getVelocity().y = MAX_JUMP_VEL;
                }
            }
        }
    }


    /*Block collision method
    *Puts velocity into state time
    *Obtains player rectangle from pool and sets it to the player's rectangle
    *Creates the startX, endX, startY, and endY variables, [[incomplete]]
    */
    private void checkBlockCollision(Enemy enemy,float v){

        enemy.getVelocity().scl(v);

        /*This segment grabs the block on either the left or right of the player based on their direction and the block
         *above that,if the blocks are solid, and checks them for collision
         */
        Rectangle enemyRectangle = rectPool.obtain();
        enemyRectangle.set(enemy.getBounds());

        int startX, endX;
        int startY = (int) enemy.getBounds().y;
        int endY = (int) (enemy.getBounds().y + enemy.getSize().y);
        if(enemy.getVelocity().x < 0) startX = endX = (int) StrictMath.floor(enemy.getBounds().x + enemy.getVelocity().x);
        else startX = endX = (int) StrictMath.floor(enemy.getBounds().x + enemy.getSize().x + enemy.getVelocity().x);

        int playerChunkIndex = (int)Math.ceil(enemy.getPosition().x/ Chunk.getSIZE().x) - 1;
        getCollidableBlocks(playerChunkIndex,startX,endX,startY,endY);

        enemyRectangle.x += enemy.getVelocity().x;

        for (Block block : collidableBlocks){
            if(enemyRectangle.overlaps(block.getBounds())){
                if(enemy.getVelocity().x < 0) enemy.getPosition().x = block.getBounds().x + block.getBounds().width;
                else enemy.getPosition().x = block.getBounds().x - enemy.getSize().x;
                enemy.getVelocity().x = 0;

                enemy.setJumpingPressed(true);
                break;
            }
        }

        enemyRectangle.x = enemy.getPosition().x;

        /*This segment grabs the block on either the top or bottom of the player based on their direction and the block
         *to the right of that,if they are solid, and checks them for collision
         */
        startX = (int) enemy.getBounds().x;
        endX = (int) (enemy.getBounds().x + enemy.getSize().x);
        if(enemy.getVelocity().y < 0) startY = endY = (int) StrictMath.floor(enemy.getBounds().y + enemy.getVelocity().y);
        else startY = endY = (int) StrictMath.floor(enemy.getBounds().y + enemy.getSize().y + enemy.getVelocity().y);

        getCollidableBlocks(playerChunkIndex,startX,endX,startY,endY);

        enemyRectangle.y += enemy.getVelocity().y;

        for (Block block : collidableBlocks){
            if(enemyRectangle.overlaps(block.getBounds())){
                if(enemy.getVelocity().y < 0){
                    enemy.getPosition().y = block.getBounds().y + block.getBounds().height;
                    if(GRAVITY < 0) enemy.setState(Enemy.State.Idle);
                }else{
                    enemy.getPosition().y = block.getBounds().y - enemy.getSize().y;
                    if(GRAVITY > 0) enemy.setState(Enemy.State.Idle);
                }
                enemy.getVelocity().y = 0;
                break;
            }
        }

        //updates the player, takes velocity out of state time, and clears the collidable array
        enemy.getPosition().add(enemy.getVelocity());
        enemy.getBounds().x = enemy.getPosition().x;
        enemy.getBounds().y = enemy.getPosition().y;
        enemy.getHitBox().x = enemy.getPosition().x + (enemy.getSize().x - enemy.getHitSize().x);
        enemy.getHitBox().y = enemy.getPosition().y;
        enemy.getVelocity().scl(1/v);
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

    public void checkKnifeCollsion(Enemy enemy){
        ArrayList<ThrowingKnife> throwingKnives = world.getThrowingKnives();

        for(ThrowingKnife knife : throwingKnives){
            if(enemy.getBounds().overlaps(knife.getBounds())){
                enemy.setHealth(enemy.getHealth() - knife.getAttackPower());
                enemy.getVelocity().y += MAX_HOP_VEL;
                enemy.getVelocity().x = (enemy.getVelocity().x > 0) ? -5f : 5f;
            }
        }
    }
}
