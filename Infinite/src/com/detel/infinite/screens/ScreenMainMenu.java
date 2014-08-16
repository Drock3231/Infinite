package com.detel.infinite.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.detel.infinite.Infinite;

/**
 * Created by Derick on 4/14/14.
 */
public class ScreenMainMenu extends ScreenBase{

    private Infinite infinite;

    private Stage stage;
    private static final Vector2 viewPort = new Vector2(20f,12f);

    private FileHandle skinHandle = Gdx.files.internal("data/uiskin.json");

    float centerPad;
    float itemWidth;
    float itemHeight;

    public ScreenMainMenu(Infinite infinite){
        this.infinite = infinite;

        centerPad = Gdx.graphics.getHeight() * 1/27;
        itemWidth = Gdx.graphics.getWidth() * 1/2;
        itemHeight = Gdx.graphics.getHeight() * 1/8;
    }

    @Override
    public void show(){

        //Create

        stage = new Stage();
        Skin skin = new Skin(skinHandle);

        Label titleLabel = new Label("Infinite", skin);

        TextButton startButton = new TextButton("Start", skin);

        //Layout

        Table table = new Table(skin).debug();
        table.setFillParent(true);
        table.center();

        table.add(titleLabel).center().pad(centerPad);
        table.row().height(itemHeight);
        table.add(startButton).width(itemWidth).pad(5f);
        table.row().height(itemHeight);
        stage.addActor(table);

        //Listeners

        startButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event,float x,float y){
                System.out.println("Clicked");
                infinite.setScreen(new ScreenGame(infinite));
            }
        });

        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void render(float v){

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        stage.act(v);
        stage.draw();

    }

    @Override
    public void resize(int i, int i2){

    }

}
