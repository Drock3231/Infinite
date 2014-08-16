package com.detel.infinite.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Derick on 2/22/14.
 */
public class Block {

    private Rectangle bounds;
    private Vector2 position;
    protected static final Vector2 SIZE = new Vector2(1f,1f);
    protected boolean isSolid = true;

    protected static Texture texture = new Texture("data/sand.png");
    public static int ID = 1;

    public Block(int x, int y){
        position = new Vector2(x,y);
        bounds = new Rectangle(x,y,SIZE.x,SIZE.y);
    }

    public Rectangle getBounds(){
        return bounds;
    }

    public Vector2 getPosition(){
        return position;
    }

    public Vector2 getSize(){
        return SIZE;
    }

    public boolean isSolid(){
        return isSolid;
    }

    public Texture getTexture(){
        return texture;
    }
}
