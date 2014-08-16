package com.detel.infinite.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.detel.infinite.models.*;

import java.util.ArrayList;

/**
 * Created by Derick on 2/21/14.
 */
public class WorldRenderer {

    private SpriteBatch spriteBatch;

    private OrthographicCamera camera;
    private Vector2 viewPort;

    private World world;
    private Texture playerTexture = new Texture("data/player.png");
    private Texture enemyTexture = new Texture("data/enemy.png");
    private Texture throwingKnifeTexture = new Texture("data/throwingknife.png");
    private Texture throwingKnifeFlippedTexture = new Texture("data/throwingknifeflipped.png");

    private OrthographicCamera hudCamera;

    private BitmapFont font;
    private FileHandle fontFileHandle = Gdx.files.internal("data/mecha.fnt");
    private FileHandle fontPNGHandle = Gdx.files.internal("data/mecha.png");

    public WorldRenderer(World world){
        spriteBatch = new SpriteBatch();

        camera = world.getCamera();
        camera.setToOrtho(false, world.getViewport().x, world.getViewport().y);
        viewPort = new Vector2(world.getViewport());

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.update();
        font = new BitmapFont(fontFileHandle, fontPNGHandle, false);

        this.world = world;
    }

    public void render(){

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        renderWorld();
        renderPlayer();
        renderKnives();
        renderEnemies();

        spriteBatch.end();

        spriteBatch.setProjectionMatrix(hudCamera.combined);
        spriteBatch.begin();

        renderHUD();

        spriteBatch.end();

    }

    private void renderWorld(){
        //acquire player object so that we can cull tiles
        Player player = world.getPlayer();

        //iterates through all the chunks and draws the blocks that are in viewport range (a little larger than viewport range, actually)
        for(int i = 0; i < world.getChunks().size(); i++){
            Chunk chunk = world.getChunks().get(i);
            for(int x = 0; x < chunk.getBlocks().length; x++){
                for(int y = 0; y < chunk.getBlocks().length; y++){
                    Block block = chunk.getBlocks()[x][y];
                    if(!(player.getPosition().x + viewPort.x < block.getPosition().x) && !(player.getPosition().x - viewPort.x > block.getPosition().x)){
                        spriteBatch.draw(block.getTexture(), block.getPosition().x, block.getPosition().y, block.getSize().x, block.getSize().y);
                    }
                }
            }
        }

    }

    private void renderPlayer(){
        Player player = world.getPlayer();
        spriteBatch.draw(playerTexture, player.getPosition().x, player.getPosition().y, player.getSize().x, player.getSize().y);
    }

    private void renderKnives(){
        ArrayList<ThrowingKnife> throwingKnives = world.getThrowingKnives();

        for(ThrowingKnife knife: throwingKnives){
            if(knife.isFacingLeft())spriteBatch.draw(throwingKnifeFlippedTexture, knife.getPosition().x, knife.getPosition().y, knife.getSize().x, knife.getSize().y);
            else spriteBatch.draw(throwingKnifeTexture, knife.getPosition().x, knife.getPosition().y, knife.getSize().x, knife.getSize().y);
        }
    }

    private void renderEnemies(){
        ArrayList<Enemy> enemies = world.getEnemies();

        for(Enemy enemy: enemies){
            spriteBatch.draw(enemyTexture, enemy.getPosition().x, enemy.getPosition().y, enemy.getSize().x, enemy.getSize().y);
        }
    }

    private void renderHUD(){
        font.setScale(2f);
        font.draw(spriteBatch, "Score: " + world.getScore(), 0, Gdx.graphics.getHeight());
    }
}
