package com.detel.infinite.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.detel.infinite.models.Chunk;
import com.detel.infinite.models.World;

/**
 * Created by Derick on 2/21/14.
 */
public class WorldController {

    private World world;

    private PlayerController playerController;
    private EnemyController enemyController;
    private KnifeController knifeController;

    private CameraController cameraController;
    private ChunkController chunkController;

    public WorldController(World world){
        this.world = world;

        playerController = new PlayerController(world);
        enemyController = new EnemyController(world);
        knifeController = new KnifeController(world);

        cameraController = new CameraController(world);
        chunkController = new ChunkController(world);

        createWorld();
    }

    private void createWorld(){
        world.addChunks(chunkController.generateFirstChunk());
        world.addChunks(chunkController.generateSecondChunk());
    }

    private void createNewChunk(){
        int i = world.getChunks().size();
        world.addChunks(chunkController.generateChunk(i));
    }

    public void update(float v){
        playerController.update(v);
        enemyController.update(v);
        knifeController.update(v);

        cameraController.update();

        if(world.getPlayer().getPosition().x > (Chunk.getSIZE().x - 10) * (world.getChunks().size() - 1)) createNewChunk();
    }

    public PlayerController getPlayerController(){
        return playerController;
    }

}
