package com.detel.infinite.models;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Derick on 2/22/14.
 */
public class BlockAir extends Block {

    protected static Texture texture = new Texture("data/air.png");
    public static int ID = 0;

    public BlockAir(int x, int y){
        super(x, y);
        isSolid = false;
    }

    @Override
    public Texture getTexture(){
        return texture;
    }

}