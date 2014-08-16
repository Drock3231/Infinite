package com.detel.infinite.models;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by Derick on 2/21/14.
 */
public class World {

    private ArrayList<Chunk> chunks;
    private ArrayList<Enemy> enemies;
    private ArrayList<ThrowingKnife> throwingKnives;

    private Player player;
    private final static int PLAYER_SPAWN_X = 50;
    private final static int PLAYER_SPAWN_Y = 3;

    private OrthographicCamera camera;
    private final static Vector2 VIEWPORT = new Vector2(20,12);

    private int overallScore;
    private int bonusScore;

    public World(){
        chunks = new ArrayList<>();
        enemies = new ArrayList<>();
        throwingKnives = new ArrayList<>();

        player = new Player(PLAYER_SPAWN_X,PLAYER_SPAWN_Y);
        camera = new OrthographicCamera();
    }

    public void addChunks(Chunk chunk){
        chunks.add(chunk);
    }

    public void addEnemy(Enemy enemy){
        enemies.add(enemy);
    }

    public void addThrowingKnife(ThrowingKnife throwingKnife) {
        throwingKnives.add(throwingKnife);
    }

    public void addBonusScore(int points){
        bonusScore += points;
    }

    public ArrayList<Chunk> getChunks(){
        return chunks;
    }

    public ArrayList<Enemy> getEnemies(){
        return enemies;
    }

    public ArrayList<ThrowingKnife> getThrowingKnives() {
        return throwingKnives;
    }

    public Player getPlayer(){
        return player;
    }

    public OrthographicCamera getCamera(){
        return camera;
    }

    public Vector2 getViewport(){
        return VIEWPORT;
    }

    public int getScore(){
        overallScore = bonusScore + (int)player.getPosition().x;
        return overallScore;
    }

}
