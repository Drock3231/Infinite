package com.detel.infinite.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.detel.infinite.Infinite;
import com.detel.infinite.controllers.WorldController;
import com.detel.infinite.models.World;
import com.detel.infinite.views.WorldRenderer;

/**
 * Created by Derick on 2/21/14.
 */
public class ScreenGame extends ScreenBase implements InputProcessor {

    private Infinite infinite;

    private World world;
    private WorldRenderer worldRenderer;
    private WorldController worldController;

    public ScreenGame(Infinite infinite){
        this.infinite = infinite;
    }

    @Override
    public void show() {
        world = new World();
        worldRenderer = new WorldRenderer(world);
        worldController = new WorldController(world);

        Gdx.input.setInputProcessor(this);
    }

    @Override
     public void render(float v) {
        if(v < 0.05f){
            worldController.update(v);
            worldRenderer.render();
        }

        if(world.getPlayer().getHealth() <= 0 ){
            infinite.setScreen(new ScreenGameOver(infinite, world, worldRenderer));
        }
     }

    @Override
    public void resize(int i, int i2) {

    }

    /*~~~~~~~~~~~Input Processor Methods~~~~~~~~~~~*/

    @Override
    public boolean keyDown(int keycode) {

        if(Gdx.app.getType() == Application.ApplicationType.Desktop){
            if(keycode == Input.Keys.LEFT){
                worldController.getPlayerController().leftPressed();
            } else if(keycode == Input.Keys.RIGHT){
                worldController.getPlayerController().rightPressed();
            } else if(keycode == Input.Keys.SPACE){
                worldController.getPlayerController().jumpPressed();
            } else if(keycode == Input.Keys.W) {
                worldController.getPlayerController().attackPressed();
            }
        }


        return false;
    }

    @Override
    public boolean keyUp(int keycode) {

        if(Gdx.app.getType() == Application.ApplicationType.Desktop){
            if(keycode == Input.Keys.LEFT){
                worldController.getPlayerController().leftReleased();
            } else if(keycode == Input.Keys.RIGHT){
                worldController.getPlayerController().rightReleased();
            } else if(keycode == Input.Keys.SPACE){
                worldController.getPlayerController().jumpReleased();
            } else if(keycode == Input.Keys.W) {
                worldController.getPlayerController().attackReleased();
            }
        }

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if(Gdx.app.getType() == Application.ApplicationType.Android){
            double realTouchX = Math.floor(screenX/(Gdx.graphics.getWidth()/10));
            double realTouchY = Math.floor(screenY/(Gdx.graphics.getHeight()/6));

            if((realTouchX == 8) && (realTouchY == 4)){
                worldController.getPlayerController().leftPressed();
            }else if(realTouchX == 9 && realTouchY == 4){
                worldController.getPlayerController().rightPressed();
            } else if(realTouchX == 2 && realTouchY == 4){
                worldController.getPlayerController().jumpPressed();
            } else if(realTouchX == 1 && realTouchY == 4) {
                worldController.getPlayerController().attackPressed();
            }
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if(Gdx.app.getType() == Application.ApplicationType.Android){
            double realTouchX = Math.floor(screenX/(Gdx.graphics.getWidth()/10));
            double realTouchY = Math.floor(screenY/(Gdx.graphics.getHeight()/6));

            if((realTouchX == 8) && (realTouchY == 4)){
                worldController.getPlayerController().leftReleased();
            }else if(realTouchX == 9 && realTouchY == 4){
                worldController.getPlayerController().rightReleased();
            } else if(realTouchX == 2 && realTouchY == 4){
                worldController.getPlayerController().jumpReleased();
            } else if(realTouchX == 1 && realTouchY == 4) {
                worldController.getPlayerController().attackReleased();
            }
        }

        return false;
    }

    @Override
    public boolean touchDragged(int i, int i2, int i3) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i2) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }
}
